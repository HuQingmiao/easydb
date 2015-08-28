package walker.easydb.exception;

/**
 * <p>
 * This Excepion throwed when builds the criteria sub-sentence of the sql.
 * </p>
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("serial")
public class CriteriaException extends BaseException {

	// private static LingualResource res = ResourceFactory.getResource();

	public static final String LOGIC_ERROR = "条件表达式发生逻辑错误！";

	public static final String CANNOT_ADD_THIS = "条件表达式发生逻辑错误，不能加入自身作为子条件！";

	public static final String REMOVE_NOTEXIST = "条件表达式发生逻辑错误，要删除的子条件不存在！";

	public static final String CRITERIA_CANNOT_EMPTY = "在方法getOne(Class, Criteria)中, 参数Criteria对象不能为空！";

	public static final String NOT_SUPPORTED_OP = "CriteriaGroup只支持如下运算符:'AND','OR'！";

	public static final String CRITERIA_EXCEED_SCOPE = "条件值的类型只能是以下几种:String,EString,Integer,EInteger,Long,Elong,Double,EDouble,Float,EFloat！";

	public CriteriaException() {
		this.message = LOGIC_ERROR;
	}

	public CriteriaException(String message) {
		super(message);
	}

	/**
	 * 构造子
	 * 
	 * @param type
	 *            异常类自身提供的错误类型
	 * @param message
	 *            需要补充的对错误信息的描述
	 */
	public CriteriaException(String type, String message) {
		this.message = type + " " + message;
	}

}