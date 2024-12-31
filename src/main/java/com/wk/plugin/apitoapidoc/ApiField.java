package com.wk.plugin.apitoapidoc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 出参字段实体类
 */
public class ApiField {
    /**
     * 字段英文名
     */
    private String name;
    /**
     * 字段中文名
     */
    private String cnName;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 所属版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * 嵌套子字段（用于复杂结构）
     */
    private List<ApiField> subFields = new ArrayList<>();

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
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
        return new StringJoiner(", ", ApiField.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("cnName='" + cnName + "'")
                .add("type='" + type + "'")
                .add("version='" + version + "'")
                .add("description='" + description + "'")
                .add("subFields=" + subFields)
                .toString();
    }
}