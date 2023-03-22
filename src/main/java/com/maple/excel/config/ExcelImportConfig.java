package com.maple.excel.config;

import com.alibaba.excel.context.AnalysisContext;
import com.maple.excel.header.ExcelHeader;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author gaojinfeng
 * @date 2023/3/22
 * @description
 */
public class ExcelImportConfig {
    private List<ExcelHeader> customHeaderList;
    private List<ExcelHeader> basicHeaderList;
    private Integer headerNumber;
    private Consumer<AnalysisContext> doAllAnalysedConsumer;

    public boolean valid() {
        return !getCustomHeaderList().isEmpty() || !getBasicHeaderList().isEmpty();
    }

    public List<ExcelHeader> getCustomHeaderList() {
        return customHeaderList;
    }

    public void setCustomHeaderList(List<ExcelHeader> customHeaderList) {
        this.customHeaderList = customHeaderList;
    }

    public List<ExcelHeader> getBasicHeaderList() {
        return basicHeaderList;
    }

    public void setBasicHeaderList(List<ExcelHeader> basicHeaderList) {
        this.basicHeaderList = basicHeaderList;
    }

    public Integer getHeaderNumber() {
        return headerNumber;
    }

    public void setHeaderNumber(Integer headerNumber) {
        this.headerNumber = headerNumber;
    }

    public Consumer<AnalysisContext> getDoAllAnalysedConsumer() {
        return doAllAnalysedConsumer;
    }

    public void setDoAllAnalysedConsumer(Consumer<AnalysisContext> doAllAnalysedConsumer) {
        this.doAllAnalysedConsumer = doAllAnalysedConsumer;
    }

    public ExcelImportConfig() {
        this.customHeaderList = Collections.emptyList();
        this.basicHeaderList = Collections.emptyList();
        this.headerNumber = 1;
    }

    public ExcelImportConfig builder() {
        return this;
    }

    public ExcelImportConfig basicHeaderList(List<ExcelHeader> headerList) {
        this.basicHeaderList = headerList;
        return this;
    }

    public ExcelImportConfig extendHeaderList(List<ExcelHeader> headerList) {
        this.customHeaderList = headerList;
        return this;
    }

    public ExcelImportConfig headerNumber(Integer headerNumber) {
        this.headerNumber = headerNumber;
        return this;
    }

    public ExcelImportConfig doAllAnalysedConsumer(Consumer<AnalysisContext> doAllAnalysedConsumer) {
        this.doAllAnalysedConsumer = doAllAnalysedConsumer;
        return this;
    }

}
