package com.github.walker.easydb.dao;

import java.util.ArrayList;

/**
 * 支持分页查询的ArrayList
 *
 * @author HuQingmiao
 */
@SuppressWarnings("rawtypes")
public class PageList extends ArrayList {

    private static final long serialVersionUID = 1L;

    /**
     * 在分页查询中，totalRecordCount是数据库中所有满足业务条件的记录总数;
     * <p/>
     * 而this.size(), 即ArrayList#size()所得到的是本次分页实际获取的记录条数
     */

    private int totalRecordCount;

    /**
     * 取得数据库中所有满足业务条件的记录总数
     *
     * @return 分页查询中，所有满足业务条件的记录总数
     */
    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    /**
     * 设置所有满足业务条件的记录总数
     *
     * @param totalRecordCount
     */
    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
