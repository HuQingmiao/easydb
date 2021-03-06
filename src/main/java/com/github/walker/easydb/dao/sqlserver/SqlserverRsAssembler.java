package com.github.walker.easydb.dao.sqlserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import com.github.walker.easydb.assistant.DateTimeUtil;
import com.github.walker.easydb.dao.BaseEntity;
import com.github.walker.easydb.dao.PageList;
import com.github.walker.easydb.dao.ResultAssembler;
import com.github.walker.easydb.dao.SettingMethodExp;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.FileAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.EDouble;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.EInteger;
import com.github.walker.easydb.datatype.ELString;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.EString;
import com.github.walker.easydb.datatype.ETimestamp;
import com.github.walker.easydb.datatype.ETxtFile;

/**
 *
 * The sqlserver database version of ResultAssembler.
 *
 * @author HuQingmiao
 *
 */
class SqlserverRsAssembler extends ResultAssembler {

	public String getDBType() {
		return "sqlserver";
	}

	protected SqlserverRsAssembler(ResultSet rs, Class<?> entityClass) {
		super(rs, entityClass);
	}

	/**
	 * Build ArrayList object with query result.
	 *
	 * @return
	 *
	 * @throws com.github.walker.easydb.exception.IllegalEntityException
	 * @throws com.github.walker.easydb.exception.DataAccessException
	 */
	@SuppressWarnings("unchecked")
	protected void buildList() throws IllegalEntityException, DataAccessException, FileAccessException {

		// 用于处理BLOB/CLOB的输入输出流
		OutputStream out = null;
		InputStream in = null;

		try {
			this.rsList = new PageList();

			while (rs.next()) {

				// Get the entity instance by the constructor and empty
				// parameter
				BaseEntity entity = (BaseEntity) cons.newInstance(new Object[] {});

				for (Iterator<Integer> it = indexMethodExpMap.keySet().iterator(); it.hasNext();) {
					Integer col = it.next();
					SettingMethodExp mdExp = (SettingMethodExp) indexMethodExpMap.get(col);

					Method method = mdExp.getMethod();
					String paraType = mdExp.getParamType();

					if ("walker.easydb.datatype.EString".equals(paraType)) {
						String v = rs.getString(col);
						EString ev = (v == null) ? new EString() : new EString(v);
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ELong".equals(paraType)) {
						String v = rs.getString(col);
						ELong ev = (v == null) ? new ELong() : new ELong(rs.getLong(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.EInteger".equals(paraType)) {
						String v = rs.getString(col);
						EInteger ev = (v == null) ? new EInteger() : new EInteger(rs.getInt(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.EDouble".equals(paraType)) {
						String v = rs.getString(col);
						EDouble ev = (v == null) ? new EDouble() : new EDouble(rs.getDouble(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ETimestamp".equals(paraType)) {
						Timestamp v = rs.getTimestamp(col);
						ETimestamp ev = (v == null) ? new ETimestamp() : new ETimestamp(v.getTime());
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ELString".equals(paraType)) {
						Clob clob = rs.getClob(col);

						if (clob == null) {
							method.invoke(entity, new Object[] { new ELString() });
						}

					} else if ("walker.easydb.datatype.EBinFile".equals(paraType)) {
						Blob blob = rs.getBlob(col);

						if (blob == null) {
							method.invoke(entity, new Object[] { new EBinFile() });

						} else {

							// 如果没有在easydb.property中配置文件存放目录, 则取java临时目录
							String tmpDir = BASE_FILE_DIRC;
							if (tmpDir == null) {
								Properties env = System.getProperties();
								tmpDir = env.getProperty("java.io.tmpdir");
							}

							// if the #{tmpDir} not exists, created
							File dir = new File(tmpDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							// Ouput the file to the directory #{tmpDir},
							// and build the file name: tmpDir + entityName +'#'
							// + fieldName
							StringBuffer fileName = new StringBuffer(tmpDir);

							// entity name
							String className = entity.getClass().getName();
							int index = className.lastIndexOf('.');
							fileName.append(className.substring(index + 1));

							fileName.append("#");

							// field name
							fileName.append(method.getName().substring("Set".length()));
							fileName.append(DateTimeUtil.format(new Date(), "HHmmss"));

							File file = new EBinFile(fileName.toString());
							fileName.delete(0, fileName.length());

							if (file.exists()) {
								file.delete();
							}

							// get input stream from clob
							in = blob.getBinaryStream();
							byte[] b = new byte[(int) blob.length()];

							// build output stream
							out = new FileOutputStream(file);
							int len = 0;
							while ((len = in.read(b)) != -1)
								out.write(b, 0, len);

							in.close();
							out.close();

							method.invoke(entity, new Object[] { file });
						}
					} else if ("walker.easydb.datatype.ETxtFile".equals(paraType)) {
						Clob clob = rs.getClob(col);

						if (clob == null) {
							method.invoke(entity, new Object[] { new ETxtFile() });
						} else {

							// 如果没有在easydb.property中配置文件存放目录, 则取java临时目录
							String tmpDir = BASE_FILE_DIRC;
							if (tmpDir == null) {
								Properties env = System.getProperties();
								tmpDir = env.getProperty("java.io.tmpdir");
							}

							// if the #{tmpDir} not exists, created
							File dir = new File(tmpDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							// Ouput the file to the directory #{tmpDir},
							// and build the file name: tmpDir + entityName +'#'
							// + fieldName
							StringBuffer fileName = new StringBuffer(tmpDir);

							// entity name
							String className = entity.getClass().getName();
							int index = className.lastIndexOf('.');
							fileName.append(className.substring(index + 1));

							fileName.append("#");

							// field name
							fileName.append(method.getName().substring("Set".length()));
							fileName.append(DateTimeUtil.format(new Date(), "HHmmss"));

							File file = new ETxtFile(fileName.toString());
							fileName.delete(0, fileName.length());

							if (file.exists()) {
								file.delete();
							}

							// get input stream from clob
							in = clob.getAsciiStream();
							byte[] b = new byte[(int) clob.length()];

							// build output stream
							out = new FileOutputStream(file);
							int len = 0;
							while ((len = in.read(b)) != -1)
								out.write(b, 0, len);

							in.close();
							out.close();

							method.invoke(entity, new Object[] { file });
						}
					} else if ("walker.easydb.datatype.EFloat".equals(paraType)) {

						String v = rs.getString(col);
						EFloat ev = (v == null) ? new EFloat() : new EFloat(rs.getFloat(col));
						method.invoke(entity, new Object[] { ev });

					} else {
						// 不支持的数据类型
						throw new IllegalEntityException(IllegalEntityException.DATATYPE_NOT_SUPPORT, paraType);
					}
					paraType = null;
				}
				rsList.add(entity);
			}

		} catch (SQLException e) {
			log.error(e.getErrorCode(), e);
			throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
			throw new IllegalEntityException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new FileAccessException(FileAccessException.FILE_NOTFOUND,e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new FileAccessException(FileAccessException.IO_EXCEPTION,e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				log.error("", e1);
			}
		}
	}

	/**
	 * Load data to the specified entity
	 *
	 */
	protected boolean loadData(BaseEntity entity) throws IllegalEntityException, DataAccessException,
			FileAccessException {

		// 用于处理BLOB/CLOB的输入输出流
		OutputStream out = null;
		InputStream in = null;

		try {
			if (rs.next()) {
				for (Iterator<Integer> it = indexMethodExpMap.keySet().iterator(); it.hasNext();) {
					Integer col = it.next();
					SettingMethodExp mdExp = (SettingMethodExp) indexMethodExpMap.get(col);

					Method method = mdExp.getMethod();
					String paraType = mdExp.getParamType();

					if ("walker.easydb.datatype.EString".equals(paraType)) {
						String v = rs.getString(col);
						EString ev = (v == null) ? new EString() : new EString(v);
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ELong".equals(paraType)) {
						String v = rs.getString(col);
						ELong ev = (v == null) ? new ELong() : new ELong(rs.getLong(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.EInteger".equals(paraType)) {
						String v = rs.getString(col);
						EInteger ev = (v == null) ? new EInteger() : new EInteger(rs.getInt(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.EDouble".equals(paraType)) {
						String v = rs.getString(col);
						EDouble ev = (v == null) ? new EDouble() : new EDouble(rs.getDouble(col));
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ETimestamp".equals(paraType)) {
						Timestamp v = rs.getTimestamp(col);
						ETimestamp ev = (v == null) ? new ETimestamp() : new ETimestamp(v.getTime());
						method.invoke(entity, new Object[] { ev });

					} else if ("walker.easydb.datatype.ELString".equals(paraType)) {
						Clob clob = rs.getClob(col);

						if (clob == null) {
							method.invoke(entity, new Object[] { new ELString() });
						}

					} else if ("walker.easydb.datatype.EBinFile".equals(paraType)) {
						Blob blob = rs.getBlob(col);

						if (blob == null) {
							method.invoke(entity, new Object[] { new EBinFile() });

						} else {

							// 如果没有在easydb.property中配置文件存放目录, 则取java临时目录
							String tmpDir = BASE_FILE_DIRC;
							if (tmpDir == null) {
								Properties env = System.getProperties();
								tmpDir = env.getProperty("java.io.tmpdir");
							}

							// if the #{tmpDir} not exists, created
							File dir = new File(tmpDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							// Ouput the file to the directory #{tmpDir},
							// and build the file name: tmpDir + entityName +'#'
							// + fieldName
							StringBuffer fileName = new StringBuffer(tmpDir);

							// entity name
							String className = entity.getClass().getName();
							int index = className.lastIndexOf('.');
							fileName.append(className.substring(index + 1));

							fileName.append("#");

							// field name
							fileName.append(method.getName().substring("Set".length()));
							fileName.append(DateTimeUtil.format(new Date(), "HHmmss"));

							File file = new EBinFile(fileName.toString());
							fileName.delete(0, fileName.length());

							if (file.exists()) {
								file.delete();
							}

							// get input stream from clob
							in = blob.getBinaryStream();
							byte[] b = new byte[(int) blob.length()];

							// build output stream
							out = new FileOutputStream(file);
							int len = 0;
							while ((len = in.read(b)) != -1)
								out.write(b, 0, len);

							in.close();
							out.close();

							method.invoke(entity, new Object[] { file });
						}
					} else if ("walker.easydb.datatype.ETxtFile".equals(paraType)) {
						Clob clob = rs.getClob(col);

						if (clob == null) {
							method.invoke(entity, new Object[] { new ETxtFile() });
						} else {

							// 如果没有在easydb.property中配置文件存放目录, 则取java临时目录
							String tmpDir = BASE_FILE_DIRC;
							if (tmpDir == null) {
								Properties env = System.getProperties();
								tmpDir = env.getProperty("java.io.tmpdir");
							}

							// if the #{tmpDir} not exists, created
							File dir = new File(tmpDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							// Ouput the file to the directory #{tmpDir},
							// and build the file name: tmpDir + entityName +'#'
							// + fieldName
							StringBuffer fileName = new StringBuffer(tmpDir);

							// entity name
							String className = entity.getClass().getName();
							int index = className.lastIndexOf('.');
							fileName.append(className.substring(index + 1));

							fileName.append("#");

							// field name
							fileName.append(method.getName().substring("Set".length()));
							fileName.append(DateTimeUtil.format(new Date(), "HHmmss"));

							File file = new ETxtFile(fileName.toString());
							fileName.delete(0, fileName.length());

							if (file.exists()) {
								file.delete();
							}

							// get input stream from clob
							in = clob.getAsciiStream();
							byte[] b = new byte[(int) clob.length()];

							// build output stream
							out = new FileOutputStream(file);
							int len = 0;
							while ((len = in.read(b)) != -1)
								out.write(b, 0, len);

							in.close();
							out.close();

							method.invoke(entity, new Object[] { file });
						}
					} else if ("walker.easydb.datatype.EFloat".equals(paraType)) {

						String v = rs.getString(col);
						EFloat ev = (v == null) ? new EFloat() : new EFloat(rs.getFloat(col));
						method.invoke(entity, new Object[] { ev });

					} else {
						// 不支持的数据类型
						throw new IllegalEntityException(IllegalEntityException.DATATYPE_NOT_SUPPORT, paraType);
					}
					paraType = null;
				}

				// 查询出来的结果数大于1，请检查WHERE子句中的查询条件！
				if (rs.next()) {
					throw new DataAccessException(DataAccessException.RS_COUNT_GREATER1,"");
				}

				return true;
			}// end if(rs.next())

			// 没找到与主键匹配的数据,载入失败
			return false;

		} catch (SQLException e) {
			log.error(e.getErrorCode(), e);
			throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("", e);
			throw new IllegalEntityException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.error("", e);
			throw new FileAccessException(FileAccessException.FILE_NOTFOUND,e.getMessage());
		} catch (IOException e) {
			log.error("", e);
			throw new FileAccessException(FileAccessException.IO_EXCEPTION,e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				log.error("", e1);
			}
		}
	}

	/**
	 * return the ArrayList object that have already assembled.
	 */
	public ArrayList<BaseEntity> getRsList() {
		return rsList;
	}


}
