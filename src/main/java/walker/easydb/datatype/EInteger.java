package walker.easydb.datatype;

import java.io.Serializable;

/**
 * EInteger类, 提供了Integer类的功能并实现了UpdateIdentifier接口.
 * 
 * 
 * @see walker.easydb.datatype.UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */

@SuppressWarnings({ "rawtypes", "serial" })
public class EInteger extends Number implements UpdateIdentifier, Serializable,Comparable {

    private Integer aInteger = null;

    //标识是否把对应列更新
    private boolean needUpdate = false;

    /**
     * 构造子，其创建的对象所对应的列将被更新为NULL
     *  
     */
    public EInteger() {
        this.aInteger = null;
        this.needUpdate = true;
    }

    /**
     * 
     * 构造子，其创建的对象将被默认需要更到数据库.
     *  
     */
    public EInteger(int i) {
        this.aInteger = new Integer(i);
        this.needUpdate = true;
    }

    /**
     * 
     * 构造子，其创建的对象将被默认需要更到数据库.
     *  
     */
    public EInteger(String s) {
        this.aInteger = new Integer(s);
        this.needUpdate = true;
    }



    
    public int compareTo(Object o) {
        if (this.aInteger != null) {
            if (o instanceof Integer) {
                return this.aInteger.compareTo((Integer) o);
            } else if (o instanceof EInteger) {
                return this.aInteger.compareTo(((EInteger) o).toInteger());
            }
        }
        return 0;
	}
    


    /**
     * 
     * 返回Integer类型的数据
     *  
     */
    public Integer toInteger() {
        return this.aInteger;
    }

    /**
     * 
     * Returns a string representation of this Double object.
     * 
     * @see java.lang.Integer#toString() 
     */
    public String toString() {
        if (!this.isEmpty()) {
            return this.aInteger.toString();
        }
        return "";
    }

    /**
     * 
     * @see java.lang.Integer#doubleValue()
     */
    public double doubleValue() {
        return this.aInteger.doubleValue();
    }

    /**
     * 
     * @see java.lang.Integer#floatValue()
     */
    public float floatValue() {
        return this.aInteger.floatValue();
    }

    /**
     * 
     * @see java.lang.Integer#intValue()
     */
    public int intValue() {
        return this.aInteger.intValue();
    }

    /**
     * 
     * @see java.lang.Integer#longValue()
     */
    public long longValue() {
        return this.aInteger.longValue();
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
     *判断属性值是否为null, 以决定是否应该将对应列置null
     * 
     */
    public boolean isEmpty() {
        return this.aInteger == null;
    }
    
    public boolean equals(EInteger ei) {
                
        if (this.aInteger != null) {
            return this.aInteger.equals(ei.toInteger());
        }else{
            if(ei==null){
                return true;
            } 
            return false;
        }
    }
}
