package com.github.walker.easydb.exception;


/**
 * <p>
 * This Excepion throwed if the definition of BaseEntity is illegal.
 * </p>
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("serial")
public class IllegalEntityException extends BaseException {

	// private static LingualResource res = ResourceFactory.getResource();

	public static final String ILLEGAL_ENTITY = "��ʵ����Ķ��岻��BaseEntity�ĵ���ƹ淶��";

	public static final String DATATYPE_NOT_SUPPORT = "ʵ����ֻ֧�������������ͣ�EString,EInteger,Elong,EDouble,EFloat,EBinFile,ETxtFile,ETimestamp��";

	public static final String EXCEED_PK_COLUMN_SCOPE = "�ں��д��ֶ��еı��У�ֻ�������ֻ��ַ������͵�����Ϊ������������������ʱ�����ֶ����ͣ�";

	public static final String NOTFOUND_REFLECT_COLUMN = "�����ݿ��У�û���ҵ��������ӳ����У�";

	public static final String NOT_SPECIFY_PK = "�����Բ����ڣ��������������Ӧ��GET�����ķ��ز�����ƥ�䣡";

	public static final String NOT_SPECIFY_PK2 = "�����Բ����ڣ��������������Ӧ��SET�����Ĳ�����ƥ�䣡";

	public static final String PROPERTY_NOTEXIST_OR_GET = "����ʵ�����pk()������ָ�����������������飡";

	public static final String PROPERTY_NOTEXIST_OR_SET = "���Ҫ�Դ��ֶ��н��в���, �������ʵ�����pk()������ָ�����������������飡";

	public IllegalEntityException() {
		this.message = ILLEGAL_ENTITY;
	}

	public IllegalEntityException(String message) {
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
	public IllegalEntityException(String type, String message) {
		this.message = type + " " + message;
	}

}