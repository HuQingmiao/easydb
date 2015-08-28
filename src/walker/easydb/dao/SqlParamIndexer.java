package walker.easydb.dao;

import java.util.Vector;

/**
 * @author HuQingmiao
 * 
 */
public class SqlParamIndexer {

	// SQL构造器构造的参数化SQL
	private String sql;

	// 按'?'在SQL中的先后顺序存储对应属性名
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
