package com.maple.excel.header;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author gaojinfeng
 * @date 2023/3/20
 * @description excel导出列的标题
 */
public class ExcelHeader {
    private List<String> extendHeaderTitle;
    private String filedName;
    private Integer order;

    public ExcelHeader(String extendHeaderTitle, String filedName, Integer order) {
        if (Objects.isNull(extendHeaderTitle) || extendHeaderTitle.length() == 0) {
            throw new IllegalArgumentException("列名称不能为空");
        }
        this.extendHeaderTitle = Arrays.asList(extendHeaderTitle.split(","));
        this.filedName = filedName;
        this.order = order;
    }

    public ExcelHeader(List<String> extendHeaders, String filedName, Integer order) {
        this.extendHeaderTitle = extendHeaders;
        this.filedName = filedName;
        this.order = order;
    }

    public List<String> getExtendHeaderTitle() {
        return extendHeaderTitle;
    }

    public void setExtendHeaderTitle(List<String> extendHeaderTitle) {
        this.extendHeaderTitle = extendHeaderTitle;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
