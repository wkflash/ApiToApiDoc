package com.wk.plugin.apitoapidoc;

import java.util.ArrayList;
import java.util.List;

public class ApiField {
    private String name; // 字段英文名
    private String descriptionCn; // 字段中文名
    private String type; // 字段类型（如 String, Integer, List<Object>）
    private String version; // 所属版本
    private String description; // 描述
    private List<ApiField> subFields = new ArrayList<>(); // 嵌套子字段（用于复杂结构）

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

    public List<ApiField> getSubFields() {
        return subFields;
    }

    public void setSubFields(List<ApiField> subFields) {
        this.subFields = subFields;
    }

    @Override
    public String toString() {
        return "ApiField{" +
                "name='" + name + '\'' +
                ", descriptionCn='" + descriptionCn + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", subFields=" + subFields +
                '}';
    }
}