package com.github.walker.easydb.criterion;

import com.github.walker.easydb.assistant.MappingUtil;

/**
 * A criterion representing a "like" expression
 *
 * @author HuQingmiao
 */
public class LikeExpression extends Criteria {
    private String colName;

    private String value;

    private Character escapeChar;

    protected LikeExpression(String propertyName, String value, Character escapeChar) {
        this.colName = MappingUtil.getColumnName(propertyName);
        String v = value.toString();
        this.value = v.replace("\'", "''");// 将条件中的单引号替换成丙个单引号
        this.escapeChar = escapeChar;
    }

    protected LikeExpression(String propertyName, String value, char escapeChar) {
        this.colName = MappingUtil.getColumnName(propertyName);
        String v = value.toString();
        this.value = v.replace("\'", "''");// 将条件中的单引号替换成丙个单引号
        this.escapeChar = new Character(escapeChar);
    }

    protected LikeExpression(String propertyName, String value) {
        this.colName = MappingUtil.getColumnName(propertyName);
        String v = value.toString();
        this.value = v.replace("\'", "''");// 将条件中的单引号替换成丙个单引号
        this.escapeChar = null;
    }

    public String toSqlString() {

        StringBuffer buff = new StringBuffer();
        buff.append(colName).append(" LIKE \'").append(value).append("\'");

        if (escapeChar != null) {
            buff.append(" ESCAPE \'").append(escapeChar).append("\'");
        }

        return buff.toString();
    }

}
