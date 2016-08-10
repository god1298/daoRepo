package com.atom.util;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2015/8/22.
 */
public class FillEntityFile {
    // 1表示创建基本类型 2表示创建包装类型 默认为基本类型
    static int fieldType=1;

    static String fillContent4Entity(TableClass tableClass, String packagePath) throws SQLException {
        StringBuilder entitySb = new StringBuilder();
        entitySb.append("package "+packagePath+";").append("\n\n");
        entitySb.append("import java.io.Serializable;").append("\n");
        entitySb.append("#BIGDECIMAL#");
        entitySb.append("#STRING#");
        entitySb.append("#DATE#");
        entitySb.append("\n");
        entitySb.append("/**").append("\n");
        entitySb.append(" * @date " + Utils.dateFormat() + "  entity for table ").append(tableClass.getTablename()).append("\n");
        entitySb.append(" */").append("\n");

        entitySb.append("public class " + tableClass.getClassname()  + " implements Serializable{").append("\n");
        entitySb.append("\n");
        entitySb.append("\tprivate static final long serialVersionUID = 1L;").append("\n");

        StringBuilder sb4GetSet = new StringBuilder();
        StringBuilder sb4args = new StringBuilder();
        boolean bDFlag = false;
        boolean strFlag = false;
        boolean dateFlag = false;
        List<ColumnField> columnFieldList = tableClass.getColumnFieldList();
        for (int i = 0; i < columnFieldList.size(); i++) {
            ColumnField columnField = columnFieldList.get(i);
            String classFieldName = columnField.getClassFieldName();
            String classFieldType = columnField.getClassFieldType();
            if("BigDecimal".equals(classFieldType)){
                bDFlag = true;
            }else if("String".equals(classFieldType)){
                strFlag = true;
            }else if("Date".equals(classFieldType)){
                dateFlag = true;
            }
            String defaultClassValue = columnField.getDefaultClassValue();
            sb4args.append("\t//").append(columnField.getFieldComment()).append("PRI".equals(columnField.getKeyType())?" 数据库主键" : "").append(" db:defaultValue:" + (columnField.getDefaultValue() == "" ? "emptyString" : columnField.getDefaultValue())).append("\n");
            if (defaultClassValue != null){
                sb4args.append("\tprivate ").append(classFieldType + " ").append("" + classFieldName + " = ").append(defaultClassValue).append(";").append("\n");
            }else{
                sb4args.append("\tprivate ").append(classFieldType + " ").append("" + classFieldName + "").append(";").append("\n");
            }
            sb4GetSet.append("\tpublic ").append(classFieldType + " ").append("get").append(Utils.upperFirstChar(classFieldName)).append("(){").append("\n");
            sb4GetSet.append("\t\treturn this.").append(classFieldName).append(";").append("\n");
            sb4GetSet.append("\t}").append("\n\n");

            sb4GetSet.append("\tpublic void ").append("set").append(Utils.upperFirstChar(classFieldName)).append("(").append(classFieldType + " ").append(classFieldName)
                    .append("){").append("\n");
            sb4GetSet.append("\t\t this.").append(classFieldName).append("=").append(classFieldName).append(";").append("\n");
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


    static void write2File(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String entityFileContent = fillContent4Entity(tableClass, packagePath);
        Utils.writeFile(filePath+File.separator+tableClass.getClassname()+".java", entityFileContent);
    }

}
