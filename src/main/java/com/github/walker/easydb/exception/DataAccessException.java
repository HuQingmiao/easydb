package com.github.walker.easydb.exception;

import java.sql.SQLException;


/**
 * <p>
 * This Excepion may be throwed when operating, accessing the permanent data.
 * </p>
 * 
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class DataAccessException extends BaseException {

	//private static LingualResource res = ResourceFactory.getResource();

	public static final String DB_CONNECT_FAILED = "�������ݿ�ʧ�ܣ�";

	public static final String USERNAME_PASSWORD_ERROR = "���ݿ��û��������벻��ȷ��";

	public static final String ONLY_FOR_SELECT = "��ѯ��䲻����'UPDATE ','DELETE ','INSERT ','TRUNCATE ','DROP ','ALTER '��ͷ��";

	public static final String SQL_SYNTAX_ERROR = "SQL���﷨����";

	public static final String TABLE_NOT_EXIST = "�����ͼ�����ڣ�";

	public static final String COLUMN_NOT_EXIST = "���������ڣ�";

	public static final String VALUE_EXCEED_PRECISION = "��ֵ���������ȣ�";

	public static final String INVALID_NUMBER = "��Ч�����֣�";

	public static final String VALUE_EXCEED_BOUND = "�����ֵ�����й���";

	public static final String INVALID_DATE = "��Ч�����ڻ�ʱ�䣡";

	public static final String DISOBEY_UNIQUE = "Υ��ΨһԼ��: �ü�¼�Ѿ����ڣ��벻Ҫ�ظ�¼�룡";

	public static final String DISOBEY_NOTNULL = "�����Ϊ��, ���飡";

	public static final String NOT_SPECIFIED_PK = "��û��ָ��������";

	public static final String WRITING_BIGTYPE_FORONLYONE = "��BLOB/CLOB��д�룬ÿ��ֻ��������������ֵ��ʶ��Ψһ��¼�ϣ�";

	public static final String NOT_ALL_VARIABLE_RELATED = "�������б������ѹ���������ֵ��'?'����٣�";

	public static final String BATCH_PARAM_MORE = "��������ʱ, ĳ�������ֵ��'?'�࣡";

	public static final String BATCH_PARAM_LESS = "��������ʱ, ĳ�������ֵ��'?'�٣�";

	public static final String SQL_CANNOT_NULL = "ִ�е� SQL ��䲻��ΪNULL���ֵ��";

	public static final String RS_COUNT_GREATER1 = "��ѯ�����Ľ��������1������WHERE�Ӿ��еĲ�ѯ������";

	public static final String PK_NOT_CORRECT = "��ѯ�����Ľ��������1������ʵ����pk()������ָ�������������Ƿ���ȷ��";

	public static final String FUNCTION_NOT_EXIST = "�����ڵ����ݿ⺯����";

	// public static final String NODATA_TOLOAD =
	// "��ִ��loadByPK(BaseEntity)����ʱ��û���ҵ�������ֵƥ������ݣ�";

	public static final String NOTFOUND_DB_CONFIG = "û���ҵ����ݿ��������Ϣ��";

	public static final String UNSUPPORTED_DBTABASE = "EasyDB���Ŀǰ����֧���������͵����ݿ⣡";

	public static final String UNKNOW_ERROR = "δ֪�Ĵ���";

	public DataAccessException() {
		super();
	}

	public DataAccessException(String message) {
		super(message);
	}

	/**
	 * ������
	 * 
	 * @param errMsg
	 *            ������Ϣ
	 * @param desc
	 *            ��Ҫ����ĶԴ�����Ϣ������
	 */
	public DataAccessException(String errMsg, String desc) {
		this.message = errMsg + " " + message;
	}
	
	
	/**
	 * 
	 * @param dbType
	 * @param e
	 * @deprecated �������ã������ new DataAccessException(dbType, e.getErrorCode)
	 */
    public DataAccessException(String dbType, SQLException e) {
		this(dbType,e.getErrorCode());
    }
    
	public DataAccessException(String dbType, int errCode) {

		ErrorLocator errLocator = ErrorLocator.getInstance(dbType);

		this.message = errLocator.getErrorMsg(errCode);

	}
	
	public DataAccessException(String dbType, int errCode, String desc) {

		ErrorLocator errLocator = ErrorLocator.getInstance(dbType);

		this.message = errLocator.getErrorMsg(errCode) + " " + desc;

	}
}