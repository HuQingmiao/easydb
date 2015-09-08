package com.github.walker.easydb.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.UpdateIdentifier;
import com.github.walker.easydb.exception.IllegalParamException;
import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;

/**
 * 
 * This class parses the BaseEntity object to following parts: the class name of
 * the given BaseEntity object, the fileds and methods of the BaseEntity object.
 * 
 * @author HuQingmiao
 * 
 */
public class EntityParser {

	private static Logger log = LogFactory.getLogger(EntityParser.class);

	private Connection conn;

	private String className;// the class name of the entity object

	private HashMap<String, FieldExp> fieldExpMap; // �ȴ����µ�����(fieldName,fieldExp)

	private HashMap<String, Method> fieldMethodMap; // �ȴ����µ����Լ���ӦGet����,map(fieldName,gettingMethod)

	private HashSet<String> pkSet; // �������Ե�����

	// ����װ�ط��ÿյ��ļ����͵���������
	// loads the field name of non-empty big field
	private HashSet<String> bigFieldNameSet;

	private String dbType = null;

	/**
	 * 
	 * @param conn
	 * @param entity
	 * 
	 * @throws IllegalEntityException
	 * @throws DataAccessException
	 */
	public EntityParser(String dbType, Connection conn, BaseEntity entity) throws IllegalEntityException,
			DataAccessException {

		this.dbType = dbType;

		this.conn = conn;
		this.className = entity.getClass().getName();

		String[] pkArray = entity.pk();

		// ������pk()������ָ����������
		// if (pkArray == null || pkArray.length == 0) {
		// throw new IllegalEntityException(
		// IllegalEntityException.NOT_SPECIFY_PK);
		// }
		this.pkSet = new HashSet<String>(Arrays.asList(pkArray));
		this.bigFieldNameSet = new HashSet<String>(2);

		this.fieldExpMap = new HashMap<String, FieldExp>(10);
		this.fieldMethodMap = new HashMap<String, Method>(10);

		// the getting method name of class BaseEntity
		StringBuffer methodName = new StringBuffer();

		try {
			// the columns set which belong to this table
			HashSet<String> columnSet = this.getColumnNames();

			Field[] fields = entity.getClass().getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {

				// the field name of BaseEntity
				String fieldName = fields[i].getName();

				// builds the method name, such as: "getXX"
				methodName.append("get");
				methodName.append(Character.toUpperCase(fieldName.charAt(0)));
				methodName.append(fieldName.substring(1));

				Method method = entity.getClass().getMethod(methodName.toString(), new Class[] {});
				methodName.delete(0, methodName.length());

				// Getting value of the field by the method.
				Object fieldValue = method.invoke(entity, new Object[] {});

				if (fieldValue != null) {
					UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldValue;

					// ֻ�е���������Ҫ����, ���߸����Թ�������ʱ, ���б�Ҫ���з���
					if (idFieldValue.needUpdate() || pkSet.contains(fieldName)) {

						// check the field wether or not having column
						// reflecting
						if (!columnSet.contains(MappingUtil.getColumnName(fieldName))) {
							// ����������ر���û���ҵ���Ӧ��, ���Թ�
							// throw new IllegalEntityException(
							// IllegalEntityException.NOTFOUND_REFLECT_COLUMN,
							// fieldName);
							continue;
						}

						// save the field to map temporarily
						String fieldType = fields[i].getType().getName();
						FieldExp fieldExp = new FieldExp(fieldName, fieldType, fieldValue);
						fieldExpMap.put(fieldName, fieldExp);

						// save the getting method to map temporarily
						fieldMethodMap.put(fieldName, method);

						// ���ڷ��ÿյĴ��ֶ����ԣ� ��Ҫ��������������bigFieldNameSet, �Է������Դ��ֶν��д���
						if (fieldValue instanceof EBinFile || fieldValue instanceof ETxtFile) {

							if (!idFieldValue.isEmpty()) {
								bigFieldNameSet.add(fieldName);
							}
						}

					}// end if (idFieldValue.needUpdate(

				}// end if (fieldValue != null

			}

		} catch (SQLException e) {
			log.error(e.getErrorCode(), e);
			throw new DataAccessException(dbType, e.getErrorCode(), e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		}
	}

	/**
	 * ֻ�����������Ż���õ��˷���
	 * 
	 * @param entityParser
	 *            �״ν�����
	 * @param entity
	 *            ��ȡֵ��entity
	 * 
	 * @throws IllegalEntityException
	 * @throws com.github.walker.easydb.exception.IllegalParamException
	 */
	public EntityParser(EntityParser entityParser, BaseEntity entity) throws IllegalEntityException,
			IllegalParamException {

		// ���ø�����entityParser�е�����
		this.conn = entityParser.conn;
		this.className = entityParser.className;
		this.pkSet = entityParser.pkSet;

		// �����������������漰���ֶ��У� ��˲��ö�������Ը�ֵ
		// this.bigFieldNameSet = new HashSet(2);

		this.fieldExpMap = entityParser.fieldExpMap;
		this.fieldMethodMap = entityParser.fieldMethodMap;

		// ����fieldExpMap�е�fieldֵ, ���Ҽ�¼���ÿյĴ��ֶ�����
		this.retrieveFieldValue(entity);
	}

	// ����fieldExpMap�е�fieldֵ, ���Ҽ�¼���ÿյĴ��ֶ�����
	private void retrieveFieldValue(BaseEntity entity) throws IllegalEntityException, IllegalParamException {
		try {
			for (Iterator<String> it = this.fieldExpMap.keySet().iterator(); it.hasNext();) {
				String fieldName = (String) it.next();
				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				Method method = (Method) fieldMethodMap.get(fieldName);

				Object fieldValue = method.invoke(entity, new Object[] {});

				// ���entity�и����Ե�ֵΪ��, �ͱ�����entity��ֵ�Ĵ��λ�����״ν�����������entity��һ��
				// ��ĳ��ֵ��������Ҫô��ΪNULL, Ҫô����ΪNULL.
				if (fieldValue == null) {
					throw new IllegalParamException(IllegalParamException.ENTITY_ARRAY_NOT_IDENTICAL, fieldName);
				}

				UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldValue;

				// �����е�ĳ��Ҫô����Ҫ���£�Ҫô������Ҫ���£�.
				if (!idFieldValue.needUpdate()) {
					throw new IllegalParamException(IllegalParamException.ENTITY_ARRAY_NOT_IDENTICAL, fieldName);
				}

				// ��������ʱ��������Դ��ֶ��н�������д�룡�����������ÿ�
				if ((fieldValue instanceof EBinFile || fieldValue instanceof ETxtFile)
						&& !((UpdateIdentifier) fieldValue).isEmpty()) {
					throw new IllegalParamException(IllegalParamException.BIGCOLUMN_CANNOT_BATCH, fieldName);
				}

				// set the value to fieldExp
				fieldExp.setFieldValue(fieldValue);
			}

		} catch (IllegalArgumentException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		}

	}

	// Gets the column names of the table which reflected this entity class
	private HashSet<String> getColumnNames() throws SQLException {

		// SQL Constructor
		SqlConstructor sqlConstructor = SqlConstructor.getInstance(dbType);

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			String sql = sqlConstructor.buildGettingMetaSql(MappingUtil.getTableName(this.className));
			log.info("SQL: " + sql);

			stmt = this.conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i <= colCount; i++) {
				set.add(rsmd.getColumnName(i).toUpperCase());
			}
			return set;

		} catch (SQLException e) {
			log.error("", e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public HashMap<String, FieldExp> getFieldExpMap() {
		return fieldExpMap;
	}

	public HashMap<String, Method> getFieldMethodMap() {
		return fieldMethodMap;
	}

	public HashSet<String> getBigFieldNameSet() {
		return bigFieldNameSet;
	}

	public HashSet<String> getPKSet() {
		return pkSet;
	}

	public void myFinalize() {

		try {
			if (pkSet != null) {
				pkSet.clear();
			}

			if (bigFieldNameSet != null) {
				bigFieldNameSet.clear();
			}

			if (fieldExpMap != null) {
				fieldExpMap.clear();
			}

			if (fieldMethodMap != null) {
				fieldMethodMap.clear();
			}

			pkSet = null;
			bigFieldNameSet = null;
			fieldExpMap = null;
			fieldMethodMap = null;

			super.finalize();

		} catch (Throwable e) {
			log.error("", e);
		}
	}
}
