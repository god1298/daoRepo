package com.atom.util;

/**
 * Created by Administrator on 2015/8/22.
 */
public class ColumnField {
    // 数据库字段名
    private String fieldName;
    // 数据库字段类型
    private String fieldType;
    private String collation;
    // 是否允许为null
    private String isAllowNull;
    // 数据库索引类型
    private String keyType;
    // 默认值
    private String defaultValue;
    // 扩展 如 自增主键
    private String extra;
    // 权限
    private String privileges;
    // 数据库字段注释
    private String fieldComment;
    // 类字段名
    private String classFieldName;
    // 类字段类型
    private String classFieldType;

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        this.classFieldName = Utils.delUnderline(this.fieldName);
    }
    public String getFieldType() {
        return fieldType;
    }
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
        if(fieldType.indexOf("(") != -1){
            fieldType=fieldType.substring(0, fieldType.indexOf("("));
        }
        this.classFieldType = Utils.fieldType2ClassType(fieldType);
    }
    public String getCollation() {
        return collation;
    }
    public void setCollation(String collation) {
        this.collation = collation;
    }
    public String getIsAllowNull() {
        return isAllowNull;
    }
    public void setIsAllowNull(String isAllowNull) {
        this.isAllowNull = isAllowNull;
    }
    public String getKeyType() {
        return keyType;
    }
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
    public String getPrivileges() {
        return privileges;
    }
    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }
    public String getFieldComment() {
        return fieldComment;
    }
    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }

    public void setClassFieldName(String classFieldName) {
        this.classFieldName = classFieldName;
    }

    public String getClassFieldName() {
        return classFieldName;
    }

    public void setClassFieldType(String classFieldType) {
        this.classFieldType = classFieldType;
    }

    public String getClassFieldType() {
        return classFieldType;
    }
}
