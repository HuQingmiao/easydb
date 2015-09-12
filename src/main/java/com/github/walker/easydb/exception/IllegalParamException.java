package com.github.walker.easydb.exception;

/**
 * <p>
 * <p/>
 * 如果方法的参数值不满足要求, 则抛出此异常.
 * <p/>
 * This Excepion throwed if the parameter value is not meet with some method.
 * </p>
 *
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class IllegalParamException extends BaseException {

    // private static LingualResource res = ResourceFactory.getResource();

    public static final String ILLEGAL_PARAMETER = "参数不合法！";

    public static final String PARAMETER_MUST_FROM1 = "参数不合法，ParameterMap中的参数位置是从1开始的！";


    public static final String NOT_INDEXED_PARAM = "该位置的参数值不应为NULL！";

    public static final String PARAM_CANNOT_NULL = "参数不能为NULL, 或数组长度不能为0！";


    public static final String ENTITY_NOVALUE = "该实体对象中没有属性值！";

    public static final String NOVALUE_FOR_ENTITY_PK = "实体对象中没有设置主键值！";


    public static final String ENTITY_ARRAY_NOT_IDENTICAL = "批量操作时，参数Entity数组中列值的存放结构必须一致，即:数组中的某列要么都需要写入，要么都不需要写入！";

    public static final String BIGCOLUMN_CANNOT_BATCH = "批量操作时，不允许对大字段列进行批量写入！";


    public static final String NOT_SUPPORTED_PARAM_TYPE = "ParameterMap中只能存放如下数据类型：EString,String,ELong,Long,long,EInteger,Integer,int,EDouble,Double,double,EFloat,Float,float,ETimestame,Timestamp！";

    public static final String EXP_NOT_SUPPORTED_TYPE = "类Exp中的方法只支持如下数据类型:EString,String,ELong,Long,long,EInteger,Integer,int,EDouble,Double,double,EFloat,Float,float！";


    public static final String PARAMETER_MUSTBE_IN = "参数不合法, 该方法的操作符必须是\"in\"或者\"not in\"！";

    public static final String ONLY_FOR_SELECT = "查询语句不能以'UPDATE ','DELETE ','INSERT ','TRUNCATE ','DROP ','ALTER '开头！";


    public IllegalParamException() {
        this.message = ILLEGAL_PARAMETER;
    }

    public IllegalParamException(String message) {
        super(message);
    }

    /**
     * 构造子
     *
     * @param type    异常类自身提供的错误类型
     * @param message 需要补充的对错误信息的描述
     */
    public IllegalParamException(String type, String message) {
        this.message = type + " " + message;
    }

}
