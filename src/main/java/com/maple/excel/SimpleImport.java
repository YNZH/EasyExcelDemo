package com.maple.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhankun
 * @date 2023/3/24 11:15
 */
public class SimpleImport<T> {

    private int headerNum;

    private Map<Integer, String> index2FieldNameMap;

    private Class<T> clazz;


    public SimpleImport(int headerNum, Class<T> clazz) {
        this.headerNum = headerNum;
        this.clazz = clazz;
        setIndex2FieldNameMap(clazz);
    }

    private void setIndex2FieldNameMap(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields.length == 0) {
            return;
        }
        index2FieldNameMap = Maps.newHashMapWithExpectedSize(declaredFields.length);
        ExcelProperty annotation;
        for (Field field : declaredFields) {
            annotation = field.getAnnotation(ExcelProperty.class);
            if (annotation != null) {
                index2FieldNameMap.put(annotation.index(), field.getName());
            }
        }
    }

    public List<T> getParsedResultList(InputStream inputStream) {
        if (MapUtils.isEmpty(index2FieldNameMap)) {
            throw new RuntimeException("请对实体类变量添加注解ExcelProperty");
        }
        List<Map<Integer, Object>> dataList = new ArrayList<>();
        List<CellExtra> extraMergeInfoList = new ArrayList<>();
        EasyExcelFactory.read(inputStream, new AnalysisEventListener<Map<Integer, Object>>() {
            @Override
            public void invoke(Map<Integer, Object> stringObjectMap, AnalysisContext analysisContext) {
                dataList.add(stringObjectMap);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }

            @Override
            public void extra(CellExtra extra, AnalysisContext context) {
                if (Objects.requireNonNull(extra.getType()) == CellExtraTypeEnum.MERGE
                        && extra.getRowIndex() >= headerNum) {
                    extraMergeInfoList.add(extra);
                }
            }
        }).extraRead(CellExtraTypeEnum.MERGE).sheet().headRowNumber(headerNum).doRead();
        //含合并单元格，需要再次计算赋值
        dealMergeCell(dataList, extraMergeInfoList);
        //dataList的map中key:index转换为fieldName
        List<Map<String, Object>> objectDataList = dealDataListFieldName(dataList);
        return JSONArray.parseArray(JSON.toJSONString(objectDataList), clazz);
    }

    private ArrayList<Map<String, Object>> dealDataListFieldName(List<Map<Integer, Object>> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>(dataList.size());
        for (Map<Integer, Object> map : dataList) {
            Map<String, Object> fieldName2ValueMap = Maps.newHashMapWithExpectedSize(index2FieldNameMap.size());
            map.forEach((key, value) -> fieldName2ValueMap.put(index2FieldNameMap.get(key), value));
            list.add(fieldName2ValueMap);
        }
        return list;
    }

    private void dealMergeCell(List<Map<Integer, Object>> dataList, List<CellExtra> extraMergeInfoList) {
        if (CollectionUtils.isEmpty(extraMergeInfoList)) {
            return;
        }
        for (CellExtra cellExtra : extraMergeInfoList) {
            int firstRowIndex = cellExtra.getFirstRowIndex() - headerNum;
            int lastRowIndex = cellExtra.getLastRowIndex() - headerNum;
            Object value = dataList.get(firstRowIndex).get(cellExtra.getFirstColumnIndex());
            if (value == null) {
                continue;
            }
            // 设置后面的值与第一个值相同
            for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                for (int j = cellExtra.getFirstColumnIndex(); j <= cellExtra.getLastColumnIndex(); j++) {
                    dataList.get(i).put(j, value);
                }
            }
        }
    }

}
