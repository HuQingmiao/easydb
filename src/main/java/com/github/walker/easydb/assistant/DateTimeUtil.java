package com.github.walker.easydb.assistant;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 提供各种关于日期、时间的常用方法， 并能方便地将java.util.Date转换为java.sql.Timestamp。
 *
 * @author HuQingmiao
 */
public class DateTimeUtil {

    private static Logger log = LogFactory.getLogger(DateTimeUtil.class);

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 把给定的日期时间字符串按照指定的格式进行解析，以生成相应的数据库能接纳的日期时间对象(Timestamp)。
     * <p/>
     * Parses text from the given string to produce a java.sql.Timestamp.
     *
     * @param datetimeStr the data/time string which format is given by the second
     *                    parameter.
     * @param format      the format of data/time string, such as: "yyyy-MM-dd
     *                    HH:mm:ss.SSS"
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
     * 把给定的日期时间类解析成指定格式的字符串。
     * <p/>
     * Formats a Date to produce a date/time string, which format
     * like:'yyyy-MM-dd HH:mm:ss.SSS'
     *
     * @param date   java.util.Date object, that need to be formatted.
     * @param format the format of the data/time string, such as: "yyyy-MM-dd
     *               HH:mm:ss.SSS"
     */
    public static String format(java.util.Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 取得相对于当前时间指定间隔天数的时间。参数n为负数，则返回当前时间之前第n天的时间； n等于0， 则返回当前时间； n为正数，
     * 则返回当前时间之后的第n天的时间。
     *
     * @param n 相对当前时间的间隔天数
     */
    public static Timestamp getOneDay(int n) {
        long milliseconds = System.currentTimeMillis() + (long) n * 24 * 60 * 60 * 1000;
        return new Timestamp(milliseconds);
    }

    /**
     * 取得相对于指定时间的间隔天数的时间。参数n为负数，则返回当前时间之前第n天的时间； n等于0， 则返回当前时间； n为正数，
     * 则返回当前时间之后的第n天的时间。
     *
     * @param n 相对当前时间的间隔天数
     */
    public static Timestamp getOneDay(Date raletiveDate, int n) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(raletiveDate);

        int day = calendar.get(Calendar.DAY_OF_MONTH) + n;
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 取得当前日期时间
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
