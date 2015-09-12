package com.github.walker.easydb.criterion;

import com.github.walker.easydb.assistant.MappingUtil;
import com.github.walker.easydb.datatype.EString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Constrains the property to a specified list of values
 *
 * @author HuQingmiao
 */
public class InExpression extends Criteria {

    private final String colName;

    private final Object[] values;

    protected InExpression(String propertyName, Object[] values) {
        this.colName = MappingUtil.getColumnName(propertyName);
        this.values = values;
    }

    public String toSqlString() {
        if (values == null || values.length == 0) {
            return " 1 = 2 ";
        }
        // return colName + " IN (" + toString(values) + ")";
        return buildInClause(colName, values);
    }

    // private String toString(Object[] array) {
    // int len = array.length;
    // if (len == 0)
    // return "";
    //
    // StringBuffer buf = new StringBuffer(len * 10);
    //
    // for (int i = 0; i < len - 1; i++) {
    // if (array[i] instanceof String || array[i] instanceof EString || array[i]
    // instanceof Character) {
    // buf.append("\'").append(array[i]).append("\',");
    // } else {
    // buf.append(array[i]).append(",");
    // }
    // }
    //
    // if (array[len - 1] instanceof String
    // || array[len - 1] instanceof EString || array[len - 1] instanceof
    // Character) {
    // buf.append("\'").append(array[len - 1]).append("\'");
    // } else {
    // buf.append(array[len - 1]);
    // }
    //
    // return buf.toString();
    // }

    /**
     * 构造形如" A.COLUMN IN (a,b,c,...)"的IN子句. 若参数set为空, 则返回 "1=1". 若参数 set为NULL,
     * 将会抛出java.lang.NullPointerException.
     *
     * @param colName 列名,或带有表的别名的列名
     * @param set     含字符或数字类型元素的集合
     * @return 返回形如" A.COLUMN IN (a,b,c,...)"的IN子句; 但若参数array长度为0, 则返回 "1=1"
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String buildInClause(String colName, Set set) {

        final int maxItemCnt = 1000; // in子句允许的最大项数

        if (set.isEmpty()) {
            return " 1 =1 ";
        }

        StringBuffer buff = new StringBuffer();

        if (set.size() <= maxItemCnt) {
            buff.append(colName);
            buff.append(" IN (" + toString(set) + ") ");

        } else {

            // 将集合减裁后分别存放其中对象的子集合
            Set[] subSetArray = new HashSet[(set.size() - 1) / maxItemCnt + 1];

            // 将子集合初始化
            for (int i = 0; i < subSetArray.length; i++) {
                subSetArray[i] = new HashSet();
            }

            int i = 0;// 从第一个元素开始迭代
            for (Iterator it = set.iterator(); it.hasNext(); ) {
                Object obj = it.next();

                // 计算当前对象应该复制到哪个子集合
                int idx = i / maxItemCnt;

                subSetArray[idx].add(obj);
                i++;
            }

            buff.append(" ( ");
            buff.append(colName);
            buff.append(" IN (" + toString(subSetArray[0]) + ") ");

            for (i = 1; i < subSetArray.length; i++) {

                buff.append(" OR ");
                buff.append(colName);
                buff.append(" IN (" + toString(subSetArray[i]) + ") ");
            }

            buff.append(" ) ");

            for (i = 0; i < subSetArray.length; i++) {
                subSetArray[i].clear();
                subSetArray[i] = null;
            }
        }

        return buff.toString();
    }

    /**
     * 构造形如" A.COLUMN IN (a,b,c,...)"的IN子句. 若参数array长度为0, 则返回 "1=1".
     *
     * @param colName 列名,或带有表的别名的列名.
     * @param array   含字符或数字类型元素的数组
     * @return 返回形如" A.COLUMN IN (a,b,c,...)"的IN子句; 但若参数array长度为0, 则返回 "1=1"
     */

    public static String buildInClause(String colName, Object[] array) {

        HashSet<Object> set = new HashSet<Object>(Arrays.asList(array));

        return buildInClause(colName, set);
    }

    /**
     * 将Set<Object>中的项目转换成以逗号分隔的内容.
     *
     * @param set Set<String>
     * @return 类似如:"4,5,33,43,'a' "的字符串
     */
    private static String toString(Set<Object> set) {

        StringBuffer buff = new StringBuffer();
        for (Iterator<Object> it = set.iterator(); it.hasNext(); ) {
            Object obj = it.next();

            // 如果是字符或字符串型, 则转出时需要在元素两边加上单引号'
            if (obj instanceof String || obj instanceof EString || obj instanceof Character) {
                String s = obj.toString();
                s = s.replace("\'", "''");// 将条件中的单引号替换成丙个单引号

                if (!s.trim().equals("")) {
                    buff.append("\'");
                    buff.append(s.trim());
                    buff.append("\',");
                }
            } else {
                buff.append(obj.toString());
                buff.append(",");
            }
        }
        if (buff.length() > 0) {
            buff.deleteCharAt(buff.length() - 1);
        }

        return buff.toString();
    }

//	public static void main(String args[]) {
//		StringBuffer sql = new StringBuffer();
//
//		sql.append(" SELECT A.OWNER_CODE,A.CS_CP_NO,A.CS_CP_ID,A.CR_NO,A.BELONG_AREA ");
//		sql.append("       ,A.CR_NAME,A.CR_MODEL,A.GOODS_CODE ");
//		sql.append("       ,A.GOODS_NAME,A.GOODS_MODEL,A.DECL_UNIT");
//		sql.append("       ,A.FACTORY_WT,A.USED_LJ_WT,A.STATE, A.APPLY_REMARK ");
//		sql.append("       ,A.HG_AUDIT_CODE,A.HG_AUDIT_NAME,A.HG_AUDIT_TIME ");
//		sql.append("       ,A.HG_FIRST_AUDIT_CODE,A.HG_FIRST_AUDIT_NAME,A.HG_FIRST_AUDIT_TIME ");
//		sql.append("       ,A.INNER_RECEIVE_TIME,A.CUSTOM_CODE ");
//		sql.append("   FROM CONSUME_CP_FINAL A");
//		sql.append(" WHERE 1=1 ");
//
//		HashSet<Object> customSet = new HashSet<Object>();
//		customSet.add("asd");
//		if (customSet != null && !customSet.isEmpty()) {
//			sql.append(" AND " + InExpression.buildInClause("A.CUSTOM_CODE", customSet));
//		}
//
//		System.out.println(sql.toString());
//	}

}
