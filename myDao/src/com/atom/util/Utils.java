package com.atom.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static String delUnderline(String str) {
		String[] ss = str.split("_");
		if (ss.length == 1) {
			return str;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ss.length; i++) {
				if (i != 0) {
					sb.append(upperFirstChar(ss[i].toLowerCase()));
				} else {
					sb.append(ss[i]);
				}
			}
			return sb.toString();
		}
	}

	public static String upperFirstChar(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String lowerFirstChar(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	public static String dateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ret = sdf.format(new Date());
		return ret.toUpperCase();
	}

	public static boolean isContainsLowerCase(String str) {
		if (str == null)
			return false;
		int length = str.length();
		if (length == 0)
			return false;
		for (int i = 0; i < length; i++) {
			if (Character.isLowerCase(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static String readFile(String path)throws Exception{
		StringBuffer content = new StringBuffer();
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String tmpContent = "";
		while((tmpContent = br.readLine()) != null){
			content.append(tmpContent+"\n");
		}
		fr.close();
		br.close();
		return content.toString();
	}
	
	public static void writeFile(String path, String content)throws Exception{
//		FileWriter fw = new FileWriter(path);
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(content);
//		bw.flush();
//		fw.flush();
//		fw.close();
//		bw.close();
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content.toString().getBytes("UTF-8"));
		fos.flush();
		fos.close();
	}


	public static String fieldType2ClassType(String fieldType) {
		if(fieldType.indexOf("(") != -1){
			fieldType=fieldType.substring(0, fieldType.indexOf("("));
		}
		switch (fieldType) {
			case "tinyint":
				return "int";
			case "smallint":
				return "int";
			case "mediumint":
				return "int";
			case "int":
				return "int";
			case "integer":
				return "long";
			case "bigint":
				return "BigInteger";
			case "bit":
				return "boolean";
			case "real":
				return "BigDecimal";
			case "double":
				return "double";
			case "float":
				return "float";
			case "decimal":
				return "BigDecimal";
			case "numeric":
				return "BigDecimal";
			case "char":
				return "String";
			case "varchar":
				return "String";
			case "date":
				return "String";
			case "time":
				return "Time";
			case "year":
				return "Date";
			case "timestamp":
				return "Timestamp";
			case "datetime":
				return "Date";
			default:
				return "String";
		}
	}
	
	public static void main(String[] args)throws Exception {
		String path = "D:\\workspace\\niiwoo-app\\niiwoo-dao\\src\\main\\resources\\mybatis-config.xml";
		String content = readFile(path);
		StringBuffer str = new StringBuffer(content);
		System.out.println(str.insert(str.indexOf("\t</mappers>"), "test1111"));
	}
}
