package com.maple.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.maple.excel.header.ExcelHeader;
import com.maple.excel.extend.ExtendFiled;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaojinfeng
 * @date 2023/3/22
 * @description
 */
public class Exporter {
    public static <T extends ExtendFiled> void export(String fileName, String sheetName, Class<T> clazz, List<ExcelHeader> customHeaderList, List<T> data) {
        List<ExcelHeader> headerList = getHeaderList(clazz);
        customHeaderList = customHeaderList.stream().sorted(Comparator.comparingInt(ExcelHeader::getOrder)).collect(Collectors.toList());
        headerList.addAll(customHeaderList);

        EasyExcel.write(new File(fileName))
                .autoCloseStream(true)
                .excelType(ExcelTypeEnum.XLSX)
                .head(headerList.stream().map(ExcelHeader::getExtendHeaderTitle).collect(Collectors.toList()))
                .sheet(sheetName)
                .doWrite(getExportListData(data, headerList));
    }

    /**
     * 按照顺序从小到达 返回
     *
     * @param clazz clazz
     * @return {@link List}<{@link List}<{@link String}>>
     */
    public static List<ExcelHeader> getHeaderList(Class<?> clazz) {
        return getHeaderListByClazz(clazz);
    }

    public static List<List<Object>> getExportListData(List<? extends ExtendFiled> dataList, List<ExcelHeader> headerList) {
        List<List<Object>> rows = new ArrayList<>(dataList.size());
        for (ExtendFiled data : dataList) {
            List<Object> list = new ArrayList<>(headerList.size());
            Map<String, Object> map = (Map<String, Object>) JSON.toJSON(data);
            map.putAll(data.getExtendFiledMap());

            //add filed orderly
            List<String> needExtendHeaderList = headerList.stream().map(ExcelHeader::getFiledName).collect(Collectors.toList());
            needExtendHeaderList.forEach(header -> list.add(map.get(header)));
            rows.add(list);
        }
        return rows;
    }


    private static List<ExcelHeader> getHeaderListByClazz(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Objects.nonNull(field.getAnnotation(ExcelProperty.class)))
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(ExcelProperty.class).index()))
                .map(field -> new ExcelHeader(Arrays.asList(field.getAnnotation(ExcelProperty.class).value()), field.getName(),
                        field.getAnnotation(ExcelProperty.class).index()))
                .collect(Collectors.toList());
    }

}
