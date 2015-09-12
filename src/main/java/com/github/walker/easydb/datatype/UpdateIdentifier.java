package com.github.walker.easydb.datatype;

/**
 * 更新标识类, 这个类可标识持久化动作时是否真正更新对应列, 是否将某列置NULL.
 *
 * @author HuQingmiao
 */
public interface UpdateIdentifier {

    /**
     * 设置是否把对应列更新到数据库
     *
     * @param flag
     */
    public abstract void setUpdate(boolean flag);

    /**
     * 只有当此方法返回true时, 持久化动作才会更新对应列.
     */
    public abstract boolean needUpdate();

    /**
     * 测试当前属性是否为空, 以决定是否将对应列置空
     */
    public abstract boolean isEmpty();


}
