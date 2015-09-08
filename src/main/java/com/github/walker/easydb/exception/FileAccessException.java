package com.github.walker.easydb.exception;


/**
 * <p>
 * This Excepion may be throwed when accessing the disk file.
 * </p>
 * 
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class FileAccessException extends BaseException {

	//private static LingualResource res = ResourceFactory.getResource();

	public static final String FILE_NOTFOUND = "û���ҵ�����ļ���";

	public static final String IO_EXCEPTION = "�����жϻ��д�ļ�ʱ��������";

	public FileAccessException() {
		this.message = IO_EXCEPTION;
	}

	public FileAccessException(String message) {
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
	public FileAccessException(String type, String message) {
		this.message = type + " " + message;
	}

}