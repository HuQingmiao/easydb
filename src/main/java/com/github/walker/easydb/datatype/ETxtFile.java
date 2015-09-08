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
 * �ı��ļ����ͣ���ӳ�����ݿ���ı����ֶ�����, ����Oracle�е�CLOB,����CLOB�ֶ�д��ʱ�����õ������͡�
 * 
 * This class extends the File class, it reflects the big text type column in
 * database, such as CLOB type of oracle.
 * 
 * If you want to write binary file into database, such as BLOB type of oracle
 * or mysql, you can using walker.easydb.datatype.BinaryFile.
 * 
 * @see EBinFile
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings("serial")
public class ETxtFile extends File  implements UpdateIdentifier{



    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    //��ʶ�Ƿ�Ѷ�Ӧ���ÿ�
    private boolean isEmpty = false;
    

    /**
     * �����ӣ�������Ķ�������Ӧ�Ĵ��ֶ��У�e.g CLOB����������Ϊnull.
     * 
     * Constructor, produce a empty Timestamp instance.
     *
     */
    public ETxtFile() {
        super("");
        this.isEmpty = true; //�ÿ�
        this.needUpdate = true; //Ҫ����
    }

    /**
     * ���ݸ������ļ�·������һ���������ļ���ʵ����
     * 
     * Creates a new File instance by converting the given pathname string into
     * an abstract pathname.
     * 
     * @param pathname
     *            a pathname string.
     */
    public ETxtFile(String pathname) {
        super(pathname);
                
        this.isEmpty = false; //�ǿ�
        this.needUpdate = true; //Ҫ����
    }

    /**
     * �����Ƿ�Ѷ�Ӧ�и���;
     * 
     * @param flag
     */
    public void setUpdate(boolean flag) {
        this.needUpdate = flag;

    }

    /**
     * ֻ�е��˷�������trueʱ, �־û������Ż���¶�Ӧ��.
     *  
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * �ж�����ֵ�Ƿ�Ϊnull, �Ծ����Ƿ�Ӧ�ý���Ӧ����null
     *  
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }
    
    public String toString(){
        if(!this.isEmpty()){
            return super.toString();
        }
        
        return "";
    }

    public String getPath(){
        if(!this.isEmpty()){
            return super.getPath();
        }
        
        return "";
    }
    
    public String getCanonicalPath() throws IOException{
        if(!this.isEmpty()){
            return super.getCanonicalPath();
        }
        
        return "";
    }
    
    public String getAbsolutePath(){
        if(!this.isEmpty()){
            return super.getAbsolutePath();
        }
       
        return ""; 
    }
    
    /**
     * ȡ�ļ�����
     * 
     * @return �ַ�����ʽ���ļ�����
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
     * ��ȡָ�����ı��ļ�������
     * 
     * @param file
     * @return �ַ�����ʽ���ļ�����
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
     * 
     * ���ַ���д��ָ�����ļ�
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