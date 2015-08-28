package walker.easydb.criterion;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import walker.easydb.exception.CriteriaException;

/**
 * 复合表达式类
 * 
 * @author HuQingmiao
 * 
 */
public class CriteriaGroup extends Criteria {

	public final static String and = "AND";

	public final static String or = "OR";

	private static HashSet<String> opSet = new HashSet<String>(4);
	static {
		opSet.add(and);
		opSet.add(or);
	}

	private Vector<Criteria> vec = new Vector<Criteria>(5);

	private String op;// 运算符

	/**
	 * 构造条件组
	 * 
	 * @param operator
	 *            二元运算符, 如 "AND","OR"
	 */
	public CriteriaGroup(String operator) {
		operator = operator.toUpperCase();

		// CriteriaGroup只支持如下运算符:'AND','OR' 。
		if (!opSet.contains(operator)) {
			new CriteriaException(CriteriaException.NOT_SUPPORTED_OP, "");
		}

		this.op = operator;
	}

	/**
	 * 添加子条件
	 * 
	 * @param c
	 *            子条件表达示
	 */
	public CriteriaGroup add(Criteria c) throws CriteriaException {
		if (c == this) {
			throw new CriteriaException(CriteriaException.CANNOT_ADD_THIS, "");
		}
		vec.add(c);
		return this;
	}

	/**
	 * 删除子条件
	 * 
	 * @param c
	 *            子条件表达示
	 */
	public CriteriaGroup remove(Criteria c) throws CriteriaException {
		if (!vec.contains(c)) {
			throw new CriteriaException(CriteriaException.REMOVE_NOTEXIST, "");
		}
		vec.remove(c);
		return this;
	}

	/**
	 * 取得表达式中操作符左边字符串对应的值
	 * 
	 * @param left
	 *            表达式中操作符左边的字符串
	 * 
	 * @return 表达式左边字符串对应的值
	 */
	public String getValueByLeft(String left) {

		String upColName = left.toUpperCase();

		for (Iterator<Criteria> it = vec.iterator(); it.hasNext();) {
			Criteria c = it.next();
			if (c instanceof SimpleExpression) {
				SimpleExpression c1 = (SimpleExpression) c;
				// if the operator is '=', and the column name equals the
				// parameter
				if (c1.getOp().equals("=") && c1.getLeft().equals(upColName)) {
					return c1.getValue();
				}
			} else if (c instanceof CriteriaGroup) {
				CriteriaGroup cg = (CriteriaGroup) c;

				// 只有条件组的操作符为AND,才能取得某列的精确值
				if (cg.op.equals(CriteriaGroup.and)) {
					String value = cg.getValueByLeft(left);
					if (value != null) {
						return value;
					}
				}
			}
		}
		return null;
	}

	public String toSqlString() {
		StringBuffer buff = new StringBuffer();
		buff.append("(");

		for (Iterator<Criteria> it = vec.iterator(); it.hasNext();) {
			Criteria c = it.next();
			buff.append(c.toString()).append(" ");
			buff.append(this.op).append(" ");
		}

		// 删除最后的操作符及其前后空格
		if (buff.indexOf(this.op) > 0) {
			buff.delete(buff.length() - this.op.length() - 2, buff.length());
		}

		buff.append(")");

		return buff.toString();
	}

}
