package com.maple.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.maple.excel.extend.ExtendFiled;

/**
 * @author zhankun
 * @date 2023/3/23 16:38
 */
public class ImportEventTypeBean implements ExtendFiled {

    /**
     * 大类代码
     */
    @ExcelProperty(index = 0)
    private String mainTypeCode;

    /**
     * 大类名称
     */
    @ExcelProperty(index = 1)
    private String mainTypeName;

    /**
     * 小类代码
     */
    @ExcelProperty(index = 2)
    private String subTypeCode;

    /**
     * 小类名称
     */
    @ExcelProperty(index = 3)
    private String subTypeName;

    /**
     * 环境属性
     */
    @ExcelProperty(index = 4)
    private String envProp;

    /**
     * 立案明细编码
     */
    @ExcelProperty(index = 5)
    private String instCondID;

    /**
     * 立案条件
     */
    @ExcelProperty(index = 6)
    private String instCondName;

    /**
     * 处置时限-A 核心区
     */
    @ExcelProperty(value = {"可选处置时限,A类"}, index = 7)
    private String dealLimitA;

    /**
     * 处置时限-B 一般区
     */
    @ExcelProperty(value = {"可选处置时限,B类"}, index = 8)
    private String dealLimitB;

    /**
     * 处置时限-C 一般区
     */
    @ExcelProperty(value = {"可选处置时限,C类"}, index = 9)
    private String dealLimitC;

    /**
     * 结案条件
     */
    @ExcelProperty(index = 10)
    private String archiveName;

    /**
     * 备注
     */
    @ExcelProperty(index = 11)
    private String remark;


    public String getMainTypeCode() {
        return mainTypeCode;
    }

    public void setMainTypeCode(String mainTypeCode) {
        this.mainTypeCode = mainTypeCode;
    }

    public String getMainTypeName() {
        return mainTypeName;
    }

    public void setMainTypeName(String mainTypeName) {
        this.mainTypeName = mainTypeName;
    }

    public String getSubTypeCode() {
        return subTypeCode;
    }

    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }

    public String getEnvProp() {
        return envProp;
    }

    public void setEnvProp(String envProp) {
        this.envProp = envProp;
    }

    public String getInstCondID() {
        return instCondID;
    }

    public void setInstCondID(String instCondID) {
        this.instCondID = instCondID;
    }

    public String getInstCondName() {
        return instCondName;
    }

    public void setInstCondName(String instCondName) {
        this.instCondName = instCondName;
    }

    public String getDealLimitA() {
        return dealLimitA;
    }

    public void setDealLimitA(String dealLimitA) {
        this.dealLimitA = dealLimitA;
    }

    public String getDealLimitB() {
        return dealLimitB;
    }

    public void setDealLimitB(String dealLimitB) {
        this.dealLimitB = dealLimitB;
    }

    public String getDealLimitC() {
        return dealLimitC;
    }

    public void setDealLimitC(String dealLimitC) {
        this.dealLimitC = dealLimitC;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ImportEventTypeBean{" +
                "mainTypeCode='" + mainTypeCode + '\'' +
                ", mainTypeName='" + mainTypeName + '\'' +
                ", subTypeCode='" + subTypeCode + '\'' +
                ", subTypeName='" + subTypeName + '\'' +
                ", envProp='" + envProp + '\'' +
                ", instCondID='" + instCondID + '\'' +
                ", instCondName='" + instCondName + '\'' +
                ", dealLimitA='" + dealLimitA + '\'' +
                ", dealLimitB='" + dealLimitB + '\'' +
                ", dealLimitC='" + dealLimitC + '\'' +
                ", archiveName='" + archiveName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
