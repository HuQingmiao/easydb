package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * EFloat��, �ṩ��Float��Ĺ��ܲ�ʵ����UpdateIdentifier�ӿ�.
 * 
 * 
 * @see UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class EFloat extends Number implements UpdateIdentifier, Serializable, Comparable {

    private Float aFloat = null;

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    /**
     * �����ӣ��䴴���Ķ�������Ӧ���н�������ΪNULL
     *  
     */
    public EFloat() {
        this.aFloat = null;
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public EFloat(float f) {
        this.aFloat = new Float(f);
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public EFloat(String s) {
        this.aFloat = new Float(s);
        this.needUpdate = true;
    }



    public int compareTo(Object o) {
        if (this.aFloat != null) {
            if (o instanceof Float) {
                return this.aFloat.compareTo((Float) o);
            } else if (o instanceof EFloat) {
                return this.aFloat.compareTo(((EFloat) o).toFloat());
            }
        }
        return 0;
    }

    /**
     * 
     * ����Float���͵�����
     *  
     */
    public Float toFloat() {
        return this.aFloat;
    }

    /**
     * 
     * Returns a string representation of this Float object.
     * 
     * @see java.lang.Float#toString()
     *  
     */
    public String toString() {
        if (!this.isEmpty()) {
            return this.aFloat.toString();
        }
        return "";
    }

    /**
     * 
     * @see java.lang.Float#doubleValue()
     */
    public double doubleValue() {
        return this.aFloat.doubleValue();
    }

    /**
     * 
     * @see java.lang.Float#floatValue()
     */
    public float floatValue() {
        return this.aFloat.floatValue();
    }

    /**
     * 
     * @see java.lang.Float#intValue()
     */
    public int intValue() {
        return this.aFloat.intValue();
    }

    /**
     * 
     * @see java.lang.Float#longValue()
     */
    public long longValue() {
        return this.aFloat.longValue();
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
        return this.aFloat == null;
    }
    
    public boolean equals(EFloat ef) {
        if (this.aFloat != null) {
            return this.aFloat.equals(ef.toFloat());
        }else{
            if(ef==null){
                return true;
            } 
            return false;
        }
    }    
}
