package com.github.walker.easydb.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.github.walker.easydb.assistant.EasyConfig;
import com.github.walker.easydb.exception.DataAccessException;
import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;

/**
 * 
 * Connecion pool, it responsibles for geting and recycling the connecions.
 * Class EasyDBConnPool and ServerConnPool implemented it.
 * 
 * @author HuQingmiao
 * 
 */
public abstract class ConnectionPool {

	protected static Logger log = LogFactory.getLogger(ConnectionPool.class);

	private static HashMap<String, ConnectionPool> instances = new HashMap<String, ConnectionPool>(3); // instance
																										// pool

	/**
	 * Gets the default instance of this class. This method decides which
	 * connection pool to be adopted. The ServerConnPool is prior to the
	 * EasyDBConnPool.<br>
	 * 
	 * ȡ�����ӳص�Ĭ�Ϸ���, �÷�����ͼȡ����"jndi.name"��Ӧ�����ӳ�, ���û���������ļ��� �趨"jndi.name"����Ӧֵ,
	 * ���ȡEasyDB��ʵ�ֵ����ӳ�.
	 * 
	 * @return the instace of subclass .
	 */
	public synchronized static ConnectionPool getInstance() {

		// Ĭ�ϵ�JNDI��������"jndi.name"
		return getInstance("jndi.name");

	}

	/**
	 * ��ȡ���ݿ�����
	 * 
	 * @return ���ݿ�����,��'mysql', 'oracle'
	 */
	public abstract String getDBType();

	/**
	 * 
	 * ȡ��ָ��jndi������Ӧ�����ӳ�.
	 * 
	 * @return the instace of subclass .
	 */
	public synchronized static ConnectionPool getInstance(String jndiKey) {

		String jndiName = EasyConfig.getProperty(jndiKey);

		// ��������ļ���Ϊjndi.name������ֵ, �����web�����������ӳ�
		if (jndiName != null && !"".equals(jndiName.trim())) {
			if (instances.containsKey(jndiName)) {
				return (ConnectionPool) instances.get(jndiName);
			} else {
				ConnectionPool instance = new ServerConnPool(jndiKey);
				instances.put(jndiName, instance);
				return instance;
			}

			// ������ʵ�ֵ����ӳ�
		} else {
			if (instances.containsKey("NO_JNDI")) {
				return (ConnectionPool) instances.get("NO_JNDI");

			} else {
				String driverStr = EasyConfig.getProperty("driverStr");
				String dataSource = EasyConfig.getProperty("dataSource");
				String username = EasyConfig.getProperty("username");

				if (driverStr != null && dataSource != null && username != null) {
					ConnectionPool instance = new EasyDBConnPool();
					instances.put("NO_JNDI", instance);
					return instance;
				}

				// û���ҵ����ݿ��������Ϣ
				String errorMsg = "Getting the database configuration... FAILED!  Please check the file '"
						+ EasyConfig.CONFIG_FILENAME + "'. ";
				log.error(errorMsg);

				return null;
			}
		}
	}

	/**
	 * Gets database connection
	 * 
	 */
	public abstract Connection getConnection() throws DataAccessException;

	/**
	 * Releases the connection.
	 */
	public abstract void freeConnection(Connection conn) throws DataAccessException;

	/**
	 * �ͷ���Դ
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 * @throws SQLException
	 */
	public void release(Connection conn, Statement stmt, ResultSet rs) throws DataAccessException {
		this.release(rs);
		this.release(stmt);
		this.freeConnection(conn);
	}

	/**
	 * �ͷ���Դ
	 * 
	 * @param conn
	 * @param stmt
	 * @throws SQLException
	 */
	public void release(Connection conn, Statement stmt) throws DataAccessException {
		this.release(stmt);
		this.freeConnection(conn);
	}

	/**
	 * �ͷ��α�
	 * 
	 * @param stmt
	 * 
	 * @throws SQLException
	 */
	public void release(Statement stmt) throws DataAccessException {

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			log.error("", e);
			throw new DataAccessException(e.getMessage());
		}
	}

	/**
	 * �ͷŲ�ѯ�����
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public void release(ResultSet rs) throws DataAccessException {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			log.error("", e);
			throw new DataAccessException(e.getMessage());
		}
	}

}