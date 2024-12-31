package com.wk.plugin.apitoapidoc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 接口入参实体类
 */
public class ApiParameter {
    /**
     * 参数英文名
     */
    private String name;
    /**
     * 参数中文名
     */
    private String cnName;
    /**
     * 参数类型
     */
    private String type;
    /**
     * 参数长度
     */
    private Integer length;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 所属版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * 嵌套子参数（用于复杂结构）
     */
    private List<ApiParameter> subParameters = new ArrayList<>();

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
        return new StringJoiner(", ", ApiParameter.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("cnName='" + cnName + "'")
                .add("type='" + type + "'")
                .add("length=" + length)
                .add("required=" + required)
                .add("version='" + version + "'")
                .add("description='" + description + "'")
                .add("subParameters=" + subParameters)
                .toString();
    }
}