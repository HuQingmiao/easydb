package com.github.walker.easydb.entitygen;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.connection.ConnectionPool;
import com.github.walker.easydb.datatype.*;


import java.sql.Types;
import java.sql.*;
import java.util.*;

/**
 * 元数据映射类
 * <p/>
 * Created by HuQingmiao.
 */
class MetaMapping {

    //表名
    private String tableName;

    //表的各列及元数据
    private Map<String, MetaDataDescr> colNameMetaMap = new LinkedHashMap<String, MetaDataDescr>();


    protected MetaMapping(String tableName) {
        try {
            this.tableName = tableName;
            this.parseMetaData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Map<String, MetaDataDescr> getColNameMetaMap() {
        return this.colNameMetaMap;
    }

    /**
     * 取得表的元数据，即取得各列名及类型
     *
     * @return 列名及其列类型：LinkedHashMap<String, MyMetaData> map
     * @throws Exception
     */
    protected void parseMetaData()
            throws Exception {
        ConnectionPool connPool = ConnectionPool.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = connPool.getConnection();

            //定位主键字段
            Set<String> keySet = new HashSet<String>();

            rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), null
                    , tableName.toUpperCase());
            for (; rs.next(); ) {
                keySet.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
            rs.close();

            //获取列元数据
            String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String colName = rsmd.getColumnName(i).toLowerCase();
                System.out.println(colName + ": " + rsmd.getColumnType(i) + "("
                        + rsmd.getColumnTypeName(i) + "), " + rsmd.getPrecision(i) + "(精确度), " + rsmd.getScale(i) + "(小数点后位数)");

                MetaDataDescr md = new MetaDataDescr();
                md.setColName(colName);
                md.setColType(rsmd.getColumnType(i));
                md.setPrecision(rsmd.getPrecision(i));
                md.setScale(rsmd.getScale(i));

                if (keySet.contains(colName)) {
                    md.setPk(true);
                } else {
                    md.setPk(false);
                }

                String fileldName = MappingUtil.getFieldName(colName);
                md.setFieldName(fileldName);

                //把列类型映射为类属性类型
                md.setFieldType(reflectToFieldType(md.getColType(), md.getScale()));

                colNameMetaMap.put(colName, md);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connPool.release(conn, stmt, rs);
        }
    }

    /**
     * 把列类型映射为类属性类型
     *
     * @param colType
     * @return
     * @throws Exception
     */
    private Class reflectToFieldType(int colType, int scale) throws Exception {

        switch (colType) {
            case Types.BIT:
                return EInteger.class;

            case Types.TINYINT:
                return EInteger.class;
            case Types.SMALLINT:
                return EInteger.class;
            case Types.INTEGER:
                return EInteger.class;
            case Types.BIGINT:
                return ELong.class;

            case Types.FLOAT:
                return EFloat.class;
            case Types.REAL:
                return EDouble.class;
            case Types.DOUBLE:
                return EDouble.class;
            case Types.NUMERIC:
                if (scale == 0) {
                    return ELong.class;
                } else {
                    throw new Exception("不支持的列类型:" + colType);
                    //return java.math.BigDecimal.class;
                }
            case Types.DECIMAL:
                if (scale == 0) {
                    return ELong.class;
                } else {
                    throw new Exception("不支持的列类型:" + colType);
                    //return java.math.BigDecimal.class;
                }
            case Types.CHAR:
                return EString.class;
            case Types.VARCHAR:
                return EString.class;
            case Types.LONGVARCHAR:
                return EString.class;

            case Types.DATE:
                return ETimestamp.class;
            case Types.TIME:
                return ETimestamp.class;
            case Types.TIMESTAMP:
                return ETimestamp.class;

            case Types.BINARY:
                return EBinFile.class;
            case Types.VARBINARY:
                return EBinFile.class;
            case Types.LONGVARBINARY:
                return EBinFile.class;

            case Types.BLOB:
                return EBinFile.class;
            case Types.CLOB:
                return ETxtFile.class;
        }

        throw new Exception("不能识别的列类型:" + colType);
    }

    public static void main(String[] args) {
        System.out.println(Byte[].class.getName());
        System.out.println(Byte[].class.getSimpleName());

        System.out.println(byte[].class.getName());
        System.out.println(byte[].class.getSimpleName());
    }
}
