package com.github.walker.easydb.datatype;

import java.sql.Timestamp;

import com.github.walker.easydb.assistant.DateTimeUtil;

/**
 * ����ʱ����, �̳���java.sql.Timestamp�ಢʵ����UpdateIdentifier�ӿ�.
 * 
 * @author HuQingmiao
 *  
 */
@SuppressWarnings("serial")
public class ETimestamp extends java.sql.Timestamp implements UpdateIdentifier {

    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    //��ʶ�Ƿ�Ѷ�Ӧ���ÿ�
    private boolean isEmpty = false;

    /**
     * �����ӣ������������ʱ���������Ӧ���н�������ΪNULL.
     * 
     * Constructor, produce a null instance.
     *  
     */
    public ETimestamp() {
        super(DateTimeUtil.parse("0001-01-01", "yyyy-MM-dd").getTime());//���������
        this.isEmpty = true; //�ÿ�
        this.needUpdate = true; //Ҫ����
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
        this.isEmpty = false; //�ǿ�
        this.needUpdate = true; //Ҫ����
    }

    /**
     * 
     * Constructs a ETimestamp object using a Timestamp object.
     * 
     * 
     */
    public ETimestamp(Timestamp timestamp) {
        super(timestamp.getTime());
        this.isEmpty = false; //�ǿ�
        this.needUpdate = true; //Ҫ����
    }

    /**
     * �����Ƿ�Ѷ�Ӧ�и���;
     * 
     * @param flag
     */
    public void setUpdate(boolean flag) {
        this.needUpdate = flag;

    }

    /**
     * ֻ�е��˷�������trueʱ, �־û������Ż���¶�Ӧ��.
     *  
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * �ж�����ֵ�Ƿ�Ϊnull, �Ծ����Ƿ�Ӧ�ý���Ӧ����null
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
