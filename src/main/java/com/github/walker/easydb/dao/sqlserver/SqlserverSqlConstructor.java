package com.github.walker.easydb.dao.sqlserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.EntityParser;
import com.github.walker.easydb.dao.FieldExp;
import com.github.walker.easydb.dao.SqlConstructor;
import com.github.walker.easydb.dao.SqlParamIndexer;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.UpdateIdentifier;
import com.github.walker.easydb.exception.IllegalEntityException;
import com.github.walker.easydb.exception.IllegalParamException;

/**
 *
 * The sqlserver version of sql constructor.
 *
 * @author HuQingmiao
 *
 */
public class SqlserverSqlConstructor extends SqlConstructor {

	// // 以下关键字是EasyDB专用的, 在客户程序传入的SQL中不得使用.
	// private static final String EASYDB_PAGER_ROW = "EASYDB_PAGER_ROW";
	//
	// private static final String EASYDB_A = "EASYDB_A";
	//
	// private static final String EASYDB_B = "EASYDB_B";

	private static final String EASYDB_COUNT = "EASYDB_COUNT";

	private static SqlserverSqlConstructor instance = new SqlserverSqlConstructor();

	private SqlserverSqlConstructor() {
	}

	public static SqlserverSqlConstructor getInstance() {
		return instance;
	}

	public SqlParamIndexer buildInsert(EntityParser entityParser) throws IllegalParamException {

		StringBuffer forePart = new StringBuffer();// the fore part of the sql
		StringBuffer backPart = new StringBuffer();// the back part of the sql

		// 等待更新的属性
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// 非置空的大字段属性
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// 按'?'在values子句中的先后顺序存储对应属性名
		Vector<String> valuesParamVec = new Vector<String>(10);

		boolean hasValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = it.next();

			String columnName = MappingUtil.getColumnName(fieldName);
			forePart.append(columnName + ',');

			// 如果此属性是非置空的文件类型
			if (bigFieldNameSet.contains(fieldName)) {
				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

				// 对于BLOB/CLOB类型的字段， 写入前需要对其初始化
				if (idFieldValue instanceof EBinFile) {
					backPart.append("EMPTY_BLOB(),");
				} else if (idFieldValue instanceof ETxtFile) {
					backPart.append("EMPTY_CLOB(),");
				}
			} else {
				backPart.append("?,");

				valuesParamVec.add(fieldName);
			}
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

		// 非置空的大字段属性
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			// 如果不是主键属性
			if (!pkSet.contains(fieldName)) {
				update.append(columnName);

				// 如果此属性是非置空的文件类型, 则初始化
				if (bigFieldNameSet.contains(fieldName)) {
					FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
					UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

					// 对于BLOB/CLOB类型的字段， 写入前需要对其初始化
					if (idFieldValue instanceof EBinFile) {
						update.append("=EMPTY_BLOB(),");
					} else if (idFieldValue instanceof ETxtFile) {
						update.append("=EMPTY_CLOB(),");
					}
				} else {
					update.append("=?,");

					indexedParamVec.add(fieldName);
				}
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

		// 非置空的大字段属性
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// 按'?'在SQL中的先后顺序存储对应属性名
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;
		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();

			String columnName = MappingUtil.getColumnName(fieldName);
			sql.append(columnName);

			// 如果此属性是非置空的文件类型
			if (bigFieldNameSet.contains(fieldName)) {

				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

				// 对于BLOB/CLOB类型的字段， 写入前需要对其初始化
				if (idFieldValue instanceof EBinFile) {
					sql.append("=EMPTY_BLOB(),");
				} else if (idFieldValue instanceof ETxtFile) {
					sql.append("=EMPTY_CLOB(),");
				}

			} else {
				sql.append("=?,");

				indexedParamVec.add(fieldName);
			}
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

		return null;
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

	// // 忽略大小写, 检索baseStr中从位置startPos至endPos的子串, 看是否存在子串indexedStr. 返回第一次匹配的位置.
	// private int indexIgloreCase(String baseStr, String indexedStr, int
	// startPos, int endPos) {
	//
	// int baseLength = baseStr.length();
	// int indexedLength = indexedStr.length();
	//
	// for (int i = startPos; i < (baseLength - indexedLength) && i < endPos;
	// i++) {
	//
	// if (indexedStr.equalsIgnoreCase(baseStr.substring(i, i + indexedLength)))
	// {
	// return i;
	// }
	// }
	// return -1;
	// }

	// /**
	// * 去掉左右括号中间的字符
	// *
	// * @param str
	// * 左右括号必须成对出现的字符串
	// */
	// private String trimBracket(String colstr) {
	//
	// String str = colstr.trim();
	// int i = str.indexOf("(");
	// if (i < 0) {
	// return str;
	// }
	//
	// StringBuffer buff = new StringBuffer(str);
	// buff.deleteCharAt(i);// 删除出现的第一个'(', 此时i指向'('后面的那个字符
	//
	// int leftCnt = 1;// 出现的左括号数
	// int rightCnt = 0;// 出现的右括号数
	//
	// while (i < buff.length() && rightCnt < leftCnt) {
	// char ch = buff.charAt(i);
	//
	// buff.deleteCharAt(i);
	//
	// if (ch == '(') {
	// leftCnt++;
	// }
	//
	// if (ch == ')') {
	// rightCnt++;
	// }
	// }
	// String s = buff.toString();
	// buff.delete(0, s.length());
	//
	// return trimBracket(s);
	// }

	public static void main(String[] arsg) {

		SqlserverSqlConstructor osc = new SqlserverSqlConstructor();

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT  L.CREATE_USER,K.KIND_DESC FEE_KIND,W.OPERATOR_NAME ");
		sql.append("            ,(SUM(L.CHARGED_PAGES)) FREE_PAGES,SUM(L.CHARGED_FEE) CHARGED_FEE ");
		sql.append("   FROM FEE_LOG L,FEE_KIND K,WORK_CARD W ");
		sql.append("  WHERE L.FEE_KIND = K.KIND  AND K.TC = ?  AND L.CREATE_USER = W.CARD_CODE");

		String a = osc.buildPageSql(sql.toString(), 1, 11);

		System.out.println(a);

	}
}