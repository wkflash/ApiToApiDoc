package com.wk.plugin.apitoapidoc;

import java.util.ArrayList;
import java.util.List;

public class ApiParameter {
    private String name; // 参数英文名
    private String descriptionCn; // 参数中文名
    private String type; // 参数类型（如 String, Integer, List<Object>）
    private Integer length; // 参数长度
    private boolean required; // 是否必填
    private String version; // 所属版本
    private String description; // 描述
    private List<ApiParameter> subParameters = new ArrayList<>(); // 嵌套子参数（用于复杂结构）

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionCn() {
        return descriptionCn;
    }

    public void setDescriptionCn(String descriptionCn) {
        this.descriptionCn = descriptionCn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ApiParameter> getSubParameters() {
        return subParameters;
    }

    public void setSubParameters(List<ApiParameter> subParameters) {
        this.subParameters = subParameters;
    }

    @Override
    public String toString() {
        return "ApiParameter{" +
                "name='" + name + '\'' +
                ", descriptionCn='" + descriptionCn + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", required=" + required +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", subParameters=" + subParameters +
                '}';
    }
}