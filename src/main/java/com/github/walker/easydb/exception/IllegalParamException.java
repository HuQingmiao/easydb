package com.github.walker.easydb.exception;

/**
 * <p>
 * 
 * ��������Ĳ���ֵ������Ҫ��, ���׳����쳣.
 * 
 * This Excepion throwed if the parameter value is not meet with some method.
 * </p>
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("serial")
public class IllegalParamException extends BaseException {

	// private static LingualResource res = ResourceFactory.getResource();

	public static final String ILLEGAL_PARAMETER = "�������Ϸ���";

	public static final String PARAMETER_MUST_FROM1 = "�������Ϸ���ParameterMap�еĲ���λ���Ǵ�1��ʼ�ģ�";

	
	public static final String NOT_INDEXED_PARAM = "��λ�õĲ���ֵ��ӦΪNULL��";
	
	public static final String PARAM_CANNOT_NULL = "��������ΪNULL, �����鳤�Ȳ���Ϊ0��";
	
	
	public static final String ENTITY_NOVALUE = "��ʵ�������û������ֵ��";

	public static final String NOVALUE_FOR_ENTITY_PK = "ʵ�������û����������ֵ��";
	

	public static final String ENTITY_ARRAY_NOT_IDENTICAL = "��������ʱ������Entity��������ֵ�Ĵ�Žṹ����һ�£���:�����е�ĳ��Ҫô����Ҫд�룬Ҫô������Ҫд�룡";

	public static final String BIGCOLUMN_CANNOT_BATCH = "��������ʱ��������Դ��ֶ��н�������д�룡";


	public static final String NOT_SUPPORTED_PARAM_TYPE = "ParameterMap��ֻ�ܴ�������������ͣ�EString,String,ELong,Long,long,EInteger,Integer,int,EDouble,Double,double,EFloat,Float,float,ETimestame,Timestamp��";

	public static final String EXP_NOT_SUPPORTED_TYPE = "��Exp�еķ���ֻ֧��������������:EString,String,ELong,Long,long,EInteger,Integer,int,EDouble,Double,double,EFloat,Float,float��";

	
	public static final String PARAMETER_MUSTBE_IN = "�������Ϸ�, �÷����Ĳ�����������\"in\"����\"not in\"��";

	public static final String ONLY_FOR_SELECT = "��ѯ��䲻����'UPDATE ','DELETE ','INSERT ','TRUNCATE ','DROP ','ALTER '��ͷ��";


	public IllegalParamException() {
		this.message = ILLEGAL_PARAMETER;
	}

	public IllegalParamException(String message) {
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
	public IllegalParamException(String type, String message) {
		this.message = type + " " + message;
	}

}