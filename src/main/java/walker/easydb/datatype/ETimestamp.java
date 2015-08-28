package walker.easydb.datatype;

import java.sql.Timestamp;

import walker.easydb.assistant.DateTimeUtil;

/**
 * 日期时间类, 继承了java.sql.Timestamp类并实现了UpdateIdentifier接口.
 * 
 * @author HuQingmiao
 *  
 */
@SuppressWarnings("serial")
public class ETimestamp extends java.sql.Timestamp implements UpdateIdentifier {

    //标识是否把对应列更新
    private boolean needUpdate = false;

    //标识是否把对应列置空
    private boolean isEmpty = false;

    /**
     * 构造子，其产生的日期时间对象所对应的列将被更新为NULL.
     * 
     * Constructor, produce a null instance.
     *  
     */
    public ETimestamp() {
        super(DateTimeUtil.parse("0001-01-01", "yyyy-MM-dd").getTime());//象征性语句
        this.isEmpty = true; //置空
        this.needUpdate = true; //要更新
    }

    /**
     * 
     * Constructs a ETimestamp object using a milliseconds time value.
     * 
     * @param time
     *            milliseconds since January 1, 1970, 00:00:00 GMT. A negative
     *            number is the number of milliseconds before January 1, 1970,
     *            00:00:00 GMT.
     * 
     * 
     * @see java.sql.Timestamp#Timestamp(long)
     *  
     */
    public ETimestamp(long time) {
        super(time);
        this.isEmpty = false; //非空
        this.needUpdate = true; //要更新
    }

    /**
     * 
     * Constructs a ETimestamp object using a Timestamp object.
     * 
     * 
     */
    public ETimestamp(Timestamp timestamp) {
        super(timestamp.getTime());
        this.isEmpty = false; //非空
        this.needUpdate = true; //要更新
    }

    /**
     * 设置是否把对应列更新;
     * 
     * @param flag
     */
    public void setUpdate(boolean flag) {
        this.needUpdate = flag;

    }

    /**
     * 只有当此方法返回true时, 持久化动作才会更新对应列.
     *  
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 判断属性值是否为null, 以决定是否应该将对应列置null
     *  
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }
    
    
    public String toString(){
        if(!this.isEmpty()){
            return super.toString();
        }
        
        return "";
    }
}
