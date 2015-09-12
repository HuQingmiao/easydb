package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * ELong类, 提供了Long类的功能并实现了UpdateIdentifier接口.
 *
 * @author Huqingmiao
 * @see UpdateIdentifier
 */
@SuppressWarnings({"rawtypes", "serial"})
public class ELong extends Number implements UpdateIdentifier, Serializable, Comparable {

    private Long aLong = null;

    //标识是否把对应列更新
    private boolean needUpdate = false;

    /**
     * 构造子，其创建的对象所对应的列将被更新为NULL
     */
    public ELong() {
        this.aLong = null;
        this.needUpdate = true;
    }

    /**
     * 构造子，其创建的对象将被默认需要更到数据库.
     */
    public ELong(long l) {
        this.aLong = new Long(l);
        this.needUpdate = true;
    }

    /**
     * 构造子，其创建的对象将被默认需要更到数据库.
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
     * 返回Long类型的数据
     */
    public Long toLong() {
        return this.aLong;
    }

    /**
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
     * @see java.lang.Long#doubleValue()
     */
    public double doubleValue() {
        return this.aLong.doubleValue();
    }

    /**
     * @see java.lang.Long#floatValue()
     */
    public float floatValue() {
        return this.aLong.floatValue();
    }

    /**
     * @see java.lang.Long#intValue()
     */
    public int intValue() {
        return this.aLong.intValue();
    }

    /**
     * @see java.lang.Long#longValue()
     */
    public long longValue() {
        return this.aLong.longValue();
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
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 判断属性值是否为null, 以决定是否应该将对应列置null
     */
    public boolean isEmpty() {
        return this.aLong == null;
    }

    public boolean equals(ELong el) {

        if (this.aLong != null) {
            return this.aLong.equals(el.toLong());
        } else {
            if (el == null) {
                return true;
            }
            return false;
        }
    }
}
