package walker.easydb.exception;

/**
 * <p>
 * Locator of mysql error, it points out the detail error message.
 * </p>
 * 
 * @author HuQingmiao
 */
public class MysqlErrorLocator extends ErrorLocator {

	public String getErrorMsg(int errorCode) {

		String errMsg = DataAccessException.UNKNOW_ERROR;

		switch (errorCode) {

		// 连接不到数据库,数值超过处理精度,数值超过处理精度,插入的值对于列过大,无效数字,无效的日期或时间
		// 并非所有变量都已关联，参数值比'?'多或少！
		// 批量操作时, 某组参数的值比'?'多！
		// 批量操作时, 某组参数的值比'?'少！
		case 0:
			errMsg = DataAccessException.UNKNOW_ERROR;
			break;

		case 1045: // 用户名/口令错误
			errMsg = DataAccessException.USERNAME_PASSWORD_ERROR;
			break;

		case 1064: // SQL出现语法错误
			errMsg = DataAccessException.SQL_SYNTAX_ERROR;
			break;
		//
		case 1062: // 违反唯一约束
			errMsg = DataAccessException.DISOBEY_UNIQUE;
			break;

		case 942: // 表或视图不存在
			errMsg = DataAccessException.TABLE_NOT_EXIST;
			break;

		case 1054: // 列名不存在
			errMsg = DataAccessException.COLUMN_NOT_EXIST;
			break;

		case 1048:// 违反非空约束
			errMsg = DataAccessException.DISOBEY_NOTNULL;
			break;

		case 1065:// 要执行的 SQL 语句不得为空白或空值！
			errMsg = DataAccessException.SQL_CANNOT_NULL;
			break;

		case 1305:// 不存在的数据库函数！
			errMsg = DataAccessException.FUNCTION_NOT_EXIST;
			break;

		}

		return errMsg;

	}

}