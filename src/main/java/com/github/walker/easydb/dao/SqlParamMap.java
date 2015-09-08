package com.github.walker.easydb.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import com.github.walker.easydb.exception.IllegalParamException;
import com.github.walker.easydb.datatype.EDouble;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.EInteger;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.EString;
import com.github.walker.easydb.datatype.ETimestamp;

/**
 * ����Ϊ������SQLװ�ز���ֵ��
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
	 * ������(SQL�е�'?')��λ�������ֵ����������
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

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, String value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, ELong value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Long value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EInteger value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Integer value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EDouble value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Double value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, EFloat value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Float value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, ETimestamp value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
		if (index <= 0) {
			throw new IllegalParamException(IllegalParamException.PARAMETER_MUST_FROM1, "");
		}
		map.put(new Integer(index), value);
	}

	public void put(int index, Timestamp value) throws IllegalParamException {

		// �������Ϸ�, ������ڻ����1��
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
	 * ȡ��ָ��λ�õĲ���ֵ��
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
	 * ȡ�ò���ֵ��������
	 * 
	 * Retrieve the size of map.
	 * 
	 * @return the size of map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * ������в���ֵ.
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
