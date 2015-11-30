package com.github.walker.easydb.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.exception.BaseException;
import com.github.walker.easydb.exception.IllegalEntityException;
import org.json.JSONObject;

/**
 * 抽象实体类， 所有实体类的基类， 其子类与数据库的元数据形成映射。 <br>
 * <p/>
 * This class reflects the meta data in database. The definition of its subclass
 * must observe following items. <br>
 * <br>
 * <br>
 * <p/>
 * 1. 类名与表名（或视图名）必须有明确的对应关系；除表名中的第一个字母、以及紧跟在下化线"_"后的字母在类名中必须大写外，
 * 其它字母在类名中必须小写；且表名中各字母前的下化线"_"在类名中要被删除。举例如下： <br>
 * 表名为 "COMPANY"， 则对应的类名只能为"Company"; 表名为 "COMPANY_INFO"，
 * 则对应的类名只能为"CompanyInfo"; 表名为 "NET_INFO3"， 则对应的类名只能为"NetInfo3"; 表名为
 * "NET_INFO_3"， 则对应的类名只能为"NetInfo_3"。 <br>
 * <br>
 * <p/>
 * The relation of Class name and table name is one to one, except for the first
 * character and the character behind the underline('_') must be uppercase, all
 * other character must be lowercase in class name. <br>
 * <br>
 * <br>
 * <p/>
 * <p/>
 * 2. 在某实体类中，属性名与列名(或列的别名)也必须有明确的对应关系；除紧跟在下化线”_”后的字母在列名中必须大写外，
 * 其它字母在列名中必须小写；且列名中各字母前的下化线"_"在属性名中要被删除。举例如下： <br>
 * 列名为 “NAME”， 则对应的类名只能为“name”； 列名为 “COMPANY_NAME”， 则对应的类名只能为“companyName”； 列名为
 * “NET_TYPE2”， 则对应的类名只能为“NetType2”； 列名为 “NET_TYPE_2”， 则对应的类名只能为“NetType_2”。
 * <br>
 * <br>
 * <p/>
 * In one Entity Class, The relation of field name and column name is one to
 * one, except for the character behind the underline('_') must be uppercase,
 * all other character must be lowercase in class name. <br>
 * <br>
 * <br>
 * <p/>
 * <p/>
 * 3. 属性类型必须与下表中的对应关系一致。 如：<br>
 * 对于Oracle数据库， 如果某列被定义为 CHAR， 则其在对应实体类中必须被声明为 walker.easydb.datatype.EString;
 * 如果某列被定义为BLOB，则其在对应实体类中必须被声明为walker.easydb.datatype.EBinFile类型。 如下表所示: <br>
 * <br>
 * <p/>
 * The field types of entity class cann't be primitive, and must correspond with
 * the column data type, as follows: <br>
 * <br>
 * <p/>
 * <p/>
 * <table width=90% border=1 cellspacing=0 cellpadding=0>
 * <p/>
 * <tr>
 * <td align=center height=20>数据库类型</td>
 * <td align=center>数据库中列的数据类型</td>
 * <td align=center>实体类中属性的数据类型</td>
 * </tr>
 * <tr>
 * <td align=center rowspan=5 valign="middle">Oracle</td>
 * <td align=left >&nbspCHAR <br>
 * &nbspVARCHAR <br>
 * &nbspVARCHAR2</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EString</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspNUMBER</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EInteger <br>
 * &nbspwalker.easydb.datatype.ELong <br>
 * &nbspwalker.easydb.datatype.EFloat <br>
 * &nbspwalker.easydb.datatype.EDouble <br>
 * </td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspDATE</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.Timestamp</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspCLOB</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.ETxtFile</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspBLOB</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EBinFile</td>
 * </tr>
 * <p/>
 * <tr>
 * <td align=center rowspan=10 valign="middle">mysql</td>
 * <td align=left >&nbspCHAR <br>
 * &nbspVARCHAR <br>
 * &nbspSET</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EString</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspSMALLINT</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EInteger <br>
 * &nbspwalker.easydb.datatype.ELong</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspINT</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EInteger <br>
 * &nbspwalker.easydb.datatype.ELong</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspBIGINT</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.ELong</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspFLOAT</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EFloat <br>
 * &nbspwalker.easydb.datatype.EDouble</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspDOUBLE</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EDouble</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspDATE <br>
 * &nbspDATETIME</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.Timestamp</td>
 * </tr>
 * <tr>
 * <td align=left >&nbspTINYTEXT <br>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EString <br>
 * &nbspwalker.easydb.datatype.ETxtFile</td>
 * </tr>
 * <tr>
 * <td align=left >&nbspTEXT <br>
 * &nbspMEDIUMTEXT <br>
 * &nbspLONGTEXT <br>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.ETxtFile</td>
 * </tr>
 * <tr>
 * <td align=left valign=middle>&nbspTINYBLOB <br>
 * &nbspBLOB <br>
 * &nbspMEDIUMBLOB <br>
 * &nbspLONGBLOB</td>
 * <td align=left valign=middle>&nbspwalker.easydb.datatype.EBinFile</td>
 * </tr>
 * </table>
 * <br>
 * <p/>
 * <p/>
 * 4. 子类必须实现方法: public abstract String[] pk(), 该方法用来指定主键属性.
 * <br>
 * <br>
 *
 * @author HuQingmiao
 */
//* =====================================================================<br>
//* | 数据库类型 | 数据库中列的数据类型 | 实体类中属性的数据类型 |<br>
//* |-------------------------------------------------------------------|<br>
//* | | CHAR | |<br>
//* | | VARCHAR |walker.easydb.datatype.EString |<br>
//* | | VARCHAR2 | |<br>
//* | |------------------------------------------------------|<br>
//* | | | walker.easydb.datatype.EInteger |<br>
//* | | NUMBER | walker.easydb.datatype.ELong |<br>
//* | Oracle | | walker.easydb.datatype.EFloat |<br>
//* | | | walker.easydb.datatype.EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DATE |walker.easydb.datatype.ETimestamp|<br>
//* | |------------------------------------------------------|<br>
//* | | CLOB |walker.easydb.datatype.ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | BLOB |walker.easydb.datatype.EBinFile |<br>
//* |-------------------------------------------------------------------|<br>
//* | | CHAR | |<br>
//* | | VARCHAR |walker.easydb.datatype.EString |<br>
//* | | SET | |<br>
//* | |------------------------------------------------------|<br>
//* | | SMALLINT |walker.easydb.datatype.EInteger |<br>
//* | |------------------------------------------------------|<br>
//* | | INT |walker.easydb.datatype.EInteger |<br>
//* | | |walker.easydb.datatype.ELong |<br>
//* | |------------------------------------------------------|<br>
//* | | BIGINT |walker.easydb.datatype.ELong |<br>
//* | |------------------------------------------------------|<br>
//* | mysql | FLOAT |walker.easydb.datatype.EFloat |<br>
//* | | |walker.easydb.datatype.EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DOUBLE |walker.easydb.datatype.EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DATE | |<br>
//* | | DATETIME |walker.easydb.datatype.ETimestamp|<br>
//* | |------------------------------------------------------|<br>
//* | | |walker.easydb.datatype.EString |<br>
//* | | TINYTEXT |walker.easydb.datatype.ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | TEXT | |<br>
//* | | MEDIUMTEXT | |<br>
//* | | LONGTEXT |walker.easydb.datatype.ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | TINYBLOB | |<br>
//* | | BLOB | |<br>
//* | | MEDIUMBLOB | |<br>
//* | | LONGBLOB |walker.easydb.datatype.EBinFile |<br>
//* |-------------------------------------------------------------------|<br>
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {


    private static Logger log = LogFactory.getLogger(BaseEntity.class);

    //存放当前实体类的属性及类型
    private HashMap<String, Class<?>> fieldNameTypeMap = new HashMap<String, Class<?>>(10);

    //    public EString rowid;//行版本号. 必须为public,否则反射不可见.

    public BaseEntity() {

        //将属性及其类型存入map(fieldName,fieldType)
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            fieldNameTypeMap.put(fields[i].getName(), fields[i].getType());
        }
    }

    /**
     * 取得代表主键的属性组
     *
     * @return 代表主键的属性组
     */
    public abstract String[] pk();

    /**
     * 根据属性名取值
     *
     * @param propertyName 属性名
     * @return 属性值
     * @throws BaseException
     */
    public Object get(String propertyName) throws BaseException {

        String firstChar = propertyName.substring(0, 1).toUpperCase();
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
            firstChar = firstChar.toLowerCase();
        }
        String methodName = "get" + firstChar + propertyName.substring(1);

        try {
            //builds the method name, such as: "getXX"
            Method method = this.getClass().getMethod(methodName,
                    new Class[]{});

            //Getting value of the field by the method.
            Object fieldValue = method.invoke(this, new Object[]{});

            return fieldValue;

        } catch (NoSuchMethodException e) {
            log.error("", e);
            throw new IllegalEntityException(
                    IllegalEntityException.PROPERTY_NOTEXIST_OR_GET,
                    propertyName);
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
     * 对指定属性设置值
     *
     * @param propertyName
     * @param value
     * @throws BaseException
     */
    public void set(String propertyName, Object value) throws BaseException {

        String firstChar = propertyName.substring(0, 1).toUpperCase();
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
            firstChar = firstChar.toLowerCase();
        }
        String methodName = "set" + firstChar + propertyName.substring(1);

        try {
            Method method = this.getClass().getMethod(methodName,
                    new Class[]{(Class<?>) fieldNameTypeMap.get(propertyName)});

            //Setting value of the field by the method.
            method.invoke(this, new Object[]{value});

        } catch (NoSuchMethodException e) {
            log.error("", e);
            throw new IllegalEntityException(
                    IllegalEntityException.PROPERTY_NOTEXIST_OR_SET,
                    propertyName);
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


    public HashMap<String, Class<?>> fieldNameTypeMap() {
        return this.fieldNameTypeMap;
    }


    @Override
    public int hashCode() {
        int hashCode = 1;
        Map<String, Object> keyObjectMap = new HashMap<String, Object>();
        try {
            for (Iterator<String> it = fieldNameTypeMap.keySet().iterator(); it.hasNext(); ) {
                String filedName = it.next();
                Object obj = this.get(filedName);
                hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        keyObjectMap.clear();
        return hashCode;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseEntity)) {
            return false;
        }

        BaseEntity vo2 = ((BaseEntity) o);
        HashMap<String, Class<?>> ftMap2 = vo2.fieldNameTypeMap();
        if (fieldNameTypeMap.size() != ftMap2.size()) {
            return false;
        }

        try {
            for (Iterator<String> it = fieldNameTypeMap.keySet().iterator(); it.hasNext(); ) {
                String filedName = it.next();
                Object obj1 = this.get(filedName);
                Object obj2 = vo2.get(filedName);
                if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
