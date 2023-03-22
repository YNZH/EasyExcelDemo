package com.maple.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.fastjson.JSON;
import com.maple.excel.header.ExcelHeader;
import com.maple.excel.config.ExcelImportConfig;
import com.maple.excel.extend.ExtendFiled;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author gaojinfeng
 * @date 2023/3/22
 * @description
 */
public class Importer {

    class XY {
        private Integer rowIdx;
        private Integer colIdx;

        public Integer getRowIdx() {
            return rowIdx;
        }

        public void setRowIdx(Integer rowIdx) {
            this.rowIdx = rowIdx;
        }

        public Integer getColIdx() {
            return colIdx;
        }

        public void setColIdx(Integer colIdx) {
            this.colIdx = colIdx;
        }

        public XY(Integer rowIdx, Integer colIdx) {
            this.rowIdx = rowIdx;
            this.colIdx = colIdx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            XY xy = (XY) o;
            return Objects.equals(rowIdx, xy.getRowIdx()) && Objects.equals(colIdx, xy.getColIdx());
        }

        @Override
        public int hashCode() {
            return Objects.hash(rowIdx, colIdx);
        }
    }

    public <T extends ExtendFiled> List<T> importFile(InputStream in, Class<T> clazz, ExcelImportConfig importExcelImportConfig) {
        if (importExcelImportConfig == null || !importExcelImportConfig.valid()) {
            throw new IllegalArgumentException("导入配置不能为空或者配置错误");
        }

        List<ExcelHeader> customHeaderList = importExcelImportConfig.getCustomHeaderList();
        List<ExcelHeader> headerList = importExcelImportConfig.getBasicHeaderList();

        //获取 每行列下标和字段的对应关系
        Map<Integer, String> basicIndex2Field = new HashMap<>(headerList.size());
        Map<Integer, String> extendIndex2Field = new HashMap<>(customHeaderList.size());
        for (int columnIndex = 0; columnIndex < headerList.size() + customHeaderList.size(); columnIndex++) {
            if (columnIndex < headerList.size()) {
                basicIndex2Field.put(columnIndex, headerList.get(columnIndex).getFiledName());
            } else {
                extendIndex2Field.put(columnIndex, customHeaderList.get(columnIndex - headerList.size()).getFiledName());
            }
        }

        List<T> dataList = new ArrayList<>();
        //合并单元格的信息
        List<CellExtra> extraMergeInfoList = new ArrayList<>();

        Map<XY, Object> xy2Val = new HashMap<>();
        EasyExcelFactory.read(in, new AnalysisEventListener<Map<Integer, Object>>() {
                    @Override
                    public void invoke(Map<Integer, Object> stringObjectMap, AnalysisContext analysisContext) {
                        Map<String, Object> basicMap = new HashMap<>(basicIndex2Field.size());
                        Map<String, Object> extendMap = new HashMap<>(extendIndex2Field.size());
                        for (Map.Entry<Integer, Object> next : stringObjectMap.entrySet()) {
                            Integer colIdx = next.getKey();
                            if (basicIndex2Field.containsKey(colIdx)) {
                                basicMap.put(basicIndex2Field.get(colIdx), next.getValue());
                            } else if (extendIndex2Field.containsKey(colIdx)) {
                                extendMap.put(extendIndex2Field.get(colIdx), next.getValue());
                            } else {
                                //ignore
                            }

                            //缓存cell的值，方便后面填充合并的单元格
                            if (Objects.nonNull(next.getValue())) {
                                xy2Val.put(new XY(analysisContext.readRowHolder().getRowIndex() - importExcelImportConfig.getHeaderNumber(), colIdx),
                                        next.getValue());
                            }
                        }
                        T data;
                        if (basicMap.isEmpty()) {
                            try {
                                data = clazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            data = JSON.parseObject(JSON.toJSONString(basicMap), clazz);
                        }
                        data.setExtendFiledMap(extendMap);
                        dataList.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                        if (Objects.nonNull(importExcelImportConfig.getDoAllAnalysedConsumer())) {
                            importExcelImportConfig.getDoAllAnalysedConsumer().accept(analysisContext);
                        }
                    }

                    @Override
                    public void extra(CellExtra extra, AnalysisContext context) {
                        if (Objects.requireNonNull(extra.getType()) == CellExtraTypeEnum.MERGE
                                && extra.getRowIndex() >= importExcelImportConfig.getHeaderNumber()) {
                            extraMergeInfoList.add(extra);
                        }
                    }
                }).extraRead(CellExtraTypeEnum.MERGE)
                .sheet()
                .headRowNumber(importExcelImportConfig.getHeaderNumber()).doRead();

        //不包含合并单元格
        if (extraMergeInfoList.isEmpty()) {
            return dataList;
        }

        //处理合并单元格中未null的字段
        return explainMergeData(dataList, extraMergeInfoList, importExcelImportConfig.getHeaderNumber(), xy2Val, headerList, customHeaderList);
    }

    private <T extends ExtendFiled> List<T> explainMergeData(List<T> dataList,
                                                             List<CellExtra> extraMergeInfoList,
                                                             Integer headerNumber,
                                                             Map<XY, Object> xy2Val,
                                                             List<ExcelHeader> basicHeaderList,
                                                             List<ExcelHeader> excelHeaderList) {
        // 循环所有合并单元格信息
        extraMergeInfoList.forEach(cellExtra -> {
            int firstRowIndex = cellExtra.getFirstRowIndex() - headerNumber;
            int lastRowIndex = cellExtra.getLastRowIndex() - headerNumber;
            int firstColumnIndex = cellExtra.getFirstColumnIndex();
            int lastColumnIndex = cellExtra.getLastColumnIndex();
            // 获取初始值
            Object initValue = xy2Val.get(new XY(firstRowIndex, firstColumnIndex));
            // 设置值
            for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                for (int j = firstColumnIndex; j <= lastColumnIndex; j++) {
                    setInitValueToList(initValue, i, j, dataList, basicHeaderList, excelHeaderList);
                }
            }
        });
        return dataList;
    }

    /**
     * 设置合并单元格的值
     *
     * @param filedValue  值
     * @param rowIndex    行
     * @param columnIndex 列
     * @param rawList     解析数据
     */
    private static <T extends ExtendFiled> void setInitValueToList(Object filedValue,
                                                                   Integer rowIndex,
                                                                   Integer columnIndex,
                                                                   List<T> rawList,
                                                                   List<ExcelHeader> basicHeaderList,
                                                                   List<ExcelHeader> excelHeaderList) {
        T object = rawList.get(rowIndex);
        if (columnIndex >= basicHeaderList.size()) {
            //扩展字段合并
            object.getExtendFiledMap().put(excelHeaderList.get(columnIndex - basicHeaderList.size()).getFiledName(), filedValue);
            return;
        }
        String filedName = basicHeaderList.get(columnIndex).getFiledName();
        for (Field field : object.getClass().getDeclaredFields()) {
            //提升反射性能，关闭安全检查
            makeAccessible(field);
            if (Objects.equals(field.getName(), filedName)) {
                Object val = filedValue;
                if (field.getType().equals(Integer.class)) {
                    val = Integer.parseInt((String) val);
                }
                setField(field, object, val);
                break;
            }
        }
    }

    private static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    private static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
