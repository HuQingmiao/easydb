package com.github.walker.easydb.dao.oracle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.dao.FieldExp;
import com.github.walker.easydb.dao.ResultAssembler;
import com.github.walker.easydb.dao.SqlConstructor;
import com.github.walker.easydb.exception.FileAccessException;
import com.github.walker.easydb.exception.IllegalParamException;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.EasyDao;
import com.github.walker.easydb.dao.EntityParser;
import com.github.walker.easydb.dao.SqlParamMap;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.EDouble;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.EInteger;
import com.github.walker.easydb.datatype.ELString;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.EString;
import com.github.walker.easydb.datatype.ETimestamp;
import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;

/**
 * Oracle database version of EasyDAO.
 *
 * @author HuQingmiao
 */
public class OracleDAO extends EasyDao {

    public OracleDAO() {
        super();
    }

    public OracleDAO(String jndiKey) {
        super(jndiKey);
    }

    public String getDBType() {
        return "oracle";
    }

    protected SqlConstructor getSqlConstructor() {
        return OracleSqlConstructor.getInstance();
    }

    // 定义字符串最大写入长度， 超过此长度则用流方式写入
    private final int LENGTHB = 2048;

    // create and return the OracleRsAssembler object
    protected ResultAssembler getResultAssembler(ResultSet rs, Class<?> entityClass) {
        return new OracleRsAssembler(rs, entityClass);
    }

    // Process the writing of big data type, such as:BLOB/CLOB
    protected void ProcessBigDataType(EntityParser parser) throws IllegalEntityException, DataAccessException,
            FileAccessException {

        // 非置空的大字段属性
        HashSet<String> bigFieldNameSet = parser.getBigFieldNameSet();
        if (bigFieldNameSet.isEmpty()) {
            return;// if not exists BLOB/CLOB column type, then return.
        }

        // 解析后的非置空的属性
        HashMap<String, FieldExp> fieldExpMap = parser.getFieldExpMap();

        // 主键属性字段
        HashSet<String> pkSet = parser.getPKSet();

        if (pkSet.isEmpty()) {
            throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK2, "");
        }

        OutputStream out = null;
        InputStream in = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            String tableName = MappingUtil.getTableName(parser.getClassName());

            // select the BLOB/CLOB column from TABLE where pk=? for update
            StringBuffer getBClobSql = new StringBuffer();
            getBClobSql.append("SELECT ");
            for (Iterator<String> it = bigFieldNameSet.iterator(); it.hasNext(); ) {
                String fieldName = (String) it.next();
                getBClobSql.append(MappingUtil.getColumnName(fieldName));
                getBClobSql.append(",");
            }
            getBClobSql.deleteCharAt(getBClobSql.length() - 1);

            getBClobSql.append(" FROM ");
            getBClobSql.append(tableName);
            getBClobSql.append(" WHERE 1 = 1 ");

            for (Iterator<String> it = pkSet.iterator(); it.hasNext(); ) {
                // the filed name which be associated with the primary key
                String pkFieldName = (String) it.next();
                getBClobSql.append(" AND ");
                getBClobSql.append(MappingUtil.getColumnName(pkFieldName));
                getBClobSql.append(" = ");

                // ==========================================================================
                // 仅这一段的处理 与 方法ProcessBigDataType(EntityParser parser,Criteria
                // criteria)不同.
                //
                // =========================== BEGIN ==========================
                // ==========================================================================

                FieldExp fieldExp = (FieldExp) fieldExpMap.get(pkFieldName);

                // 如果主键列没有匹配值, 而对BLOB/CLOB的写入必须是某主键值标识的单条记录
                if (fieldExp == null) {
                    throw new DataAccessException(DataAccessException.WRITING_BIGTYPE_FORONLYONE, parser.getClassName());
                }
                Object fieldValue = fieldExp.getFieldValue();

                // 对于字符串类型的列,在参数值前后加上 ' 号
                if (fieldValue instanceof EString) {
                    getBClobSql.append("'");
                    getBClobSql.append(fieldValue.toString());
                    getBClobSql.append("'");
                } else if (fieldValue instanceof ELong) {
                    getBClobSql.append(((ELong) fieldValue).longValue());
                } else if (fieldValue instanceof EInteger) {
                    getBClobSql.append(((EInteger) fieldValue).intValue());
                } else if (fieldValue instanceof EDouble) {
                    getBClobSql.append(((EDouble) fieldValue).doubleValue());
                } else if (fieldValue instanceof EFloat) {
                    getBClobSql.append(((EFloat) fieldValue).floatValue());
                } else {
                    // 在含有大字段列的表中，只能以数字或字符串类型的列作为主键，而不能是日期或大字段列！
                    throw new IllegalEntityException(IllegalEntityException.EXCEED_PK_COLUMN_SCOPE, "");
                }
                // ==========================================================================
                // 仅这一段的处理 与 方法(EntityParser parser,Criteria criteria)不同.
                //
                // =========================== END ==========================
                // ==========================================================================

            }// enf for (Iterator it

            getBClobSql.append(" FOR UPDATE ");
            log.info("SQL: " + getBClobSql.toString());

            stmt = conn.prepareStatement(getBClobSql.toString());
            getBClobSql.delete(0, getBClobSql.length());
            rs = stmt.executeQuery();

            if (rs.next()) {
                int k = 1;
                for (Iterator<String> it = bigFieldNameSet.iterator(); it.hasNext(); ) {
                    String fieldName = (String) it.next();
                    FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);

                    Object fieldValue = fieldExp.getFieldValue();

                    // bsolute path of the file
                    String filePath = ((File) fieldValue).getCanonicalPath();

                    // if the file not exists
                    if (!(new File(filePath).exists())) {
                        rs.close();
                        stmt.close();
                        throw new FileAccessException(FileAccessException.FILE_NOTFOUND, filePath);
                    }

                    // BLOB
                    if (fieldValue instanceof EBinFile) {
                        Blob blob = rs.getBlob(k++);

                        //采用oracle早期的驱动包ojdbc14_g.jar，对BLOB类型的处理
                        //out = ((oracle.sql.BLOB) blob).getBinaryOutputStream();

                        //采用ojdbc5.jar驱动包后，对BLOB的处理，其写法不需要再转型。
                        out = blob.setBinaryStream(1L);

                        in = new FileInputStream(filePath);
                        byte[] b = new byte[((oracle.sql.BLOB) blob).getBufferSize()];

                        int len = 0;
                        while ((len = in.read(b)) != -1)
                            out.write(b, 0, len);
                        in.close();
                        out.close();

                        // File inFile = new File(filePath);
                        // in = new FileInputStream(inFile);
                        // out = blob.setBinaryStream(inFile.length());
                        //
                        // byte[] b = new byte[1024*8];
                        // int len = 0;
                        // while ((len = in.read(b)) != -1)
                        // out.write(b, 0, len);
                        //
                        // in.close();
                        // out.close();

                        // CLOB
                    } else {
                        Clob clob = rs.getClob(k++);


                        //采用oracle早期的驱动包ojdbc14_g.jar，对CLOB类型的处理
                        //out = ((oracle.sql.CLOB) clob).getAsciiOutputStream();

                        //采用ojdbc5.jar驱动包后，对CLOB的处理，其写法不需要再转型。
                        out = clob.setAsciiStream(1L);


                        in = new FileInputStream(filePath);

                        byte[] b = new byte[((oracle.sql.CLOB) clob).getBufferSize()];
                        int len = 0;
                        while ((len = in.read(b)) != -1)
                            out.write(b, 0, len);
                        in.close();
                        out.close();
                    }
                }// end for (Iterator it
            }// end if (rs.next

        } catch (SQLException e) {
            log.error("", e);
            throw new DataAccessException(e.getMessage());
        } catch (IOException e) {
            log.error("", e);
            throw new FileAccessException(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (Exception e1) {
                log.error("", e1);
            }
        }
    }

    // Process the writing of big data type, such as:BLOB/CLOB
    protected void ProcessBigDataType(EntityParser parser, Criteria criteria) throws IllegalEntityException,
            DataAccessException, FileAccessException {

        // 非置空的大字段属性
        HashSet<String> bigFieldNameSet = parser.getBigFieldNameSet();
        if (bigFieldNameSet.isEmpty()) {
            return;// if not exists BLOB/CLOB column type, then return.
        }

        // 解析后的非置空的属性
        HashMap<String, FieldExp> fieldExpMap = parser.getFieldExpMap();

        // 主键属性字段
        HashSet<String> pkSet = parser.getPKSet();

        if (pkSet.isEmpty()) {
            throw new IllegalEntityException(IllegalEntityException.NOT_SPECIFY_PK2, "");
        }

        OutputStream out = null;
        InputStream in = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            String tableName = MappingUtil.getTableName(parser.getClassName());

            // select the BLOB/CLOB column from TABLE where pk=? for update
            StringBuffer getBClobSql = new StringBuffer();
            getBClobSql.append("SELECT ");
            for (Iterator<String> it = bigFieldNameSet.iterator(); it.hasNext(); ) {
                String fieldName = it.next();
                getBClobSql.append(MappingUtil.getColumnName(fieldName));
                getBClobSql.append(",");
            }
            getBClobSql.deleteCharAt(getBClobSql.length() - 1);

            getBClobSql.append(" FROM ");
            getBClobSql.append(tableName);
            getBClobSql.append(" WHERE 1 = 1 ");

            for (Iterator<String> it = pkSet.iterator(); it.hasNext(); ) {

                // the filed name which be associated with the primary key
                String pkFieldName = (String) it.next();
                String columnName = MappingUtil.getColumnName(pkFieldName);

                getBClobSql.append(" AND ");
                getBClobSql.append(columnName);
                getBClobSql.append(" = ");

                // ==========================================================================
                // 仅这一段的处理 与 方法ProcessBigDataType(EntityParser parser)不同.
                //
                // =========================== BEGIN
                // ==========================
                // ==========================================================================
                String colValue = criteria.getValueByLeft(columnName);

                // 如果主键列没有匹配值, 而对BLOB/CLOB的写入必须是某主键值标识的单条记录
                if (colValue == null) {
                    throw new DataAccessException(DataAccessException.WRITING_BIGTYPE_FORONLYONE, "");
                }

                getBClobSql.append(colValue);
                // ===========================================================================
                // =========================== EDN
                // ==========================
                // ===========================================================================

            }// enf for (Iterator it

            getBClobSql.append(" FOR UPDATE ");
            log.info("SQL: " + getBClobSql.toString());

            stmt = conn.prepareStatement(getBClobSql.toString());
            getBClobSql.delete(0, getBClobSql.length());
            rs = stmt.executeQuery();

            if (rs.next()) {
                int k = 1;
                for (Iterator<String> it = bigFieldNameSet.iterator(); it.hasNext(); ) {
                    String fieldName = (String) it.next();
                    FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);

                    Object fieldValue = fieldExp.getFieldValue();

                    // bsolute path of the file
                    String filePath = ((File) fieldValue).getCanonicalPath();

                    // if the file not exists
                    if (!(new File(filePath).exists())) {
                        rs.close();
                        stmt.close();
                        throw new FileAccessException(FileAccessException.FILE_NOTFOUND, filePath);
                    }

                    // BLOB
                    if (fieldValue instanceof EBinFile) {
                        Blob blob = rs.getBlob(k++);

                        //采用oracle早期的驱动包ojdbc14_g.jar，对BLOB类型的处理
                        //out = ((oracle.sql.BLOB) blob).getBinaryOutputStream();

                        //采用ojdbc5.jar驱动包后，对BLOB的处理，其写法不需要再转型。
                        out = blob.setBinaryStream(1L);

                        in = new FileInputStream(filePath);
                        byte[] b = new byte[((oracle.sql.BLOB) blob).getBufferSize()];

                        int len = 0;
                        while ((len = in.read(b)) != -1)
                            out.write(b, 0, len);
                        in.close();
                        out.close();

                        // CLOB
                    } else {
                        Clob clob = rs.getClob(k++);

                        //采用oracle早期的驱动包ojdbc14_g.jar，对CLOB类型的处理
                        //out = ((oracle.sql.CLOB) clob).getAsciiOutputStream();

                        //采用ojdbc5.jar驱动包后，对CLOB的处理，其写法不需要再转型。
                        out = clob.setAsciiStream(1L);


                        in = new FileInputStream(filePath);

                        byte[] b = new byte[((oracle.sql.CLOB) clob).getBufferSize()];
                        int len = 0;
                        while ((len = in.read(b)) != -1)
                            out.write(b, 0, len);
                        in.close();
                        out.close();
                    }
                }// end for (Iterator it
            }// end if (rs.next

        } catch (SQLException e) {
            log.error("", e);
            throw new DataAccessException(e.getMessage());
        } catch (IOException e) {
            log.error("", e);
            throw new FileAccessException(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (Exception e1) {
                log.error("", e1);
            }
        }
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

                            //在数据库编码非GBK的情况下，在写入字节数超过2048的中文时，本行代码会抛出数组下标越界的异常
                            //log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, LENGTHB / 2) + "...");

                            // 为解决以上错误，将上行代码改为：在写入字节数超过2048的字符时，在debug时只输入200个字符
                            int showTextSize = 200;
                            log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, showTextSize) + "...");


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

                            //在数据库编码非GBK的情况下，在写入字节数超过2048的中文时，本行代码会抛出数组下标越界的异常
                            //log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, LENGTHB / 2) + "...");

                            // 为解决以上错误，将上行代码改为：在写入字节数超过2048的字符时，在debug时只输入200个字符
                            int showTextSize = 200;
                            log.debug("prepared to set " + (i + 1) + ": " + e.toString().substring(0, showTextSize) + "...");


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
                            // 在数据库编码非GBK的情况下，在写入字节数超过2048的中文时，本行代码会抛出数组下标越界的异常
                            // log.debug("prepared to set " + i + ": " + e.toString().substring(0, LENGTHB / 2) + "...");

                            // 为解决以上错误，将上行代码改为：在写入字节数超过2048的字符时，在debug时只输入200个字符
                            int showTextSize = 200;
                            log.debug("prepared to set " + i + ": " + e.toString().substring(0, showTextSize) + "...");

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
                        //在数据库编码非GBK的情况下，在写入字节数超过2048的中文时，本行代码会抛出数组下标越界的异常
                        //log.debug("prepared to set " + i + ": " + e.toString().substring(0, LENGTHB / 2) + "...");

                        // 为解决以上错误，将上行代码改为：在写入字节数超过2048的字符时，在debug时只输入200个字符
                        int showTextSize = 200;
                        log.debug("prepared to set " + i + ": " + e.substring(0, showTextSize) + "...");

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
                            // 在数据库编码非GBK的情况下，在写入字节数超过2048的中文时，本行代码会抛出数组下标越界的异常
                            // log.debug("prepared to set " + i + ": " + e.toString().substring(0, LENGTHB / 2) + "...");

                            // 为解决以上错误，将上行代码改为：在写入字节数超过2048的字符时，在debug时只输入200个字符
                            int showTextSize = 200;
                            log.debug("prepared to set " + i + ": " + e.toString().substring(0, showTextSize) + "...");

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

                } else if (obj instanceof EBinFile) {
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

}