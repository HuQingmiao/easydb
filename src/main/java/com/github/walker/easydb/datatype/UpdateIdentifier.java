package com.github.walker.easydb.datatype;

/**
 * 
 * ���±�ʶ��, �����ɱ�ʶ�־û�����ʱ�Ƿ��������¶�Ӧ��, �Ƿ�ĳ����NULL.
 * 
 * @author  HuQingmiao
 * 
 *
 */
public interface UpdateIdentifier {

    /**
     * �����Ƿ�Ѷ�Ӧ�и��µ����ݿ�
     * 
     * @param flag
     */
    public abstract void setUpdate(boolean flag);

    /**
     *ֻ�е��˷�������trueʱ, �־û������Ż���¶�Ӧ��.
     * 
     */
    public abstract boolean needUpdate();

    /**
     *���Ե�ǰ�����Ƿ�Ϊ��, �Ծ����Ƿ񽫶�Ӧ���ÿ�
     * 
     */
    public abstract boolean isEmpty();
    

}
