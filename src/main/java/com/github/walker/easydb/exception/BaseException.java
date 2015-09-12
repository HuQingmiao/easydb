package com.github.walker.easydb.exception;

/**
 * <p>
 * Superclass of all self-defined exception class.
 * </p>
 *
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class BaseException extends Exception {

    protected String message;// the message attached to the exception

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
