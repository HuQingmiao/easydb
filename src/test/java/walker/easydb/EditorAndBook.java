package walker.easydb;

import walker.easydb.dao.BaseEntity;
import walker.easydb.datatype.EBinFile;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.EString;
import walker.easydb.datatype.ETxtFile;

/**
 * 
 * ĳ��ѯ��ͼ��Ӧ��ʵ��
 * 
 * @author HuQingmiao
 *  
 */
@SuppressWarnings("serial")
public class EditorAndBook extends BaseEntity {

    private EString editorName; //��������

    private EString editorSex;  //�����Ա�

    private EString title;	   //��������

    private EFloat cost; 	   //���

    private EBinFile blobContent;//�������
    
    private ETxtFile textContent;

    
    //������
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
