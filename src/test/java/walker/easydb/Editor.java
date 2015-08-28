package walker.easydb;

import walker.easydb.dao.BaseEntity;
import walker.easydb.datatype.ELong;
import walker.easydb.datatype.EString;

/**
 *
 * EDITOR ʵ��
 *
 * @author  HuQingmiao
 *
 */
@SuppressWarnings("serial")
public class Editor extends BaseEntity {

	private ELong editorId;
    private EString name;
    private EString sex;

    public String[] pk() {
        return new String[] {"editorId"};
    }

    public ELong getEditorId() {
        return editorId;
    }
    public void setEditorId(ELong editorId) {
        this.editorId = editorId;
    }
    public EString getName() {
        return name;
    }
    public void setName(EString name) {
        this.name = name;
    }
    public EString getSex() {
        return sex;
    }
    public void setSex(EString sex) {
        this.sex = sex;
    }
}
