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
			daoImplFile.delete();
			System.out.println(daoImplFile + " is delete!");
//			System.out.println(daoImplFilePath + " is exist!");
//			return;
		}
		
		FileOutputStream entityFos = null;
		FileOutputStream daoFos = null;
		FileOutputStream daoImplFos = null;
		try {
			entityFos = new FileOutputStream(entityFile);
			entityFos.write(fillContent4Entity(tablename, conn).toString().getBytes(charset));
			daoImplFos = new FileOutputStream(daoImplFile);
			daoImplFos.write(fillContent4DaoImpl(tablename, conn).toString().getBytes(charset));
			daoFos = new FileOutputStream(daoFile);
			daoFos.write(fillContent4Dao(tablename, conn).toString().getBytes(charset));
			entityFos.flush();
			daoFos.flush();
			daoImplFos.flush();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			if (entityFos != null)entityFos.close();
			if (daoFos != null)daoFos.close();
			if (daoImplFos != null)daoImplFos.close();
		}
		System.out.println("create " + entityFile + " successful");
		System.out.println("create " + daoFile + " successful");
		System.out.println("create " + daoImplFile + " successful");
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
	
	
	static String fillContent4DaoImpl(String tablename, Connection conn) throws Exception {
		String sql = "select * from " + tablename + " limit 1";
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		StringBuilder daoSb = new StringBuilder();
		daoSb.append("\n");
		daoSb.append("/**").append("\n");
		daoSb.append(" * @date " + Utils.dateFormat() + "  dao for table ").append(tablename).append("\n");
		daoSb.append(" */").append("\n");
		daoSb.append("@Repository(\""+Utils.delUnderline(tablename)+"Dao\")\n");
		daoSb.append("public class " + Tablename  + "DaoImpl implements ").append(Tablename+"Dao").append("{\n\n");
		daoSb.append("/t@Resource");
		daoSb.append("/tprivate JdbcTemplate jdbcTemplate;");
		daoSb.append(fillContent4GetCountByCondition(tablename, rs));
		daoSb.append(fillContent4FindByCondition(tablename, rs));
		daoSb.append(fillContent4FindById(tablename, rs));
		daoSb.append(fillContent4FindByIdInt(tablename, rs));
		daoSb.append(fillContent4Insert(tablename, rs));
		daoSb.append(fillContent4Update(tablename, rs));
		daoSb.append(fillContent4Delete(tablename, rs));
		daoSb.append("}");
		String content = daoSb.toString();
		return content;
	}
	
	static String fillContent4Dao(String tablename, Connection conn) throws Exception {
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder daoSb = new StringBuilder();
		daoSb.append("\n");
		daoSb.append("/**").append("\n");
		daoSb.append(" * @date " + Utils.dateFormat() + "  dao for table ").append(tablename).append("\n");
		daoSb.append(" */").append("\n");
	
		daoSb.append("public interface " + Tablename  + "Dao").append("{\n\n");
		daoSb.append("\tpublic int get").append(Tablename+"Count").append("(Map<String, Object> condition)throws Exception;\n");
		daoSb.append("\tpublic List<Map<String, Object>> find").append(Tablename+"byCondition").append("(Map<String, Object> condition)throws Exception;\n");
		daoSb.append("\tpublic "+Tablename+" find").append(Tablename+"byId").append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic "+Tablename+" find").append(Tablename+"byId").append("(int id)throws Exception;\n");
		daoSb.append("\tpublic int insert").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic int update").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic int delete").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("}");
		String content = daoSb.toString();
		return content;
	}
	
	static String fillContent4Insert(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder insertSb = new StringBuilder();
		insertSb.append("\tpublic int insert").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception{\n");
		insertSb.append("\t\tint rowCount = 0;\n");
		insertSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		insertSb.append("\t\tsql.append(\"insert into "+tablename+"\");\n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sbKey = new StringBuilder();
		StringBuilder sbValue = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			sbKey.append(columnName+",");
			sbValue.append(":"+fileName+",");
		}
		sbKey.deleteCharAt(sbKey.length()-1);
		sbValue.deleteCharAt(sbValue.length()-1);
		insertSb.append("\t\tsql.append(\"("+sbKey.toString()).append(")\");\n");
		insertSb.append("\t\tsql.append(\" values\");\n");
		insertSb.append("\t\tsql.append(\"(").append(sbValue.toString()).append(")\");\n");
		insertSb.append("\t\trowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource("+Utils.lowerFirstChar(Tablename)+"));\n");
		insertSb.append("\t\treturn rowCount;\n");
		insertSb.append("\t}\n\n");
		return insertSb.toString();
	}
	
	static String fillContent4Update(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder updateSb = new StringBuilder();
		updateSb.append("\tpublic int update").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception{\n");
		updateSb.append("\t\tint rowCount = 0;\n");
		updateSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		updateSb.append("\t\tsql.append(\"update "+tablename+" set \");\n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String fileName = Utils.delUnderline(meta.getColumnName(i));
			sb.append(fileName+"=:"+fileName+",");
			if(i<3){
				whereSb.append(" and "+fileName+"=:"+fileName);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		updateSb.append("\t\tsql.append(\""+sb+"\");\n");
		updateSb.append("\t\tsql.append(\" where 1=1 \");\n");
		updateSb.append("\t\tsql.append(\""+whereSb+"\");\n");
		updateSb.append("\t\trowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource("+Utils.lowerFirstChar(Tablename)+"));\n");
		updateSb.append("\t\treturn rowCount;\n");
		updateSb.append("\t}\n\n");
		return updateSb.toString();
	}
	
	static String fillContent4Delete(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder deleteSb = new StringBuilder();
		deleteSb.append("\tpublic int delete").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception{\n");
		deleteSb.append("\t\tint rowCount = 0;\n");
		deleteSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		deleteSb.append("\t\tsql.append(\"delete from "+tablename+" where 1=1 \");\n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			sb.append(" and "+columnName+"=:"+fileName);
			if(i>=2){
				break;
			}
		}
		deleteSb.append("\t\tsql.append(\""+sb).append("\");\n");
		deleteSb.append("\t\trowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource("+Utils.lowerFirstChar(Tablename)+"));\n");
		deleteSb.append("\t\treturn rowCount;\n");
		deleteSb.append("\t}\n\n");
		return deleteSb.toString();
	}
	
	static String fillContent4FindById(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\tpublic "+Tablename+" find").append(Tablename+"ById").append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception{\n");
		findSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		findSb.append("\t\tsql.append(\"select * from "+tablename+" where 1=1 ");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		StringBuilder valueSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			if(i<3){
				whereSb.append(" and "+columnName+"=:"+fileName);
				valueSb.append("\t\tnamedParameters.put(\""+fileName+"\","+fileName+");\n");
			}
		}
		findSb.append(whereSb).append("\");\n");
		findSb.append("\t\tMap namedParameters = new HashMap();\n");
		findSb.append(valueSb);
		findSb.append("\t\treturn jdbcTemplate.queryForObject(sql.toString(), namedParameters, new BeanPropertyRowMapper<"+Tablename+">("+Tablename+".class));\n");
		findSb.append("\t}\n\n");
		return findSb.toString();
	}
	
	
	static String fillContent4FindByIdInt(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\tpublic "+Tablename+" find").append(Tablename+"byId").append("(int id)throws Exception{\n");
		findSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		findSb.append("\t\tsql.append(\"select * from "+tablename+" where 1=1 ");
		StringBuilder whereSb = new StringBuilder();
		findSb.append(whereSb).append("\");\n");
		findSb.append("\t\tMap namedParameters = new HashMap();\n");
		findSb.append("\t\tnamedParameters.put(\"###\",\"###\");\n");
		findSb.append("\t\treturn jdbcTemplate.queryForObject(sql.toString(), namedParameters, new BeanPropertyRowMapper<"+Tablename+">("+Tablename+".class));\n");
		findSb.append("\t}\n\n");
		return findSb.toString();
	}
	
	static String fillContent4FindByCondition(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\tpublic List<Map<String, Object>> find").append(Tablename+"ByCondition").append("(Map<String, Object> condition)throws Exception{\n");
		findSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		findSb.append("\t\tsql.append(\"select * from "+tablename+" where 1=1 \");\n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			whereSb.append("\t\tif(condition.get(\""+fileName+"\") != null){\n");
			whereSb.append("\t\t\tsql.append(\" and "+columnName+"=\").append(condition.get(\""+fileName+"\").toString());\n");
			whereSb.append("\t\t}\n");
		}
		whereSb.append("\t\tif(condition.get(\"limit\") != null){\n");
		whereSb.append("\t\t\tsql.append(\" limit \").append(condition.get(\"rowOffset\")).append(\",\").append(condition.get(\"pageSize\"));\n");
		whereSb.append("\t\t}\n");
		findSb.append(whereSb);
		findSb.append("\t\treturn jdbcTemplate.getJdbcOperations().queryForList(sql.toString());\n");
		findSb.append("\t}\n\n");
		return findSb.toString();
	}
	
	static String fillContent4GetCountByCondition(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\tpublic int get").append(Tablename+"Count").append("(Map<String, Object> condition)throws Exception{\n");
		findSb.append("\t\tint rowCount = 0;\n"); 
		findSb.append("\t\tStringBuilder sql = new StringBuilder(100);\n");
		findSb.append("\t\tsql.append(\"select count(1) from "+tablename+" where 1=1 \");\n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			whereSb.append("\t\tif(condition.get(\""+fileName+"\") != null){\n");
			whereSb.append("\t\t\tsql.append(\" and "+columnName+"=\").append(condition.get(\""+fileName+"\").toString());\n");
			whereSb.append("\t\t}\n");
		}
		findSb.append(whereSb);
		findSb.append("\t\trowCount = jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), Integer.class);\n");
		findSb.append("\t\treturn rowCount;\n");
		findSb.append("\t}\n\n");
		return findSb.toString();
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
//		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.6.96:3306/ddpa_dev", "ddpa1008T", "ddpa@*Dc67");
//		CreateFileUtil.createFile("fund_trade", "", conn, "utf-8");
	}

}
