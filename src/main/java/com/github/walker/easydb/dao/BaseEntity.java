package com.github.walker.easydb.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.github.walker.easydb.exception.BaseException;
import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.exception.IllegalEntityException;

/**
 * ����ʵ���࣬ ����ʵ����Ļ��࣬ �����������ݿ��Ԫ�����γ�ӳ�䡣 <br>
 * 
 * This class reflects the meta data in database. The definition of its subclass
 * must observe following items. <br>
 * <br>
 * <br>
 * 
 * 1. ���������������ͼ������������ȷ�Ķ�Ӧ��ϵ���������еĵ�һ����ĸ���Լ��������»���"_"�����ĸ�������б����д�⣬
 * ������ĸ�������б���Сд���ұ����и���ĸǰ���»���"_"��������Ҫ��ɾ�����������£� <br>
 * ����Ϊ "COMPANY"�� ���Ӧ������ֻ��Ϊ"Company"; ����Ϊ "COMPANY_INFO"��
 * ���Ӧ������ֻ��Ϊ"CompanyInfo"; ����Ϊ "NET_INFO3"�� ���Ӧ������ֻ��Ϊ"NetInfo3"; ����Ϊ
 * "NET_INFO_3"�� ���Ӧ������ֻ��Ϊ"NetInfo_3"�� <br>
 * <br>
 * 
 * The relation of Class name and table name is one to one, except for the first
 * character and the character behind the underline('_') must be uppercase, all
 * other character must be lowercase in class name. <br>
 * <br>
 * <br>
 * 
 * 
 * 2. ��ĳʵ�����У�������������(���еı���)Ҳ��������ȷ�Ķ�Ӧ��ϵ�����������»��ߡ�_�������ĸ�������б����д�⣬
 * ������ĸ�������б���Сд���������и���ĸǰ���»���"_"����������Ҫ��ɾ�����������£� <br>
 * ����Ϊ ��NAME���� ���Ӧ������ֻ��Ϊ��name���� ����Ϊ ��COMPANY_NAME���� ���Ӧ������ֻ��Ϊ��companyName���� ����Ϊ
 * ��NET_TYPE2���� ���Ӧ������ֻ��Ϊ��NetType2���� ����Ϊ ��NET_TYPE_2���� ���Ӧ������ֻ��Ϊ��NetType_2����
 * <br>
 * <br>
 * 
 * In one Entity Class, The relation of field name and column name is one to
 * one, except for the character behind the underline('_') must be uppercase,
 * all other character must be lowercase in class name. <br>
 * <br>
 * <br>
 * 
 * 
 * 3. �������ͱ������±��еĶ�Ӧ��ϵһ�¡� �磺<br>
 * ����Oracle���ݿ⣬ ���ĳ�б�����Ϊ CHAR�� �����ڶ�Ӧʵ�����б��뱻����Ϊ EString;
 * ���ĳ�б�����ΪBLOB�������ڶ�Ӧʵ�����б��뱻����Ϊwalker.easydb.datatype.EBinFile���͡� ���±���ʾ: <br>
 * <br>
 * 
 * The field types of entity class cann't be primitive, and must correspond with
 * the column data type, as follows: <br>
 * <br>
 * 
 * 
 * <table width=90% border=1 cellspacing=0 cellpadding=0>
 * 
 * <tr>
 * <td align=center height=20>���ݿ�����</td>
 * <td align=center>���ݿ����е���������</td>
 * <td align=center>ʵ���������Ե���������</td>
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
 * 
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
 * 
 * 
 * 4. �������ʵ�ַ���: public abstract String[] pk(), �÷�������ָ����������.
 * <br>
 * <br>
 * 
 * @author HuQingmiao
 */
//* =====================================================================<br>
//* | ���ݿ����� | ���ݿ����е��������� | ʵ���������Ե��������� |<br>
//* |-------------------------------------------------------------------|<br>
//* | | CHAR | |<br>
//* | | VARCHAR |EString |<br>
//* | | VARCHAR2 | |<br>
//* | |------------------------------------------------------|<br>
//* | | | EInteger |<br>
//* | | NUMBER | ELong |<br>
//* | Oracle | | EFloat |<br>
//* | | | EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DATE |ETimestamp|<br>
//* | |------------------------------------------------------|<br>
//* | | CLOB |ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | BLOB |EBinFile |<br>
//* |-------------------------------------------------------------------|<br>
//* | | CHAR | |<br>
//* | | VARCHAR |EString |<br>
//* | | SET | |<br>
//* | |------------------------------------------------------|<br>
//* | | SMALLINT |EInteger |<br>
//* | |------------------------------------------------------|<br>
//* | | INT |EInteger |<br>
//* | | |ELong |<br>
//* | |------------------------------------------------------|<br>
//* | | BIGINT |ELong |<br>
//* | |------------------------------------------------------|<br>
//* | mysql | FLOAT |EFloat |<br>
//* | | |EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DOUBLE |EDouble |<br>
//* | |------------------------------------------------------|<br>
//* | | DATE | |<br>
//* | | DATETIME |ETimestamp|<br>
//* | |------------------------------------------------------|<br>
//* | | |EString |<br>
//* | | TINYTEXT |ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | TEXT | |<br>
//* | | MEDIUMTEXT | |<br>
//* | | LONGTEXT |ETxtFile |<br>
//* | |------------------------------------------------------|<br>
//* | | TINYBLOB | |<br>
//* | | BLOB | |<br>
//* | | MEDIUMBLOB | |<br>
//* | | LONGBLOB |EBinFile |<br>
//* |-------------------------------------------------------------------|<br>
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {


    private static Logger log = LogFactory.getLogger(BaseEntity.class);

    //��ŵ�ǰʵ��������Լ�����
    private HashMap<String,Class<?>> fieldNameTypeMap = new HashMap<String,Class<?>>(10);

    //    public EString rowid;//�а汾��. ����Ϊpublic,�����䲻�ɼ�.

    public BaseEntity() {

        //�����Լ������ʹ���map(fieldName,fieldType)
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            fieldNameTypeMap.put(fields[i].getName(), fields[i].getType());
        }
    }

    /**
     * ȡ�ô���������������
     * 
     * @return ����������������
     */
    public abstract String[] pk();

    /**
     * ����������ȡֵ
     * 
     * @param propertyName
     *            ������
     * 
     * @return ����ֵ
     * 
     * @throws com.github.walker.easydb.exception.BaseException
     */
    public Object get(String propertyName) throws BaseException {

        String methodName = "get"
                + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);

        try {
            //builds the method name, such as: "getXX"
            Method method = this.getClass().getMethod(methodName,
                    new Class[] {});

            //Getting value of the field by the method.
            Object fieldValue = method.invoke(this, new Object[] {});

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
     * ��ָ����������ֵ
     * 
     * @param propertyName
     * @param value
     * @throws BaseException
     */
    public void set(String propertyName, Object value) throws BaseException {

        String methodName = "set"
                + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);

        try {
            Method method = this.getClass().getMethod(methodName,
                    new Class[] { (Class<?>) fieldNameTypeMap.get(propertyName) });

            //Setting value of the field by the method.
            method.invoke(this, new Object[] { value });

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
    
    
    public HashMap<String,Class<?>> fieldNameTypeMap(){
        return this.fieldNameTypeMap;
    }

}
