package com.atom.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class CreateMyBatisFileUtilTest {

    public static void createFiles(Connection conn, String tablename, String classname)throws Exception{
        TableClass.classFieldTypeFlag=2;
        TableClass tableClass = new TableClass(tablename, classname, conn);

        String curdir = System.getProperty("user.dir");
        // 生成entity文件
        String entityPackagePath = "com.zhongan.badasset.domain";
        String entityFilePath = curdir+"/myDao";
        FillEntityFile.fieldType = 2;
        FillEntityFile.write2File(tableClass, entityPackagePath, entityFilePath);

        FillMapperXmlFile.entityPackagePath=entityPackagePath;
        // 生成mapper文件
        String mapperPackagePath = "com.zhongan.badasset.dao";
        String mapperFilePath = curdir+"/myDao";
        FillMapperXmlFile.write2MapperFile(tableClass, mapperPackagePath, mapperFilePath);
        // 生成mapperProvider文件
        String providerPackagePath = "com.zhongan.badasset.dao";
        String providerFilePath = curdir+"/myDao";
        FillMapperXmlFile.write2XmlFile(tableClass, providerPackagePath, providerFilePath);

        FillMapperServiceFile.entityPackagePath=entityPackagePath;
        FillMapperServiceFile.mapperPackagePath=mapperPackagePath;
        // 生成service文件
        String servicePackagePath = "com.zhongan.badasset.service";
        String serviceFilePath = curdir+"/myDao";
        FillMapperServiceFile.write2ServiceFile(tableClass, servicePackagePath, serviceFilePath);
        // 生成serviceImpl文件
        String serviceImplPackagePath = "com.zhongan.badasset.service.impl";
        String serviceImplFilePath = curdir+"/myDao";
        FillMapperServiceFile.write2ServiceImplFile(tableClass, serviceImplPackagePath, serviceImplFilePath);

    }

    public static void main(String[] args)throws Exception{
//		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.142:3306/market", "wq_market", "lstx");
//		createFiles(conn, "t_cp_product_rule_match", "product_rule_match");
        Connection conn = DriverManager.getConnection("jdbc:mysql://rdsuueafyuqbzfu.mysql.rds.aliyuncs.com:3306/credit_badasset_00", "cbs_user_dev", "cbs_user_dev_971c6a");
        createFiles(conn, "za_bad_asset_total_policy", "total_policy");
    }
}
