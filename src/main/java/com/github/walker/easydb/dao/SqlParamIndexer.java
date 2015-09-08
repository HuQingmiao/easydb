package com.github.walker.easydb.dao;

import java.util.Vector;

/**
 * @author HuQingmiao
 * 
 */
public class SqlParamIndexer {

	// SQL����������Ĳ�����SQL
	private String sql;

	// ��'?'��SQL�е��Ⱥ�˳��洢��Ӧ������
	private Vector<String> indexedFieldVec;

	public SqlParamIndexer(String sql, Vector<String> indexedFieldVec) {
		this.sql = sql;

		this.indexedFieldVec = indexedFieldVec;
	}

	public Vector<String> getIndexedFieldVec() {
		return indexedFieldVec;
	}

	public String getSql() {
		return sql;
	}
}
