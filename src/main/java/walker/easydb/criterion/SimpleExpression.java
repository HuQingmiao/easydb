package walker.easydb.criterion;

import walker.easydb.assistant.MappingUtil;
import walker.easydb.datatype.EString;

public class SimpleExpression extends Criteria {

	private String left;

	private String value;

	private String op;

	protected SimpleExpression(String propertyName, Object value, String op) {
		this.left = MappingUtil.getColumnName(propertyName);

		if (value instanceof String || value instanceof EString) {
			String v = value.toString();
			v = v.replace("\'", "''");// 将条件中的单引号替换成丙个单引号
			this.value = "\'" + v + "\'";
		} else {
			this.value = value.toString();
		}

		this.op = op;
	}

	public String toSqlString() {
		return left + ' ' + op + ' ' + value;
	}

	public String getValueByLeft(String left) {
		return this.value;
	}

	protected String getOp() {
		return this.op;
	}

	protected String getLeft() {
		return this.left;
	}

	protected String getValue() {
		return this.value;
	}

}
