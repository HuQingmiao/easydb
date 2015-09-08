package com.github.walker.easydb.dao.mysql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Vector;

import com.github.walker.easydb.dao.EasyDao;
import com.github.walker.easydb.dao.FieldExp;
import com.github.walker.easydb.dao.ResultAssembler;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.ELString;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.ETimestamp;
import com.github.walker.easydb.exception.IllegalParamException;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.EntityParser;
import com.github.walker.easydb.dao.SqlConstructor;
import com.github.walker.easydb.dao.SqlParamMap;
import com.github.walker.easydb.datatype.EDouble;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.EInteger;
import com.github.walker.easydb.datatype.EString;
import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.FileAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;

/**
 * Mysql database version of EasyDAO.
 * 
 * @author HuQingmiao
 * 
 */
public class MysqlDAO extends EasyDao {

	public MysqlDAO() {
		super();
	}

	public MysqlDAO(String jndiKey) {
		super(jndiKey);
	}

	public String getDBType() {
		return "mysql";
	}

	protected SqlConstructor getSqlConstructor() {
		return MysqlSqlConstructor.getInstance();
	}

	// create and return the OracleRsAssembler object
	protected ResultAssembler getResultAssembler(ResultSet rs, Class<?> entityClass) {
		return new MysqlRsAssembler(rs, entityClass);
	}

	// Process the writing of big data type, such as:BLOB/CLOB
	protected void ProcessBigDataType(EntityParser parser) throws IllegalEntityException, DataAccessException,
			FileAccessException {
		// ����mysql�����ڴ����ر���
	}

	// Process the writing of big data type, such as:BLOB/CLOB
	protected void ProcessBigDataType(EntityParser parser, Criteria criteria) throws IllegalEntityException,
			DataAccessException, FileAccessException {
		// ����mysql�����ڴ����ر���
	}

	// /**
	// * Sets the parameter value to the '?' mark for parameterized sql.
	// */
	// protected void fillParamToMark(PreparedStatement stmt,
	// EntityParser entityParser) throws DataAccessException,
	// FileAccessException, IllegalEntityException {
	//
	// try {
	// //������ķǿ�����
	// HashMap fieldExpMap = entityParser.getFieldExpMap();
	//
	// //the index of parameter '?'
	// int parameterIndex = 1;
	//
	// for (Iterator it = fieldExpMap.keySet().iterator(); it.hasNext();) {
	//
	// String fieldName = (String) it.next();
	// FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
	//
	// Object fieldValue = fieldExp.getFieldValue();
	// UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldValue;
	//
	// //�������Ҫ����, ������
	// if (!idFieldValue.needUpdate()) {
	// continue;
	// }
	//
	// if (idFieldValue instanceof EString) {
	//
	// EString e = (EString) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setString(parameterIndex++, e.toString());
	// } else {
	// stmt.setNull(parameterIndex++, Types.VARCHAR);
	// }
	//
	// } else if (idFieldValue instanceof ELong) {
	// ELong e = (ELong) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setLong(parameterIndex++, e.longValue());
	// } else {
	// stmt.setNull(parameterIndex++, Types.BIGINT);
	// }
	//
	// } else if (idFieldValue instanceof EInteger) {
	//
	// EInteger e = (EInteger) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setInt(parameterIndex++, e.intValue());
	// } else {
	// stmt.setNull(parameterIndex++, Types.INTEGER);
	// }
	//
	// } else if (idFieldValue instanceof EDouble) {
	// EDouble e = (EDouble) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setDouble(parameterIndex++, e.doubleValue());
	// } else {
	// stmt.setNull(parameterIndex++, Types.DOUBLE);
	// }
	//
	// } else if (idFieldValue instanceof ETimestamp) {
	// ETimestamp e = (ETimestamp) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setTimestamp(parameterIndex++, e);
	// } else {
	// stmt.setNull(parameterIndex++, Types.TIMESTAMP);
	// }
	//
	// } else if (idFieldValue instanceof EBinFile) {
	// EBinFile e = (EBinFile) fieldValue;
	// if (e.isEmpty()) {
	// stmt.setNull(parameterIndex++, Types.BLOB);
	// } else {
	// String fileName = e.getCanonicalPath();
	// //write by stream
	// InputStream in = new FileInputStream(fileName);
	// stmt.setBinaryStream(parameterIndex++, in, in
	// .available());
	// }
	// } else if (idFieldValue instanceof ETxtFile) {
	// ETxtFile e = (ETxtFile) fieldValue;
	// if (e.isEmpty()) {
	// stmt.setNull(parameterIndex++, Types.CLOB);
	// } else {
	// String fileName = e.getCanonicalPath();
	// //write by stream
	// InputStream in = new FileInputStream(fileName);
	// stmt.setAsciiStream(parameterIndex++, in, in
	// .available());
	// }
	// } else if (idFieldValue instanceof EFloat) {
	// EFloat e = (EFloat) fieldValue;
	//
	// if (!e.isEmpty()) {//������ǿ�
	// stmt.setFloat(parameterIndex++, e.floatValue());
	// } else {
	// stmt.setNull(parameterIndex++, Types.FLOAT);
	// }
	// } else {
	// throw new IllegalEntityException(
	// IllegalEntityException.DATATYPE_NOT_SUPPORT);
	// }
	// }
	//
	// } catch (SQLException e) {
	// throw new DataAccessException(e);
	// } catch (FileNotFoundException e) {
	// throw new FileAccessException(FileAccessException.FILE_NOTFOUND, e
	// .getMessage());
	// } catch (IOException e) {
	// throw new FileAccessException(FileAccessException.IO_EXCEPTION);
	// }
	// }

	private InputStream in = null;

	/**
	 * Sets the parameter value to the '?' mark for parameterized sql.
	 */
	protected void fillParamToMark(PreparedStatement stmt, Vector<String> indexedFieldVec, EntityParser entityParser)
			throws DataAccessException, FileAccessException, IllegalEntityException {

		try {
			// ������ĵȴ����µ�����
			HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

			for (int i = 0; i < indexedFieldVec.size(); i++) {

				// ������
				String fieldName = (String) indexedFieldVec.get(i);

				// ����ֵ
				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				Object fieldValue = fieldExp.getFieldValue();

				if (fieldValue instanceof EString) {
					EString e = (EString) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.toString());
						stmt.setString(i + 1, e.toString());

					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.VARCHAR);
					}

				} else if (fieldValue instanceof ELong) {
					ELong e = (ELong) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.longValue());
						stmt.setLong(i + 1, e.longValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.BIGINT);
					}

				} else if (fieldValue instanceof EInteger) {

					EInteger e = (EInteger) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.intValue());
						stmt.setInt(i + 1, e.intValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.INTEGER);
					}

				} else if (fieldValue instanceof EDouble) {
					EDouble e = (EDouble) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.doubleValue());
						stmt.setDouble(i + 1, e.doubleValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.DOUBLE);
					}

				} else if (fieldValue instanceof ETimestamp) {
					ETimestamp e = (ETimestamp) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.toString());
						stmt.setTimestamp(i + 1, e);
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.TIMESTAMP);
					}
				} else if (fieldValue instanceof ELString) {
					ELString e = (ELString) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.toString());
						stmt.setString(i + 1, e.toString());

					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.CLOB);
					}

				} else if (fieldValue instanceof EFloat) {
					EFloat e = (EFloat) fieldValue;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + (i + 1) + ": " + e.floatValue());
						stmt.setFloat(i + 1, e.floatValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.FLOAT);
					}
				}

				else if (fieldValue instanceof EBinFile) {
					EBinFile e = (EBinFile) fieldValue;

					if (!e.isEmpty()) {
						String fileName = e.getCanonicalPath();

						// write by stream
						log.debug("prepared to set " + (i + 1) + ": " + fileName);
						in = new FileInputStream(fileName);
						stmt.setBinaryStream(i + 1, in, in.available());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.BLOB);
					}

				} else if (fieldValue instanceof ETxtFile) {
					ETxtFile e = (ETxtFile) fieldValue;

					if (!e.isEmpty()) {
						String fileName = e.getCanonicalPath();

						// write by stream
						log.debug("prepared to set " + (i + 1) + ": " + fileName);
						in = new FileInputStream(fileName);
						stmt.setAsciiStream(i + 1, in, in.available());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.CLOB);
					}
				} else {
					throw new IllegalEntityException(IllegalEntityException.DATATYPE_NOT_SUPPORT, "");
				}
			}

		} catch (SQLException e) {
			log.error(e.getErrorCode(), e);
			throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
		} catch (IOException e) {
			log.error("", e);
			throw new FileAccessException(e.getMessage());
		}
	}

	/**
	 * Set the parameter value to the '?' mark for parameterized sql.
	 * 
	 * 
	 */
	protected void fillParamToMark(PreparedStatement stmt, SqlParamMap map) throws DataAccessException,
			FileAccessException, IllegalParamException {

		try {
			for (int i = 1; i <= map.size(); i++) {
				Object obj = map.get(i);

				if (obj == null) {
					throw new IllegalParamException(IllegalParamException.NOT_INDEXED_PARAM, "ParameterMap�е�" + i
							+ "��λ��û�в���ֵ!");
				}

				if (obj instanceof EString) {
					EString e = (EString) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.toString());
						stmt.setString(i, e.toString());

					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.VARCHAR);
					}
				} else if (obj instanceof String) {
					String e = (String) obj;

					log.debug("prepared to set " + i + ": " + e);
					stmt.setString(i, e.toString());

				} else if (obj instanceof ELong) {
					ELong e = (ELong) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.longValue());
						stmt.setLong(i, e.longValue());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.BIGINT);
					}

				} else if (obj instanceof Long) {
					Long e = (Long) obj;
					log.debug("prepared to set " + i + ": " + e.longValue());
					stmt.setLong(i, e.longValue());

				} else if (obj instanceof EInteger) {
					EInteger e = (EInteger) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.intValue());
						stmt.setInt(i, e.intValue());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.INTEGER);
					}
				} else if (obj instanceof Integer) {
					Integer e = (Integer) obj;

					log.debug("prepared to set " + i + ": " + e.intValue());
					stmt.setInt(i, e.intValue());

				} else if (obj instanceof EDouble) {
					EDouble e = (EDouble) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.doubleValue());
						stmt.setDouble(i, e.doubleValue());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.DOUBLE);
					}
				} else if (obj instanceof Double) {
					Double e = (Double) obj;

					log.debug("prepared to set " + i + ": " + e.doubleValue());
					stmt.setDouble(i, e.doubleValue());

				} else if (obj instanceof ETimestamp) {
					ETimestamp e = (ETimestamp) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.toString());
						stmt.setTimestamp(i, e);
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.TIMESTAMP);
					}

				} else if (obj instanceof ELString) {
					ELString e = (ELString) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.toString());
						stmt.setString(i, e.toString());

					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.CLOB);
					}
				} else if (obj instanceof java.sql.Timestamp) {
					Timestamp e = (Timestamp) obj;
					log.debug("prepared to set " + i + ": " + e.toString());
					stmt.setTimestamp(i, e);

				} else if (obj instanceof java.util.Date) {
					java.util.Date e = (java.util.Date) obj;
					log.debug("prepared to set " + i + ": " + e.toString());
					stmt.setTimestamp(i, new Timestamp(e.getTime()));

				} else if (obj instanceof EFloat) {
					EFloat e = (EFloat) obj;

					if (!e.isEmpty()) {// ������ǿ�
						log.debug("prepared to set " + i + ": " + e.floatValue());
						stmt.setFloat(i, ((EFloat) obj).floatValue());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.FLOAT);
					}
				} else if (obj instanceof Float) {
					Float e = (Float) obj;

					log.debug("prepared to set " + i + ": " + e.floatValue());
					stmt.setFloat(i, e.floatValue());
				}

				else if (obj instanceof EBinFile) {
					EBinFile e = (EBinFile) obj;

					if (!e.isEmpty()) {
						String fileName = e.getCanonicalPath();

						// write by stream
						log.debug("prepared to set " + i + ": " + fileName);
						in = new FileInputStream(fileName);
						stmt.setBinaryStream(i, in, in.available());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.BLOB);
					}

				} else if (obj instanceof ETxtFile) {
					ETxtFile e = (ETxtFile) obj;

					if (!e.isEmpty()) {
						String fileName = e.getCanonicalPath();

						// write by stream
						log.debug("prepared to set " + i + ": " + fileName);
						in = new FileInputStream(fileName);
						stmt.setAsciiStream(i, in, in.available());
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.CLOB);
					}
				} else {
					throw new IllegalParamException(IllegalParamException.NOT_SUPPORTED_PARAM_TYPE, "");
				}
			}

		} catch (SQLException e) {
			log.error(e.getErrorCode(), e);
			throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
		} catch (IOException e) {
			log.error("", e);
			throw new FileAccessException(e.getMessage());
		}
	}

	/**
	 * �������ò���ֵʱ, ��û���������ļ����ж�д, ���ֻ���ڸ��¶�����ɺ�����ͷ���ռ�õ���Դ.
	 * 
	 */
	public void freeRs() {
		try {
			if (in != null) {
				in.close();
			}
			in = null;
		} catch (Throwable e) {
			log.error("", e);
		}
	}

	public void finalize() {
		try {
			freeRs();

			super.finalize();

		} catch (Throwable e) {
			log.error("", e);
		}
	}

}