package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * EDouble��, �ṩ��Double��Ĺ��ܲ�ʵ����UpdateIdentifier�ӿ�.
 * 
 * 
 * @see UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class EDouble extends Number implements UpdateIdentifier, Serializable, Comparable {

	
    private Double aDouble = null;

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    /**
     * �����ӣ��䴴���Ķ�������Ӧ���н�������ΪNULL
     *  
     */
    public EDouble() {
        this.aDouble = null;
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
     *  
     */
    public EDouble(double d) {
        this.aDouble = new Double(d);
        this.needUpdate = true;
    }

    /**
     * 
     * �����ӣ��䴴���Ķ��󽫱�Ĭ����Ҫ�������ݿ�.
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
     * ����Double���͵�����
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
