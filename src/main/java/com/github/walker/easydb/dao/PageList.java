package com.github.walker.easydb.dao;

import java.util.ArrayList;

/**
 * ֧�ַ�ҳ��ѯ��ArrayList
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("rawtypes")
public class PageList extends ArrayList{

	private static final long serialVersionUID = 1L;

	/**
	 * �ڷ�ҳ��ѯ�У�totalRecordCount�����ݿ�����������ҵ�������ļ�¼����;
	 * 
	 * ��this.size(), ��ArrayList#size()���õ����Ǳ��η�ҳʵ�ʻ�ȡ�ļ�¼����
	 * 
	 */

	private int totalRecordCount;

	/**
	 * ȡ�����ݿ�����������ҵ�������ļ�¼����
	 * 
	 * @return ��ҳ��ѯ�У���������ҵ�������ļ�¼����
	 */
	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	/**
	 * ������������ҵ�������ļ�¼����
	 * 
	 * @param totalRecordCount
	 */
	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}
}
