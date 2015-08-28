package walker.easydb.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.log4j.Logger;

import walker.easydb.assistant.EasyConfig;
import walker.easydb.assistant.LogFactory;
import walker.easydb.exception.DataAccessException;

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
	 * 取得连接池的默认方法, 该方法试图取得与"jndi.name"相应的连接池, 如果没有在配置文件中 设定"jndi.name"的相应值,
	 * 则获取EasyDB自实现的连接池.
	 * 
	 * @return the instace of subclass .
	 */
	public synchronized static ConnectionPool getInstance() {

		// 默认的JNDI键名采用"jndi.name"
		return getInstance("jndi.name");

	}

	/**
	 * 获取数据库类型
	 * 
	 * @return 数据库类型,如'mysql', 'oracle'
	 */
	public abstract String getDBType();

	/**
	 * 
	 * 取得指定jndi键名对应的连接池.
	 * 
	 * @return the instace of subclass .
	 */
	public synchronized static ConnectionPool getInstance(String jndiKey) {

		String jndiName = EasyConfig.getProperty(jndiKey);

		// 如果配置文件中为jndi.name设置了值, 则采用web服务器的连接池
		if (jndiName != null && !"".equals(jndiName.trim())) {
			if (instances.containsKey(jndiName)) {
				return (ConnectionPool) instances.get(jndiName);
			} else {
				ConnectionPool instance = new ServerConnPool(jndiKey);
				instances.put(jndiName, instance);
				return instance;
			}

			// 采用自实现的连接池
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

				// 没有找到数据库的配置信息
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
	 * 释放资源
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
	 * 释放资源
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
	 * 释放游标
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
	 * 释放查询结果集
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