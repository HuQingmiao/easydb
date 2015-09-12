package com.github.walker.easydb.datatype;

import java.io.Serializable;

/**
 * EFloat类, 提供了Float类的功能并实现了UpdateIdentifier接口.
 *
 * @author Huqingmiao
 * @see UpdateIdentifier
 */
@SuppressWarnings({"rawtypes", "serial"})
public class EFloat extends Number implements UpdateIdentifier, Serializable, Comparable {

    private Float aFloat = null;

    //标识是否把对应列更新
    private boolean needUpdate = false;

    /**
     * 构造子，其创建的对象所对应的列将被更新为NULL
     */
    public EFloat() {
        this.aFloat = null;
        this.needUpdate = true;
    }

    /**
     * 构造子，其创建的对象将被默认需要更到数据库.
     */
    public EFloat(float f) {
        this.aFloat = new Float(f);
        this.needUpdate = true;
    }

    /**
     * 构造子，其创建的对象将被默认需要更到数据库.
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
     * 返回Float类型的数据
     */
    public Float toFloat() {
        return this.aFloat;
    }

    /**
     * Returns a string representation of this Float object.
     *
     * @see java.lang.Float#toString()
     */
    public String toString() {
        if (!this.isEmpty()) {
            return this.aFloat.toString();
        }
        return "";
    }

    /**
     * @see java.lang.Float#doubleValue()
     */
    public double doubleValue() {
        return this.aFloat.doubleValue();
    }

    /**
     * @see java.lang.Float#floatValue()
     */
    public float floatValue() {
        return this.aFloat.floatValue();
    }

    /**
     * @see java.lang.Float#intValue()
     */
    public int intValue() {
        return this.aFloat.intValue();
    }

    /**
     * @see java.lang.Float#longValue()
     */
    public long longValue() {
        return this.aFloat.longValue();
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
        return this.aFloat == null;
    }

    public boolean equals(EFloat ef) {
        if (this.aFloat != null) {
            return this.aFloat.equals(ef.toFloat());
        } else {
            if (ef == null) {
                return true;
            }
            return false;
        }
    }
}
