package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * EInteger��, �ṩ��Integer��Ĺ��ܲ�ʵ����UpdateIdentifier�ӿ�.
 * 
 * 
 * @see UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */

@SuppressWarnings({ "rawtypes", "serial" })
public class EInteger extends Number implements UpdateIdentifier, Serializable,Comparable {

    private Integer aInteger = null;

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    /**
     * �����ӣ��䴴���Ķ�������Ӧ���н�������ΪNULL
     *  
     */
    public EInteger() {
        this.aInteger = null;
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public EInteger(int i) {
        this.aInteger = new Integer(i);
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
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
     * ����Integer���͵�����
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
     *�ж�����ֵ�Ƿ�Ϊnull, �Ծ����Ƿ�Ӧ�ý���Ӧ����null
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
