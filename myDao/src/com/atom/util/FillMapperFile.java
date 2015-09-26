package com.atom.util;


import java.io.File;

/**
 * Created by Administrator on 2015/8/22.
 */
public class FillMapperFile {

    static String entityPackagePath = "";

    static String fillContent4MapperProvider(TableClass tableClass, String packagePath) throws Exception {
        StringBuilder daoSb = new StringBuilder();
        daoSb.append("package "+packagePath+";").append("\n\n");
        daoSb.append("import "+entityPackagePath+"."+tableClass.getClassname()+";\n");
        daoSb.append("import java.util.Map;\n");
        daoSb.append("import static org.apache.ibatis.jdbc.SqlBuilder.*;\n");
        daoSb.append("\n");
        daoSb.append("/**").append("\n");
        daoSb.append(" * @date " + Utils.dateFormat() + "  dao for table ").append(tableClass.getTablename()).append("\n");
        daoSb.append(" */").append("\n");
        daoSb.append("public class " + tableClass.getClassname() + "MapperProvider").append("{\n\n");


        daoSb.append(fillContent4GetCountByCondition(tableClass));
        daoSb.append(fillContent4FindMapByCondition(tableClass));
        daoSb.append(fillContent4FindEntityByCondition(tableClass));
        daoSb.append(fillContent4FindByEntity(tableClass));
        daoSb.append(fillContent4FindById(tableClass));
        daoSb.append(fillContent4Insert(tableClass));
        daoSb.append(fillContent4InsertAndGetKey(tableClass));
        daoSb.append(fillContent4Update(tableClass));
        daoSb.append(fillContent4Delete(tableClass));
        daoSb.append("}");
        String content = daoSb.toString();
        return content;
    }


    static String fillContent4Delete(TableClass tableClass)throws Exception{
        StringBuilder updateSb = new StringBuilder();
        updateSb.append("\tpublic String deleteSql").append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        updateSb.append("\t\tBEGIN();\n");
        updateSb.append("\t\tDELETE_FROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            String value = "";
            if(columnField.getClassFieldType()=="int" || columnField.getClassFieldType()=="double" || columnField.getClassFieldType()=="float"){
                value="-1";
            }else{
                value="null";
            }
            whereSb.append("\t\tif("+tableClass.getClassInstanceName()+".get"+Utils.upperFirstChar(classFieldName)+"() != "+value+"){\n");
            whereSb.append("\t\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            whereSb.append("\t\t}\n");
            rowCount ++;
            if(rowCount == 3){
                break;
            }
        }
        updateSb.append(whereSb);
        updateSb.append("\t\treturn SQL();\n");
        updateSb.append("\t}\n\n");
        return updateSb.toString();
    }

    static String fillContent4Update(TableClass tableClass)throws Exception{
        StringBuilder updateSb = new StringBuilder();
        updateSb.append("\tpublic String updateSql").append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        updateSb.append("\t\tBEGIN();\n");
        updateSb.append("\t\tUPDATE("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder setSb = new StringBuilder();
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            String value = "";
            if(columnField.getClassFieldType()=="int" || columnField.getClassFieldType()=="double" || columnField.getClassFieldType()=="float"){
                value="-1";
            }else{
                value="null";
            }
            setSb.append("\t\tif("+tableClass.getClassInstanceName()+".get"+Utils.upperFirstChar(classFieldName)+"() != "+value+"){\n");
            setSb.append("\t\t\tSET(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            setSb.append("\t\t}\n");
            rowCount ++;
            if(rowCount <= 3){
                whereSb.append("\t\tif("+tableClass.getClassInstanceName()+".get"+Utils.upperFirstChar(classFieldName)+"() != "+value+"){\n");
                whereSb.append("\t\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
                whereSb.append("\t\t}\n");
            }
        }
        updateSb.append(setSb);
        updateSb.append(whereSb);
        updateSb.append("\t\treturn SQL();\n");
        updateSb.append("\t}\n\n");
        return updateSb.toString();
    }



    static String fillContent4InsertAndGetKey(TableClass tableClass)throws Exception{
        StringBuilder insertSb = new StringBuilder();
        insertSb.append("\tpublic String insertAndGetKeySql").append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        insertSb.append("\t\tBEGIN();\n");
        insertSb.append("\t\tINSERT_INTO("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder valuesSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            // 判断是否为自增主键
            if("auto_increment".equals(columnField.getExtra())){
                valuesSb.append("\t\tVALUES(\"").append(fieldName).append("\",\"").append("null\");\n");
            }else{
                valuesSb.append("\t\tVALUES(\"").append(fieldName).append("\",\"").append("#{"+classFieldName+"}\");\n");
            }
        }
        insertSb.append(valuesSb);
        insertSb.append("\t\treturn SQL();\n");
        insertSb.append("\t}\n\n");
        return insertSb.toString();
    }


    static String fillContent4Insert(TableClass tableClass)throws Exception{
        StringBuilder insertSb = new StringBuilder();
        insertSb.append("\tpublic String insertSql").append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        insertSb.append("\t\tBEGIN();\n");
        insertSb.append("\t\tINSERT_INTO("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder valuesSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            // 判断是否为自增主键
            if("auto_increment".equals(columnField.getExtra())){
                valuesSb.append("\t\tVALUES(\"").append(fieldName).append("\",\"").append("null\");\n");
            }else{
                valuesSb.append("\t\tVALUES(\"").append(fieldName).append("\",\"").append("#{"+classFieldName+"}\");\n");
            }
        }
        insertSb.append(valuesSb);
        insertSb.append("\t\treturn SQL();\n");
        insertSb.append("\t}\n\n");
        return insertSb.toString();
    }


    static String fillContent4FindByEntity(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic String findByEntitySql").append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        findSb.append("\t\tBEGIN();\n");
        findSb.append("\t\tSELECT("+tableClass.getClassname()+"Mapper.TABLE_FIELD);\n");
        findSb.append("\t\tFROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            String value = "";
            if(columnField.getClassFieldType()=="int" || columnField.getClassFieldType()=="double" || columnField.getClassFieldType()=="float"){
                value="-1";
            }else{
                value="null";
            }
            whereSb.append("\t\tif("+tableClass.getClassInstanceName()+".get"+Utils.upperFirstChar(classFieldName)+"() != "+value+"){\n");
            whereSb.append("\t\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            whereSb.append("\t\t}\n");
        }
        findSb.append(whereSb);
        findSb.append("\t\treturn SQL();\n");
        findSb.append("\t}\n\n");
        return findSb.toString();
    }

    static String fillContent4FindById(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic String findByIdSql").append("(int id)throws Exception{\n");
        findSb.append("\t\tBEGIN();\n");
        findSb.append("\t\tSELECT("+tableClass.getClassname()+"Mapper.TABLE_FIELD);\n");
        findSb.append("\t\tFROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        int rowCount = 0;
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            whereSb.append("\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            rowCount ++;
            if(rowCount == 3){
                break;
            }
        }
        findSb.append(whereSb);
        findSb.append("\t\treturn SQL();\n");
        findSb.append("\t}\n\n");
        return findSb.toString();
    }

    static String fillContent4FindEntityByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic String findEntityByConditionSql").append("(Map<String, Object> condition)throws Exception{\n");
        findSb.append("\t\tBEGIN();\n");
        findSb.append("\t\tSELECT("+tableClass.getClassname()+"Mapper.TABLE_FIELD);\n");
        findSb.append("\t\tFROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            whereSb.append("\t\tif(condition.get(\""+classFieldName+"\") != null){\n");
            whereSb.append("\t\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            whereSb.append("\t\t}\n");
        }
        whereSb.append("\t\tStringBuilder sql= new  StringBuilder(SQL());\n");
        whereSb.append("\t\tif(condition.get(\"limit\") != null){\n");
        whereSb.append("\t\t\tsql.append(\" limit #{rowOffset},#{pageSize}\");\n");
        whereSb.append("\t\t}\n");
        findSb.append(whereSb);
        findSb.append("\t\treturn sql.toString();\n");
        findSb.append("\t}\n\n");
        return findSb.toString();
    }


    static String fillContent4FindMapByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic String findMapByConditionSql").append("(Map<String, Object> condition)throws Exception{\n");
        findSb.append("\t\tBEGIN();\n");
        findSb.append("\t\tSELECT("+tableClass.getClassname()+"Mapper.TABLE_FIELD);\n");
        findSb.append("\t\tFROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            whereSb.append("\t\tif(condition.get(\""+classFieldName+"\") != null){\n");
            whereSb.append("\t\t\tWHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            whereSb.append("\t\t}\n");
        }
        whereSb.append("\t\tStringBuilder sql= new  StringBuilder(SQL());\n");
        whereSb.append("\t\tif(condition.get(\"limit\") != null){\n");
        whereSb.append("\t\t\tsql.append(\" limit #{rowOffset},#{pageSize}\");\n");
        whereSb.append("\t\t}\n");
        findSb.append(whereSb);
        findSb.append("\t\treturn sql.toString();\n");
        findSb.append("\t}\n\n");
        return findSb.toString();
    }

    static String fillContent4GetCountByCondition(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic String getCountByConditionSql").append("(Map<String, Object> condition)throws Exception{\n");
        findSb.append("\t\tBEGIN();\n");
        findSb.append("\t\tSELECT(\"count(1)\");\n");
        findSb.append("\t\tFROM("+tableClass.getClassname()+"Mapper.TABLE_NAME);\n");
        StringBuilder whereSb = new StringBuilder();
        for (ColumnField columnField : tableClass.getColumnFieldList()) {
            String fieldName = columnField.getFieldName();
            String classFieldName = columnField.getClassFieldName();
            whereSb.append("\t\tif(condition.get(\""+classFieldName+"\") != null){\n");
            whereSb.append("\t\t\t WHERE(\""+fieldName+"=#{"+classFieldName+"}\");\n");
            whereSb.append("\t\t}\n");
        }
        findSb.append(whereSb);
        findSb.append("\t\treturn SQL();\n");
        findSb.append("\t}\n\n");
        return findSb.toString();
    }


    static String fillContent4Mapper(TableClass tableClass, String packagePath) throws Exception {
        StringBuilder daoSb = new StringBuilder();
        daoSb.append("package "+packagePath+";").append("\n\n");
        daoSb.append("import "+entityPackagePath+"."+tableClass.getClassname()+";\n");
        daoSb.append("import java.util.List;\n");
        daoSb.append("import java.util.Map;\n");
        daoSb.append("import org.apache.ibatis.annotations.DeleteProvider;\n");
        daoSb.append("import org.apache.ibatis.annotations.InsertProvider;\n");
        daoSb.append("import org.apache.ibatis.annotations.SelectProvider;\n");
        daoSb.append("import org.apache.ibatis.annotations.SelectKey;\n");
        daoSb.append("import org.apache.ibatis.annotations.UpdateProvider;\n");
        daoSb.append("import org.apache.ibatis.annotations.Insert;\n");
        daoSb.append("import org.apache.ibatis.annotations.Update;\n");
        daoSb.append("import org.apache.ibatis.annotations.Param;\n");
        daoSb.append("import org.apache.ibatis.mapping.StatementType;\n");
        daoSb.append("import org.springframework.stereotype.Repository;\n");
        daoSb.append("\n");
        daoSb.append("/**").append("\n");
        daoSb.append(" * @date " + Utils.dateFormat() + "  dao for table ").append(tableClass.getTablename()).append("\n");
        daoSb.append("*/\n");
        daoSb.append("@Repository(\"" + tableClass.getClassInstanceName() + "Mapper\")\n");
        daoSb.append("public interface " + tableClass.getClassname()  + "Mapper").append("{\n\n");

        daoSb.append("\tString TABLE_NAME = \"" + tableClass.getTablename() + "\";\n");
        daoSb.append("\tString TABLE_FIELD = \""+tableClass.getTableField()+"\";\n\n");

        daoSb.append("\t@SelectProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"getCountByConditionSql\")\n");
        daoSb.append("\tpublic int getCountByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\t@SelectProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"findMapByConditionSql\")\n");
        daoSb.append("\tpublic List<Map<String, Object>> findMapByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\t@SelectProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"findEntityByConditionSql\")\n");
        daoSb.append("\tpublic List<"+tableClass.getClassname()+"> findEntityByCondition(Map<String, Object> condition)throws Exception;\n");

        daoSb.append("\t@SelectProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"findByEntitySql\")\n");
        daoSb.append("\tpublic "+tableClass.getClassname()+" findByEntity("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\t@SelectProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"findByIdSql\")\n");
        daoSb.append("\tpublic "+tableClass.getClassname()+" findById(int id)throws Exception;\n");

        daoSb.append("\t@InsertProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"insertSql\")\n");
        daoSb.append("\tpublic int insert("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\t@InsertProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"insertAndGetKeySql\")\n");
        daoSb.append("\t@SelectKey(before = false, keyProperty = \""+tableClass.getPrimaryColumnField().getClassFieldName()+"\", resultType = Integer.class, statementType = StatementType.PREPARED, statement = \"SELECT LAST_INSERT_ID() AS "+tableClass.getPrimaryColumnField().getClassFieldName()+"\")\n");
        daoSb.append("\tpublic int insertAndGetKey("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\t@UpdateProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"updateSql\")\n");
        daoSb.append("\tpublic int update("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("\t@DeleteProvider(type = "+tableClass.getClassname()+"MapperProvider.class, method = \"deleteSql\")\n");
        daoSb.append("\tpublic int delete("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception;\n");

        daoSb.append("}");
        String content = daoSb.toString();
        return content;
    }

    static void write2MapperFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String mapperFileContent = fillContent4Mapper(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"Mapper.java", mapperFileContent);
    }

    static void write2ProviderFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String mapperProviderFileContent = fillContent4MapperProvider(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"MapperProvider.java", mapperProviderFileContent);
    }
}
