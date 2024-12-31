package com.wk.plugin.apitoapidoc;

import java.util.ArrayList;
import java.util.List;

/**
 * 单个接口文档信息
 */
public class ApiInterface {
    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口描述
     */
    private String description;
    /**
     * 请求方式
     */
    private String httpMethod;
    /**
     * 报文格式
     */
    private String contentType = "application/json";
    /**
     * 入参
     */
    private List<ApiParameter> parameters = new ArrayList<>();
    /**
     * 出参
     */
    private List<ApiField> returnFields = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }

    public List<ApiField> getReturnFields() {
        return returnFields;
    }

    public void setReturnFields(List<ApiField> returnFields) {
        this.returnFields = returnFields;
    }

    @Override
    public String toString() {
        return "ApiInterface{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", contentType='" + contentType + '\'' +
                ", parameters=" + parameters +
                ", returnFields=" + returnFields +
                '}';
    }


}