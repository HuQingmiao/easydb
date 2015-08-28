package walker.easydb.criterion;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import walker.easydb.assistant.LogFactory;
import walker.easydb.datatype.EDouble;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.EInteger;
import walker.easydb.datatype.ELong;
import walker.easydb.datatype.EString;
import walker.easydb.exception.CriteriaException;
import walker.easydb.exception.IllegalParamException;

/**
 * 原子表达式类
 * 
 * @author HuQingmiao
 */
public class Exp {

	protected static Logger log = LogFactory.getLogger(Exp.class);

	private static HashSet<Class<?>> typeSet = new HashSet<Class<?>>(25);

	static {
		typeSet.add(Integer.class);
		typeSet.add(Long.class);
		typeSet.add(Float.class);
		typeSet.add(Double.class);
		typeSet.add(String.class);

		typeSet.add(EInteger.class);
		typeSet.add(ELong.class);
		typeSet.add(EFloat.class);
		typeSet.add(EDouble.class);
		typeSet.add(EString.class);

		typeSet.add(Integer[].class);
		typeSet.add(Long[].class);
		typeSet.add(Float[].class);
		typeSet.add(Double[].class);
		typeSet.add(String[].class);

		typeSet.add(EInteger[].class);
		typeSet.add(ELong[].class);
		typeSet.add(EFloat[].class);
		typeSet.add(EDouble[].class);
		typeSet.add(EString[].class);
	}

	/**
	 * Apply a "equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression eq(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, "=");
	}

	public static SimpleExpression eq(String propertyName, int value) throws IllegalParamException {
		return eq(propertyName, new Integer(value));
	}

	public static SimpleExpression eq(String propertyName, long value) throws IllegalParamException {
		return eq(propertyName, new Long(value));
	}

	public static SimpleExpression eq(String propertyName, float value) throws IllegalParamException {
		return eq(propertyName, new Float(value));
	}

	public static SimpleExpression eq(String propertyName, double value) throws IllegalParamException {
		return eq(propertyName, new Double(value));
	}

	public static SimpleExpression eq(String propertyName, char value) throws IllegalParamException {
		return eq(propertyName, new Character(value));
	}

	/**
	 * Apply a "not equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ne(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, "<>");
	}

	public static SimpleExpression ne(String propertyName, int value) throws IllegalParamException {
		return ne(propertyName, new Integer(value));
	}

	public static SimpleExpression ne(String propertyName, long value) throws IllegalParamException {
		return ne(propertyName, new Long(value));
	}

	public static SimpleExpression ne(String propertyName, float value) throws IllegalParamException {
		return ne(propertyName, new Float(value));
	}

	public static SimpleExpression ne(String propertyName, double value) throws IllegalParamException {
		return ne(propertyName, new Double(value));
	}

	public static SimpleExpression ne(String propertyName, char value) throws IllegalParamException {
		return ne(propertyName, new Character(value));
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static LikeExpression like(String propertyName, String value) {
		return new LikeExpression(propertyName, value);
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static LikeExpression like(String propertyName, EString value) {
		return new LikeExpression(propertyName, value.toString());
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static LikeExpression like(String propertyName, String value, char escapeChar) {
		return new LikeExpression(propertyName, value, escapeChar);
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static LikeExpression like(String propertyName, EString value, Character escapeChar) {
		return new LikeExpression(propertyName, value.toString(), escapeChar);
	}

	/**
	 * Apply a "greater than" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression gt(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, ">");
	}

	public static SimpleExpression gt(String propertyName, int value) throws IllegalParamException {
		return gt(propertyName, new Integer(value));
	}

	public static SimpleExpression gt(String propertyName, long value) throws IllegalParamException {
		return gt(propertyName, new Long(value));
	}

	public static SimpleExpression gt(String propertyName, float value) throws IllegalParamException {
		return gt(propertyName, new Float(value));
	}

	public static SimpleExpression gt(String propertyName, double value) throws IllegalParamException {
		return gt(propertyName, new Double(value));
	}

	public static SimpleExpression gt(String propertyName, char value) throws IllegalParamException {
		return gt(propertyName, new Character(value));
	}

	/**
	 * Apply a "less than" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression lt(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, "<");
	}

	public static SimpleExpression lt(String propertyName, int value) throws IllegalParamException {
		return lt(propertyName, new Integer(value));
	}

	public static SimpleExpression lt(String propertyName, long value) throws IllegalParamException {
		return lt(propertyName, new Long(value));
	}

	public static SimpleExpression lt(String propertyName, float value) throws IllegalParamException {
		return lt(propertyName, new Float(value));
	}

	public static SimpleExpression lt(String propertyName, double value) throws IllegalParamException {
		return lt(propertyName, new Double(value));
	}

	public static SimpleExpression lt(String propertyName, char value) throws IllegalParamException {
		return lt(propertyName, new Character(value));
	}

	/**
	 * Apply a "less than or equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression le(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, "<=");
	}

	public static SimpleExpression le(String propertyName, int value) throws IllegalParamException {
		return le(propertyName, new Integer(value));
	}

	public static SimpleExpression le(String propertyName, long value) throws IllegalParamException {
		return le(propertyName, new Long(value));
	}

	public static SimpleExpression le(String propertyName, float value) throws IllegalParamException {
		return le(propertyName, new Float(value));
	}

	public static SimpleExpression le(String propertyName, double value) throws IllegalParamException {
		return le(propertyName, new Double(value));
	}

	public static SimpleExpression le(String propertyName, char value) throws IllegalParamException {
		return le(propertyName, new Character(value));
	}

	/**
	 * Apply a "greater than or equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ge(String propertyName, Object value) throws IllegalParamException {
		if (!typeSet.contains(value.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}
		return new SimpleExpression(propertyName, value, ">=");
	}

	public static SimpleExpression ge(String propertyName, int value) throws IllegalParamException {
		return ge(propertyName, new Integer(value));
	}

	public static SimpleExpression ge(String propertyName, long value) throws IllegalParamException {
		return ge(propertyName, new Long(value));
	}

	public static SimpleExpression ge(String propertyName, float value) throws IllegalParamException {
		return ge(propertyName, new Float(value));
	}

	public static SimpleExpression ge(String propertyName, double value) throws IllegalParamException {
		return ge(propertyName, new Double(value));
	}

	public static SimpleExpression ge(String propertyName, char value) throws IllegalParamException {
		return ge(propertyName, new Character(value));
	}

	/**
	 * Apply a "between" constraint to the named property
	 * 
	 * @param propertyName
	 * @param lo
	 *            value
	 * @param hi
	 *            value
	 * @return Criterion
	 */
	public static BetweenExpression between(String propertyName, Object lo, Object hi) throws IllegalParamException {
		if (!typeSet.contains(lo.getClass()) || !typeSet.contains(hi.getClass())) {
			throw new IllegalParamException(IllegalParamException.EXP_NOT_SUPPORTED_TYPE, "");
		}

		return new BetweenExpression(propertyName, lo, hi);
	}

	public static BetweenExpression between(String propertyName, int lo, int hi) throws IllegalParamException {
		return between(propertyName, new Integer(lo), new Integer(hi));
	}

	public static BetweenExpression between(String propertyName, long lo, long hi) throws IllegalParamException {
		return between(propertyName, new Long(lo), new Long(hi));
	}

	public static BetweenExpression ge(String propertyName, float lo, float hi) throws IllegalParamException {
		return between(propertyName, new Float(lo), new Float(hi));
	}

	public static BetweenExpression ge(String propertyName, double lo, double hi) throws IllegalParamException {
		return between(propertyName, new Double(lo), new Double(hi));
	}

	public static BetweenExpression ge(String propertyName, char lo, char hi) throws IllegalParamException {
		return between(propertyName, String.valueOf(lo), String.valueOf(hi));
	}

	/**
	 * Apply an "in" constraint to the named property
	 * 
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Criteria in(String propertyName, Object[] values) {
		return new InExpression(propertyName, values);
	}

	public static Criteria in(String propertyName, int[] values) {

		Integer[] v = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = new Integer(values[i]);
		}
		return new InExpression(propertyName, v);
	}

	public static Criteria in(String propertyName, long[] values) {
		Long[] v = new Long[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = new Long(values[i]);
		}
		return new InExpression(propertyName, v);
	}

	public static Criteria in(String propertyName, char[] values) {
		String[] v = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = String.valueOf(values[i]);
		}
		return new InExpression(propertyName, v);
	}

	public static Criteria in(String propertyName, double[] values) {
		Double[] v = new Double[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = new Double(values[i]);
		}
		return new InExpression(propertyName, v);
	}

	public static Criteria in(String propertyName, float[] values) {
		Float[] v = new Float[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = new Float(values[i]);
		}
		return new InExpression(propertyName, v);
	}

	/**
	 * Apply an "in" constraint to the named property
	 * 
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Criteria in(String propertyName, Collection<?> values) {
		return new InExpression(propertyName, values.toArray());
	}

	/**
	 * Apply an "is null" constraint to the named property
	 * 
	 * @return Criterion
	 */
	public static Criteria isNull(String propertyName) {
		return new NullExpression(propertyName);
	}

	/**
	 * Apply an "is not null" constraint to the named property
	 * 
	 * @return Criterion
	 */
	public static Criteria isNotNull(String propertyName) {
		return new NotNullExpression(propertyName);
	}

	/**
	 * Return the conjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static CriteriaGroup and(Criteria lhs, Criteria rhs) {
		try {
			CriteriaGroup cg = new CriteriaGroup(CriteriaGroup.and);
			cg.add(lhs).add(rhs);
			return cg;
		} catch (CriteriaException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Return the disjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static CriteriaGroup or(Criteria lhs, Criteria rhs) {
		try {
			CriteriaGroup cg = new CriteriaGroup(CriteriaGroup.or);
			cg.add(lhs).add(rhs);
			return cg;
		} catch (CriteriaException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	

	public static void main(String[] args) {
		// gender = 'F' and (status=1 or (status=2 and name like '%王')) and type
		// in (2,3)

		// Criterion c1 = Expression.eq("gender","F");
		//
		// Criterion c2 =
		// Expression.and(Expression.eq("status","2"),Expression.like("status","%王"));
		//
		// Expression.or(c1,c2);
		//
		// Expression.eq("status",1).or(Expression.eq("status",2).and(Expression.like("status","%王")))
		//

		try {

			// // 对名称以"永远的UNIX"开头的且价格>=70.0元的书进行更新
			// Criteria c1 = Exp.like("title", "永远的UNIX%");
			// Criteria c2 = Exp.eq("cost", new Double(70));
			// Criteria c3 = Exp.and(c1,c2);
			//
			// CriteriaGroup cg = new CriteriaGroup(CriteriaGroup.and);
			// try {
			// cg.add(c1).add(c2);
			// } catch (CriteriaException e1) {
			// e1.printStackTrace();
			// }
			//
			// System.out.println(c3.toString());
			// System.out.println(cg.toString());
			// System.out.println(">>"+c3.getValueByLeft("cost"));
			// System.out.println(">>"+cg.getValueByLeft("cost"));

			Criteria c5 = Exp.eq("status", 1);
			Criteria c6 = Exp.eq("status", 2);
			Criteria c7 = Exp.eq("name", "%王");
			Criteria c8 = Exp.eq("to_char(a.createTime,'yyyy-MM-dd')", 2);
			Criteria c10 = Exp.isNotNull("gender");
			CriteriaGroup cg2 = new CriteriaGroup(CriteriaGroup.and);
			try {
				cg2.add(c10).add(Exp.or(c5, Exp.and(c6, c7))).add(c8);
			} catch (CriteriaException e2) {
				e2.printStackTrace();
			}

			System.out.println(Exp.and(Exp.and(c10, Exp.or(c5, Exp.and(c6, c7))), c8));
			System.out.println(Exp.and(Exp.and(c10, Exp.or(c5, Exp.and(c6, c7))), c8).getValueByLeft("status"));

			System.out.println(Exp.and(Exp.or(c5, Exp.and(c6, c7)), c8));
			System.out.println(Exp.and(Exp.or(c5, Exp.and(c6, c7)), c8).getValueByLeft("status"));
			//
			// System.out.println(cg2.toSqlString());
			//
			// System.out.println("S"+Exp.and(c6,c7).getValueByLeft("name"));
			// System.out.println("S"+Exp.and(c5,Exp.and(c6,c7)).getValueByLeft("name"));

			String a = "~!@#$%^&*()_+<'>?/\''.,";

			a = a.replace("\'", "''");

			System.out.println(a);
		} catch (IllegalParamException e) {
			e.printStackTrace();
		}

	}
}
