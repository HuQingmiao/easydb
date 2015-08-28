package walker.easydb.datatype;

import java.io.File;
import java.io.IOException;

/**
 * 二进制文件类型，它映射数据库的二进制大字段类型, 比如Oracle中的BLOB, 当向BLOB字段写入时,会用到此类型。
 * 
 * This class extends the File class, it reflects the binary type column in
 * database, such as BLOB type of oracle.
 * 
 * If you want to write text file into database, such as CLOB type of oracle you
 * can using walker.easydb.datatype.TextFile.
 * 
 * 
 * @see walker.easydb.datatype.ETxtFile
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings("serial")
public class EBinFile extends File implements UpdateIdentifier {

    //标识是否把对应列更新
    private boolean needUpdate = false;

    //标识是否把对应列置空
    private boolean isEmpty = false;

    /**
     * 构造子，其产生的对象所对应的大字段列（e.g BLOB）将被更新为null.
     * 
     *
     */
    public EBinFile() {
        super("");
        this.isEmpty = true; //置空
        this.needUpdate = true; //要更新
    }

    /**
     * 根据给定的文件路径生成一个二进制文件的实例。
     * 
     * Creates a new File instance by converting the given pathname string into
     * an abstract pathname.
     * 
     * @param pathname
     *            a pathname string.
     */
    public EBinFile(String pathname) {
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
     *  
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 判断属性值是否为null, 以决定是否应该将对应列置null
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