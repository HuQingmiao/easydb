package com.github.walker.easydb.criterion;


/**
 * 
 * 
 * �������ʽ�ĳ�����, ���ڹ���WHERE�Ӿ�
 * 
 * @author HuQingmiao
 *  
 */
public abstract class Criteria {
	
    //protected static Logger log = LogFactory.getLogger(Criteria.class);

    protected abstract String toSqlString();
    
    
    /**
     * ���ݸ����������ʽ, ȡ�ö�Ӧ��ֵ
     * 
     * @param left
     *            ���ʽ�в�������ߵ��ַ���
     * 
     * @return ���ʽ�в������ұߵ��ַ���
     */
    public String getValueByLeft(String left){
        return null;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Criteria) {
            return this.toString().equals(o.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return this.toSqlString();
    }
}
