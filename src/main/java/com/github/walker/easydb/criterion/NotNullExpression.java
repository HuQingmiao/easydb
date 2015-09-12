package com.github.walker.easydb.criterion;

import com.github.walker.easydb.assistant.MappingUtil;

/**
 * Constrains a property to be non-null
 *
 * @author HuQingmiao
 */
public class NotNullExpression extends Criteria {

    private String colName;

    protected NotNullExpression(String propertyName) {
        this.colName = MappingUtil.getColumnName(propertyName);
    }

    public String toSqlString() {
        return colName + " IS NOT NULL";
    }

}
