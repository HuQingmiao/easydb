package com.github.walker.easydb.datatype;

import java.io.File;
import java.io.IOException;

/**
 * �������ļ����ͣ���ӳ�����ݿ�Ķ����ƴ��ֶ�����, ����Oracle�е�BLOB, ����BLOB�ֶ�д��ʱ,���õ������͡�
 * 
 * This class extends the File class, it reflects the binary type column in
 * database, such as BLOB type of oracle.
 * 
 * If you want to write text file into database, such as CLOB type of oracle you
 * can using walker.easydb.datatype.TextFile.
 * 
 * 
 * @see ETxtFile
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings("serial")
public class EBinFile extends File implements UpdateIdentifier {

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    //��ʶ�Ƿ�Ѷ�Ӧ���ÿ�
    private boolean isEmpty = false;

    /**
     * �����ӣ�������Ķ�������Ӧ�Ĵ��ֶ��У�e.g BLOB����������Ϊnull.
     * 
     *
     */
    public EBinFile() {
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
    public EBinFile(String pathname) {
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


}