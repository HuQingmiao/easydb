package com.github.walker.easydb.assistant;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * 
 * �ṩ���ֹ������ڡ�ʱ��ĳ��÷����� ���ܷ���ؽ�java.util.Dateת��Ϊjava.sql.Timestamp��
 * 
 * @author HuQingmiao
 */
public class DateTimeUtil {

	private static Logger log = LogFactory.getLogger(DateTimeUtil.class);

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * �Ѹ���������ʱ���ַ�������ָ���ĸ�ʽ���н�������������Ӧ�����ݿ��ܽ��ɵ�����ʱ�����(Timestamp)��
	 * 
	 * Parses text from the given string to produce a java.sql.Timestamp.
	 * 
	 * 
	 * @param datetimeStr
	 *            the data/time string which format is given by the second
	 *            parameter.
	 * 
	 * @param format
	 *            the format of data/time string, such as: "yyyy-MM-dd
	 *            HH:mm:ss.SSS"
	 * 
	 */
	public static Timestamp parse(String datetimeStr, String format) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			java.util.Date d = df.parse(datetimeStr);

			return new Timestamp(d.getTime());

		} catch (ParseException e) {
			log.error("", e);
			return null;
		}
	}

	/**
	 * �Ѹ���������ʱ���������ָ����ʽ���ַ�����
	 * 
	 * Formats a Date to produce a date/time string, which format
	 * like:'yyyy-MM-dd HH:mm:ss.SSS'
	 * 
	 * @param date
	 *            java.util.Date object, that need to be formatted.
	 * 
	 * @param format
	 *            the format of the data/time string, such as: "yyyy-MM-dd
	 *            HH:mm:ss.SSS"
	 */
	public static String format(java.util.Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	/**
	 * ȡ������ڵ�ǰʱ��ָ�����������ʱ�䡣����nΪ�������򷵻ص�ǰʱ��֮ǰ��n���ʱ�䣻 n����0�� �򷵻ص�ǰʱ�䣻 nΪ������
	 * �򷵻ص�ǰʱ��֮��ĵ�n���ʱ�䡣
	 * 
	 * @param n
	 *            ��Ե�ǰʱ��ļ������
	 * 
	 */
	public static Timestamp getOneDay(int n) {
		long milliseconds = System.currentTimeMillis() + (long) n * 24 * 60 * 60 * 1000;
		return new Timestamp(milliseconds);
	}

	/**
	 * ȡ�������ָ��ʱ��ļ��������ʱ�䡣����nΪ�������򷵻ص�ǰʱ��֮ǰ��n���ʱ�䣻 n����0�� �򷵻ص�ǰʱ�䣻 nΪ������
	 * �򷵻ص�ǰʱ��֮��ĵ�n���ʱ�䡣
	 * 
	 * @param n
	 *            ��Ե�ǰʱ��ļ������
	 * 
	 */
	public static Timestamp getOneDay(Date raletiveDate, int n) {

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(raletiveDate);

		int day = calendar.get(Calendar.DAY_OF_MONTH) + n;
		calendar.set(Calendar.DAY_OF_MONTH, day);

		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 
	 * ȡ�õ�ǰ����ʱ��
	 * 
	 */
	public static Timestamp getCurrentTime() {
		long milliseconds = System.currentTimeMillis();
		return new Timestamp(milliseconds);
	}

	// //test
	// public static void main(String[] args) {
	// System.out.println(parse("0001-01-01",
	// "yyyy-MM-dd").getTime());
	//
	// System.out.println(new java.util.Date().toString());
	// System.out.println(new Timestamp(0).toLocaleString());
	//
	// System.out.println(new Timestamp(-62135798400000l).toString());
	// System.out.println(new Timestamp(1960*12*30*24*60*60*1000).toString());
	//
	// System.out.println(new Timestamp(System.currentTimeMillis()).toString());
	// }
}
