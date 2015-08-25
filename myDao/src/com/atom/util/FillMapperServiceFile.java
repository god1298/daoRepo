package com.atom.util;

import java.io.File;

/**
 * Created by Administrator on 2015/8/22.
 */
public class FillMapperServiceFile {

    static String entityPackagePath = "";
    static String mapperPackagePath = "";


    static String fillContent4ServiceImpl(TableClass tableClass, String packagePath) throws Exception {
        StringBuilder serviceSb = new StringBuilder();
        serviceSb.append("package " + packagePath + ";").append("\n\n");
        serviceSb.append("import "+entityPackagePath+"."+tableClass.getClassname()+";\n");
        serviceSb.append("import "+mapperPackagePath+"."+tableClass.getClassname()+"Mapper;\n");
        serviceSb.append("import java.util.List;\n");
        serviceSb.append("import java.util.Map;\n");
        serviceSb.append("import javax.annotation.Resource;\n");
        serviceSb.append("import org.springframework.stereotype.Service;\n");
        serviceSb.append("\n");
        serviceSb.append("/**").append("\n");
        serviceSb.append(" * @date " + Utils.dateFormat() + "  service for table ").append(tableClass.getTablename()).append("\n");
        serviceSb.append(" */").append("\n");
        serviceSb.append("@Service(\""+tableClass.getClassInstanceName()+"Service\")\n");
        serviceSb.append("public class " + tableClass.getClassname()  + "ServiceImpl implements ").append(tableClass.getClassname()+"Service").append("{\n\n");
        serviceSb.append("\t@Resource(name = \""+tableClass.getClassInstanceName()+"Mapper\")\n");
        serviceSb.append("\tprivate "+tableClass.getClassname()+"Mapper "+tableClass.getClassInstanceName()+"Mapper;\n\n");
        serviceSb.append(fillContent4FindService(tableClass));
        serviceSb.append(fillContent4Add(tableClass));
        serviceSb.append(fillContent4Update(tableClass));
        serviceSb.append("}");
        String content = serviceSb.toString();
        return content;
    }

    static String fillContent4FindService(TableClass tableClass)throws Exception{
        StringBuilder findSb = new StringBuilder();
        findSb.append("\tpublic void find").append(tableClass.getClassname()).append("(PageHolder<Map<String, Object>> pageHolder, Map<String, Object> condition)throws Exception{\n");
        findSb.append("\t\tcondition.put(\"limit\", 1);\n");
        findSb.append("\t\tcondition.put(\"rowOffset\", pageHolder.getRowOffset());\n");
        findSb.append("\t\tcondition.put(\"pageSize\", pageHolder.getPageSize());\n");
        findSb.append("\t\tint rowCount = "+tableClass.getClassInstanceName()+"Mapper.getCountByCondition(condition);\n");
        findSb.append("\t\tpageHolder.setRowCount(rowCount);\n");
        findSb.append("\t\tif(rowCount == 0){\n");
        findSb.append("\t\t\treturn ;\n");
        findSb.append("\t\t}\n");
        findSb.append("\t\tList<Map<String, Object>> list = "+tableClass.getClassInstanceName()+"Mapper.findMapByCondition(condition);\n");
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

    static String fillContent4Update(TableClass tableClass)throws Exception{
        StringBuilder addSb = new StringBuilder();
        addSb.append("\tpublic boolean update").append(tableClass.getClassname()).append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        addSb.append("\t\tint rowCount = "+tableClass.getClassInstanceName()+"Mapper.update("+tableClass.getClassInstanceName()+");\n");
        addSb.append("\t\tif(rowCount > 0){\n");
        addSb.append("\t\t\treturn true;\n");
        addSb.append("\t\t}\n");
        addSb.append("\t\treturn false;\n");
        addSb.append("\t}\n\n");
        return addSb.toString();
    }

    static String fillContent4Add(TableClass tableClass)throws Exception{
        StringBuilder addSb = new StringBuilder();
        addSb.append("\tpublic boolean add").append(tableClass.getClassname()).append("("+tableClass.getClassname()+" "+tableClass.getClassInstanceName()+")throws Exception{\n");
        addSb.append("\t\tint rowCount = "+tableClass.getClassInstanceName()+"Mapper.insert("+tableClass.getClassInstanceName()+");\n");
        addSb.append("\t\tif(rowCount > 0){\n");
        addSb.append("\t\t\treturn true;\n");
        addSb.append("\t\t}\n");
        addSb.append("\t\treturn false;\n");
        addSb.append("\t}\n\n");
        return addSb.toString();
    }


    static String fillContent4Service(TableClass tableClass, String packagePath) throws Exception {
        StringBuilder serviceSb = new StringBuilder();
        serviceSb.append("package "+packagePath+";").append("\n\n");
        serviceSb.append("import "+entityPackagePath+"."+tableClass.getClassname()+";\n");
        serviceSb.append("import java.util.List;\n");
        serviceSb.append("import java.util.Map;\n");
        serviceSb.append("\n");
        serviceSb.append("/**").append("\n");
        serviceSb.append(" * @date " + Utils.dateFormat() + "  service for table ").append(tableClass.getTablename()).append("\n");
        serviceSb.append(" */").append("\n");
        serviceSb.append("public interface " + tableClass.getClassname()  + "Service").append("{\n\n");
        serviceSb.append("\tpublic boolean add").append(tableClass.getClassname()).append("(").append(tableClass.getClassname()).append(" ").append(tableClass.getClassInstanceName()).append(")throws Exception;\n");
        serviceSb.append("\tpublic boolean update").append(tableClass.getClassname()).append("(").append(tableClass.getClassname()).append(" ").append(tableClass.getClassInstanceName()).append(")throws Exception;\n");
        serviceSb.append("}");
        String content = serviceSb.toString();
        return content;
    }

    static void write2ServiceFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String serviceFileContent = fillContent4Service(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"Service.java", serviceFileContent);
    }

    static void write2ServiceImplFile(TableClass tableClass, String packagePath, String filePath)throws Exception{
        String serviceImplFileContent = fillContent4ServiceImpl(tableClass, packagePath);
        Utils.writeFile(filePath+ File.separator+tableClass.getClassname()+"ServiceImpl.java", serviceImplFileContent);
    }
}
