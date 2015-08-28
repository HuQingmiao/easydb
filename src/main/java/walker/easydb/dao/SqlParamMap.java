package walker.easydb.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import walker.easydb.datatype.EDouble;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.EInteger;
import walker.easydb.datatype.ELong;
import walker.easydb.datatype.EString;
import walker.easydb.datatype.ETimestamp;
import walker.easydb.exception.IllegalParamException;

/**
 * 本类为参数化SQL装载参数值。
 * 
 * This class loads the parameters for the parameterized sql.
 * 
 * @author HuQingmiao
 * 
 */
public class SqlParamMap {

	private HashMap<Integer, Object> map = null;

	public SqlParamMap() {
		map = new HashMap<Integer, Object>(5);
	}

	/**
	 * 将参数(SQL中的'?')的位置与参数值关联起来。
	 * 
	 * Associates the specified value with the specified key in this map.
	 * 
	 * @param index
	 *            the position of '?' mark in the parameterized sql, it begins
	 *            from 1.
	 * 
	 * @param value
	 *            the value
	 */
	public void put(int index, EString value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, String value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, ELong value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Long value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EInteger value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Integer value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EDouble value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Double value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EFloat value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Float value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, ETimestamp value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Timestamp value) throws IllegalParamException {

		// 参数不合法, 必须大于或等于1！
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Date value) throws IllegalParamException {

		this.put(index, new Timestamp(value.getTime()));
	}

	public void put(int index, int value) throws IllegalParamException {
		this.put(index, new Integer(value));
	}

	public void put(int index, long value) throws IllegalParamException {
		this.put(index, new Long(value));
	}

	public void put(int index, float value) throws IllegalParamException {
		this.put(index, new Float(value));
	}

	public void put(int index, double value) throws IllegalParamException {
		this.put(index, new Double(value));
	}

	/**
	 * 取得指定位置的参数值。
	 * 
	 * Retrieve the value that need to fill the '?' mark in the parameterized
	 * sql.
	 * 
	 * @param index
	 *            the position of '?' mark in the parameterized sql, it begins
	 *            with 1.
	 * 
	 * @return the parameter value on the posision 'index'
	 */
	public Object get(int index) {
		return map.get(new Integer(index));
	}

	/**
	 * 取得参数值的总量。
	 * 
	 * Retrieve the size of map.
	 * 
	 * @return the size of map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * 清空所有参数值.
	 * 
	 * Removes all mappings from this map.
	 * 
	 */
	public void clear() {
		map.clear();
	}

	protected void finalize() {
		map.clear();
		map = null;
	}

}
