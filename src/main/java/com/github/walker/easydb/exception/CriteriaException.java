package com.github.walker.easydb.exception;

/**
 * <p>
 * This Excepion throwed when builds the criteria sub-sentence of the sql.
 * </p>
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("serial")
public class CriteriaException extends BaseException {

	// private static LingualResource res = ResourceFactory.getResource();

	public static final String LOGIC_ERROR = "�������ʽ�����߼�����";

	public static final String CANNOT_ADD_THIS = "�������ʽ�����߼����󣬲��ܼ���������Ϊ��������";

	public static final String REMOVE_NOTEXIST = "�������ʽ�����߼�����Ҫɾ���������������ڣ�";

	public static final String CRITERIA_CANNOT_EMPTY = "�ڷ���getOne(Class, Criteria)��, ����Criteria������Ϊ�գ�";

	public static final String NOT_SUPPORTED_OP = "CriteriaGroupֻ֧�����������:'AND','OR'��";

	public static final String CRITERIA_EXCEED_SCOPE = "����ֵ������ֻ�������¼���:String,EString,Integer,EInteger,Long,Elong,Double,EDouble,Float,EFloat��";

	public CriteriaException() {
		this.message = LOGIC_ERROR;
	}

	public CriteriaException(String message) {
		super(message);
	}

	/**
	 * ������
	 * 
	 * @param type
	 *            �쳣�������ṩ�Ĵ�������
	 * @param message
	 *            ��Ҫ����ĶԴ�����Ϣ������
	 */
	public CriteriaException(String type, String message) {
		this.message = type + " " + message;
	}

}