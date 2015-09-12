package com.github.walker.easydb.datatype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;

/**
 * 文本文件类型，它映射数据库的文本大字段类型, 比如Oracle中的CLOB,当向CLOB字段写入时，会用到此类型。
 * <p/>
 * This class extends the File class, it reflects the big text type column in
 * database, such as CLOB type of oracle.
 * <p/>
 * If you want to write binary file into database, such as BLOB type of oracle
 * or mysql, you can using walker.easydb.datatype.BinaryFile.
 *
 * @author Huqingmiao
 * @see EBinFile
 */
@SuppressWarnings("serial")
public class ETxtFile extends File implements UpdateIdentifier {


    //标识是否把对应列更新
    private boolean needUpdate = false;

    //标识是否把对应列置空
    private boolean isEmpty = false;


    /**
     * 构造子，其产生的对象所对应的大字段列（e.g CLOB）将被更新为null.
     * <p/>
     * Constructor, produce a empty Timestamp instance.
     */
    public ETxtFile() {
        super("");
        this.isEmpty = true; //置空
        this.needUpdate = true; //要更新
    }

    /**
     * 根据给定的文件路径生成一个二进制文件的实例。
     * <p/>
     * Creates a new File instance by converting the given pathname string into
     * an abstract pathname.
     *
     * @param pathname a pathname string.
     */
    public ETxtFile(String pathname) {
        super(pathname);

        this.isEmpty = false; //非空
        this.needUpdate = true; //要更新
    }

    /**
     * 设置是否把对应列更新;
     *
     * @param flag
     */
    public void setUpdate(boolean flag) {
        this.needUpdate = flag;

    }

    /**
     * 只有当此方法返回true时, 持久化动作才会更新对应列.
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 判断属性值是否为null, 以决定是否应该将对应列置null
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }

    public String toString() {
        if (!this.isEmpty()) {
            return super.toString();
        }

        return "";
    }

    public String getPath() {
        if (!this.isEmpty()) {
            return super.getPath();
        }

        return "";
    }

    public String getCanonicalPath() throws IOException {
        if (!this.isEmpty()) {
            return super.getCanonicalPath();
        }

        return "";
    }

    public String getAbsolutePath() {
        if (!this.isEmpty()) {
            return super.getAbsolutePath();
        }

        return "";
    }

    /**
     * 取文件内容
     *
     * @return 字符串形式的文件内容
     * @throws IOException
     */
    public String getFileContent() throws IOException {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(this)));

            StringBuffer buff = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                buff.append(s);
            }
            br.close();

            return buff.toString();

        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * 读取指定的文本文件的内容
     *
     * @param file
     * @return 字符串形式的文件内容
     * @throws IOException
     */
    public static String convertToString(ETxtFile file) throws IOException {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            StringBuffer buff = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                buff.append(s);
            }
            br.close();

            return buff.toString();

        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * 把字符串写入指定的文件
     *
     * @param content
     * @param filename
     * @return ETxtFile object
     * @throws IOException
     */
    public static ETxtFile convertToFile(String content, String filename)
            throws IOException {

        FileOutputStream out;
        try {
            ETxtFile file = new ETxtFile(filename);
            if (file.exists()) {
                file.delete();
            }

            out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.flush();
            out.close();

            return file;

        } catch (IOException e) {
            Logger log2 = LogFactory.getLogger(ETxtFile.class);
            log2.error(e.getMessage(), e);
            throw e;
        }
    }
}