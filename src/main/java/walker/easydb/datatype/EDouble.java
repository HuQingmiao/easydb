package walker.easydb.datatype;

import java.io.Serializable;

/**
 * EDouble类, 提供了Double类的功能并实现了UpdateIdentifier接口.
 * 
 * 
 * @see walker.easydb.datatype.UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class EDouble extends Number implements UpdateIdentifier, Serializable, Comparable {

	
    private Double aDouble = null;

    //标识是否把对应列更新
    private boolean needUpdate = false;

    /**
     * 构造子，其创建的对象所对应的列将被更新为NULL
     *  
     */
    public EDouble() {
        this.aDouble = null;
        this.needUpdate = true;
    }

    /**
     * 
     * 构造子，其创建的对象将被默认需要更到数据库.
     *  
     */
    public EDouble(double d) {
        this.aDouble = new Double(d);
        this.needUpdate = true;
    }

    /**
     * 
     * 构造子，其创建的对象将被默认需要更到数据库.
     *  
     */
    public EDouble(String s) {
        this.aDouble = new Double(s);
        this.needUpdate = true;
    }
    

    public int compareTo(Object o) {
        if (this.aDouble != null) {
            if (o instanceof Double) {
                return this.aDouble.compareTo((Double) o);
            } else if (o instanceof EDouble) {
                return this.aDouble.compareTo(((EDouble) o).toDouble());
            }
        }
        return 0;
    }



    /**
     * 
     * 返回Double类型的数据
     *  
     */
    public Double toDouble() {
        return this.aDouble;
    }


    /**
     * 
     * Returns a string representation of this Double object.
     * 
     * @see java.lang.Double#toString() 
     *  
     */
    public String toString() {
        if (!this.isEmpty()) {
            return this.aDouble.toString();
        }
        return "";
    }

    /**
     * 
     * @see java.lang.Double#doubleValue()
     */
    public double doubleValue() {
        return this.aDouble.doubleValue();
    }

    /**
     * 
     * @see java.lang.Double#floatValue()
     */
    public float floatValue() {
        return this.aDouble.floatValue();
    }

    /**
     * 
     * @see java.lang.Double#intValue()
     */
    public int intValue() {
        return this.aDouble.intValue();
    }

    /**
     * 
     * @see java.lang.Double#longValue()
     */
    public long longValue() {
        return this.aDouble.longValue();
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
        return this.aDouble == null;
    }

    public boolean equals(EDouble ed) {
  
        if (this.aDouble != null) {
            return this.aDouble.equals(ed.toDouble());
        }else{
            if(ed==null){
                return true;
            } 
            return false;
        }
    }

}
