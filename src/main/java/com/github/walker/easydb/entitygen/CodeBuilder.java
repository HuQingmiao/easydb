package com.github.walker.easydb.entitygen;

import com.github.walker.easydb.assistant.MappingUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * 代码构建
 *
 * @author HuQingmiao
 */
public class CodeBuilder {

    protected String tableName;

    protected Map<String, MetaDataDescr> colNameMetaMap;

    /**
     * 表名
     *
     * @param tableName
     */
    public CodeBuilder(String tableName) {
        this.tableName = tableName.trim().toLowerCase();
        this.colNameMetaMap = new MetaMapping(tableName).getColNameMetaMap();
    }


    /**
     * 构造实体类的源码
     *
     * @return
     */
    protected String buildCodeStr() throws Exception {

        StringBuffer buff = new StringBuffer("\n\n");

        buff.append("import com.github.walker.easydb.dao.BaseEntity; \n");
        buff.append("import com.github.walker.easydb.datatype.EInteger; \n");
        buff.append("import com.github.walker.easydb.datatype.ELong; \n");
        buff.append("import com.github.walker.easydb.datatype.EDouble; \n");
        buff.append("import com.github.walker.easydb.datatype.EFloat; \n");
        buff.append("import com.github.walker.easydb.datatype.EString; \n");
        buff.append("\n");

        // public class AA {
        buff.append("@SuppressWarnings(\"serial\")\n");
        buff.append("public class ");
        buff.append(MappingUtil.getEntityName(tableName));
        buff.append(" extends BaseEntity{\n");


        //生成属性  private String xxx;
        for (Iterator<String> it = colNameMetaMap.keySet().iterator(); it.hasNext(); ) {
            String colName = it.next();
            MetaDataDescr md = colNameMetaMap.get(colName);
            String filedName = md.getFieldName();
            Class fieldType = md.getFieldType();
            String fieldTypeName = fieldType.getSimpleName();
            // System.out.println(">>" + fieldType.getName());
            //if (fieldType.getName().contains("java.lang") || fieldType.getName().startsWith("[")) {

            buff.append("    private " + fieldTypeName + " " + filedName + ";\n");
        }
        buff.append("\n");

        // default constructor method
        buff.append("    public String[] pk() {\n ");
        buff.append("          return new String[]{};\n");
        buff.append("    }\n\n\n");

        //生成方法  public String getXXX();
        for (Iterator<String> it = colNameMetaMap.keySet().iterator(); it.hasNext(); ) {
            String colName = it.next();
            MetaDataDescr md = colNameMetaMap.get(colName);
            String fieldName = md.getFieldName();
            Class fieldType = md.getFieldType();
            String fieldTypeName = fieldType.getSimpleName();

            if (fieldType.getName().contains("java.lang") || fieldType.getName().startsWith("[")) {
                fieldTypeName = fieldType.getSimpleName();
            }
            String firstChar = fieldName.substring(0, 1).toUpperCase();
            if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1))) {
                firstChar = firstChar.toLowerCase();
            }
            buff.append("    public " + fieldTypeName + " get");
            buff.append(firstChar + fieldName.substring(1) + "() {\n");
            buff.append("        return " + fieldName + ";\n");
            buff.append("   }\n\n");

            buff.append("    public void set");
            buff.append(firstChar + fieldName.substring(1));
            buff.append("(" + fieldTypeName + " " + fieldName + ") {\n");
            buff.append("        this." + fieldName + " = " + fieldName + ";\n");
            buff.append("   }\n\n");
        }

        buff.append("}\n\n");

        //生成各字段名拼接的字符串
        buff.append("/*List columns as follows:\n");
        int i = 0;
        for (Iterator<String> it = colNameMetaMap.keySet().iterator(); it.hasNext(); ) {
            String colName = it.next();
            buff.append("\"" + colName.toLowerCase() + "\", ");
            i++;
            if (i % 7 == 0) {
                buff.append("\n");
            }
        }
        buff.delete(buff.lastIndexOf(","), buff.length());
        buff.append("\n*/");

        return buff.toString();
    }


    /**
     * 生成实体类文件
     *
     * @param dirc 文件输出目录
     */
    public void createEntityTo(File dirc) throws IOException {
        OutputStreamWriter osw = null;
        try {
            String srcContent = this.buildCodeStr();
            String filename = dirc.getCanonicalPath() + File.separator + MappingUtil.getEntityName(tableName) + ".java";

            osw = new OutputStreamWriter(new FileOutputStream(filename));
            osw.write(srcContent, 0, srcContent.length());
            osw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (osw != null) {
                osw.close();
            }
        }
    }
}
