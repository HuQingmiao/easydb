package com.github.walker.easydb.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.github.walker.easydb.assistant.EasyConfig;
import com.github.walker.easydb.exception.DataAccessException;

/**
 * <p>
 * Self-realized connection pool.
 * </p>
 * 
 * @author HuQingmiao
 */

class EasyDBConnPool extends ConnectionPool {

	// the max intercurrent connection number of self-realize connection pool.
	private static String maxConnCnts = EasyConfig.getProperty("maxConnCount");

	// the maximum intercurrent count of
	private static int maxConnCount = Integer.parseInt(maxConnCnts);

	// connection pool
	private static int usedConnCount; // the count of used connection

	private static Vector<Connection> freeConnQueue = new Vector<Connection>(); // the
																				// free
																				// connection

	// the rate of idle connections to activity connections
	private static float maxIdleRate = (float) 1 / 3;

	// JDBC driver String, such as: "com.mysql.jdbc.Driver"
	private String driverStr = EasyConfig.getProperty("driverStr");

	// datasource, such as: "jdbc:mysql://127.0.0.1:3306/test"
	private String dataSource = EasyConfig.getProperty("dataSource");

	private String username = EasyConfig.getProperty("username");

	private String password = EasyConfig.getProperty("password");

	private String dbType = EasyConfig.getProperty("DBType");

	protected EasyDBConnPool() {
		log.info("Self-realized connection pool is adopted. ");
	}

	/**
	 * Retrieve the database connection
	 * 
	 * @return database connection
	 */
	@SuppressWarnings("resource")
	public synchronized Connection getConnection() throws DataAccessException {
		Connection con = null;

		// if exists free connection
		if (freeConnQueue.size() > 0) {

			// Retrieve the first connection of the free queue
			con = (Connection) freeConnQueue.firstElement();
			freeConnQueue.removeElementAt(0);

			try {
				// If the connection is not avaliable, recall this method
				// recursively
				if (con == null || con.isClosed()) {
					con = getConnection();
				} else {
					// if the rate of idel to activity is greater than 1/3
					if (freeConnQueue.size() / (float) usedConnCount > maxIdleRate) {
						trimQueue((int) (usedConnCount * maxIdleRate));
					}
				}
			} catch (SQLException e) {
				con = getConnection();
			}
		} else {
			try {
				if (usedConnCount < maxConnCount) {
					con = newConnection();
				} else {

					log.info("The used connection count is��" + usedConnCount);
					log.info("The size of free queue is   ��" + freeConnQueue.size());

					log.warn("the setted maximum intercurrent count of connection is not big enough, or the intercurrent users are too more. ");

					// spends three seconds to wait for other free connection
					Thread.sleep(2000);
					getConnection();
				}
			} catch (SQLException e) {
				log.error(e.getErrorCode(), e);
				throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
			} catch (ClassNotFoundException e) {
				log.error("", e);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

		usedConnCount++;

		// notify the threads which are waiting for enter this method
		this.notifyAll();

		log.debug("The used connection count is��" + usedConnCount);
		log.debug("The size of free queue is   ��" + freeConnQueue.size());

		return con;
	}

	/**
	 * Releases the connecion and adds it into the tail of free connection queue
	 * in order to recycle and reuse it.
	 * 
	 */
	public synchronized void freeConnection(Connection conn) {
		// adds the connection into the tail of free connection queue
		freeConnQueue.addElement(conn);

		usedConnCount--;

		// notify the threads which are waiting for enter this method
		this.notifyAll();
	}

	/**
	 * Creates a new connection.
	 */
	private java.sql.Connection newConnection() throws SQLException, ClassNotFoundException {
		java.sql.Connection conn = null;

		try {
			Class.forName(driverStr);

			log.info("Trying to connect to " + dataSource);
			conn = DriverManager.getConnection(dataSource, username, password);

			log.info("Creating new Connection... OK! ");

		} catch (SQLException e) {
			// log.error("Creating new Connection... FAILED!",e);
			throw e;
		} catch (ClassNotFoundException e) {
			// log.error("",e);
			throw e;
		}
		return conn;
	}

	/**
	 * Releases all the connecions in the free connection queue.
	 * 
	 */
	private synchronized void trimQueue(int resize) throws SQLException {

		int size = freeConnQueue.size();
		while (size > resize) {
			Connection c = (Connection) freeConnQueue.get(size - 1);
			if (c != null && !c.isClosed()) {
				c.close();
			}
			freeConnQueue.remove(size - 1);
			size--;
		}
	}

	/**
	 * Releases all the connecions in the free connection queue.
	 * 
	 */
	private void freeAll() throws SQLException {

		for (int i = 0; i < freeConnQueue.size(); i++) {
			Connection c = (Connection) freeConnQueue.get(i);
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		freeConnQueue.clear();
		usedConnCount = 0;
	}

	protected void finalize() {
		try {
			this.freeAll();
		} catch (SQLException e) {
			log.error("", e);
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