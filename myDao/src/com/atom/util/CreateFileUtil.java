package com.atom.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class CreateFileUtil {
	
	/**
	 * 
	 * @param tablename
	 * @param destPath
	 * @param conn
	 * @param charset
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws SQLException
	 *             通过jdbc连接数据库，根据参数 tableName(表名) 生成相应的 java源文件。 生成的java文件没有package ，所以 实际使用时，需在生成的文件中自行加上相应的包名。 destPath
	 *             为java文件的目标地址。如果空值则生成的文件在根目录下(文件创建完成后会在控制台提示生成文件的绝对地址)。
	 */
	public static void createFile(String tablename, String destPath, Connection conn, String charset) throws UnsupportedEncodingException,
			IOException, SQLException {
		String curdir = destPath;
		if (curdir == null || "".equals(curdir.trim()))
			curdir = System.getProperty("user.dir");
		String entityFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + ".java";
		String daoImplFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + "DaoImpl.java";
		String daoFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + "Dao.java";
		
		File entityFile = new File(entityFilePath);
		if (entityFile.exists()) {
			entityFile.delete();
			System.out.println(entityFile + " is delete!");
			// System.out.println(entityFilePath + " is exist!");
			// return;
		}
		File daoFile = new File(daoFilePath);
		if (daoFile.exists()) {
			daoFile.deleteOnExit();
			System.out.println(daoFile + " is delete!");
//			System.out.println(daoFilePath + " is exist!");
//			return;
		}
		File daoImplFile = new File(daoImplFilePath);
		if (daoImplFile.exists()) {
			daoImplFile.deleteOnExit();
			System.out.println(daoImplFile + " is delete!");
//			System.out.println(daoImplFilePath + " is exist!");
//			return;
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(entityFile);
			
			fos.write(fillContent4Entity(tablename, conn).toString().getBytes(charset));
			fos.flush();
		} finally {
			if (fos != null)
				fos.close();
		}
		System.out.println("create " + entityFile + " successful");
	}
	
	static String fillContent4Entity(String tablename, Connection conn) throws SQLException {
		String sql = "select * from " + tablename + " limit 1";
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		StringBuilder entitySb = new StringBuilder();
		entitySb.append("import java.io.Serializable;").append("\n");
		entitySb.append("#BIGDECIMAL#");
		entitySb.append("#STRING#");
		entitySb.append("#DATE#");
		entitySb.append("\n");
		entitySb.append("/**").append("\n");
		entitySb.append(" * @date " + Utils.dateFormat() + "  entity for table ").append(tablename).append("\n");
		entitySb.append(" */").append("\n");
	
		entitySb.append("public class " + Tablename  + " implements Serializable{").append("\n");
		entitySb.append("\n");
		entitySb.append("\tprivate static final long serialVersionUID = 1L;").append("\n");
	
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb4GetSet = new StringBuilder();
		StringBuilder sb4args = new StringBuilder();
		boolean bDFlag = false;
		boolean strFlag = false;
		boolean dateFlag = false;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String fileName = Utils.delUnderline(meta.getColumnName(i));
			int javaType = meta.getColumnType(i);
			if (!meta.isSigned(i) && (javaType == Types.INTEGER)) {
				javaType = Types.BIGINT;
			}
			String type = type2String(javaType);
			if("BigDecimal".equals(type)){
				bDFlag = true;
			}else if("String".equals(type)){
				strFlag = true;
			}else if("Date".equals(type)){
				dateFlag = true;
			}
			String initField = initField(type2String(javaType));
			if(!Utils.isContainsLowerCase(fileName)){
				fileName = fileName.toLowerCase();
			}
			if (initField != null) {
				sb4args.append("\tprivate ").append(type + " ").append("" + fileName + " = ").append(initField).append(";").append("\n");
			} else {
				sb4args.append("\tprivate ").append(type + " ").append("" + fileName + "").append(";").append("\n");
			}
			sb4GetSet.append("\tpublic ").append(type + " ").append("get").append(Utils.upperFirstChar(fileName)).append("(){").append("\n");
			sb4GetSet.append("\t\treturn this.").append(fileName).append(";").append("\n");
			sb4GetSet.append("\t}").append("\n\n");
	
			sb4GetSet.append("\tpublic void ").append("set").append(Utils.upperFirstChar(fileName)).append("(").append(type + " ").append(fileName)
					.append("){").append("\n");
			sb4GetSet.append("\t\t this.").append(fileName).append("=").append(fileName).append(";").append("\n");
			sb4GetSet.append("\t}").append("\n");
		}
	
		entitySb.append(sb4args.toString()).append("\n");
		entitySb.append(sb4GetSet.toString());
		entitySb.append("}");
		String content = entitySb.toString();
		if(bDFlag){
			content = content.replace("#BIGDECIMAL#", "import java.math.BigDecimal;\n");
		}else{
			content = content.replace("#BIGDECIMAL#", "");
		}
		if(strFlag){
			content = content.replace("#STRING#", "import java.lang.String;\n");
		}else{
			content = content.replace("#STRING#", "");
		}
		if(dateFlag){
			content = content.replace("#DATE#", "import java.util.Date;\n");
		}else{
			content = content.replace("#DATE#", "");
		}
		return content;
	}
	
	
	static String fillContent4DaoImpl(String tablename, Connection conn) throws SQLException {
		String sql = "select * from " + tablename + " limit 1";
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		StringBuilder entitySb = new StringBuilder();
		entitySb.append("import java.io.Serializable;").append("\n");
		entitySb.append("\n");
		entitySb.append("/**").append("\n");
		entitySb.append(" * @date " + Utils.dateFormat() + "  entity for table ").append(tablename).append("\n");
		entitySb.append(" */").append("\n");
	
		entitySb.append("public class " + Tablename  + "DaoImpl implements ").append(Tablename+"Dao").append("{\n");
		entitySb.append("\n");
		entitySb.append("\tpublic int insert").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+"){\n");
		entitySb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		entitySb.append("\t\tsql.append(\"insert into "+tablename+"\n");
		entitySb.append("\t\t(");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sbKey = new StringBuilder();
		StringBuilder sbValue = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String fileName = Utils.delUnderline(meta.getColumnName(i));
			sbKey.append(fileName+",");
			sbValue.append(":"+fileName+",");
		}
		sbKey.deleteCharAt(sbKey.length()-1);
		sbValue.deleteCharAt(sbValue.length()-1);
		entitySb.append(sbKey.toString()).append(")\n");
		entitySb.append("\t\tvalues\n");
		entitySb.append("\t\t(").append(sbValue.toString()).append(")\n");
		entitySb.append("\t\t");
		entitySb.append("}");
		String content = entitySb.toString();
		return content;
	}
	
	static String type2String(int javaType) {
		switch (javaType) {
		case Types.DECIMAL:
			return "BigDecimal";
		case Types.NUMERIC:
			return "BigDecimal";
		case Types.SMALLINT:
			return "int";
		case Types.TINYINT:
			return "int";
		case Types.INTEGER:
			return "int";
		case Types.DOUBLE:
			return "double";
		case Types.FLOAT:
			return "float";
		case Types.REAL:
			return "BigDecimal";
		case Types.TIMESTAMP:
			return "Date";
		case Types.BIGINT:
			return "BigDecimal";
		case Types.DATE:
			return "Date";
		case Types.TIME:
			return "Date";
		case Types.CHAR:
			return "String";
		case Types.LONGVARBINARY:
			return "byte[]";
		case Types.VARCHAR:
			return "String";
		case Types.BINARY:
			return "byte[]";
		case Types.VARBINARY:
			return "byte[]";
		case Types.LONGNVARCHAR:
			return "String";
		case Types.BIT:
			return "boolean";
		default:
			return "String";
		}
	}
	
	static String initField(String type) {
		if ("java.math.BigDecimal".equals(type)) {
			return "new java.math.BigDecimal(\"0\")";
		} 
//		else if ("int".equals(type)) {
//			return "0";
//		} 
		else {
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
		CreateFileUtil.createFile("Student", "", conn, "utf-8");
	}

}
