package com.github.walker.easydb.exception;

import java.sql.SQLException;


/**
 * <p>
 * This Excepion may be throwed when operating, accessing the permanent data.
 * </p>
 *
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class DataAccessException extends BaseException {

    //private static LingualResource res = ResourceFactory.getResource();

    public static final String DB_CONNECT_FAILED = "连接数据库失败！";

    public static final String USERNAME_PASSWORD_ERROR = "数据库用户名或密码不正确！";

    public static final String ONLY_FOR_SELECT = "查询语句不能以'UPDATE ','DELETE ','INSERT ','TRUNCATE ','DROP ','ALTER '开头！";

    public static final String SQL_SYNTAX_ERROR = "SQL有语法错误！";

    public static final String TABLE_NOT_EXIST = "表或视图不存在！";

    public static final String COLUMN_NOT_EXIST = "列名不存在！";

    public static final String VALUE_EXCEED_PRECISION = "数值超过处理精度！";

    public static final String INVALID_NUMBER = "无效的数字！";

    public static final String VALUE_EXCEED_BOUND = "插入的值对于列过大！";

    public static final String INVALID_DATE = "无效的日期或时间！";

    public static final String DISOBEY_UNIQUE = "违反唯一约束: 该记录已经存在，请不要重复录入！";

    public static final String DISOBEY_NOTNULL = "该项不能为空, 请检查！";

    public static final String NOT_SPECIFIED_PK = "表没有指定主键！";

    public static final String WRITING_BIGTYPE_FORONLYONE = "对BLOB/CLOB的写入，每次只能作用在用主键值标识的唯一记录上！";

    public static final String NOT_ALL_VARIABLE_RELATED = "并非所有变量都已关联，参数值比'?'多或少！";

    public static final String BATCH_PARAM_MORE = "批量操作时, 某组参数的值比'?'多！";

    public static final String BATCH_PARAM_LESS = "批量操作时, 某组参数的值比'?'少！";

    public static final String SQL_CANNOT_NULL = "执行的 SQL 语句不能为NULL或空值！";

    public static final String RS_COUNT_GREATER1 = "查询出来的结果数大于1，请检查WHERE子句中的查询条件！";

    public static final String PK_NOT_CORRECT = "查询出来的结果数大于1，请检查实体类pk()方法中指定的主键属性是否正确！";

    public static final String FUNCTION_NOT_EXIST = "不存在的数据库函数！";

    // public static final String NODATA_TOLOAD =
    // "当执行loadByPK(BaseEntity)方法时，没有找到与主键值匹配的数据！";

    public static final String NOTFOUND_DB_CONFIG = "没有找到数据库的配置信息！";

    public static final String UNSUPPORTED_DBTABASE = "EasyDB组件目前还不支持这种类型的数据库！";

    public static final String UNKNOW_ERROR = "未知的错误！";

    public DataAccessException() {
        super();
    }

    public DataAccessException(String message) {
        super(message);
    }

    /**
     * 构造子
     *
     * @param errMsg 错误信息
     * @param desc   需要补充的对错误信息的描述
     */
    public DataAccessException(String errMsg, String desc) {
        this.message = errMsg + " " + message;
    }


    /**
     * @param dbType
     * @param e
     * @deprecated 不建议用，请改用 new DataAccessException(dbType, e.getErrorCode)
     */
    public DataAccessException(String dbType, SQLException e) {
        this(dbType, e.getErrorCode());
    }

    public DataAccessException(String dbType, int errCode) {

        ErrorLocator errLocator = ErrorLocator.getInstance(dbType);

        this.message = errLocator.getErrorMsg(errCode);

    }

    public DataAccessException(String dbType, int errCode, String desc) {

        ErrorLocator errLocator = ErrorLocator.getInstance(dbType);

        this.message = errLocator.getErrorMsg(errCode) + " " + desc;

    }
}
