package walker.easydb.dao.mysql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import walker.easydb.assistant.MappingUtil;
import walker.easydb.criterion.Criteria;
import walker.easydb.dao.EntityParser;
import walker.easydb.dao.FieldExp;
import walker.easydb.dao.SqlConstructor;
import walker.easydb.dao.SqlParamIndexer;
import walker.easydb.exception.IllegalEntityException;
import walker.easydb.exception.IllegalParamException;

/**
 * 
 * The mysql version of sql constructor.
 * 
 * @author HuQingmiao
 * 
 */
public class MysqlSqlConstructor extends SqlConstructor {

	// 以下关键字是EasyDB专用的,在客户端传入的SQL中不得使用.
	private static final String EASYDB_COUNT = "EASYDB_COUNT";

	private static MysqlSqlConstructor instance = new MysqlSqlConstructor();

	private MysqlSqlConstructor() {
	}

	public static MysqlSqlConstructor getInstance() {
		return instance;
	}

	public SqlParamIndexer buildInsert(EntityParser entityParser) throws IllegalParamException {

		StringBuffer forePart = new StringBuffer();// the fore part of the sql
		StringBuffer backPart = new StringBuffer();// the back part of the sql

		// 等待更新的属性
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// 按'?'在values子句中的先后顺序存储对应属性名
		Vector<String> valuesParamVec = new Vector<String>(10);

		boolean hasValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			forePart.append(columnName + ',');
			backPart.append("?,");

			valuesParamVec.add(fieldName);
			hasValue = true;
		}

		if (hasValue) {
			forePart.deleteCharAt(forePart.length() - 1);
			backPart.deleteCharAt(backPart.length() - 1);
		} else {
			throw new IllegalParamException(IllegalParamException.ENTITY_NOVALUE, "");
		}

		forePart.insert(0, "INSERT INTO " + MappingUtil.getTableName(entityParser.getClassName()) + " (");
		forePart.append(") ");

		backPart.insert(0, "VALUES (");
		backPart.append(") ");

		String sql = forePart.append(backPart).toString();

		return new SqlParamIndexer(sql, valuesParamVec);
	}

	public SqlParamIndexer buildUpdateByPk(EntityParser entityParser) throws IllegalParamException,
			IllegalEntityException {
		// update子句
		StringBuffer update = new StringBuffer("UPDATE ");
		update.append(MappingUtil.getTableName(entityParser.getClassName()));
		update.append(" SET ");

		// where子句
		StringBuffer where = new StringBuffer(" WHERE 1=1");

		// 等待更新的属性
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// 主键属性
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			// 如果不是主键属性
			if (!pkSet.contains(fieldName)) {
				update.append(columnName);
				update.append("=?,");

				indexedParamVec.add(fieldName);
				hasUpdateValue = true;
			}
		}
		// 没有要更新的属性
		if (!hasUpdateValue) {
			throw new IllegalParamException(IllegalParamException.ENTITY_NOVALUE, "");
		}
		// 删除最后的','
		update.deleteCharAt(update.length() - 1);

		// 以主键为更新条件
		boolean hasPkValue = false;
		for (Iterator<String> it = pkSet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			where.append(" AND ");
			where.append(columnName);
			where.append("=? ");

			indexedParamVec.add(fieldName);
			hasPkValue = true;
		}

		// 没有找到主键值
		if (!hasPkValue) {
			throw new IllegalParamException(IllegalParamException.NOVALUE_FOR_ENTITY_PK, "");
		}

		// combom the where clause
		update.append(where.toString());

		return new SqlParamIndexer(update.toString(), indexedParamVec);
	}

	public SqlParamIndexer buildUpdateByCriteria(EntityParser entityParser, Criteria criteria)
			throws IllegalParamException {

		StringBuffer sql = new StringBuffer("UPDATE ");
		sql.append(MappingUtil.getTableName(entityParser.getClassName()));
		sql.append(" SET ");

		// 等待更新的属性
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;
		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			sql.append(columnName);
			sql.append("=?,");

			indexedParamVec.add(fieldName);
			hasUpdateValue = true;
		}
		// 没有要更新的属性
		if (!hasUpdateValue) {
			throw new IllegalParamException(IllegalParamException.ENTITY_NOVALUE, "");
		}
		// 删除最后的','
		sql.deleteCharAt(sql.length() - 1);

		// 以参数Criteria为更新条件
		String c = criteria.toString().trim();
		if (!"".equals(c)) {
			// build the where clause
			sql.append(" WHERE  ");
			sql.append(criteria.toString());
		}

		return new SqlParamIndexer(sql.toString(), indexedParamVec);
	}

	/**
	 * Builds the pager sql by decorating the common business logic sql.
	 * 
	 * @param originSql
	 *            the common business logic sql
	 * @param start
	 *            starting position in searching records
	 * @param end
	 *            end position of in searching records
	 * 
	 * @return
	 */
	public String buildPageSql(String originSql, int startPos, int endPos) {

		// SELECT * FROM book b order by book_id limit 4,2
		return originSql + " LIMIT " + (startPos - 1) + ", " + (endPos - startPos);
	}

	/**
	 * Builds the sql for retrieveing the column meta of the table.
	 * 
	 * @param tableName
	 * 
	 * @return the sql
	 */
	public String buildGettingMetaSql(String tableName) {
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(tableName);
		sql.append(" A WHERE 1 = 2");

		return sql.toString();
	}

	/**
	 * Builds the sql which calculate the total records of pager query.
	 * 
	 * @param originSql
	 *            the common business logic sql
	 * 
	 * @return
	 */
	public String buildCountSql(String originSql) {

		final String orderBy = " ORDER BY ";

		// 去掉ORDER BY 子句
		int orderPosi = originSql.indexOf(orderBy);
		if (orderPosi > 0) {
			originSql = originSql.substring(0, orderPosi);
		}

		StringBuffer countSql = new StringBuffer("SELECT COUNT(*) FROM (");
		countSql.append(originSql);
		countSql.append(") ").append(EASYDB_COUNT);

		return countSql.toString();
	}

}
