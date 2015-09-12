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

    public static final String FILE_NOTFOUND = "没有找到相关文件！";

    public static final String IO_EXCEPTION = "网络中断或读写文件时发生错误！";

    public FileAccessException() {
        this.message = IO_EXCEPTION;
    }

    public FileAccessException(String message) {
        super(message);
    }

    /**
     * 构造子
     *
     * @param type    异常类自身提供的错误类型
     * @param message 需要补充的对错误信息的描述
     */
    public FileAccessException(String type, String message) {
        this.message = type + " " + message;
    }

}
