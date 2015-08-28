package test;

import walker.easydb.dao.BaseEntity;
import walker.easydb.datatype.EBinFile;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.EString;
import walker.easydb.datatype.ETxtFile;

/**
 * 
 * 某查询视图对应的实体
 * 
 * @author HuQingmiao
 *  
 */
@SuppressWarnings("serial")
public class EditorAndBook extends BaseEntity {

    private EString editorName; //编者姓名

    private EString editorSex;  //编者性别

    private EString title;	   //所著书名

    private EFloat cost; 	   //书价

    private EBinFile blobContent;//书的内容
    
    private ETxtFile textContent;

    
    //无主键
    public String[] pk() {
        return null;
    }
    
    public EBinFile getBlobContent() {
        return blobContent;
    }
    public void setBlobContent(EBinFile blobContent) {
        this.blobContent = blobContent;
    }
    public EFloat getCost() {
        return cost;
    }
    public void setCost(EFloat cost) {
        this.cost = cost;
    }
    public EString getEditorName() {
        return editorName;
    }
    public void setEditorName(EString editorName) {
        this.editorName = editorName;
    }
    public EString getEditorSex() {
        return editorSex;
    }
    public void setEditorSex(EString editorSex) {
        this.editorSex = editorSex;
    }
    public ETxtFile getTextContent() {
        return textContent;
    }
    public void setTextContent(ETxtFile textContent) {
        this.textContent = textContent;
    }
    public EString getTitle() {
        return title;
    }
    public void setTitle(EString title) {
        this.title = title;
    }
}
