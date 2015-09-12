package com.github.walker.easydb.exception;


public class SqlserverErrorLocator extends ErrorLocator {

    public String getErrorMsg(int errorCode) {
        String errMsg = DataAccessException.UNKNOW_ERROR;

        switch (errorCode) {

            case 17002:// 连接不到数据库
                errMsg = DataAccessException.DB_CONNECT_FAILED;
                break;
        }

        return errMsg;
    }
}
