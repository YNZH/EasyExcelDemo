package com.maple.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.maple.excel.extend.ExtendFiled;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojinfeng
 * @date 2023/3/20
 * @description
 */
public class Data implements ExtendFiled {
    @ExcelProperty(value = "column1", index = 0)
    private Integer a;

    @ExcelProperty(value = "column2", index = 1)
    private String b;
    @JSONField(serialize = false)
    private Map<String, Object> extendField = new HashMap<>();

    @Override
    @JSONField(serialize = false)
    public Map<String, Object> getExtendFiledMap() {
        return this.extendField;
    }

    @Override
    public void setExtendFiledMap(Map<String, Object> extendFiledMap) {
        this.extendField = extendFiledMap;
    }

    public Map<String, Object> getExtendField() {
        return extendField;
    }

    public void setExtendField(Map<String, Object> extendField) {
        this.extendField = extendField;
    }

    public Data() {
    }

    public Data(int a, String b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
