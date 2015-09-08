package com.github.walker.easydb.exception;

/**
 * <p>
 * Locator of mysql error, it points out the detail error message.
 * </p>
 * 
 * @author HuQingmiao
 */
public class MysqlErrorLocator extends ErrorLocator {

	public String getErrorMsg(int errorCode) {

		String errMsg = DataAccessException.UNKNOW_ERROR;

		switch (errorCode) {

		// ���Ӳ������ݿ�,��ֵ����������,��ֵ����������,�����ֵ�����й���,��Ч����,��Ч�����ڻ�ʱ��
		// �������б������ѹ���������ֵ��'?'����٣�
		// ��������ʱ, ĳ�������ֵ��'?'�࣡
		// ��������ʱ, ĳ�������ֵ��'?'�٣�
		case 0:
			errMsg = DataAccessException.UNKNOW_ERROR;
			break;

		case 1045: // �û���/�������
			errMsg = DataAccessException.USERNAME_PASSWORD_ERROR;
			break;

		case 1064: // SQL�����﷨����
			errMsg = DataAccessException.SQL_SYNTAX_ERROR;
			break;
		//
		case 1062: // Υ��ΨһԼ��
			errMsg = DataAccessException.DISOBEY_UNIQUE;
			break;

		case 942: // �����ͼ������
			errMsg = DataAccessException.TABLE_NOT_EXIST;
			break;

		case 1054: // ����������
			errMsg = DataAccessException.COLUMN_NOT_EXIST;
			break;

		case 1048:// Υ���ǿ�Լ��
			errMsg = DataAccessException.DISOBEY_NOTNULL;
			break;

		case 1065:// Ҫִ�е� SQL ��䲻��Ϊ�հ׻��ֵ��
			errMsg = DataAccessException.SQL_CANNOT_NULL;
			break;

		case 1305:// �����ڵ����ݿ⺯����
			errMsg = DataAccessException.FUNCTION_NOT_EXIST;
			break;

		}

		return errMsg;

	}

}