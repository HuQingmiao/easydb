package com.github.walker.easydb.dao;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import com.github.walker.easydb.assistant.EasyConfig;
import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.connection.ConnectionPool;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.criterion.Exp;
import com.github.walker.easydb.dao.mysql.MysqlDAO;
import com.github.walker.easydb.dao.oracle.OracleDAO;
import com.github.walker.easydb.dao.sqlserver.SqlserverDAO;
import org.apache.log4j.Logger;

import com.github.walker.easydb.exception.BaseException;
import com.github.walker.easydb.exception.CriteriaException;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.FileAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;
import com.github.walker.easydb.exception.IllegalParamException;

/**
 * EasyDB的功能接口
 *
 * @author HuQingmiao
 */
public abstract class EasyDao {

    protected Logger log = LogFactory.getLogger(this.getClass());

    protected ConnectionPool connPool = null;

    protected Connection conn = null;

    private int transCnt = 0; // transaction count that already launched.

    protected EasyDao() {
        this.connPool = ConnectionPool.getInstance();

    }

    protected EasyDao(String jndiKey) {
        this.connPool = ConnectionPool.getInstance(jndiKey);
    }

    /**
     * 获取默认的JNDI键名对应的EasyDao对象.
     *
     * @return 返回EasyDao对象, 该对象将采用默认的JNDI键值, 即'jndiName'及其对应值获取数据库连接
     */
    public static EasyDao getInstance() {
        String jndiKey = "jndi.name";

        String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
        String dbType = EasyConfig.getProperty(jndiDBTypeKey);

        // 如果该jndiKey不存在, 则取自实现的连接配置
        if (dbType == null || "".equals(dbType.trim())) {
            dbType = EasyConfig.getProperty("DBType");
        }

        if ("mysql".equalsIgnoreCase(dbType)) {
            return new MysqlDAO();

        } else if ("oracle".equalsIgnoreCase(dbType)) {
            return new OracleDAO();

        } else if ("sqlserver".equalsIgnoreCase(dbType)) {
            return new SqlserverDAO();
        }

        Logger log = LogFactory.getLogger(LogFactory.MODULE_EASYDB);
        log.error("Getting the 'DBType' from '" + EasyConfig.CONFIG_FILENAME + "'... FAILED!  Please check it. ");

        return null;
    }

    /**
     * 获取指定JNDI键名对应的EasyDao对象
     *
     * @param jndiKey 指定的配置文件中的某个JNDI键
     * @return 返回EasyDao对象, 该对象将采用指定的JNDI键值获取数据库连接
     */
    public static EasyDao getInstance(String jndiKey) {

        String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
        String dbType = EasyConfig.getProperty(jndiDBTypeKey);

        // 如果该jndiKey不存在, 则取自实现的连接配置
        if (dbType == null || "".equals(dbType.trim())) {
            dbType = EasyConfig.getProperty("DBType");
        }

        if ("mysql".equalsIgnoreCase(dbType)) {
            return new MysqlDAO(jndiKey);

        } else if ("oracle".equalsIgnoreCase(dbType)) {
            return new OracleDAO(jndiKey);

        } else if ("sqlserver".equalsIgnoreCase(dbType)) {
            return new SqlserverDAO(jndiKey);
        }

        Logger log = LogFactory.getLogger(LogFactory.MODULE_EASYDB);
        log.error("Getting the " + jndiDBTypeKey + " from 'easydb.properties'... FAILED!  Please check it. ");

        return null;
    }

    /**
     * 获取数据库类型
     *
     * @return 数据库类型, 如'mysql', 'oracle'
     */
    public abstract String getDBType();

    /**
     * 开启事务。 支持嵌套事务，需要与endTrans(boolean)成对使用。
     * <p/>
     * Gets a database connection and begin a transaction. Only when transCount
     * is 0, will the transaction start. This method supports for nested
     * transaction with method endTrans().
     *
     * @throws com.github.walker.easydb.exception.BaseException
     * @see EasyDao#endTrans(boolean)
     */
    public void beginTrans() throws BaseException {
        try {
            if (transCnt == 0) { // if no transaction started
                conn = connPool.getConnection();
                conn.setAutoCommit(false);
            }
            transCnt++;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * 结束事务。 支持嵌套事务，需要与beginTrans()成对使用。
     * <p/>
     * Ends a database transaction and close the database connection. Only when
     * transCount is 1, will the transaction end. This method supports for
     * nested transaction with method beginTrans().
     *
     * @param commit If true, commit the transaction, else rollback the
     *               transaction.
     * @throws BaseException
     * @see EasyDao#beginTrans()
     */
    public void endTrans(boolean commit) throws BaseException {
        try {
            // if no transaction started, calling this method is nonsensical
            if (transCnt == 0) {
                return;
            }

            if (transCnt == 1) {
                if (commit) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                connPool.freeConnection(conn);
            }
            transCnt--;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * 插入一条记录。
     * <p/>
     * Persist the given entity instance, Inserts one data into database.
     *
     * @param entity BaseEntity object which need to be persisted.
     * @return the row count for INSERT
     * @throws BaseException
     */
    public int save(BaseEntity entity) throws BaseException {

        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            // parse the entity
            parser = new EntityParser(getDBType(), this.conn, entity);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildInsert(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter value to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());
            this.fillParamToMark(stmt, sqlParamIndex.getIndexedFieldVec(), parser);

            // insert
            int rowCnt = stmt.executeUpdate();
            stmt.close();

            // 处理大数据类型的写入
            if (rowCnt > 0) {
                this.ProcessBigDataType(parser);
            }

            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }
    }

    /**
     * 批量插入多条记录。
     * <p/>
     * 注意: 1.参数Entity数组中列值的存放结构必须一致，即:数组中的某列要么都需要写入，要么都不需要写入！
     * 2.此方法不支持大字段列(e.g.BLOB/CLOB)的写入.
     * <p/>
     * Persist the given entity instances, Inserts multiple rows of data into
     * database.
     *
     * @param entityArray the array of BaseEntity object which need to be persisted.
     * @return an array of INSERT counts containing one element for each
     * BaseEntity object in the parameter array. The elements of the
     * array are ordered according to the order of the parameter array.
     * @throws BaseException
     */
    public int[] save(BaseEntity[] entityArray) throws BaseException {

        if (entityArray.length == 0 || entityArray[0] == null) {
            return new int[]{};
        }

        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            // 解析第一个实体，从而得到共用的解析器
            parser = new EntityParser(getDBType(), this.conn, entityArray[0]);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildInsert(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());

            // filled the '?' with paramter values
            Vector<String> indexedFieldVec = sqlParamIndex.getIndexedFieldVec();
            this.fillParamToMark(stmt, indexedFieldVec, parser);

            stmt.addBatch();

            // 采用共用的解析器,处理后面的实体
            int size = entityArray.length;
            for (int i = 1; i < size; i++) {
                if (entityArray[i] == null) {
                    continue;
                }
                EntityParser p = new EntityParser(parser, entityArray[i]);
                this.fillParamToMark(stmt, indexedFieldVec, p);

                stmt.addBatch();
            }
            indexedFieldVec.clear();

            int[] rowCnt = stmt.executeBatch();

            // 由于JDBC在执行INSERT操作时，返回的插入条数是未知的， 因此还需要计算真正插入的条数
            for (int i = 0; i < rowCnt.length; i++) {

                // 如果返回成功标识 或 插入条数>0
                if (rowCnt[i] == Statement.SUCCESS_NO_INFO || rowCnt[i] > 0) {
                    rowCnt[i] = 1;
                }
            }

            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }

    }

    /**
     * 以参数entity中的主键值为条件, 更新一条记录. <br>
     * Updates one record, the updating criteria is the primary key contained in
     * the entity.
     *
     * @param entity BaseEntity object which data will override the old data in the
     *               database.
     * @return the row count for UPDATE
     * @throws BaseException
     */
    public int update(BaseEntity entity) throws BaseException {
        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            parser = new EntityParser(getDBType(), this.conn, entity);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildUpdateByPk(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());
            this.fillParamToMark(stmt, sqlParamIndex.getIndexedFieldVec(), parser);

            // update
            int rowCnt = stmt.executeUpdate();

            // 处理大数据类型的写入
            if (rowCnt > 0) {
                this.ProcessBigDataType(parser);
            }

            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }
    }

    /**
     * 以参数entityArray中各元素对应实体的主键值为条件, 批量更新多条记录. * 注意:
     * 1.参数Entity数组中列值的存放结构必须一致，即:数组中的某列要么都需要更新，要么都不需要更新！
     * 2.此方法不支持大字段列(e.g.BLOB/CLOB)的写入. <br>
     * Updates records by batch, the updating criteria is the primary key
     * contained in the elements of entity array.
     *
     * @param entityArray the array of BaseEntity object which data will override the
     *                    old data in the database.
     * @return an array of UPDATE counts containing one element for each
     * BaseEntity object in the parameter array. The elements of the
     * array are ordered according to the order of the parameter array.
     * @throws BaseException
     */
    public int[] update(BaseEntity[] entityArray) throws BaseException {
        // //参数不能为空, 且长度不能为0
        // if(entityArray==null||entityArray.length==0){
        // throw new
        // IllegalParamException(IllegalParamException.PARAM_CANNOT_NULL);
        // }

        if (entityArray.length == 0 || entityArray[0] == null) {
            return new int[]{};
        }

        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            // 解析第一个实体，从而得到共用的解析器
            parser = new EntityParser(getDBType(), this.conn, entityArray[0]);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildUpdateByPk(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());

            // filled the '?' with paramter values
            Vector<String> indexedFieldVec = sqlParamIndex.getIndexedFieldVec();
            this.fillParamToMark(stmt, indexedFieldVec, parser);

            stmt.addBatch();

            // 采用共用的解析器,处理后面的实体
            int size = entityArray.length;
            for (int i = 1; i < size; i++) {
                if (entityArray[i] == null) {
                    continue;
                }
                EntityParser p = new EntityParser(parser, entityArray[i]);
                this.fillParamToMark(stmt, indexedFieldVec, p);

                stmt.addBatch();
            }
            indexedFieldVec.clear();

            int[] rowCnt = stmt.executeBatch();

            // 由于JDBC在执行UPDATE操作时，返回的插入条数是未知的， 因此还需要计算真正插入的条数
            for (int i = 0; i < rowCnt.length; i++) {

                // 如果返回成功标识 或 插入条数>0
                if (rowCnt[i] == Statement.SUCCESS_NO_INFO || rowCnt[i] > 0) {
                    rowCnt[i] = 1;
                }
            }

            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }

    }

    /**
     * 指定criteria为条件或更新范围，以参数entity中的数据项更新相应表的记录。
     * 注意：如果参数entity装载有文件类型的数据，或者说需要向某个表的大字段列(e.g.BLOB/CLOB)写入数据，
     * 则参数criteria必须含有主键值以唯一标识需要更新的那条记录。 <br>
     * Updates some records, Using the property values in the entity to override
     * correponding columns of records. <br>
     * <p/>
     * This method support updating for BLOB/CLOB column, but when you call this
     * method and need writing data to BLOB/CLOB column,the criteria must
     * contain the primary key value.
     *
     * @param entity   BaseEntity object which data will override the old data in the
     *                 database.
     * @param criteria Criteria object which is the criteria of updating.
     * @return the row count for UPDATE
     * @throws BaseException
     */
    public int update(BaseEntity entity, Criteria criteria) throws BaseException {
        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            parser = new EntityParser(getDBType(), this.conn, entity);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildUpdateByCriteria(parser, criteria);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());
            this.fillParamToMark(stmt, sqlParamIndex.getIndexedFieldVec(), parser);

            // update
            int rowCnt = stmt.executeUpdate();

            // 处理大数据类型的写入
            if (rowCnt > 0) {
                this.ProcessBigDataType(parser, criteria);
            }
            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }
    }

    /**
     * 以参数entity中的数据项更新相应表的所有记录。 <br>
     * Updates all records, Using the property values in the entity to override
     * correponding columns of all records.
     *
     * @param entity BaseEntity object which data will override the old data in the
     *               database.
     * @return the row count for UPDATE
     * @throws BaseException
     */
    public int updateAll(BaseEntity entity) throws BaseException {

        return this.update(entity, Exp.eq("1", 1));
    }

    /**
     * 删除一条记录, 其删除依据是参数entity中的主键值.
     * <p/>
     * Deletes one record, the deleteing criteria is the primary key contained
     * in the entity.
     *
     * @param entity BaseEntity object which data will be deleted from database.
     * @return the row count for DELETE
     * @throws BaseException
     */
    public int delete(BaseEntity entity) throws BaseException {
        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            parser = new EntityParser(getDBType(), this.conn, entity);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildDeleteByPk(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());
            this.fillParamToMark(stmt, sqlParamIndex.getIndexedFieldVec(), parser);

            // delete
            int rowCnt = stmt.executeUpdate();
            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }
    }

    /**
     * 批量删除多条记录, 其删除依据是参数entityArray中各元素对应实体的主键值.
     * <p/>
     * Deletes records by batch, the deleteing criteria is the primary key
     * contained in the elements of entity array.
     *
     * @param entityArray the array of BaseEntity which need to be delete.
     * @return an array of DELETE counts containing one element for each
     * BaseEntity object in the parameter array. The elements of the
     * array are ordered according to the order of the parameter array.
     * @throws BaseException
     */
    public int[] delete(BaseEntity[] entityArray) throws BaseException {
        // //参数不能为空, 且长度不能为0
        // if(entityArray==null||entityArray.length==0){
        // throw new
        // IllegalParamException(IllegalParamException.PARAM_CANNOT_NULL);
        // }
        if (entityArray.length == 0 || entityArray[0] == null) {
            return new int[]{};
        }

        PreparedStatement stmt = null;
        EntityParser parser = null;
        try {
            // 解析第一个实体，从而得到共用的解析器
            parser = new EntityParser(getDBType(), this.conn, entityArray[0]);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildDeleteByPk(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());

            // filled the '?' with paramter values
            Vector<String> indexedFieldVec = sqlParamIndex.getIndexedFieldVec();
            this.fillParamToMark(stmt, indexedFieldVec, parser);

            stmt.addBatch();

            // 采用共用的解析器,处理后面的实体
            int size = entityArray.length;
            for (int i = 1; i < size; i++) {
                if (entityArray[i] == null) {
                    continue;
                }
                EntityParser p = new EntityParser(parser, entityArray[i]);
                this.fillParamToMark(stmt, indexedFieldVec, p);

                stmt.addBatch();
            }
            indexedFieldVec.clear();

            int[] rowCnt = stmt.executeBatch();

            // 由于JDBC在执行DELETE操作时，返回的删除条数是未知的， 因此还需要计算真正删除的条数
            for (int i = 0; i < rowCnt.length; i++) {

                // 如果返回成功标识 或 删除条数>0
                if (rowCnt[i] == Statement.SUCCESS_NO_INFO || rowCnt[i] > 0) {
                    rowCnt[i] = 1;
                }
            }

            return rowCnt;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt);
        }

    }

    /**
     * 指定criteria为删除条件，删除entityClass所表示的某个表的部分记录.
     * <p/>
     * <br>
     * Deletes some records from the table specified by the 'entityClass'.
     *
     * @param entityClass Class object which represented the table, such as
     *                    'Book.class'.
     * @param criteria    the criteria of delete.
     * @return the count of rows affected by the delete operation.
     * @throws BaseException
     */
    public int delete(Class<?> entityClass, Criteria criteria) throws BaseException {

        PreparedStatement stmt = null;
        try {
            // build the sql
            String sql = getSqlConstructor().buildDeleteByCriteria(entityClass, criteria);
            log.info("SQL: " + sql);

            // fill the question mark with data
            stmt = this.conn.prepareStatement(sql);

            return stmt.executeUpdate();

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } finally {
            this.freeResource(stmt);
        }
    }

    /**
     * 删除entityClass所表示的某个表的所有记录. <br>
     * Deletes all records from the table specified by the 'entityClass'.
     *
     * @param entityClass Class object which represented the table, such as
     *                    'Book.class'.
     * @return the count of rows affected by the delete operation.
     * @throws BaseException
     */
    public int deleteAll(Class<?> entityClass) throws BaseException {
        return this.delete(entityClass, Exp.eq("1", 1));
    }

    /**
     * 执行给定的SQL语句.
     * <p/>
     * 注意：此方法不支持大字段列(e.g.BLOB/CLOB)的写入.
     * <p/>
     * Executes the given sql. Notice, this method doesn't support writing
     * BLOB/CLOB column.
     *
     * @param eSql the executed sql, such as: "INSERT INTO
     *             TABLEX(COLUMN1,COLUMN2) VALUES(1,'aa')
     * @return the row count for INSERT/UPDATE/DELETE
     * @throws BaseException
     */
    public int exec(String eSql) throws BaseException {

        eSql = eSql.replace('	', ' ');

        PreparedStatement stmt = null;
        try {
            log.info("SQL: " + eSql);

            stmt = this.conn.prepareStatement(eSql);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            log.error("", e);
            throw new DataAccessException(e.getMessage());
        } finally {
            this.freeResource(stmt);
        }
    }

    /**
     * 执行给定的存储过程调用语句SQL语句.
     *
     * Executes the given procedure calling sql.
     *
     * @param pSql
     *            the procedure calling sql, such as:"{?=CALL STAT_USERS(?,?)}";
     *
     * @param paramMap
     *            SqlParamMap object, which loads the prameter values for the
     *            sql.
     *
     * @return the SqlParamMap object, which loads the return values after this
     *         call.
     *
     * @throws BaseException
     *
     */
    /*
     * public void callProcedure(String pSql, SqlParamMap paramMap) throws
	 * BaseException {
	 * 
	 * CallableStatement stmt = null; try { log.info("SQL: " + pSql); stmt =
	 * this.conn.prepareCall(pSql);
	 * 
	 * int index = pSql.indexOf("=");
	 * 
	 * int retValueIdx = 0;
	 * 
	 * if (index > 0) {
	 * 
	 * //对'='前的的各个?, 注册返回值类型 for (int i = 0; i <= index; i++) { if
	 * (pSql.charAt(i) == '?') { retValueIdx++; UpdateIdentifier obj =
	 * (UpdateIdentifier) paramMap .get(retValueIdx);
	 * 
	 * if (obj instanceof EString) { stmt.registerOutParameter(retValueIdx,
	 * Types.VARCHAR); } if (obj instanceof EInteger || obj instanceof ELong) {
	 * stmt.registerOutParameter(retValueIdx, Types.NUMERIC); } else if (obj
	 * instanceof EDouble || obj instanceof EFloat) {
	 * stmt.registerOutParameter(retValueIdx, java.sql.Types.NUMERIC);
	 * 
	 * } else if (obj instanceof EDouble || obj instanceof EFloat) {
	 * stmt.registerOutParameter(retValueIdx, java.sql.Types.DOUBLE);
	 * 
	 * } else if (obj instanceof ETimestamp) {
	 * stmt.registerOutParameter(retValueIdx, java.sql.Types.TIMESTAMP); } } }
	 * }// end if(index>0)
	 * 
	 * //存储过程的参数 this.fillParamToMark(stmt, paramMap); stmt.executeUpdate();
	 * 
	 * //设置返回值 if (index > 0) {
	 * 
	 * for (int i = 1; i <= retValueIdx; i++) { UpdateIdentifier obj =
	 * (UpdateIdentifier) paramMap .get(i);
	 * 
	 * if (obj instanceof EString) { paramMap.put(i, new
	 * EString(stmt.getString(i))); } if (obj instanceof EInteger) {
	 * paramMap.put(i, new EInteger(stmt.getInt(i))); } if (obj instanceof
	 * ELong) { paramMap.put(i, new ELong(stmt.getLong(i)));
	 * 
	 * } else if (obj instanceof EDouble) { paramMap.put(i, new
	 * EDouble(stmt.getDouble(i)));
	 * 
	 * } else if (obj instanceof EFloat) { paramMap.put(i, new
	 * EFloat(stmt.getFloat(i)));
	 * 
	 * } else if (obj instanceof ETimestamp) { paramMap.put(i, new
	 * ETimestamp(stmt.getTimestamp(i))); } } }
	 * 
	 * } catch (SQLException e) { log.error("", e); throw new
	 * DataAccessException(this.getDBType(),e); } finally {
	 * this.freeResource(stmt); } }
	 */

    /**
     * 执行给定的SQL语句.
     * <p/>
     * 注意：此方法不支持大字段列(e.g.BLOB/CLOB)的写入.
     * <p/>
     * Executes the given sql. Notice, this method doesn't support writing
     * BLOB/CLOB column.
     *
     * @param pSql     the parameterize sql, such as: "INSERT INTO
     *                 TABLEX(COLUMN1,COLUMN2) VALUES(?,?)
     * @param paramMap SqlParamMap object, which loads the prameter values for the
     *                 sql.
     * @return the row count for INSERT/UPDATE/DELETE
     * @throws BaseException
     */
    public int exec(String pSql, SqlParamMap paramMap) throws BaseException {

        if (pSql == null) {
            pSql = "";
        }
        pSql = pSql.replace('	', ' ');

        PreparedStatement stmt = null;
        try {

            log.info("SQL: " + pSql);
            stmt = this.conn.prepareStatement(pSql);

            // set the parameter to the '?' mark
            this.fillParamToMark(stmt, paramMap);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt);
        }
    }

    /**
     * 执行给定的SQL。 注意：该方法不支持对大字段列(e.g. BLOB/CLOB)的写入。
     * <p/>
     * Executes the given sql. Notice, this method doesn't support writing
     * BLOB/CLOB column.
     *
     * @param pSql          parameterized sql, such as: "INSERT INTO
     *                      TABLEX(COLUMN1,COLUMN2) VALUES(?,?)
     * @param paramMapArray the array of SqlParamMap object.
     * @return an array of INSERT/UPDATE/DELETE counts containing one element
     * for each SqlParamMap object in the parameter array. The elements
     * of the array are ordered according to the order of the parameter
     * array.
     * @throws BaseException
     */
    public int[] exec(String pSql, SqlParamMap[] paramMapArray) throws BaseException {

        if (pSql == null) {
            pSql = "";
        }

        pSql = pSql.replace('	', ' ');

        // 参数长度不能为0
        if (paramMapArray.length == 0) {
            throw new IllegalParamException(IllegalParamException.PARAM_CANNOT_NULL, "");
        }

        PreparedStatement stmt = null;
        try {
            log.info("SQL: " + pSql);
            stmt = this.conn.prepareStatement(pSql);

            // set the parameter to the '?' mark
            for (int i = 0; i < paramMapArray.length; i++) {

                // 参数组中某组为空， 则过滤之
                if (paramMapArray[i] == null) {
                    continue;
                }

                this.fillParamToMark(stmt, paramMapArray[i]);
                stmt.addBatch();
            }

            int[] rowCnt = stmt.executeBatch();

            // 由于JDBC在执行批量INSERT/UPDATE操作时，返回的插入条数是未知的， 因此还需要计算真正发生影响的记录条数
            for (int i = 0; i < rowCnt.length; i++) {

                // 如果返回成功标识 或 插入条数>0
                if (rowCnt[i] == Statement.SUCCESS_NO_INFO || rowCnt[i] > 0) {
                    rowCnt[i] = 1;
                }
            }

            return rowCnt;

        } catch (BatchUpdateException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt);
        }
    }

    /**
     * 根据参数entity中的主键值, 装载相应实体, 即加载该实体的其它属性.
     * <p/>
     * Loads one data to the entity by the primary key that contained in the
     * entity.
     *
     * @param entity BaseEntity object which contains the values of primary key.
     * @return true, only when exists the record which matching the primary key;
     * otherwise fase;
     * @throws BaseException
     */
    public boolean loadByPK(BaseEntity entity) throws BaseException {

        PreparedStatement stmt = null;
        EntityParser parser = null;
        ResultSet rs = null;
        try {
            parser = new EntityParser(getDBType(), this.conn, entity);

            // build the sql
            SqlParamIndexer sqlParamIndex = getSqlConstructor().buildSelectByPk(parser);
            log.info("SQL: " + sqlParamIndex.getSql());

            // set the parameter to the '?' mark
            stmt = this.conn.prepareStatement(sqlParamIndex.getSql());
            this.fillParamToMark(stmt, sqlParamIndex.getIndexedFieldVec(), parser);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entity.getClass());

            return assembler.loadEntity(entity);

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            if (parser != null) {
                parser.myFinalize();
            }
            this.freeResource(stmt, rs);
        }
    }

    /**
     * 指定criteria作为查询条件，从entityClass所表示的某个表或无名视图中取一条记录. 如果检索到符合条件的记录数大于1，则抛出异常.
     * <p/>
     * Gets one data from the talbe(or view) specified by the 'entityClass'.
     *
     * @param entityClass Class object which represented the table, such as
     *                    'Book.class'.
     * @param criteria    the criteria of query.
     * @throws BaseException
     */
    public BaseEntity getOne(Class<?> entityClass, Criteria criteria) throws BaseException {

        // 在方法getOne(Class, Criteria)中, Criteria对象不能为空！
        if (criteria == null || "".equals(criteria.toString().trim())) {
            throw new CriteriaException(CriteriaException.CRITERIA_CANNOT_EMPTY, "");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            SqlConstructor sqlConstructor = this.getSqlConstructor();

            // build the sql
            String sql = sqlConstructor.buildSelectSql(entityClass, criteria);

            // 分页查询, 取一条记录
            String pagerSql = sqlConstructor.buildPageSql(sql, 1, 2);
            if (pagerSql == null) {
                String jndiKey = "jndi.name";

                String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
                String dbType = EasyConfig.getProperty(jndiDBTypeKey);

                // 如果该jndiKey不存在, 则取自实现的连接配置
                if (dbType == null || "".equals(dbType.trim())) {
                    dbType = EasyConfig.getProperty("DBType");
                }

                if ("sqlserver".equalsIgnoreCase(dbType)) {
                    throw new DataAccessException("EasyDB对Sqlserver不支持分页查询!");
                }
            }

            log.info("SQL: " + pagerSql);

            stmt = this.conn.prepareStatement(pagerSql);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entityClass);
            assembler.buildEntityList();
            ArrayList<BaseEntity> rsList = assembler.getRsList();

            // 没有符合条件的记录
            if (rsList.size() == 0) {
                return null;
            }

            return (BaseEntity) rsList.get(0);

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt, rs);
        }
    }

    /**
     * 执行给定的查询语句，从参数entityClas所表示的表或视图(包括无名视图)中取一条或多条记录. <br>
     * <p/>
     * Gets some datas from the talbe(or view) specified by the 'entityClass'.
     *
     * @param eSql        executable sql, such as: "SELECT A.NAME,A.TYPE FROM TABLEXX
     *                    WHERE A.NAME = 'JONE' "
     * @param entityClass the Class object of BaseEntity which loads the query results.
     * @throws BaseException
     */
    @SuppressWarnings("rawtypes")
    public ArrayList get(String eSql, Class<?> entityClass) throws BaseException {

        if (eSql == null) {
            eSql = "";
        }
        eSql = eSql.replace('	', ' ');

        // 校验SQL是否安全
        if (!validateQuerySql(eSql)) {
            throw new IllegalParamException(IllegalParamException.ONLY_FOR_SELECT, "");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            log.info("SQL: " + eSql);
            stmt = this.conn.prepareStatement(eSql);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entityClass);
            assembler.buildEntityList();

            // 对于非分页查询, 将所有满足业务条件的记录总数设为实际查询出的记录条数
            PageList rsList = (PageList) assembler.getRsList();
            rsList.setTotalRecordCount(rsList.size());

            return rsList;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt, rs);
        }
    }

    /**
     * 根据给定的参数化SQL及相关参数值，从参数entityClas所表示的表或视图(包括无名视图)中取一条或多条记录. <br>
     * Gets some datas from the talbe(or view) specified by the 'entityClass'.
     *
     * @param pSql        parameterized sql, such as: "SELECT A.NAME,A.TYPE FROM TABLEX
     *                    * WHERE A.NAME = ? "
     * @param paramMap    the SqlParamMap object, which associates the specified value
     *                    with the specified '?' in the parameterized sql.
     * @param entityClass the Class object of BaseEntity which loads the query results.
     * @throws BaseException
     */
    @SuppressWarnings("rawtypes")
    public ArrayList get(String pSql, SqlParamMap paramMap, Class<?> entityClass) throws BaseException {

        if (pSql == null) {
            pSql = "";
        }
        pSql = pSql.replace('	', ' ');

        // 校验SQL是否安全
        if (!validateQuerySql(pSql)) {
            throw new IllegalParamException(IllegalParamException.ONLY_FOR_SELECT, "");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            log.info("SQL: " + pSql);
            stmt = this.conn.prepareStatement(pSql);

            // set the parameter to the '?' mark
            this.fillParamToMark(stmt, paramMap);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entityClass);
            assembler.buildEntityList();

            // 对于非分页查询, 将所有满足业务条件的记录总数设为实际查询出的记录条数
            PageList rsList = (PageList) assembler.getRsList();
            rsList.setTotalRecordCount(rsList.size());

            return rsList;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt, rs);
        }
    }

    /**
     * 本方法将给定的SQL包装成分页查询SQL, 再通过分页查询SQL取得某段范围(大于等于startPos, 且小于endPos)内的一条或多条记录.
     * <p/>
     * 注意：不允许在SQL的WHERE子句中出现某列与子查询返回结果相比较的语句， 例如下面两种类型的SQL是禁用的: <br>
     * 1.SELECT A.COL_1, A.COL_2 FROM A WHERE A.COL_1 = (SELECT B.COL FROM B ) <br>
     * 2.SELECT A.COL_1, A.COL_2 FROM A WHERE A.COL_1 IN (SELECT B.COL FROM B ). <br>
     * <p/>
     * 请将上面两种被禁用的SQL改成如下形式, 即用EXISTS语句代替子条件语句: 1.SELECT A.COL_1, A.COL_2 FROM A
     * WHERE EXISTS (SELECT 1 FROM B WHERE A.COL_1 =B.COL FROM B )
     * <p/>
     * <br>
     * Decrates the given sql to pager sql, according the pager sql, gets
     * pesistent data which position is between startPos and endPos (greater
     * than or equals startPos, and less than endPos).
     *
     * @param eSql        executable sql, such as: "SELECT A.NAME,A.TYPE FROM TABLEXX
     *                    WHERE A.NAME = 'JONE' "
     * @param entityClass the Class object of BaseEntity which loads the query results.
     * @param startPos    starting position in searching records, it begins from 1
     * @param endPos      end position in searching records, it begins from 1
     * @return PageList object, which contains pesistent data which position is
     * between startPos and endPos (greater than or equals startPos, and
     * less than endPos).
     * @throws BaseException
     */
    public PageList get(String eSql, Class<?> entityClass, int startPos, int endPos) throws BaseException {

        if (eSql == null) {
            eSql = "";
        }
        eSql = eSql.replace('	', ' ');

        // 校验SQL是否安全
        if (!validateQuerySql(eSql)) {
            throw new IllegalParamException(IllegalParamException.ONLY_FOR_SELECT, "");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // 取SQL构造器
            SqlConstructor sqlConstructor = this.getSqlConstructor();

            // 分页查询主SQL
            String pagerSql = sqlConstructor.buildPageSql(eSql, startPos, endPos);
            if (pagerSql == null) {
                String jndiKey = "jndi.name";

                String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
                String dbType = EasyConfig.getProperty(jndiDBTypeKey);

                // 如果该jndiKey不存在, 则取自实现的连接配置
                if (dbType == null || "".equals(dbType.trim())) {
                    dbType = EasyConfig.getProperty("DBType");
                }

                if ("sqlserver".equalsIgnoreCase(dbType)) {
                    throw new DataAccessException("EasyDB对Sqlserver不支持分页查询!");
                }
            }

            log.info("SQL: " + pagerSql);

            // 查询位于startPos 至 endPos 之间的记录
            stmt = this.conn.prepareStatement(pagerSql);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entityClass);
            assembler.buildEntityList();
            PageList pageList = (PageList) assembler.getRsList();

            rs.close();
            stmt.close();

            // 取分页查询所能获取的总记录数的SQL
            String countSql = sqlConstructor.buildCountSql(eSql);
            log.info("SQL: " + countSql);

            // 查询、设置所有满足业务条件的记录总数
            stmt = this.conn.prepareStatement(countSql);
            rs = stmt.executeQuery();
            rs.next();
            pageList.setTotalRecordCount(rs.getInt(1));

            return pageList;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt, rs);
        }
    }

    /**
     * 本方法将给定的SQL装饰成分页查询SQL, 再通过分页查询SQL及参数值取得某段范围(大于等于startPos,
     * 且小于endPos)内的一条或多条记录.
     * <p/>
     * 注意：不允许在SQL的WHERE子句中出现某列与子查询返回结果相比较的语句， 例如下面两种类型的SQL是禁用的: <br>
     * 1.SELECT A.COL_1, A.COL_2 FROM A WHERE A.COL_1 = (SELECT B.COL FROM B ) <br>
     * 2.SELECT A.COL_1, A.COL_2 FROM A WHERE A.COL_1 IN (SELECT B.COL FROM B ). <br>
     * <p/>
     * 请将上面两种被禁用的SQL改成如下形式, 即用EXISTS语句代替子条件语句: 1.SELECT A.COL_1, A.COL_2 FROM A
     * WHERE EXISTS (SELECT 1 FROM B WHERE A.COL_1 =B.COL FROM B )<br>
     * <p/>
     * <p/>
     * <br>
     * Decrates the given sql to pager sql, according the pager sql, gets
     * pesistent data which position is between startPos and endPos (greater
     * than or equals startPos, and less than endPos).
     *
     * @param pSql        executable sql, such as: "SELECT A.NAME,A.TYPE FROM TABLEXX
     *                    WHERE A.NAME = ? "
     * @param entityClass the Class object of BaseEntity which loads the query results.
     * @param paramMap    SqlParamMap object, which loads the prameter values for the
     *                    sql.
     * @param startPos    starting position in searching records, it begins from 1
     * @param endPos      end position in searching records, it begins from 1
     * @return PageList object, which contains pesistent data which position is
     * between startPos and endPos (greater than or equals startPos, and
     * less than endPos).
     * @throws BaseException
     */
    public PageList get(String pSql, SqlParamMap paramMap, Class<?> entityClass, int startPos, int endPos)
            throws BaseException {

        if (pSql == null) {
            pSql = "";
        }
        pSql = pSql.replace('	', ' ');

        // 校验SQL是否安全
        if (!validateQuerySql(pSql)) {
            throw new IllegalParamException(IllegalParamException.ONLY_FOR_SELECT, "");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // 取SQL构造器
            SqlConstructor sqlConstructor = this.getSqlConstructor();

            // 分页查询主SQL
            String pagerSql = sqlConstructor.buildPageSql(pSql, startPos, endPos);
            if (pagerSql == null) {
                String jndiKey = "jndi.name";

                String jndiDBTypeKey = jndiKey.substring(0, jndiKey.indexOf('.')) + ".DBType";
                String dbType = EasyConfig.getProperty(jndiDBTypeKey);

                // 如果该jndiKey不存在, 则取自实现的连接配置
                if (dbType == null || "".equals(dbType.trim())) {
                    dbType = EasyConfig.getProperty("DBType");
                }

                if ("sqlserver".equalsIgnoreCase(dbType)) {
                    throw new DataAccessException("EasyDB对Sqlserver不支持分页查询!");
                }
            }
            log.info("SQL: " + pagerSql);

            // 查询位于startPos 至 endPos 之间的记录
            stmt = this.conn.prepareStatement(pagerSql);
            // set the parameter to the '?' mark
            this.fillParamToMark(stmt, paramMap);
            rs = stmt.executeQuery();

            // parse the rs and transform it into PageList
            ResultAssembler assembler = this.getResultAssembler(rs, entityClass);
            assembler.buildEntityList();
            PageList pageList = (PageList) assembler.getRsList();

            rs.close();
            stmt.close();

            // 计算所有满足业务条件的记录总数的SQL
            String countSql = sqlConstructor.buildCountSql(pSql);
            log.info("SQL: " + countSql);

            // 查询、设置所有满足业务条件的记录总数
            stmt = this.conn.prepareStatement(countSql);
            this.fillParamToMark(stmt, paramMap);
            rs = stmt.executeQuery();
            rs.next();
            pageList.setTotalRecordCount(rs.getInt(1));

            return pageList;

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(this.getDBType(), e.getErrorCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        } catch (IllegalEntityException e) {
            log.error("", e);
            throw e;
        } catch (IllegalParamException e) {
            log.error("", e);
            throw e;
        } catch (FileAccessException e) {
            log.error("", e);
            throw e;
        } finally {
            this.freeResource(stmt, rs);
        }
    }

    // close the Statement in order to release resource
    protected void freeResource(Statement stmt) throws DataAccessException {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            log.error("", e);
            throw new DataAccessException(e.getMessage());
        }
    }

    // close the Statement and ResultSet in order to release resource
    protected void freeResource(Statement stmt, ResultSet rs) throws DataAccessException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            log.error("", e);
            throw new DataAccessException(e.getMessage());
        }
    }

    // 校验查询语句, 不得以UPDATE,DELETE,INSERT,TRUNCATE,DROP,ALTER等关键字开头
    private boolean validateQuerySql(String sql) {

        final String UPDATE_KEYWORD = "UPDATE ";
        final String DELETE_KEYWORD = "DELETE ";
        final String INSERT_KEYWORD = "INSERT ";

        final String TRUNCATE_KEYWORD = "TRUNCATE ";
        final String DROP_KEYWORD = "DROP ";
        final String ALTER_KEYWORD = "ALTER ";

        String upperSql = sql.toUpperCase().trim();

        if (upperSql.startsWith(UPDATE_KEYWORD) || upperSql.startsWith(DELETE_KEYWORD)
                || upperSql.startsWith(INSERT_KEYWORD) || upperSql.startsWith(TRUNCATE_KEYWORD)
                || upperSql.startsWith(DROP_KEYWORD) || upperSql.startsWith(ALTER_KEYWORD)) {
            return false;
        }

        return true;
    }

    /**
     * Retrieve the Sql Constructor.
     *
     * @return
     */
    protected abstract SqlConstructor getSqlConstructor();

    /**
     * Retrieve the ResultAssembler object which puts the query result into
     * PageList.
     */
    protected abstract ResultAssembler getResultAssembler(ResultSet rs, Class<?> entityClass)
            throws DataAccessException, IllegalEntityException;

    // Process the writing of big data type, such as:BLOB/CLOB
    protected abstract void ProcessBigDataType(EntityParser parser) throws IllegalEntityException, DataAccessException,
            FileAccessException;

    // Process the writing of big data type, such as:BLOB/CLOB
    protected abstract void ProcessBigDataType(EntityParser dataParser, Criteria criteria)
            throws IllegalEntityException, DataAccessException, FileAccessException;

    // Set the parameter value to the '?' mark for parameterized sql.
    protected abstract void fillParamToMark(PreparedStatement stmt, Vector<String> indexedFieldVec,
                                            EntityParser entityParser) throws DataAccessException, FileAccessException, IllegalEntityException;

    // Set the parameter value to the '?' mark for parameterized sql.
    protected abstract void fillParamToMark(PreparedStatement stmt, SqlParamMap map) throws DataAccessException,
            FileAccessException, IllegalParamException;

}