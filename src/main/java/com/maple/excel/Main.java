package com.maple.excel;

import com.maple.excel.config.ExcelImportConfig;
import com.maple.excel.extend.ExtendFiled;
import com.maple.excel.header.ExcelHeader;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaojinfeng
 * @date ${DATE}
 * @description
 */
public class Main {


    public static void main(String[] args) {
        List<? extends ExtendFiled> importList = testImport();
//        testExport();
    }

    private static List<? extends ExtendFiled> testImport() {
        InputStream in = Main.class.getClassLoader().getResourceAsStream("import.xlsx");
        Class<? extends ExtendFiled> clazz = Data.class;
        ExcelImportConfig excelImportConfig = new ExcelImportConfig().builder()
                .headerNumber(1)
                .basicHeaderList(Exporter.getHeaderList(clazz))
                .extendHeaderList(getCustomHeaderList())
                .doAllAnalysedConsumer((analysisContext -> System.out.println("finish")));
        Importer importer = new Importer();
        return importer.importFile(in, clazz, excelImportConfig);
    }

    /**
     * 简单导入，没有自定义扩展字段，兼容合并单元格
     */
    @Test
    public void simpleImport() {
        InputStream in = Main.class.getClassLoader().getResourceAsStream("eventTypeTemplate.xls");
        SimpleImport<ImportEventTypeBean> simpleImport = new SimpleImport<>(2, ImportEventTypeBean.class);
        List<ImportEventTypeBean> parsedResultList = simpleImport.getParsedResultList(in);
        System.out.println(parsedResultList);
    }

    private static void testExport() {
        List<ExcelHeader> customHeaderList = getCustomHeaderList();
        Exporter.export("test-file" + "_" + System.currentTimeMillis() + ".xlsx",
                "sheet1",
                Data.class,
                customHeaderList,
                getList());
    }

    private static List<ExcelHeader> getCustomHeaderList() {
        return Arrays.asList(
                new ExcelHeader("扩展字段1", "extend1", 1),
                new ExcelHeader("扩展字段2", "extend2", 2)
        );
    }

    public static List<Data> getList() {

        Data data1 = new Data(1, "11");
        data1.getExtendField().put("extend1", 11);
        data1.getExtendField().put("extend2", 22);
        Data data2 = new Data(2, "22");
        data2.getExtendField().put("extend1", 11111);
        data2.getExtendField().put("extend2", 22222);
        return Arrays.asList(data1, data2);
    }
}