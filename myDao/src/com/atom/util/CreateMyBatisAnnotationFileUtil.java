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
import java.util.ArrayList;
import java.util.List;


public class CreateMyBatisAnnotationFileUtil {
	

	
	
	
//	public static void main(String[] args) throws Exception {
//		Class.forName("com.mysql.jdbc.Driver");
////		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
////		CreateFileUtil.createFile("Student", "", conn, "utf-8");
//		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
//		// DB_PREFIX = "kaojinweixin.";
//		PACKAGE_PREFIX = "com.grow.coach";
//		PACKAGE_PATH = "com/grow/coach";
//		FILE_DIR = "student";
//		REAL_TABLE_NAME="grow_coach_teacher";
//		DB_PREFIX="";
//		CreateFileUtil.createFile("teacher", "", conn, "utf-8");
//	}


	public static void createFiles(Connection conn, String tablename, String classname)throws Exception{
		TableClass tableClass = new TableClass(tablename, classname, conn);

		String curdir = curdir = System.getProperty("user.dir");
		// 生成entity文件
		String entityPackagePath = "com.tourstock.marketing.api.model.coupon";
		String entityFilePath = curdir+"/myDao";
		FillEntityFile.write2File(tableClass, entityPackagePath, entityFilePath);

		FillMapperFile.entityPackagePath=entityPackagePath;
		// 生成mapper文件
		String mapperPackagePath = "com.tourstock.marketing.dao.coupon";
		String mapperFilePath = curdir+"/myDao";
		FillMapperFile.write2MapperFile(tableClass, mapperPackagePath, mapperFilePath);
		// 生成mapperProvider文件
		String providerPackagePath = "com.tourstock.marketing.dao.coupon";
		String providerFilePath = curdir+"/myDao";
		FillMapperFile.write2ProviderFile(tableClass, providerPackagePath, providerFilePath);

		FillMapperServiceFile.entityPackagePath=entityPackagePath;
		FillMapperServiceFile.mapperPackagePath=mapperPackagePath;
		// 生成service文件
		String servicePackagePath = "com.tourstock.marketing.api.service.coupon";
		String serviceFilePath = curdir+"/myDao";
		FillMapperServiceFile.write2ServiceFile(tableClass, servicePackagePath, serviceFilePath);
		// 生成serviceImpl文件
		String serviceImplPackagePath = "com.tourstock.marketing.service.coupon";
		String serviceImplFilePath = curdir+"/myDao";
		FillMapperServiceFile.write2ServiceImplFile(tableClass, serviceImplPackagePath, serviceImplFilePath);

	}

	public static void main(String[] args)throws Exception{
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.142:3306/market", "wq_market", "lstx");
		createFiles(conn, "t_promo_project", "promo_project");
	}

}
