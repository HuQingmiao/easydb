package com.github.walker.easydb.criterion;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.github.walker.easydb.exception.CriteriaException;

/**
 * ���ϱ��ʽ��
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

	private String op;// �����

	/**
	 * ����������
	 * 
	 * @param operator
	 *            ��Ԫ�����, �� "AND","OR"
	 */
	public CriteriaGroup(String operator) {
		operator = operator.toUpperCase();

		// CriteriaGroupֻ֧�����������:'AND','OR' ��
		if (!opSet.contains(operator)) {
			new CriteriaException(CriteriaException.NOT_SUPPORTED_OP, "");
		}

		this.op = operator;
	}

	/**
	 * ���������
	 * 
	 * @param c
	 *            ���������ʾ
	 */
	public CriteriaGroup add(Criteria c) throws CriteriaException {
		if (c == this) {
			throw new CriteriaException(CriteriaException.CANNOT_ADD_THIS, "");
		}
		vec.add(c);
		return this;
	}

	/**
	 * ɾ��������
	 * 
	 * @param c
	 *            ���������ʾ
	 */
	public CriteriaGroup remove(Criteria c) throws CriteriaException {
		if (!vec.contains(c)) {
			throw new CriteriaException(CriteriaException.REMOVE_NOTEXIST, "");
		}
		vec.remove(c);
		return this;
	}

	/**
	 * ȡ�ñ��ʽ�в���������ַ�����Ӧ��ֵ
	 * 
	 * @param left
	 *            ���ʽ�в�������ߵ��ַ���
	 * 
	 * @return ���ʽ����ַ�����Ӧ��ֵ
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

				// ֻ��������Ĳ�����ΪAND,����ȡ��ĳ�еľ�ȷֵ
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

		// ɾ�����Ĳ���������ǰ��ո�
		if (buff.indexOf(this.op) > 0) {
			buff.delete(buff.length() - this.op.length() - 2, buff.length());
		}

		buff.append(")");

		return buff.toString();
	}

}
