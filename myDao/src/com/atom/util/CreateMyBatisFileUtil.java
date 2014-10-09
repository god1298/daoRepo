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


public class CreateMyBatisFileUtil {
	
	public static String DB_PREFIX;
	
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
		String daoImplFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + "Mapper.xml";
		String daoFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + "Mapper.java";
		
		String serviceImplFilePath = curdir + File.separator + Utils.upperFirstChar(Utils.delUnderline(tablename)) + "ServiceImpl.java";
		
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
		
		File serviceImplFile = new File(serviceImplFilePath);
		if (serviceImplFile.exists()) {
			serviceImplFile.delete();
			System.out.println(serviceImplFile + " is delete!");
//			System.out.println(daoImplFilePath + " is exist!");
//			return;
		}
		
		FileOutputStream entityFos = null;
		FileOutputStream daoFos = null;
		FileOutputStream daoImplFos = null;
		
		FileOutputStream serviceImplFos = null;
		try {
			entityFos = new FileOutputStream(entityFile);
			entityFos.write(fillContent4Entity(tablename, conn).toString().getBytes(charset));
			
			daoImplFos = new FileOutputStream(daoImplFile);
			daoImplFos.write(fillContent4DaoImpl(tablename, conn).toString().getBytes(charset));
			
			daoFos = new FileOutputStream(daoFile);
			daoFos.write(fillContent4Dao(tablename, conn).toString().getBytes(charset));
			
			serviceImplFos = new FileOutputStream(serviceImplFile);
			serviceImplFos.write(fillContent4ServiceImpl(tablename, conn).toString().getBytes(charset));
			
			entityFos.flush();
			daoFos.flush();
			daoImplFos.flush();
			serviceImplFos.flush();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			if (entityFos != null)entityFos.close();
			if (daoFos != null)daoFos.close();
			if (daoImplFos != null)daoImplFos.close();
			if (serviceImplFos != null)serviceImplFos.close();
		}
		System.out.println("create " + entityFile + " successful");
		System.out.println("create " + daoFile + " successful");
		System.out.println("create " + daoImplFile + " successful");
		System.out.println("create " + serviceImplFile + " successful");
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
		daoSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		daoSb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n");
		daoSb.append("<mapper namespace=\"com.niiwoo.dao.mapper.sys."+Tablename+"\" >\n\n");
		daoSb.append(fillContent4ResultMap(tablename, rs));
		daoSb.append(fillContent4GetCountByCondition(tablename, rs));
		daoSb.append(fillContent4FindByCondition(tablename, rs));
		daoSb.append(fillContent4FindObjByCondition(tablename, rs));
		daoSb.append(fillContent4FindById(tablename, rs));
		daoSb.append(fillContent4FindByIdInt(tablename, rs));
		daoSb.append(fillContent4Insert(tablename, rs));
		daoSb.append(fillContent4Update(tablename, rs));
		daoSb.append(fillContent4Delete(tablename, rs));
		daoSb.append("</mapper>");
		String content = daoSb.toString();
		return content;
	}
	
	
	static String fillContent4Dao(String tablename, Connection conn) throws Exception {
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder daoSb = new StringBuilder();
		daoSb.append("\n");
		daoSb.append("import java.util.List;\n");
		daoSb.append("import java.util.Map;\n");
		daoSb.append("\n");
		daoSb.append("/**").append("\n");
		daoSb.append(" * @date " + Utils.dateFormat() + "  dao for table ").append(tablename).append("\n");
		daoSb.append(" */").append("\n");
	
		daoSb.append("public interface " + Tablename  + "Mapper").append("{\n\n");
		daoSb.append("\tpublic int get").append(Tablename+"Count").append("(Map<String, Object> condition)throws Exception;\n");
		daoSb.append("\tpublic List<Map<String, Object>> find").append(Tablename+"ByCondition").append("(Map<String, Object> condition)throws Exception;\n");
		daoSb.append("\tpublic List<"+Tablename+"> find").append(Tablename+"ObjByCondition").append("(Map<String, Object> condition)throws Exception;\n");
		daoSb.append("\tpublic "+Tablename+" find").append(Tablename+"ByObj").append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic "+Tablename+" find").append(Tablename+"ById").append("(int id)throws Exception;\n");
		daoSb.append("\tpublic int insert").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic int update").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("\tpublic int delete").append(Tablename).append("("+Tablename+" "+Utils.lowerFirstChar(Tablename)+")throws Exception;\n");
		daoSb.append("}");
		String content = daoSb.toString();
		return content;
	}
	
	static String fillContent4Insert(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder insertSb = new StringBuilder();
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sbKey = new StringBuilder();
		StringBuilder sbValue = new StringBuilder();
		String primaryKeyStr = "";
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			if(i == 1){
				primaryKeyStr = fileName;
			}
			sbKey.append(columnName+",");
			sbValue.append("#{"+fileName+"},");
		}
		insertSb.append("\t<insert id=\"insert").append(Tablename+"\" parameterType=\""+className+"\" useGeneratedKeys=\"true\" keyProperty=\""+primaryKeyStr+"\">\n");
		insertSb.append("\t\tinsert into "+DB_PREFIX+tablename+"\n");
		sbKey.deleteCharAt(sbKey.length()-1);
		sbValue.deleteCharAt(sbValue.length()-1);
		insertSb.append("\t\t("+sbKey.toString()).append(")\n");
		insertSb.append("\t\tvalues\n");
		insertSb.append("\t\t(").append(sbValue.toString()).append(")\n");
		insertSb.append("\t</insert>\n\n");
		return insertSb.toString();
	}
	
	static String fillContent4Update(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder updateSb = new StringBuilder();
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			sb.append(columnName+"=#{"+fileName+"},");
			if(i<3){
				whereSb.append(" and "+columnName+"=#{"+fileName+"}");
			}
		}
		updateSb.append("\t<update id=\"update").append(Tablename+"\" parameterType=\""+className+"\" >\n");
		updateSb.append("\t\tupdate "+DB_PREFIX+tablename+" set\n");
		sb.deleteCharAt(sb.length()-1);
		updateSb.append("\t\t"+sb+"\n");
		updateSb.append("\t\twhere 1=1 \n");
		updateSb.append("\t\t"+whereSb+"\n");
		updateSb.append("\t</update>\n\n");
		return updateSb.toString();
	}
	
	static String fillContent4Delete(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder deleteSb = new StringBuilder();
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			sb.append(" and "+columnName+"=#{"+fileName+"}");
			if(i>=2){
				break;
			}
		}
		deleteSb.append("\t<delete id=\"delete").append(Tablename+"\" parameterType=\""+className+"\" >\n");
		deleteSb.append("\t\tdelete from "+DB_PREFIX+tablename+" where 1=1\n");
		deleteSb.append("\t\t"+sb+"\n");
		deleteSb.append("\t</delete>\n\n");
		return deleteSb.toString();
	}
	
	static String fillContent4FindById(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\t<select id=\"find").append(Tablename+"ByObj\" parameterType=\""+className+"\" resultMap=\""+className+"Result\" >\n");
		findSb.append("\t\tselect * from "+DB_PREFIX+tablename+" where 1=1 \n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			if(i<3){
				whereSb.append("\t\t<if test=\""+fileName+" != null\">\n");
				whereSb.append("\t\t\tand "+columnName+"=#{"+fileName+"}\n");
				whereSb.append("\t\t</if>\n");
			}
		}
		findSb.append(whereSb);
		findSb.append("\t</select>\n\n");
		return findSb.toString();
	}
	
	
	static String fillContent4FindByIdInt(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\t<select id=\"find").append(Tablename+"ById\" parameterType=\"int\" resultMap=\""+className+"Result\" >\n");
		findSb.append("\t\tselect * from "+DB_PREFIX+tablename+" where 1=1 \n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			if(i<2){
				whereSb.append("\t\t<if test=\""+fileName+" != null\">\n");
				whereSb.append("\t\t\tand "+columnName+"=#{"+fileName+"}\n");
				whereSb.append("\t\t</if>\n");
			}
		}
		findSb.append(whereSb);
		findSb.append("\t</select>\n\n");
		return findSb.toString();
	}
	
	static String fillContent4FindByCondition(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\t<select id=\"find").append(Tablename+"ByCondition\" parameterType=\"java.util.Map\" resultType=\"java.util.Map\" >\n");
		findSb.append("\t\tselect * from "+DB_PREFIX+tablename+" where 1=1 \n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			whereSb.append("\t\t<if test=\""+fileName+" != null\">\n");
			whereSb.append("\t\t\tand "+columnName+"=#{"+fileName+"}\n");
			whereSb.append("\t\t</if>\n");
		}
		whereSb.append("\t\t<if test=\"limit != null\">\n");
		whereSb.append("\t\t\tlimit #{rowOffset},#{pageSize}\n");
		whereSb.append("\t\t</if>\n");
		findSb.append(whereSb);
		findSb.append("\t</select>\n\n");
		return findSb.toString();
	}
	
	static String fillContent4FindObjByCondition(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\t<select id=\"find").append(Tablename+"ObjByCondition\" parameterType=\"java.util.Map\" resultMap=\""+className+"Result\" >\n");
		findSb.append("\t\tselect * from "+DB_PREFIX+tablename+" where 1=1 \n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			whereSb.append("\t\t<if test=\""+fileName+" != null\">\n");
			whereSb.append("\t\t\tand "+columnName+"=#{"+fileName+"}\n");
			whereSb.append("\t\t</if>\n");
		}
		whereSb.append("\t\t<if test=\"limit != null\">\n");
		whereSb.append("\t\t\tlimit #{rowOffset},#{pageSize}\n");
		whereSb.append("\t\t</if>\n");
		findSb.append(whereSb);
		findSb.append("\t</select>\n\n");
		return findSb.toString();
	}
	
	static String fillContent4ResultMap(String tablename, ResultSet rs)throws Exception{
		String className = Utils.lowerFirstChar(Utils.delUnderline(tablename));
		StringBuilder resultSb = new StringBuilder();
		resultSb.append("\t<resultMap type=\""+className+"\" id=\""+className+"Result\">\n");
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			resultSb.append("\t\t<result property=\""+fileName+"\" column=\""+columnName+"\"/>\n");
		}
		resultSb.append("\t</resultMap>\n\n");
		return resultSb.toString();
	}
	
	static String fillContent4GetCountByCondition(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\t<select id=\"get").append(Tablename+"Count\" parameterType=\"java.util.Map\" resultType=\"int\" >\n");
		findSb.append("\t\tselect count(1) from "+DB_PREFIX+tablename+" where 1=1 \n");
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder whereSb = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			String fileName = Utils.delUnderline(columnName);
			whereSb.append("\t\t<if test=\""+fileName+" != null\">\n");
			whereSb.append("\t\t\tand "+columnName+"=#{"+fileName+"}\n");
			whereSb.append("\t\t</if>\n");
		}
		findSb.append(whereSb);
		findSb.append("\t</select>\n\n");
		return findSb.toString();
	}
	
	static String fillContent4ServiceImpl(String tablename, Connection conn) throws Exception {
		String sql = "select * from " + tablename + " limit 1";
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		StringBuilder daoSb = new StringBuilder();
		daoSb.append("\n");
		daoSb.append("import java.util.List;\n");
		daoSb.append("import java.util.Map;\n");
		daoSb.append("import javax.annotation.Resource;\n");
		daoSb.append("import org.springframework.stereotype.Service;\n");
		daoSb.append("\n");
		daoSb.append("/**").append("\n");
		daoSb.append(" * @date " + Utils.dateFormat() + "  service for table ").append(tablename).append("\n");
		daoSb.append(" */").append("\n");
		daoSb.append("@Service(\""+Utils.delUnderline(tablename)+"Service\")\n");
		daoSb.append("public class " + Tablename  + "ServiceImpl implements ").append(Tablename+"Service").append("{\n\n");
		daoSb.append("\t@Resource(name = \""+Utils.delUnderline(tablename)+"Dao\")\n");
		daoSb.append("\tprivate "+Tablename+"Dao "+Utils.delUnderline(tablename)+"Dao;\n\n");
		daoSb.append(fillContent4FindService(tablename, rs));
		daoSb.append("}");
		String content = daoSb.toString();
		return content;
	}
	
	static String fillContent4FindService(String tablename, ResultSet rs)throws Exception{
		String Tablename = Utils.upperFirstChar(Utils.delUnderline(tablename));
		StringBuilder findSb = new StringBuilder();
		findSb.append("\tpublic void find").append(Tablename).append("(PageHolder<Map<String, Object>> pageHolder, Map<String, Object> condition)throws Exception{\n");
		findSb.append("\t\tcondition.put(\"limit\", 1);\n");
		findSb.append("\t\tcondition.put(\"rowOffset\", pageHolder.getRowOffset());\n");
		findSb.append("\t\tcondition.put(\"pageSize\", pageHolder.getPageSize());\n");
		findSb.append("\t\tlong rowCount = "+Utils.delUnderline(tablename)+"Dao.get"+Tablename+"Count(condition);\n");
		findSb.append("\t\tpageHolder.setRowCount(rowCount);\n");
		findSb.append("\t\tif(rowCount == 0){\n");
		findSb.append("\t\t\treturn ;\n");
		findSb.append("\t\t}\n");
		findSb.append("\t\tList<Map<String, Object>> list = "+Utils.delUnderline(tablename)+"Dao.find"+Tablename+"ByCondition(condition);\n");
		findSb.append("\t\tif(list != null && list.size() > 0){\n");
		findSb.append("\t\t\tfor(int i=0; i<list.size(); i++){\n");
		findSb.append("\t\t\t\tMap<String, Object> map = list.get(i);\n");
		findSb.append("\t\t\t\tlist.set(i, map);\n");
		findSb.append("\t\t\t}\n");
		findSb.append("\t\t}\n");
		findSb.append("\t\tpageHolder.setList(list);\n");
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
			return "Integer";
		case Types.TINYINT:
			return "Integer";
		case Types.INTEGER:
			return "Integer";
		case Types.DOUBLE:
			return "Double";
		case Types.FLOAT:
			return "Float";
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
			return "Boolean";
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
//		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
//		CreateFileUtil.createFile("Student", "", conn, "utf-8");
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.25:3306/niiwoo", "niiwoowrite", "tuandai123");
		DB_PREFIX = "niiwoo.";
		CreateMyBatisFileUtil.createFile("sys_menu", "", conn, "utf-8");
		//String filePath = "D:\workspace\niiwoo-app\niiwoo-dao\src\main\resources\mybatis-config.xml";
	}

}
