package com.atom.util;


import java.io.File;

/**
 * Created by Administrator on 2015/8/22.
 */
public class FillMapperXmlFile {

    static String entityPackagePath = "";
    static String parameterClassType = "";

    static String fillContent4MapperXml(TableClass tableClass, String packagePath) throws Exception {
        parameterClassType = entityPackagePath+"."+tableClass.getClassname();
        StringBuilder daoSb = new StringBuilder();
        daoSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        daoSb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n");
        daoSb.append("<mapper namespace=\""+packagePath+"."+tableClass.getClassname()+"Mapper\" >\n\n");
        daoSb.append(fillContent4ResultMap(tableClass));
        daoSb.append(fillContent4GetCountByCondition(tableClass));
        daoSb.append(fillContent4FindMapByCondition(tableClass));
        daoSb.append(fillContent4FindEntityByCondition(tableClass));
        daoSb.append(fillContent4FindByEntity(tableClass));
        daoSb.append(fillContent4FindByPrimaryKey(tableClass));
        daoSb.append(fillContent4Insert(tableClass));
        daoSb.append(fillContent4InsertAndGetKey(tableClass));
        daoSb.append(fillContent4Update(tableClass));
        daoSb.append(fillContent4Delete(tableClass));

        daoSb.append("</mapper>");
        String content = daoSb.toString();
        return content;
    }


    static String fillContent4Delete(TableClass tableClass)throws Exception{
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != "+null+"\">\n");
            whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
            whereSb.append("\t\t\t</if>\n");
            rowCount ++;
            if(rowCount == 3){
                break;
            }
        }

        StringBuilder deleteSb = new StringBuilder();
        deleteSb.append("\t<delete id=\"delete\" parameterType=\"" + parameterClassType +"\" >\n");
        deleteSb.append("\t\tdelete from "+tableClass.getTablename()+"\n");
        deleteSb.append("\t\t<where>\n");
        deleteSb.append(whereSb+"\n");
        deleteSb.append("\t\t</where>\n");
        deleteSb.append("\t</delete>\n\n");
        return deleteSb.toString();
    }

    static String fillContent4Update(TableClass tableClass)throws Exception{
        StringBuilder updateSb = new StringBuilder();
        updateSb.append("\t<update id=\"update\" parameterType=\"" + parameterClassType+"\" >\n");
        updateSb.append("\t\tupdate "+tableClass.getTablename()+" \n");
        StringBuilder setSb = new StringBuilder();
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            setSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != "+null+"\">\n");
            setSb.append("\t\t\t\t"+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"},\n");
            setSb.append("\t\t\t</if>\n");
            rowCount ++;
            if(rowCount <= 3){
                whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != "+null+"\">\n");
                whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
                whereSb.append("\t\t\t</if>\n");
            }
        }

        updateSb.append("\t\t<set>\n");
        updateSb.append(""+setSb+"\n");
        updateSb.append("\t\t</set>\n");
        updateSb.append("\t\t<where>\n");
        updateSb.append(whereSb+"\n");
        updateSb.append("\t\t</where>\n");
        updateSb.append("\t</update>\n\n");
        return updateSb.toString();
    }



        static String fillContent4InsertAndGetKey(TableClass tableClass)throws Exception{
        StringBuilder insertSb = new StringBuilder();
        insertSb.append("\t<insert id=\"insertAndGetKey\" parameterType=\"" + parameterClassType + "\" useGeneratedKeys=\"true\" keyProperty=\"" + tableClass.getPrimaryColumnField().getClassFieldName() + "\">\n");
        insertSb.append("\t\tinsert into "+tableClass.getTablename()+"\n");
        StringBuilder sbKey = new StringBuilder();
        sbKey.append("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        StringBuilder sbValue = new StringBuilder();
        sbValue.append("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            sbKey.append("\t\t\t<if test=\""+classFieldName+" != null\">\n");
            sbKey.append("\t\t\t\t"+fieldName+",\n");
            sbKey.append("\t\t\t</if>\n");
            //            if("auto_increment".equals(columnField.getExtra())){
            //                sbValue.append("null,");
            //            }else{
            //                sbValue.append("#{"+classFieldName+"},");
            //            }
            sbValue.append("\t\t\t<if test=\""+classFieldName+" != null\">\n");
            sbValue.append("\t\t\t\t#{"+columnField.getClassFieldName()+"},\n");
            sbValue.append("\t\t\t</if>\n");
        }
//        sbKey.deleteCharAt(sbKey.length()-1);
//        sbValue.deleteCharAt(sbValue.length()-1);
//        insertSb.append("\t\t("+sbKey.toString()).append(")\n");
//        insertSb.append("\t\tvalues\n");
//        insertSb.append("\t\t(").append(sbValue.toString()).append(")\n");
//        insertSb.append("\t</insert>\n\n");
        sbKey.append("\t\t</trim>\n");
        sbValue.append("\t\t</trim>\n");
        insertSb.append(""+sbKey.toString()).append("");
        insertSb.append("\t\tvalues\n");
        insertSb.append("").append(sbValue.toString()).append("");
        insertSb.append("\t</insert>\n\n");
        return insertSb.toString();
    }


    static String fillContent4Insert(TableClass tableClass)throws Exception{
        StringBuilder insertSb = new StringBuilder();
        insertSb.append("\t<insert id=\"insert\" parameterType=\""+parameterClassType+"\">\n");
        insertSb.append("\t\tinsert into "+tableClass.getTablename()+"\n");
        StringBuilder sbKey = new StringBuilder();
        sbKey.append("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        StringBuilder sbValue = new StringBuilder();
        sbValue.append("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            sbKey.append("\t\t\t<if test=\""+classFieldName+" != null\">\n");
            sbKey.append("\t\t\t\t"+fieldName+",\n");
            sbKey.append("\t\t\t</if>\n");
//            if("auto_increment".equals(columnField.getExtra())){
//                sbValue.append("null,");
//            }else{
//                sbValue.append("#{"+classFieldName+"},");
//            }
            sbValue.append("\t\t\t<if test=\""+classFieldName+" != null\">\n");
            sbValue.append("\t\t\t\t#{"+columnField.getClassFieldName()+"},\n");
            sbValue.append("\t\t\t</if>\n");
        }
        sbKey.append("\t\t</trim>\n");
        sbValue.append("\t\t</trim>\n");
//        sbKey.deleteCharAt(sbKey.length()-1);
//        sbValue.deleteCharAt(sbValue.length()-1);
        insertSb.append(""+sbKey.toString()).append("");
        insertSb.append("\t\tvalues\n");
        insertSb.append("").append(sbValue.toString()).append("");
        insertSb.append("\t</insert>\n\n");
        return insertSb.toString();
    }

    static String fillContent4FindByEntity(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\t<select id=\"findByEntity\" parameterType=\"" + parameterClassType + "\" resultMap=\"" + tableClass.getClassInstanceName() + "Result\" >\n");
        findSb.append("\t\tselect * from "+tableClass.getTablename()+"\n");
        findSb.append("\t\t<where>\n");
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != null\">\n");
            whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
            whereSb.append("\t\t\t</if>\n");
            rowCount ++;
            if(rowCount == 3){
                break;
            }
        }
        findSb.append(whereSb);
        findSb.append("\t\t</where>\n");
        findSb.append("\t</select>\n\n");
        return findSb.toString();
    }

    static String fillContent4FindByPrimaryKey(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\t<select id=\"findBy" + Utils.upperFirstChar(tableClass.getPrimaryColumnField().getClassFieldName()) + "\" parameterType=\"" + tableClass.getPrimaryColumnField().getFullClassFieldType() + "\" resultMap=\"" + tableClass.getClassInstanceName() + "Result\" >\n");
        findSb.append("\t\tselect * from "+tableClass.getTablename()+"\n");
        StringBuilder whereSb = new StringBuilder();
        findSb.append("\t\t<where>\n");
        whereSb.append("\t\t\t\tand "+tableClass.getPrimaryColumnField().getFieldName()+"=#{"+tableClass.getPrimaryColumnField().getClassFieldName()+"}\n");
        findSb.append(whereSb);
        findSb.append("\t\t</where>\n");
        findSb.append("\t</select>\n\n");
        return findSb.toString();
    }

    static String fillContent4FindEntityByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\t<select id=\"findEntityByCondition\" parameterType=\"java.util.Map\" resultMap=\"" + tableClass.getClassInstanceName() + "Result\" >\n");
        findSb.append("\t\tselect * from "+tableClass.getTablename()+"\n");
        findSb.append("\t\t<where>\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != null\">\n");
            whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
            whereSb.append("\t\t\t</if>\n");
        }
        whereSb.append("\t\t\t<if test=\"limit != null\">\n");
        whereSb.append("\t\t\t\tlimit #{rowOffset},#{pageSize}\n");
        whereSb.append("\t\t\t</if>\n");
        findSb.append(whereSb);
        findSb.append("\t\t</where>\n");
        findSb.append("\t</select>\n\n");
        return findSb.toString();
    }


    static String fillContent4FindMapByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\t<select id=\"findMapByCondition\" parameterType=\"java.util.Map\" resultType=\"java.util.Map\" >\n");
        findSb.append("\t\tselect * from "+tableClass.getTablename()+"\n");
        findSb.append("\t\t<where>\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != null\">\n");
            whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
            whereSb.append("\t\t\t</if>\n");
        }
        whereSb.append("\t\t\t<if test=\"limit != null\">\n");
        whereSb.append("\t\t\t\tlimit #{rowOffset},#{pageSize}\n");
        whereSb.append("\t\t\t</if>\n");
        findSb.append(whereSb);
        findSb.append("\t\t</where>\n");
        findSb.append("\t</select>\n\n");
        return findSb.toString();
    }

    static String fillContent4GetCountByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\t<select id=\"getCountByCondition\" parameterType=\"java.util.Map\" resultType=\"int\" >\n");
        findSb.append("\t\tselect count(1) from "+tableClass.getTablename()+"\n");
        findSb.append("\t\t<where>\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            whereSb.append("\t\t\t<if test=\""+columnField.getClassFieldName()+" != null\">\n");
            whereSb.append("\t\t\t\tand "+columnField.getFieldName()+"=#{"+columnField.getClassFieldName()+"}\n");
            whereSb.append("\t\t\t</if>\n");
        }
        findSb.append(whereSb);
        findSb.append("\t\t</where>\n");
        findSb.append("\t</select>\n\n");
        return findSb.toString();
    }


    static String fillContent4ResultMap(TableClass tableClass)throws Exception{
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("\t<resultMap type=\""+parameterClassType+"\" id=\""+tableClass.getClassInstanceName()+"Result\">\n");
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            resultSb.append("\t\t<result property=\""+columnField.getClassFieldName()+"\" column=\""+columnField.getFieldName()+"\"/>\n");
        }
        resultSb.append("\t</resultMap>\n\n");
        return resultSb.toString();
    }


    static String fillContent4Mapper(TableClass tableClass, String packagePath) throws Exception {
        StringBuilder daoSb = new StringBuilder();
        daoSb.append("package "+packagePath+";").append("\n\n");
        daoSb.append("import "+entityPackagePath+"."+tableClass.getClassname()+";\n");
        daoSb.append("import java.util.List;\n");
        daoSb.append("import java.util.Map;\n");
        daoSb.append("import org.springframework.stereotype.Repository;\n");
        daoSb.append("\n");
        daoSb.append("/**").append("\n");
        daoSb.append(" * @date " + Utils.dateFormat() + "  mapper for table ").append(tableClass.getTablename()).append("\n");
        daoSb.append("*/\n");
        daoSb.append("@Repository(\"" + tableClass.getClassInstanceName() + "Mapper\")\n");
        daoSb.append("public interface " + tableClass.getClassname()  + "Mapper").append("{\n\n");

        daoSb.append("\tpublic int getCountByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\tpublic List<Map<String, Object>> findMapByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\tpublic List<"+tableClass.getClassname()+"> findEntityByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\tpublic "+tableClass.getClassname()+" findByEntity("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\tpublic "+tableClass.getClassname()+" findBy"+Utils.upperFirstChar(tableClass.getPrimaryColumnField().getClassFieldName())+"("+tableClass.getPrimaryColumnField().getClassFieldType()+" "+tableClass.getPrimaryColumnField().getClassFieldName()+")throws Exception;\n");

        daoSb.append("\tpublic int insert("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\tpublic int insertAndGetKey("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\tpublic int update("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\tpublic int delete("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("}");
        String content = daoSb.toString();
        return content;
    }

    static void write2MapperFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String mapperFileContent = fillContent4Mapper(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"Mapper.java", mapperFileContent);
    }

    static void write2XmlFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String mapperProviderFileContent = fillContent4MapperXml(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"Mapper.xml", mapperProviderFileContent);
    }
}
