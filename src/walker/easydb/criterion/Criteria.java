package walker.easydb.criterion;


/**
 * 
 * 
 * 条件表达式的抽象类, 用于构造WHERE子句
 * 
 * @author HuQingmiao
 *  
 */
public abstract class Criteria {
	
    //protected static Logger log = LogFactory.getLogger(Criteria.class);

    protected abstract String toSqlString();
    
    
    /**
     * 根据给定的左侧表达式, 取得对应的值
     * 
     * @param left
     *            表达式中操作符左边的字符串
     * 
     * @return 表达式中操作符右边的字符串
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
