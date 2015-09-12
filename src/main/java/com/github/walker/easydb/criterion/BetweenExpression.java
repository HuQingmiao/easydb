package com.github.walker.easydb.criterion;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.datatype.EString;


/**
 * Constrains a property to between two values
 *
 * @author HuQingmiao
 */
public class BetweenExpression extends Criteria {

    private String colName;
    private Object lo;
    private Object hi;

    protected BetweenExpression(String propertyName, Object lo, Object hi) {
        this.colName = MappingUtil.getColumnName(propertyName);

        if (lo instanceof String || lo instanceof EString) {
            this.lo = "\'" + lo.toString() + "\'";
        } else {
            this.lo = lo.toString();
        }

        if (hi instanceof String || hi instanceof EString) {
            this.hi = "\'" + hi.toString() + "\'";
        } else {
            this.hi = hi.toString();
        }
    }


    public String toSqlString() {
        return colName + " BETWEEN " + lo + " AND " + hi;
    }
}
