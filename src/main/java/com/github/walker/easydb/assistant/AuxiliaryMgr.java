package com.github.walker.easydb.assistant;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * EasyDB�ĸ���������, Ŀǰֻ���ṩ����ļ����Ŀ¼�ķ���
 * 
 * @author HuQingmiao
 * 
 */
public class AuxiliaryMgr {

	private static Logger log = LogFactory.getLogger(AuxiliaryMgr.class);

	private AuxiliaryMgr() {
	}

	/**
	 * ��ջ����ļ�Ŀ¼
	 * 
	 * @throws IOException
	 * 
	 */
	public static void clearBaseFileDirc() {

		// ȡ��ʱ�ļ�
		String baseFileDirc = EasyConfig.getProperty("baseFileDirc");
		File dirc = new File(baseFileDirc);

		// ���ɾ��
		File[] files = dirc.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		log.info("Deleted All files in the base file dircotry. ");
	}

	/**
	 * ���ļ�ռ�õ��ܿռ�ﵽ����ָ�����ֽ���ʱ, ����ջ����ļ�Ŀ¼.
	 * 
	 * @param maxSpaceSize
	 *            ��������ļ�Ŀ¼ռ�õ�����ֽ���
	 * 
	 * @throws IOException
	 */
	public static void clearBaseFileDirc(int maxSpaceSize) {

		// ȡ��ʱ�ļ�
		String baseFileDirc = EasyConfig.getProperty("baseFileDirc");
		File dirc = new File(baseFileDirc);

		// �����Ŀ¼�¸��ļ�ռ�õĿռ��С
		File[] files = dirc.listFiles();
		int size = 0;
		for (int i = 0; i < files.length; i++) {
			size += files[i].length();
		}

		if (size >= maxSpaceSize) {
			// ���ɾ��
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
		log.info("Deleted All files in the base file dircotry. ");
	}

	public static void main(String[] args) {
		clearBaseFileDirc(100000);
	}

}