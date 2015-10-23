package com.github.walker.easydb.entitygen;


import com.github.walker.easydb.assistant.MappingUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 实体类代码辅助生成工具
 *
 * @author HuQingmiao
 */
public class EntityGenerator {

    public static void main(String[] args) {
        try {
            EntityGenerator.build("Book", new File("d:/"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void build(String tableName, File dirc) throws IOException {
        OutputStreamWriter osw = null;
        try {
            String srcContent = new CodeBuilder(tableName).buildEntitySource();
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
