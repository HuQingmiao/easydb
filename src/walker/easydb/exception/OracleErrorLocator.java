package walker.easydb.exception;

/**
 * <p>
 * Locator of database error, it translates the error code of Oracle into
 * unified error errMsg.
 * </p>
 * 
 * @author HuQingmiao
 */
public class OracleErrorLocator extends ErrorLocator {

	public String getErrorMsg(int errorCode) {

		String errMsg = DataAccessException.UNKNOW_ERROR;

		switch (errorCode) {

		case 17002:// 连接不到数据库
			errMsg = DataAccessException.DB_CONNECT_FAILED;
			break;

		case 1017: // 用户名/口令错误
			errMsg = DataAccessException.USERNAME_PASSWORD_ERROR;
			break;

		case 923: // 找不到FROM关键字
		case 933: // SQL出现语法错误
			errMsg = DataAccessException.SQL_SYNTAX_ERROR;
			break;

		case 1: // 违反唯一约束
			errMsg = DataAccessException.DISOBEY_UNIQUE;
			break;

		case 942: // 表或视图不存在
			errMsg = DataAccessException.TABLE_NOT_EXIST;
			break;

		case 904: // 列名不存在
			errMsg = DataAccessException.COLUMN_NOT_EXIST;
			break;

		case 1438: // 数值超过处理精度
			errMsg = DataAccessException.VALUE_EXCEED_PRECISION;
			break;

		case 1401: // 插入的值对于列过大
		case 17070:
			errMsg = DataAccessException.VALUE_EXCEED_BOUND;
			break;

		case 1722: // 无效数字
			errMsg = DataAccessException.INVALID_NUMBER;
			break;

		case 1858:
		case 1847:
		case 1861:// 无效的日期或时间
			errMsg = DataAccessException.INVALID_DATE;
			break;

		case 1400:
		case 1407:// 违反非空约束
			errMsg = DataAccessException.DISOBEY_NOTNULL;
			break;

		case 1008:// 并非所有变量都已关联，参数值比'?'多或少！
			errMsg = DataAccessException.NOT_ALL_VARIABLE_RELATED;
			break;
		case 17003:// 批量操作时, 某组参数的值比'?'多！
			errMsg = DataAccessException.BATCH_PARAM_MORE;
			break;
		case 17041:// 批量操作时, 某组参数的值比'?'少！
			errMsg = DataAccessException.BATCH_PARAM_LESS;
			break;
		case 17104:// 要执行的 SQL 语句不得为空白或空值！
			errMsg = DataAccessException.SQL_CANNOT_NULL;
			break;
		}
		return errMsg;
	}
}