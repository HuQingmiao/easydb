package com.github.walker.easydb.criterion;

import com.github.walker.easydb.assistant.MappingUtil;

/**
 * Constrains a property to be null
 * 
 * @author HuQingmiao
 */
public class NullExpression extends Criteria {

	private String colName;

	protected NullExpression(String propertyName) {
		this.colName = MappingUtil.getColumnName(propertyName);
	}

	public String toSqlString() {
		return colName + " IS NULL";
	}

}
