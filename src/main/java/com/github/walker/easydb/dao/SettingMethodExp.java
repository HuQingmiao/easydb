package com.github.walker.easydb.dao;

import java.lang.reflect.Method;

/**
 * This class express the setting Method of BaseEntity object.
 *
 * @author HuQingmiao
 */
public class SettingMethodExp {

    private String paramType;// the parameter of method

    private Method method; // the method

    public SettingMethodExp(String paramType, Method method) {
        this.paramType = paramType;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
}
