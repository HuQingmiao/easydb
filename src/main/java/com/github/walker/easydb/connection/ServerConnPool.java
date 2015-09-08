package com.github.walker.easydb.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.github.walker.easydb.assistant.EasyConfig;
import com.github.walker.easydb.exception.DataAccessException;

/**
 * 
 * This class gets the database connection from the connection pool of web
 * server, and maintains it.
 * 
 * @author HuQingmiao
 */

class ServerConnPool extends ConnectionPool {

	// Uses Hashtable as the datasource cache, in case of lookuping datasource
	// continually by jndi.
	// private static Hashtable datasourceCache = new Hashtable(2);

	private DataSource dataSource = null;

	private String jndiName = null;

	private String dbType = null;

	protected ServerConnPool(String jndiKey) {
		jndiName = EasyConfig.getProperty(jndiKey);

		String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
		dbType = EasyConfig.getProperty(jndiDBTypeKey);

		log.info("Connection Pool of web server is adopted. ");
	}

	/**
	 * Retrieves the database connection
	 * 
	 * @throws SQLException
	 *             , BaseException
	 */
	public Connection getConnection() throws DataAccessException {
		InitialContext initCtx = null;
		Connection conn = null;
		log.debug("jndi.name: " + jndiName);
		try {
			if (dataSource == null) {

				// Hashtable props = new Hashtable();
				// props.put(InitialContext.INITIAL_CONTEXT_FACTORY,
				// contextFactory);
				// props.put(InitialContext.PROVIDER_URL, providerUrl);
				// initCtx = new InitialContext(props);

				initCtx = new InitialContext();
				dataSource = (DataSource) initCtx.lookup(jndiName);
			}
			conn = dataSource.getConnection();

		} catch (SQLException e) {
			log.error("", e);
			throw new DataAccessException(e.getMessage());
		} catch (NamingException e) {
			log.error("", e);
			throw new DataAccessException(e.getMessage());
		} finally {
			try {
				if (initCtx != null)
					initCtx.close();
			} catch (NamingException e) {
				log.error("", e);
				throw new DataAccessException(e.getMessage());
			}
		}
		return conn;
	}

	/**
	 * Close the connection.
	 * 
	 * throws SQLException
	 */
	public void freeConnection(Connection conn) throws DataAccessException {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("", e);
			throw new DataAccessException(e.getMessage());
		}
	}

	/**
	 * ��ȡ���ݿ�����
	 * 
	 * @return
	 */
	public String getDBType() {
		return this.dbType;
	}

}