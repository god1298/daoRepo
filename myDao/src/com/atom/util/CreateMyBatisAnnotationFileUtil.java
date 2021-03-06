package com.atom.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class CreateMyBatisAnnotationFileUtil {



    public static void createFiles(Connection conn, String tablename, String classname)throws Exception{
        TableClass tableClass = new TableClass(tablename, classname, conn);

        String curdir = System.getProperty("user.dir");
        // 生成entity文件
//		String entityPackagePath = "com.tourstock.marketing.api.model.coupon";
//		String entityFilePath = curdir+"/myDao";
//		FillEntityFile.write2File(tableClass, entityPackagePath, entityFilePath);
//
//		FillMapperFile.entityPackagePath=entityPackagePath;
//		// 生成mapper文件
//		String mapperPackagePath = "com.tourstock.marketing.dao.coupon";
//		String mapperFilePath = curdir+"/myDao";
//		FillMapperFile.write2MapperFile(tableClass, mapperPackagePath, mapperFilePath);
//		// 生成mapperProvider文件
//		String providerPackagePath = "com.tourstock.marketing.dao.coupon";
//		String providerFilePath = curdir+"/myDao";
//		FillMapperFile.write2ProviderFile(tableClass, providerPackagePath, providerFilePath);
//
//		FillMapperServiceFile.entityPackagePath=entityPackagePath;
//		FillMapperServiceFile.mapperPackagePath=mapperPackagePath;
//		// 生成service文件
//		String servicePackagePath = "com.tourstock.marketing.api.service.coupon";
//		String serviceFilePath = curdir+"/myDao";
//		FillMapperServiceFile.write2ServiceFile(tableClass, servicePackagePath, serviceFilePath);
//		// 生成serviceImpl文件
//		String serviceImplPackagePath = "com.tourstock.marketing.service.coupon";
//		String serviceImplFilePath = curdir+"/myDao";
//		FillMapperServiceFile.write2ServiceImplFile(tableClass, serviceImplPackagePath, serviceImplFilePath);


        // 生成entity文件
        String entityPackagePath = "com.tourstock.tour.model.order";
        String entityFilePath = curdir+"/myDao";
        FillEntityFile.write2File(tableClass, entityPackagePath, entityFilePath);

        FillMapperFile.entityPackagePath=entityPackagePath;
        // 生成mapper文件
        String mapperPackagePath = "com.tourstock.tour.dao.order";
        String mapperFilePath = curdir+"/myDao";
        FillMapperFile.write2MapperFile(tableClass, mapperPackagePath, mapperFilePath);
        // 生成mapperProvider文件
        String providerPackagePath = "com.tourstock.tour.dao.order";
        String providerFilePath = curdir+"/myDao";
        FillMapperFile.write2ProviderFile(tableClass, providerPackagePath, providerFilePath);

        FillMapperServiceFile.entityPackagePath=entityPackagePath;
        FillMapperServiceFile.mapperPackagePath=mapperPackagePath;
        // 生成service文件
        String servicePackagePath = "com.tourstock.tour.service.order";
        String serviceFilePath = curdir+"/myDao";
        FillMapperServiceFile.write2ServiceFile(tableClass, servicePackagePath, serviceFilePath);
        // 生成serviceImpl文件
        String serviceImplPackagePath = "com.tourstock.tour.service.order";
        String serviceImplFilePath = curdir+"/myDao";
        FillMapperServiceFile.write2ServiceImplFile(tableClass, serviceImplPackagePath, serviceImplFilePath);

    }

    public static void main(String[] args)throws Exception{
//		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.142:3306/market", "wq_market", "lstx");
//		createFiles(conn, "t_cp_product_rule_match", "product_rule_match");
        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.142:3306/grouptour", "wq_grouptour", "lstx");
        createFiles(conn, "t_tour_booking_contact_info", "booking_contact_info");
    }

}
