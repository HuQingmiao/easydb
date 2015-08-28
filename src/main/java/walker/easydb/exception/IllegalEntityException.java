package walker.easydb.exception;


/**
 * <p>
 * This Excepion throwed if the definition of BaseEntity is illegal.
 * </p>
 * 
 * @author HuQingmiao
 * 
 */
@SuppressWarnings("serial")
public class IllegalEntityException extends BaseException {

	// private static LingualResource res = ResourceFactory.getResource();

	public static final String ILLEGAL_ENTITY = "该实体类的定义不合BaseEntity的的设计规范！";

	public static final String DATATYPE_NOT_SUPPORT = "实体类只支持如下数据类型：EString,EInteger,Elong,EDouble,EFloat,EBinFile,ETxtFile,ETimestamp！";

	public static final String EXCEED_PK_COLUMN_SCOPE = "在含有大字段列的表中，只能以数字或字符串类型的列作为主键，而不能是日期时间或大字段类型！";

	public static final String NOTFOUND_REFLECT_COLUMN = "在数据库中，没有找到与此属性映射的列！";

	public static final String NOT_SPECIFY_PK = "该属性不存在，或者其类型与对应的GET方法的返回参数不匹配！";

	public static final String NOT_SPECIFY_PK2 = "该属性不存在，或者其类型与对应的SET方法的参数不匹配！";

	public static final String PROPERTY_NOTEXIST_OR_GET = "请在实体类的pk()方法中指定代表主键的属性组！";

	public static final String PROPERTY_NOTEXIST_OR_SET = "如果要对大字段列进行操作, 则必须在实体类的pk()方法中指定代表主键的属性组！";

	public IllegalEntityException() {
		this.message = ILLEGAL_ENTITY;
	}

	public IllegalEntityException(String message) {
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
	public IllegalEntityException(String type, String message) {
		this.message = type + " " + message;
	}

}