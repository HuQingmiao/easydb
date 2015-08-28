package walker.easydb.dao.sqlserver;

import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Vector;

import walker.easydb.criterion.Criteria;
import walker.easydb.dao.EasyDao;
import walker.easydb.dao.EntityParser;
import walker.easydb.dao.FieldExp;
import walker.easydb.dao.ResultAssembler;
import walker.easydb.dao.SqlConstructor;
import walker.easydb.dao.SqlParamMap;
import walker.easydb.datatype.EBinFile;
import walker.easydb.datatype.EDouble;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.EInteger;
import walker.easydb.datatype.ELString;
import walker.easydb.datatype.ELong;
import walker.easydb.datatype.EString;
import walker.easydb.datatype.ETimestamp;
import walker.easydb.datatype.ETxtFile;
import walker.easydb.exception.DataAccessException;
import walker.easydb.exception.FileAccessException;
import walker.easydb.exception.IllegalEntityException;
import walker.easydb.exception.IllegalParamException;

/**
 * sqlserver database version of EasyDAO.
 * 
 * @author HuQingmiao
 * 
 */
public class SqlserverDAO extends EasyDao {

	// 定义字符串最大写入长度， 超过此长度则用流方式写入
	private final int LENGTHB = 2048;

	public SqlserverDAO() {
		super();
	}

	public SqlserverDAO(String jndiKey) {
		super(jndiKey);
	}

	public String getDBType() {
		return "sqlserver";
	}

	protected SqlConstructor getSqlConstructor() {
		return SqlserverSqlConstructor.getInstance();
	}

	/**
	 * Retrieve the ResultAssembler object which puts the query result into
	 * PageList.
	 */
	protected ResultAssembler getResultAssembler(ResultSet rs, Class<?> entityClass) throws DataAccessException,
			IllegalEntityException {
		return new SqlserverRsAssembler(rs, entityClass);
	}

	// Process the writing of big data type, such as:BLOB/CLOB
	protected void ProcessBigDataType(EntityParser parser) throws IllegalEntityException, DataAccessException,
			FileAccessException {
	}

	// Process the writing of big data type, such as:BLOB/CLOB
	protected void ProcessBigDataType(EntityParser dataParser, Criteria criteria) throws IllegalEntityException,
			DataAccessException, FileAccessException {
	}

	/**
	 * Sets the parameter value to the '?' mark for parameterized sql.
	 */
	protected void fillParamToMark(PreparedStatement stmt, Vector<String> indexedFieldVec, EntityParser entityParser)
			throws DataAccessException, IllegalEntityException {

		try {
			// 解析后的等待更新的属性
			HashMap<String, FieldExp> fieldExpMap = entityParser.getFieldExpMap();

			for (int i = 0; i < indexedFieldVec.size(); i++) {

				// 属性名
				String fieldName = (String) indexedFieldVec.get(i);

				// 属性值
				FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
				Object fieldValue = fieldExp.getFieldValue();

				if (fieldValue instanceof EString) {
					EString e = (EString) fieldValue;

					if (!e.isEmpty()) {// 如果不是空

						if (e.toString().getBytes().length <= LENGTHB) {
							log.debug("prepared to set " + (i + 1) + ": " + e.toString());
							stmt.setString(i + 1, e.toString());
						} else {
							log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, LENGTHB / 4)
									+ "...");

							// 对于超过2K字节左右长度的字符串则用流方式写入, 对于Oracle数据库是必须这样处理的
							Reader reader = new StringReader(e.toString());
							stmt.setCharacterStream((i + 1), reader, e.length());
						}
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.VARCHAR);
					}

				} else if (fieldValue instanceof ELong) {
					ELong e = (ELong) fieldValue;

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + (i + 1) + ": " + e.longValue());
						stmt.setLong(i + 1, e.longValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.BIGINT);
					}

				} else if (fieldValue instanceof EInteger) {

					EInteger e = (EInteger) fieldValue;

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + (i + 1) + ": " + e.intValue());
						stmt.setInt(i + 1, e.intValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.INTEGER);
					}

				} else if (fieldValue instanceof EDouble) {
					EDouble e = (EDouble) fieldValue;

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + (i + 1) + ": " + e.doubleValue());
						stmt.setDouble(i + 1, e.doubleValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.DOUBLE);
					}

				} else if (fieldValue instanceof ETimestamp) {
					ETimestamp e = (ETimestamp) fieldValue;

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + (i + 1) + ": " + e.toString());
						stmt.setTimestamp(i + 1, e);
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.TIMESTAMP);
					}

				} else if (fieldValue instanceof ELString) {

					ELString e = (ELString) fieldValue;

					if (!e.isEmpty()) {// 如果不是空

						if (e.toString().getBytes().length <= LENGTHB) {
							log.debug("prepared to set " + (i + 1) + ": " + e.toString());
							stmt.setString(i + 1, e.toString());
						} else {
							log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, LENGTHB / 4)
									+ "...");

							// 对于超过2K字节左右长度的字符串则用流方式写入, 对于Oracle数据库是必须这样处理的
							Reader reader = new StringReader(e.toString());
							stmt.setCharacterStream((i + 1), reader, e.length());
						}
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.CLOB);
					}

				} else if (fieldValue instanceof EFloat) {
					EFloat e = (EFloat) fieldValue;

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + (i + 1) + ": " + e.floatValue());
						stmt.setFloat(i + 1, e.floatValue());
					} else {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.FLOAT);
					}

				} else if (fieldValue instanceof EBinFile) {
					EBinFile e = (EBinFile) fieldValue;

					// 对于非NULL的文件类型, 在构造SQL时，
					// 已经将对应CLOB字段的参数初始化为EMPTY_CLOB()；
					// 而对于空的文件类型，在此需要把相应字段置空
					if (e.isEmpty()) {
						log.debug("prepared to set " + (i + 1) + ": NULL");
						stmt.setNull(i + 1, Types.BLOB);
					}
				} else if (fieldValue instanceof ETxtFile) {
					ETxtFile e = (ETxtFile) fieldValue;

					// 对于非NULL的文件类型, 在构造SQL时，
					// 已经将对应CLOB字段的参数初始化为EMPTY_CLOB()；
					// 而对于空的文件类型，在此需要把相应字段置空；
					if (e.isEmpty()) {
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
		}
	}

	/**
	 * Set the parameter value to the '?' mark for parameterized sql.
	 * 
	 * 
	 */
	protected void fillParamToMark(PreparedStatement stmt, SqlParamMap map) throws DataAccessException,
			IllegalParamException {

		try {

			for (int i = 1; i <= map.size(); i++) {
				Object obj = map.get(i);

				if (obj == null) {
					throw new IllegalParamException(IllegalParamException.NOT_INDEXED_PARAM, "ParameterMap中第" + i
							+ "个位置没有参数值!");
				}

				if (obj instanceof EString) {
					EString e = (EString) obj;

					if (!e.isEmpty()) {// 如果不是空

						if (e.toString().getBytes().length <= LENGTHB) {
							log.debug("prepared to set " + i + ": " + e.toString());
							stmt.setString(i, e.toString());
						} else {
							log.debug("prepared to set " + i + ": " + e.toString().substring(0, LENGTHB / 4) + "...");

							// 对于超过2K字节左右长度的字符串则用流方式写入, 对于Oracle数据库是必须这样处理的
							Reader reader = new StringReader(e.toString());
							stmt.setCharacterStream(i, reader, e.length());
						}
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.VARCHAR);
					}

				} else if (obj instanceof String) {
					String e = (String) obj;

					if (e.toString().getBytes().length <= LENGTHB) {
						log.debug("prepared to set " + i + ": " + e);
						stmt.setString(i, e);
					} else {
						log.debug("prepared to set " + i + ": " + e.substring(0, LENGTHB / 4) + "...");

						// 对于超过2K字节左右长度的字符串则用流方式写入, 对于Oracle数据库是必须这样处理的
						Reader reader = new StringReader(e.toString());
						stmt.setCharacterStream(i, reader, e.length());
					}

				} else if (obj instanceof ELong) {
					ELong e = (ELong) obj;

					if (!e.isEmpty()) {// 如果不是空
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

					if (!e.isEmpty()) {// 如果不是空
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

					if (!e.isEmpty()) {// 如果不是空
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

					if (!e.isEmpty()) {// 如果不是空
						log.debug("prepared to set " + i + ": " + e.toString());
						stmt.setTimestamp(i, e);
					} else {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.TIMESTAMP);
					}

				} else if (obj instanceof ELString) {
					ELString e = (ELString) obj;

					if (!e.isEmpty()) {// 如果不是空

						if (e.toString().getBytes().length <= LENGTHB) {
							log.debug("prepared to set " + i + ": " + e.toString());
							stmt.setString(i, e.toString());
						} else {
							log.debug("prepared to set " + i + ": " + e.toString().substring(0, LENGTHB / 4) + "...");

							// 对于超过2K字节左右长度的字符串则用流方式写入, 对于Oracle数据库是必须这样处理的
							Reader reader = new StringReader(e.toString());
							stmt.setCharacterStream(i, reader, e.length());
						}
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

					if (!e.isEmpty()) {// 如果不是空
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

					// 对于非NULL的文件类型, 在构造SQL时，
					// 已经将对应CLOB字段的参数初始化为EMPTY_CLOB()；
					// 而对于空的文件类型，在此需要把相应字段置空
					if (e.isEmpty()) {
						log.debug("prepared to set " + i + ": NULL");
						stmt.setNull(i, Types.BLOB);
					}
				} else if (obj instanceof ETxtFile) {
					ETxtFile e = (ETxtFile) obj;

					// 对于非NULL的文件类型, 在构造SQL时，
					// 已经将对应CLOB字段的参数初始化为EMPTY_CLOB()；
					// 而对于空的文件类型，在此需要把相应字段置空；
					if (e.isEmpty()) {
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
		}
	}

	public static void main(String[] args) {

	}
}