package com.github.walker.easydb.dao.oracle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.EntityParser;
import com.github.walker.easydb.dao.FieldExp;
import com.github.walker.easydb.dao.SqlConstructor;
import com.github.walker.easydb.dao.SqlParamIndexer;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.datatype.UpdateIdentifier;
import com.github.walker.easydb.exception.IllegalEntityException;
import com.github.walker.easydb.exception.IllegalParamException;

/**
 * 
 * The oracle version of sql constructor.
 * 
 * @author HuQingmiao
 * 
 */
public class OracleSqlConstructor extends SqlConstructor {

	// ���¹ؼ�����EasyDBר�õ�, �ڿͻ��������SQL�в���ʹ��.
	private static final String EASYDB_PAGER_ROW = "EASYDB_PAGER_ROW";

	private static final String EASYDB_A = "EASYDB_A";

	private static final String EASYDB_B = "EASYDB_B";

	private static final String EASYDB_COUNT = "EASYDB_COUNT";

	private static OracleSqlConstructor instance = new OracleSqlConstructor();

	private OracleSqlConstructor() {
	}

	public static OracleSqlConstructor getInstance() {
		return instance;
	}

	public SqlParamIndexer buildInsert(EntityParser entityParser) throws IllegalParamException {

		StringBuffer forePart = new StringBuffer();// the fore part of the sql
		StringBuffer backPart = new StringBuffer();// the back part of the sql

		// �ȴ����µ�����
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// ���ÿյĴ��ֶ�����
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// ��'?'��values�Ӿ��е��Ⱥ�˳��洢��Ӧ������
		Vector<String> valuesParamVec = new Vector<String>(10);

		boolean hasValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = it.next();

			String columnName = MappingUtil.getColumnName(fieldName);
			forePart.append(columnName + ',');

			// ����������Ƿ��ÿյ��ļ�����
			if (bigFieldNameSet.contains(fieldName)) {
				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

				// ����BLOB/CLOB���͵��ֶΣ� д��ǰ��Ҫ�����ʼ��
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
		// update�Ӿ�
		StringBuffer update = new StringBuffer("UPDATE ");
		update.append(MappingUtil.getTableName(entityParser.getClassName()));
		update.append(" SET ");

		// where�Ӿ�
		StringBuffer where = new StringBuffer(" WHERE 1=1");

		// �ȴ����µ�����
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// ��������
		HashSet<String> pkSet = entityParser.getPKSet();

		if (pkSet.isEmpty()) {
			throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK, "");
		}

		// ���ÿյĴ��ֶ�����
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// ��'?'��SQL�е��Ⱥ�˳��洢��Ӧ������
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;

		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			String columnName = MappingUtil.getColumnName(fieldName);

			// ���������������
			if (!pkSet.contains(fieldName)) {
				update.append(columnName);

				// ����������Ƿ��ÿյ��ļ�����, ���ʼ��
				if (bigFieldNameSet.contains(fieldName)) {
					FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
					UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

					// ����BLOB/CLOB���͵��ֶΣ� д��ǰ��Ҫ�����ʼ��
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
		// û��Ҫ���µ�����
		if (!hasUpdateValue) {
			throw new IllegalParamException(IllegalParamException.ENTITY_NOVALUE, "");
		}
		// ɾ������','
		update.deleteCharAt(update.length() - 1);

		// ������Ϊ��������
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

		// û���ҵ�����ֵ
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

		// �ȴ����µ�����
		HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

		// ���ÿյĴ��ֶ�����
		HashSet<String> bigFieldNameSet = entityParser.getBigFieldNameSet();

		// ��'?'��SQL�е��Ⱥ�˳��洢��Ӧ������
		Vector<String> indexedParamVec = new Vector<String>(12);

		boolean hasUpdateValue = false;
		for (Iterator<String> it = fieldExpMap.keySet().iterator(); it.hasNext();) {
			String fieldName = (String) it.next();

			String columnName = MappingUtil.getColumnName(fieldName);
			sql.append(columnName);

			// ����������Ƿ��ÿյ��ļ�����
			if (bigFieldNameSet.contains(fieldName)) {

				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldExp.getFieldValue();

				// ����BLOB/CLOB���͵��ֶΣ� д��ǰ��Ҫ�����ʼ��
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
		// û��Ҫ���µ�����
		if (!hasUpdateValue) {
			throw new IllegalParamException(IllegalParamException.ENTITY_NOVALUE, "");
		}
		// ɾ������','
		sql.deleteCharAt(sql.length() - 1);

		// �Բ���CriteriaΪ��������
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

		final String select = "SELECT ";
		final String from = " FROM ";

		originSql = originSql.trim();
		int selectPosi = this.indexIgloreCase(originSql, select, 0, originSql.length());

		int fromPosi = this.indexIgloreCase(originSql, from, 0, originSql.length());

		// ȡ��Ҫ��ѯ����, ��originSql��"SELECT" �� "FROM" ֮����ַ���
		String columns = originSql.substring(selectPosi + select.length(), fromPosi);

		// ȥ���������ż����м������, ��SELECT A.NAME,
		// (TO_DATE('2003-02-04','YYYY-MM-DD')-SYSDATE) EXCEED_DAY_CN
		columns = trimBracket(columns);

		// ���ת����Ĳ�ѯ��
		StringBuffer colStr = new StringBuffer();

		StringTokenizer st = new StringTokenizer(columns);

		// ȡ�ø�����ѯ��
		while (st.hasMoreElements()) {
			String col = st.nextToken(",").trim();

			// ������д��б������ı���, �����֮
			int dotPosi = col.indexOf('.');
			if (dotPosi > 0) {
				col = col.substring(dotPosi + 1);
			}

			// ������ж����˱���, ��ȡ������Ϊ����
			int asPosi = col.lastIndexOf(' ');
			if (asPosi > 0) {
				col = col.substring(asPosi + 1).trim();
			}

			colStr.append(EASYDB_A).append('.').append(col).append(',');
		}

		colStr.deleteCharAt(colStr.length() - 1);

		// �����ҳSQL
		StringBuffer pagerSql = new StringBuffer("SELECT ").append(EASYDB_B).append(".*");
		pagerSql.append(" FROM ( SELECT ");
		pagerSql.append(colStr.toString());
		pagerSql.append(", ROWNUM ");
		pagerSql.append(EASYDB_PAGER_ROW);
		pagerSql.append(" FROM (").append(originSql).append(") ");
		pagerSql.append(EASYDB_A);
		pagerSql.append(" WHERE ROWNUM < ").append(endPos);
		pagerSql.append(" ) ").append(EASYDB_B);
		pagerSql.append(" WHERE ");
		pagerSql.append(EASYDB_B).append('.').append(EASYDB_PAGER_ROW);
		pagerSql.append(" >= ").append(startPos);

		colStr.delete(0, colStr.length());

		return pagerSql.toString();
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

		// ȥ��ORDER BY �Ӿ�
		int orderPosi = originSql.indexOf(orderBy);
		if (orderPosi > 0) {
			originSql = originSql.substring(0, orderPosi);
		}

		StringBuffer countSql = new StringBuffer("SELECT COUNT(*) FROM (");
		countSql.append(originSql);
		countSql.append(") ").append(EASYDB_COUNT);

		return countSql.toString();
	}

	// ���Դ�Сд, ����baseStr�д�λ��startPos��endPos���Ӵ�, ���Ƿ�����Ӵ�indexedStr. ���ص�һ��ƥ���λ��.
	private int indexIgloreCase(String baseStr, String indexedStr, int startPos, int endPos) {

		int baseLength = baseStr.length();
		int indexedLength = indexedStr.length();

		for (int i = startPos; i < (baseLength - indexedLength) && i < endPos; i++) {
			if (indexedStr.equalsIgnoreCase(baseStr.substring(i, i + indexedLength))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ȥ�����������м���ַ�
	 * 
	 * @param str
	 *            �������ű���ɶԳ��ֵ��ַ���
	 */
	private String trimBracket(String colstr) {

		String str = colstr.trim();
		int i = str.indexOf("(");
		if (i < 0) {
			return str;
		}

		StringBuffer buff = new StringBuffer(str);
		buff.deleteCharAt(i);// ɾ�����ֵĵ�һ��'(', ��ʱiָ��'('������Ǹ��ַ�

		int leftCnt = 1;// ���ֵ���������
		int rightCnt = 0;// ���ֵ���������

		while (i < buff.length() && rightCnt < leftCnt) {
			char ch = buff.charAt(i);

			buff.deleteCharAt(i);

			if (ch == '(') {
				leftCnt++;
			}
			if (ch == ')') {
				rightCnt++;
			}
		}
		String s = buff.toString();
		buff.delete(0, s.length());

		return trimBracket(s);
	}

	public static void main(String[] arsg) {
		OracleSqlConstructor osc = new OracleSqlConstructor();

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT  L.CREATE_USER,K.KIND_DESC FEE_KIND,W.OPERATOR_NAME ");
		sql.append("            ,(SUM(L.CHARGED_PAGES)) FREE_PAGES,SUM(L.CHARGED_FEE) CHARGED_FEE ");
		sql.append("   FROM FEE_LOG L,FEE_KIND K,WORK_CARD W ");
		sql.append("  WHERE L.FEE_KIND = K.KIND  AND K.TC = ?  AND L.CREATE_USER = W.CARD_CODE");

		String a = osc.buildPageSql(sql.toString(), 1, 11);

		System.out.println(a);

	}
}