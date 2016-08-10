package com.atom.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TableClass {
	// 表名
	private String tablename;
	// 表中所有的字段 逗号分隔
	private String tableField;
	// 对象名
	private String classname;
	// 对象实例名字
	private String classInstanceName;
	// 字段对象列表
	private List<ColumnField> columnFieldList;

	private ColumnField primaryColumnField;

	public TableClass(String tablename) {
		this.tablename = tablename;
		this.classname = Utils.upperFirstChar(Utils.delUnderline(this.tablename));
		this.classInstanceName = Utils.lowerFirstChar(this.classname);
	}

	public TableClass(String tablename, String classname) {
		this.tablename = tablename;
		this.classname = Utils.upperFirstChar(Utils.delUnderline(classname));
		this.classInstanceName = Utils.lowerFirstChar(this.classname);
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = Utils.upperFirstChar(Utils.delUnderline(classname));
		this.classInstanceName = Utils.lowerFirstChar(this.classname);
	}

	public String getClassInstanceName() {
		return classInstanceName;
	}

	public void setClassInstanceName(String classInstanceName) {
		this.classInstanceName = classInstanceName;
	}

	public List<ColumnField> getColumnFieldList() {
		return columnFieldList;
	}

	public void setColumnFieldList(List<ColumnField> columnFieldList) {
		StringBuilder tableFieldSB = new StringBuilder();
		for(ColumnField columnField : columnFieldList){
			tableFieldSB.append(columnField.getFieldName()).append(",");
		}
		tableFieldSB.deleteCharAt(tableFieldSB.length()-1);
		this.tableField = tableFieldSB.toString();
		this.columnFieldList = columnFieldList;
	}

	public String getTableField() {
		return tableField;
	}

	public void setTableField(String tableField) {
		this.tableField = tableField;
	}


	// 取得表信息
	public TableClass(String tablename, String classname, Connection conn)throws Exception{
		String sql = "show full columns from  "+tablename;
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		List<ColumnField> columnFieldList = new ArrayList<>();
		while(rs.next()){
			String fieldName = rs.getString("Field");
			String fieldType = rs.getString("Type");
			String collation = rs.getString("collation");
			String isAllowNull = rs.getString("Null");
			String keyType = rs.getString("Key");
			String defaultValue = rs.getString("Default");
			String extra = rs.getString("Extra");
			String privileges = rs.getString("Privileges");
			String fieldComment = rs.getString("Comment");
			System.out.println(fieldName+"|"+fieldType+"|"+collation+"|"+isAllowNull+"|"+keyType+"|"+defaultValue+"|"+extra+"|"+privileges+"|"+fieldComment);
			ColumnField columnField = new ColumnField();
			columnField.setFieldName(fieldName);
			columnField.setFieldType(fieldType);
            columnField.setClassFieldType(getClassFieldType(fieldType));
            columnField.setFullClassFieldType(getFullClassFieldType(columnField.getClassFieldType()));
			columnField.setCollation(collation);
			columnField.setIsAllowNull(isAllowNull);
			columnField.setKeyType(keyType);
			columnField.setDefaultValue(defaultValue);
            columnField.setDefaultClassValue(getClassFieldDefaultValue(columnField.getClassFieldType()));
			columnField.setExtra(extra);
			columnField.setPrivileges(privileges);
			columnField.setFieldComment(fieldComment);
			columnFieldList.add(columnField);
			if("PRI".equals(keyType)){
				this.setPrimaryColumnField(columnField);
			}
		}
		this.setTablename(tablename);
		this.setColumnFieldList(columnFieldList);
		this.setClassname(classname);
	}


    static int classFieldTypeFlag = 1;
    static String getClassFieldType(String fieldType){
        if(classFieldTypeFlag == 1){
            if ( fieldType.indexOf("char") > -1 ) {
                return "String";
            } else if ( fieldType.indexOf("bigint") > -1 ) {
                return "long";
            } else if ( fieldType.indexOf("int") > -1 ) {
                return "int";
            } else if ( fieldType.indexOf("date") > -1 ) {
                return "Date";
            } else if ( fieldType.indexOf("text") > -1 ) {
                return "String";
            } else if ( fieldType.indexOf("timestamp") > -1 ) {
                return "Date";
            } else if ( fieldType.indexOf("bit") > -1 ) {
                return "boolean";
            } else if ( fieldType.indexOf("decimal") > -1 ) {
                return "BigDecimal";
            } else if ( fieldType.indexOf("blob") > -1 ) {
                return "byte[]";
            }
        }else{
            if ( fieldType.indexOf("char") > -1 ) {
                return "String";
            } else if ( fieldType.indexOf("bigint") > -1 ) {
                return "Long";
            } else if ( fieldType.indexOf("int") > -1 ) {
                return "Integer";
            } else if ( fieldType.indexOf("date") > -1 ) {
                return "Date";
            } else if ( fieldType.indexOf("text") > -1 ) {
                return "String";
            } else if ( fieldType.indexOf("timestamp") > -1 ) {
                return "Date";
            } else if ( fieldType.indexOf("bit") > -1 ) {
                return "Boolean";
            } else if ( fieldType.indexOf("decimal") > -1 ) {
                return "BigDecimal";
            } else if ( fieldType.indexOf("blob") > -1 ) {
                return "byte[]";
            }
        }
        return null;
    }

    static String getFullClassFieldType(String classFieldType){
        if(classFieldTypeFlag != 1){
            return "java.lang."+classFieldType;
        }
        return null;
    }


    static String getClassFieldDefaultValue(String classFieldType){
        if(classFieldTypeFlag == 1){
            if ("BigDecimal".equals(classFieldType)) {
                return "new java.math.BigDecimal(\"0\")";
            }
            else if ("int".equals(classFieldType)) {
                return "-1";
            }else if ("double".equals(classFieldType)) {
                return "-1";
            }else if ("float".equals(classFieldType)) {
                return "-1";
            }else if("boolean".equals(classFieldType)){
                return "false";
            }else if("long".equals(classFieldType)){
                return "-1l";
            }
        }
        return null;
    }

	public void setPrimaryColumnField(ColumnField primaryColumnField) {
		this.primaryColumnField = primaryColumnField;
	}

	public ColumnField getPrimaryColumnField() {
		return primaryColumnField;
	}
}
