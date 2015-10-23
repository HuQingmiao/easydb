package com.github.walker.easydb.entitygen;


import java.io.File;


/**
 * 实体类代码辅助生成工具
 *
 * @author HuQingmiao
 */
public class EntityGenerator {

    public static void main(String[] args) {
        try {
            new CodeBuilder("book").createEntityTo(new File("d:/"));
            new CodeBuilder("editor").createEntityTo(new File("d:/"));
            new CodeBuilder("book_editor").createEntityTo(new File("d:/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
