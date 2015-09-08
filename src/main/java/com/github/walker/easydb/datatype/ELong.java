package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * ELong��, �ṩ��Long��Ĺ��ܲ�ʵ����UpdateIdentifier�ӿ�.
 * 
 * 
 * @see UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ELong extends Number implements UpdateIdentifier, Serializable, Comparable {

    private Long aLong = null;

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    /**
     * �����ӣ��䴴���Ķ�������Ӧ���н�������ΪNULL
     *  
     */
    public ELong() {
        this.aLong = null;
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public ELong(long l) {
        this.aLong = new Long(l);
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public ELong(String s) {
        this.aLong = new Long(s);
        this.needUpdate = true;
    }



    public int compareTo(Object o) {
        if (this.aLong != null) {
            if (o instanceof Long) {
                return this.aLong.compareTo((Long) o);
            } else if (o instanceof ELong) {
                return this.aLong.compareTo(((ELong) o).toLong());
            }
        }
        
        return 0;
    }

    /**
     * 
     * ����Long���͵�����
     *  
     */
    public Long toLong() {
        return this.aLong;
    }

    /**
     * 
     * Returns a string representation of this Long object.
     * 
     * @see java.lang.Long#toString()
     */
    public String toString() {
        if (!this.isEmpty()) {
            return this.aLong.toString();
        }
        return "";
    }


    /**
     * 
     * @see java.lang.Long#doubleValue()
     */
    public double doubleValue() {
        return this.aLong.doubleValue();
    }

    /**
     * 
     * @see java.lang.Long#floatValue()
     */
    public float floatValue() {
        return this.aLong.floatValue();
    }

    /**
     * 
     * @see java.lang.Long#intValue()
     */
    public int intValue() {
        return this.aLong.intValue();
    }

    /**
     * 
     * @see java.lang.Long#longValue()
     */
    public long longValue() {
        return this.aLong.longValue();
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
        return this.aLong == null;
    }
    
    public boolean equals(ELong el) {
       
        if (this.aLong != null) {
            return this.aLong.equals(el.toLong());
        }else{
            if(el==null){
                return true;
            } 
            return false;
        }
    }  
}
