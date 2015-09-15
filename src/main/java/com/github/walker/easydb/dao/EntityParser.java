package com.github.walker.easydb.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.ETxtFile;
import com.github.walker.easydb.datatype.UpdateIdentifier;
import com.github.walker.easydb.exception.BaseException;
import com.github.walker.easydb.exception.DataAccessException;
import com.github.walker.easydb.exception.IllegalEntityException;
import com.github.walker.easydb.exception.IllegalParamException;
import org.apache.log4j.Logger;

/**
 * This class parses the BaseEntity object to following parts: the class name of
 * the given BaseEntity object, the fileds and methods of the BaseEntity object.
 *
 * @author HuQingmiao
 */
public class EntityParser {

    private static Logger log = LogFactory.getLogger(EntityParser.class);

    private Connection conn;

    private String className;// the class name of the entity object

    private HashMap<String, FieldExp> fieldExpMap; // 等待更新的属性(fieldName,fieldExp)

    private HashMap<String, Method> fieldMethodMap; // 等待更新的属性及相应Get方法,map(fieldName,gettingMethod)

    private HashSet<String> pkSet; // 主键属性的名称

    // 用于装载非置空的文件类型的属性名称
    // loads the field name of non-empty big field
    private HashSet<String> bigFieldNameSet;

    private String dbType = null;

    /**
     * @param conn
     * @param entity
     * @throws com.github.walker.easydb.exception.IllegalEntityException
     * @throws com.github.walker.easydb.exception.DataAccessException
     */
    public EntityParser(String dbType, Connection conn, BaseEntity entity) throws IllegalEntityException,
            DataAccessException,BaseException {

        this.dbType = dbType;

        this.conn = conn;
        this.className = entity.getClass().getName();

        String[] pkArray = entity.pk();

        // 必须在pk()方法中指定主键属性
        // if (pkArray == null || pkArray.length == 0) {
        // throw new IllegalEntityException(
        // IllegalEntityException.NOT_SPECIFY_PK);
        // }
        this.pkSet = new HashSet<String>(Arrays.asList(pkArray));
        this.bigFieldNameSet = new HashSet<String>(2);

        this.fieldExpMap = new HashMap<String, FieldExp>(10);
        this.fieldMethodMap = new HashMap<String, Method>(10);

        // the getting method name of class BaseEntity
        StringBuffer methodName = new StringBuffer();

        try {
            // the columns set which belong to this table
            HashSet<String> columnSet = this.getColumnNames();

            Field[] fields = entity.getClass().getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {

                // the field name of BaseEntity
                String fieldName = fields[i].getName();

                // builds the method name, such as: "getXX"
                String firstChar = fieldName.substring(0, 1).toUpperCase();
                if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1))) {
                    firstChar = firstChar.toLowerCase();
                }
                methodName.append("get").append(firstChar).append(fieldName.substring(1));
                Method method = entity.getClass().getMethod(methodName.toString(), new Class[]{});
                methodName.delete(0, methodName.length());

                // Getting value of the field by the method.
                Object fieldValue = method.invoke(entity, new Object[]{});

                if (fieldValue != null) {
                    UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldValue;

                    // 只有当该属性需要更新, 或者该属性构成主键时, 才有必要进行分析
                    if (idFieldValue.needUpdate() || pkSet.contains(fieldName)) {

                        // check the field wether or not having column
                        // reflecting
                        if (!columnSet.contains(MappingUtil.getColumnName(fieldName))) {
                            // 该属性在相关表中没有找到对应列, 则略过
                            // throw new IllegalEntityException(
                            // IllegalEntityException.NOTFOUND_REFLECT_COLUMN,
                            // fieldName);
                            continue;
                        }

                        // save the field to map temporarily
                        String fieldType = fields[i].getType().getName();
                        FieldExp fieldExp = new FieldExp(fieldName, fieldType, fieldValue);
                        fieldExpMap.put(fieldName, fieldExp);

                        // save the getting method to map temporarily
                        fieldMethodMap.put(fieldName, method);

                        // 对于非置空的大字段属性， 还要将其属性名存入bigFieldNameSet, 以方便后面对大字段进行处理
                        if (fieldValue instanceof EBinFile || fieldValue instanceof ETxtFile) {

                            if (!idFieldValue.isEmpty()) {
                                bigFieldNameSet.add(fieldName);
                            }
                        }

                    }// end if (idFieldValue.needUpdate(

                }// end if (fieldValue != null

            }

        } catch (SQLException e) {
            log.error(e.getErrorCode(), e);
            throw new DataAccessException(dbType, e.getErrorCode(), e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        }
    }

    /**
     * 只有批量操作才会调用到此方法
     *
     * @param entityParser 首次解析器
     * @param entity       待取值的entity
     * @throws IllegalEntityException
     * @throws com.github.walker.easydb.exception.IllegalParamException
     */
    public EntityParser(EntityParser entityParser, BaseEntity entity) throws IllegalEntityException,
            IllegalParamException {

        // 引用给定的entityParser中的属性
        this.conn = entityParser.conn;
        this.className = entityParser.className;
        this.pkSet = entityParser.pkSet;

        // 由于批量操作不会涉及大字段列， 因此不用对这个属性赋值
        // this.bigFieldNameSet = new HashSet(2);

        this.fieldExpMap = entityParser.fieldExpMap;
        this.fieldMethodMap = entityParser.fieldMethodMap;

        // 重置fieldExpMap中的field值, 并且记录非置空的大字段属性
        this.retrieveFieldValue(entity);
    }

    // 设置fieldExpMap中的field值, 并且记录非置空的大字段属性
    private void retrieveFieldValue(BaseEntity entity) throws IllegalEntityException, IllegalParamException {
        try {
            for (Iterator<String> it = this.fieldExpMap.keySet().iterator(); it.hasNext(); ) {
                String fieldName = (String) it.next();
                FieldExp fieldExp = (FieldExp) fieldExpMap.get(fieldName);
                Method method = (Method) fieldMethodMap.get(fieldName);

                Object fieldValue = method.invoke(entity, new Object[]{});

                // 如果entity中该属性的值为空, 就表明此entity中值的存放位置与首次解析器解析的entity不一致
                // 即某列值在数组中要么都为NULL, 要么都不为NULL.
                if (fieldValue == null) {
                    throw new IllegalParamException(IllegalParamException.ENTITY_ARRAY_NOT_IDENTICAL, fieldName);
                }

                UpdateIdentifier idFieldValue = (UpdateIdentifier) fieldValue;

                // 数组中的某列要么都需要更新，要么都不需要更新！.
                if (!idFieldValue.needUpdate()) {
                    throw new IllegalParamException(IllegalParamException.ENTITY_ARRAY_NOT_IDENTICAL, fieldName);
                }

                // 批量操作时，不允许对大字段列进行批量写入！但可以批量置空
                if ((fieldValue instanceof EBinFile || fieldValue instanceof ETxtFile)
                        && !((UpdateIdentifier) fieldValue).isEmpty()) {
                    throw new IllegalParamException(IllegalParamException.BIGCOLUMN_CANNOT_BATCH, fieldName);
                }

                // set the value to fieldExp
                fieldExp.setFieldValue(fieldValue);
            }

        } catch (IllegalArgumentException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("", e);
            throw new IllegalEntityException(e.getMessage());
        }

    }

    // Gets the column names of the table which reflected this entity class
    private HashSet<String> getColumnNames() throws SQLException {

        // SQL Constructor
        SqlConstructor sqlConstructor = SqlConstructor.getInstance(dbType);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            String sql = sqlConstructor.buildGettingMetaSql(MappingUtil.getTableName(this.className));
            log.info("SQL: " + sql);

            stmt = this.conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            HashSet<String> set = new HashSet<String>();
            for (int i = 1; i <= colCount; i++) {
                set.add(rsmd.getColumnName(i).toUpperCase());
            }
            return set;

        } catch (SQLException e) {
            log.error("", e);
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public HashMap<String, FieldExp> getFieldExpMap() {
        return fieldExpMap;
    }

    public HashMap<String, Method> getFieldMethodMap() {
        return fieldMethodMap;
    }

    public HashSet<String> getBigFieldNameSet() {
        return bigFieldNameSet;
    }

    public HashSet<String> getPKSet() {
        return pkSet;
    }

    public void myFinalize() {

        try {
            if (pkSet != null) {
                pkSet.clear();
            }

            if (bigFieldNameSet != null) {
                bigFieldNameSet.clear();
            }

            if (fieldExpMap != null) {
                fieldExpMap.clear();
            }

            if (fieldMethodMap != null) {
                fieldMethodMap.clear();
            }

            pkSet = null;
            bigFieldNameSet = null;
            fieldExpMap = null;
            fieldMethodMap = null;

            super.finalize();

        } catch (Throwable e) {
            log.error("", e);
        }
    }
}
