package walker.easydb.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import walker.easydb.assistant.EasyConfig;
import walker.easydb.assistant.LogFactory;
import walker.easydb.assistant.MappingUtil;
import walker.easydb.criterion.Criteria;
import walker.easydb.dao.mysql.MysqlSqlConstructor;
import walker.easydb.dao.oracle.OracleSqlConstructor;
import walker.easydb.dao.sqlserver.SqlserverSqlConstructor;
import walker.easydb.exception.IllegalEntityException;
import walker.easydb.exception.IllegalParamException;

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

		// 主键属性
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(2);

		// 以主键为删除条件
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

		// 没有找到主键值
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

		// 主键属性
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(2);

		// 以主键为查询条件
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

		// 没有找到主键值
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
	 * @return 包装构造后的SQL
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
	 * @return 包装构造后的SQL
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