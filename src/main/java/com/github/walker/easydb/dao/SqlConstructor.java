package com.github.walker.easydb.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.dao.mysql.MysqlSqlConstructor;
import com.github.walker.easydb.dao.oracle.OracleSqlConstructor;
import com.github.walker.easydb.exception.IllegalParamException;
import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.EasyConfig;
import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.sqlserver.SqlserverSqlConstructor;
import com.github.walker.easydb.exception.IllegalEntityException;

/**
 * This class constructs the INSERT/DELETE/QUERY/UPDATE sql, it supports for
 * multiple database.
 * 
 * @author HuQingmiao
 * 
 */
public abstract class SqlConstructor {

	protected Logger log = LogFactory.getLogger(this.getClass());

	/**
	 * 
	 * @param dbType
	 * 
	 * @return SqlConstructor
	 */
	public static SqlConstructor getInstance(String dbType) {

		if ("mysql".equalsIgnoreCase(dbType)) {
			return MysqlSqlConstructor.getInstance();

		} else if ("oracle".equalsIgnoreCase(dbType)) {
			return OracleSqlConstructor.getInstance();

		} else if ("sqlserver".equalsIgnoreCase(dbType)) {
			return SqlserverSqlConstructor.getInstance();
		}

		Logger log = LogFactory.getLogger(LogFactory.MODULE_EASYDB);
		log.error("The database type in the file '" + EasyConfig.CONFIG_FILENAME
				+ "' is not be specified, please config it! ");

		return null;
	}

	public abstract SqlParamIndexer buildInsert(EntityParser entityParser) throws IllegalParamException;

	public abstract SqlParamIndexer buildUpdateByPk(EntityParser entityParser) throws IllegalParamException,
			IllegalEntityException;

	public abstract SqlParamIndexer buildUpdateByCriteria(EntityParser entityParser, Criteria criteria)
			throws IllegalParamException;

	public SqlParamIndexer buildDeleteByPk(EntityParser entityParser) throws IllegalParamException,
			IllegalEntityException {

		StringBuffer sql = new StringBuffer("DELETE FROM ");
		sql.append(MappingUtil.getTableName(entityParser.getClassName()));
		sql.append(" WHERE 1=1");

		// ��������
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// ��'?'��SQL�е��Ⱥ�˳��洢��Ӧ������
		Vector<String> indexedParamVec = new Vector<String>(2);

		// ������Ϊɾ������
		boolean hasPkValue = false;
		for (Iterator<String> it = pkSet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			sql.append(" AND ");
			sql.append(columnName);
			sql.append("=? ");

			indexedParamVec.add(fieldName);
			hasPkValue = true;
		}

		// û���ҵ�����ֵ
		if (!hasPkValue) {
			throw new IllegalParamException(IllegalParamException.NOVALUE_FOR_ENTITY_PK, "");
		}

		return new SqlParamIndexer(sql.toString(), indexedParamVec);
	}

	public String buildDeleteByCriteria(Class<?> entityClass, Criteria criteria) {

		StringBuffer sql = new StringBuffer("DELETE FROM ");
		sql.append(MappingUtil.getTableName(entityClass.getName()));

		String c = criteria.toString().trim();
		if (!"".equals(c)) {
			// build the where clause
			sql.append(" WHERE  ");
			sql.append(criteria.toString());
		}

		return sql.toString();
	}

	public SqlParamIndexer buildSelectByPk(EntityParser entityParser) throws IllegalParamException,
			IllegalEntityException {

		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(MappingUtil.getTableName(entityParser.getClassName()));
		sql.append(" WHERE 1=1");

		// ��������
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// ��'?'��SQL�е��Ⱥ�˳��洢��Ӧ������
		Vector<String> indexedParamVec = new Vector<String>(2);

		// ������Ϊ��ѯ����
		boolean hasPkValue = false;
		for (Iterator<String> it = pkSet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			sql.append(" AND ");
			sql.append(columnName);
			sql.append("=? ");

			indexedParamVec.add(fieldName);
			hasPkValue = true;
		}

		// û���ҵ�����ֵ
		if (!hasPkValue) {
			throw new IllegalParamException(IllegalParamException.NOVALUE_FOR_ENTITY_PK, "");
		}

		return new SqlParamIndexer(sql.toString(), indexedParamVec);
	}

	/**
	 * Builds the select sql For querying one data.
	 */
	public String buildSelectSql(Class<?> entityClass, Criteria criteria) {

		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(MappingUtil.getTableName(entityClass.getName()));

		sql.append(" WHERE  ");
		sql.append(criteria.toString());

		return sql.toString();
	}

	/**
	 * Builds the pager sql by decorating the common business logic sql.
	 * 
	 * @param sql
	 *            the common business logic sql
	 * @param start
	 *            starting position in searching records
	 * @param end
	 *            end position of in searching records
	 * 
	 * @return ��װ������SQL
	 */
	public abstract String buildPageSql(String sql, int start, int end);

	/**
	 * Builds the sql for retrieveing the column meta of the table.
	 * 
	 * @param tableName
	 * 
	 * @return the sql
	 */
	public abstract String buildGettingMetaSql(String tableName);

	/**
	 * Builds the sql which calculate the total records of pager query.
	 * 
	 * @param sql
	 *            the common business logic sql
	 * 
	 * @return ��װ������SQL
	 */
	public abstract String buildCountSql(String sql);
	

	public static void main(String[] args) {

		try {
			SqlConstructor sc = SqlConstructor.getInstance("oracle");
			String sql = "SELECT A.NAME, (TO_DATE('2003-02-04','YYYY-MM-DD')-SYSDATE) S1, (TO_DATE('2ss003-02-04','YYYY-MMss-DD')-SYSDATE) S2 FROM TABLE_X A ";

			System.out.println(sc.buildPageSql(sql, 1, 5));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}