package com.github.walker.easydb.exception;

/**
 * <p>
 * Locator of database error, it translates the error code of Oracle into
 * unified error errMsg.
 * </p>
 * 
 * @author HuQingmiao
 */
public class OracleErrorLocator extends ErrorLocator {

	public String getErrorMsg(int errorCode) {

		String errMsg = DataAccessException.UNKNOW_ERROR;

		switch (errorCode) {

		case 17002:// ���Ӳ������ݿ�
			errMsg = DataAccessException.DB_CONNECT_FAILED;
			break;

		case 1017: // �û���/�������
			errMsg = DataAccessException.USERNAME_PASSWORD_ERROR;
			break;

		case 923: // �Ҳ���FROM�ؼ���
		case 933: // SQL�����﷨����
			errMsg = DataAccessException.SQL_SYNTAX_ERROR;
			break;

		case 1: // Υ��ΨһԼ��
			errMsg = DataAccessException.DISOBEY_UNIQUE;
			break;

		case 942: // �����ͼ������
			errMsg = DataAccessException.TABLE_NOT_EXIST;
			break;

		case 904: // ����������
			errMsg = DataAccessException.COLUMN_NOT_EXIST;
			break;

		case 1438: // ��ֵ����������
			errMsg = DataAccessException.VALUE_EXCEED_PRECISION;
			break;

		case 1401: // �����ֵ�����й���
		case 17070:
			errMsg = DataAccessException.VALUE_EXCEED_BOUND;
			break;

		case 1722: // ��Ч����
			errMsg = DataAccessException.INVALID_NUMBER;
			break;

		case 1858:
		case 1847:
		case 1861:// ��Ч�����ڻ�ʱ��
			errMsg = DataAccessException.INVALID_DATE;
			break;

		case 1400:
		case 1407:// Υ���ǿ�Լ��
			errMsg = DataAccessException.DISOBEY_NOTNULL;
			break;

		case 1008:// �������б������ѹ���������ֵ��'?'����٣�
			errMsg = DataAccessException.NOT_ALL_VARIABLE_RELATED;
			break;
		case 17003:// ��������ʱ, ĳ�������ֵ��'?'�࣡
			errMsg = DataAccessException.BATCH_PARAM_MORE;
			break;
		case 17041:// ��������ʱ, ĳ�������ֵ��'?'�٣�
			errMsg = DataAccessException.BATCH_PARAM_LESS;
			break;
		case 17104:// Ҫִ�е� SQL ��䲻��Ϊ�հ׻��ֵ��
			errMsg = DataAccessException.SQL_CANNOT_NULL;
			break;
		}
		return errMsg;
	}
}