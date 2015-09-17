package com.github.walker.easydb;

import com.github.walker.easydb.assistant.DateTimeUtil;
import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.criterion.Exp;
import com.github.walker.easydb.dao.EasyDao;
import com.github.walker.easydb.dao.SqlParamMap;
import com.github.walker.easydb.datatype.*;
import com.github.walker.easydb.vo.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Demostrates the using of EasyDB component.
 *
 * @author HuQingmiao
 */
public class DemoService {

    private EasyDao dao = EasyDao.getInstance();

    private Logger log = LogFactory.getLogger(getClass());


    /**
     * 清空测试数据，以备测试
     *
     * @throws Exception
     */
    public void clearTestData() throws Exception {
        log.info("\nclearTestData()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            dao.deleteAll(Book.class);

            dao.deleteAll(Editor.class);

            dao.deleteAll(BookEditor.class);

            // 开放此代码, 试图删除一个不存在的表, 将抛出异常且整个事务将回滚
            //dao.deleteAll(Boek.class);
            commit = true;// 标识事务成功执行

        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }

    /**
     * 增加一本书;
     *
     * @throws Exception
     */
    public void addOneBook() throws Exception {
        log.info("\naddOneBook()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            Book book = new Book();
            book = new Book();
            book.setBookId(new ELong(1));
            book.setTitle(new EString("Unix网络编程"));
            book.setCost(new EFloat(30.0f));
            book.setPublishTime(new ETimestamp(DateTimeUtil.getCurrentTime()));

            // 开放此代码, 试图把一个不存在的文件写入数据库, 将抛出异常
            book.setTextContent(new ETxtFile("d:/三国演义.txt"));

            // 开放此代码, 试图向不存在与此属性对应的列写数据, EasyDB会对其忽略
            //book.setaNotExistCol(new EString("asdff"));
            dao.save(book); // 持久化实体

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }

    /**
     * 批量增加多本书;
     *
     * @throws Exception
     */
    public void addMultiBooks() throws Exception {
        log.info("\naddMultiBooks()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            Book[] BookArray = new Book[4];

            BookArray[0] = new Book();
            BookArray[0].setBookId(new ELong(101));
            BookArray[0].setTitle(new EString("UNIX-上册"));
            BookArray[0].setCost(new EFloat(100.0f));

            // 开放此代码，将抛出异常：批量操作时，不允许对大字段列进行批量写入
            // BookArray[0].setBlobContent(new EBinFile("d:\\unix.chm"));

            BookArray[1] = new Book();
            BookArray[1].setBookId(new ELong(105));
            BookArray[1].setTitle(new EString("UNIX-中册"));

            // 屏蔽此代码，将抛出异常：参数Entity数组中列值存放位置必须一致！
            BookArray[1].setCost(new EFloat(52.0f));

            BookArray[2] = new Book();
            BookArray[2].setBookId(new ELong(103));
            BookArray[2].setTitle(new EString("UNIX-下册"));
            BookArray[2].setCost(new EFloat(35.0f));

            dao.save(BookArray); // 持久化实体

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }

    /**
     * 更新指定主键所表示的那本书的信息
     *
     * @throws Exception
     */
    public void updateOneBook() throws Exception {
        log.info("\nupdateOneBook()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            // 设置要更新的项:书名,书的二进制内容,书的文本内容
            Book newBook = new Book();
            newBook.setTitle(new EString("三国演义(第二版)"));
            newBook.setTextContent(new ETxtFile("d:/三国演义.txt"));
            newBook.setBlobContent(new EBinFile());

            // 如果要将大字段列置空,则..
            //newBook.setTextContent(new ETxtFile());//以文本方式更新内容

            //必须指定主键, 它是更新的依据
            newBook.setBookId(new ELong(1));
            dao.update(newBook);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 先从数据库检索一批书, 再对这些书的信息进行批量更新
     *
     * @throws Exception
     */
    public void updateMulitBooks() throws Exception {
        log.info("\nupdateMulitBooks()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            String sql = " select book_id,title,cost,publish_time from book ";
            ArrayList list = dao.get(sql, Book.class);

            Book[] books = new Book[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Book b = (Book) list.get(i);

                //设置新的出版时间和价格
                b.setPublishTime(new ETimestamp(DateTimeUtil.parse("2008-01", "yyyy-MM")));
                b.setCost(new EFloat(b.getCost().floatValue() - 10.0f));
                books[i] = b;
            }
            list.clear();
            dao.update(books);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 删除指定主键所表示的那本书
     *
     * @throws Exception
     */
    public void deleteOneBook() throws Exception {
        log.info("\ndeleteOneBook()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {

            Book book = new Book();
            book.setBookId(new ELong(101));

            //先从数据库载入, 再删除
            if (dao.loadByPK(book)) {
                dao.delete(book);
            }

            //或者指定主键进行删除
//			Book book2 = new Book();
//			book2.setBookId(new ELong(101));
//			dao.delete(book2);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 先从数据库检索一批书, 再删除这些书
     *
     * @throws Exception
     */
    public void deleteMulitBooks() throws Exception {
        log.info("\ndeleteMulitBooks()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            String sql = " select book_id,title,cost,publish_time from book where cost>20";
            ArrayList list = dao.get(sql, Book.class);

            //将list转为数组后删除
            Book[] books = (Book[]) list.toArray(new Book[0]);
            list.clear();
            dao.delete(books);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 指定条件, 删除一部分书
     *
     * @throws Exception
     */
    public void deleteBookByCriteria() throws Exception {
        log.info("\ndeleteBookByCriteria()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
//            Set<String> set = new HashSet<String>();
//            set.add("UNIX-上册");
//            set.add("UNIX-下册");
//            dao.delete(Book.class, Exp.in("title", set));

            Criteria c1 = Exp.like("title", "三国%");
            dao.delete(Book.class, c1);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 分别以两种SQL的方式添加两本书;
     *
     * @throws Exception
     */
    public void executeSqlToAddBook() throws Exception {
        log.info("\nexecuteSqlToAddBook()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            // 方式1：可执行SQL
            String sql = " INSERT INTO BOOK (BOOK_ID,TITLE,COST) "
                    + " VALUES (3, '鲁宾逊漂流记', 20.0) ";

            dao.exec(sql);


            // 方式2：参数化SQL
            sql = " INSERT INTO BOOK (BOOK_ID,TITLE,COST,PUBLISH_TIME) VALUES (?,?,?,?) ";
            SqlParamMap pMap = new SqlParamMap();
            pMap.put(1, "4");
            pMap.put(2, "资本论");
            pMap.put(3, 22.0f);
            pMap.put(4, DateTimeUtil.parse("2007-0-10", "yyyy-MM-dd"));

            dao.exec(sql, pMap);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }

    }

    /**
     * 以SQL的方式添加几位编者信息
     *
     * @throws Exception
     */
    public void executeSqlToAddEditors() throws Exception {
        log.info("\nexecuteSqlToAddEditors()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            String sql = " INSERT INTO EDITOR (EDITOR_ID,NAME,SEX) VALUES (?,?,?) ";

            SqlParamMap[] pMapArray = new SqlParamMap[4];

            int k = 1;
            pMapArray[0] = new SqlParamMap();
            pMapArray[0].put(k++, 1);
            pMapArray[0].put(k++, "Richard Stevens");

            // 屏蔽此代码,将抛出异常：批量操作时, 某组参数的值比'?'少！
            pMapArray[0].put(k++, "M");

            // 开放此代码,将抛出异常：批量操作时, 某组参数的值比'?'多！
            //pMapArray[0].put(k++, "M");

            k = 1;
            pMapArray[1] = new SqlParamMap();
            pMapArray[1].put(k++, 2);
            pMapArray[1].put(k++, "易中天");
            pMapArray[1].put(k++, "M");

            k = 1;
            pMapArray[2] = new SqlParamMap();
            pMapArray[2].put(k++, 3);
            pMapArray[2].put(k++, "罗贯中");
            pMapArray[2].put(k++, "M");

            k = 1;
            pMapArray[3] = new SqlParamMap();
            pMapArray[3].put(k++, 4);
            pMapArray[3].put(k++, "笛福");
            pMapArray[3].put(k++, "M");

            dao.exec(sql, pMapArray);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }

    /**
     * 以SQL的方式添加几条书与编者的关联信息;
     *
     * @throws Exception
     */
    public void executeSqlToAddBookEditor() throws Exception {
        log.info("\nexecuteSqlToAddBookEditor()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            String sql = " INSERT INTO BOOK_EDITOR (EDITOR_ID,BOOK_ID) VALUES (?,?) ";

            SqlParamMap[] pMapArray = new SqlParamMap[6];

            // Richard Stevens -> 永远的UNIX_上册
            pMapArray[0] = new SqlParamMap();
            pMapArray[0].put(1, 1);
            pMapArray[0].put(2, 101);

            // Richard Stevens -> 永远的UNIX_中册
            pMapArray[1] = new SqlParamMap();
            pMapArray[1].put(1, 1);
            pMapArray[1].put(2, 102);

            // Richard Stevens -> 永远的UNIX_下册
            pMapArray[2] = new SqlParamMap();
            pMapArray[2].put(1, 1);
            pMapArray[2].put(2, 103);

            // 罗贯中 -> 三国演义
            pMapArray[4] = new SqlParamMap();
            pMapArray[4].put(1, 3);
            // 请仔细观察，屏蔽此行后的执行效果， 其结果与JDBC的效果一致， 即继承上组参数值
            pMapArray[4].put(2, 1);

            // 罗贯中 -> 三国演义
            pMapArray[5] = new SqlParamMap();
            pMapArray[5].put(1, 4);
            // 请仔细观察，屏蔽此行后的执行效果， 其结果与JDBC的效果一致， 即继承上组参数值
            pMapArray[5].put(2, 3);

            dao.exec(sql, pMapArray);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 找出某位作者的资料及其所编写的书
     *
     * @throws Exception
     */
    public void getEditorAndBook() throws Exception {
        log.info("\ngetEditorAndBook()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT A.NAME EDITOR_NAME, A.SEX EDITOR_SEX, ");
            sql.append(" C.TITLE, C.COST, C.BLOB_CONTENT, C.TEXT_CONTENT ");
            sql.append(" FROM  EDITOR A, BOOK_EDITOR B, BOOK C ");
            sql.append(" WHERE A.EDITOR_ID = B.EDITOR_ID ");
            sql.append("   AND  B.BOOK_ID =  C.BOOK_ID ");
            sql.append("   AND  A.NAME like 'Richard%' ");
            sql.append(" ORDER BY C.COST ");

            @SuppressWarnings("rawtypes")
            ArrayList list = dao.get(sql.toString(), EditorAndBook.class);

            // 打印出查询结果
            StringBuffer buff = new StringBuffer("查询结果: \n");
            for (int i = 0; i < list.size(); i++) {
                EditorAndBook editorBook = (EditorAndBook) list.get(i);

                // 输出作者姓名、性别、作者、作者所著书的书名、书价、书的内容
                buff.append(editorBook.getEditorName() + "\t");
                buff.append(editorBook.getEditorSex() + "\t");
                buff.append(editorBook.getTitle() + "\t");
                buff.append(editorBook.getCost() + "\t");

                // EasyDB已经从数据库读取到书的内容， 并以文件的形式存放在JAVA临时目
                // 录(java.io.tmp), 现在打印出该文件的全路径。
                buff.append(editorBook.getBlobContent() + "\t");
                buff.append(editorBook.getTextContent() + "\t");

                buff.append("\n");
            }

            log.info(buff.toString());

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 分页查询, 查出每2-3条记录
     * <p/>
     * 找出某位作者的资料及其所编写的书 Demostrates the method: exec(String pSql, ParameterMap[]
     * paraMapArray).
     *
     * @throws Exception
     */
    public void getEditorAndBookByPager() throws Exception {
        log.info("\ngetEditorAndBookByPager()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {
            StringBuffer sql = new StringBuffer();
            // sql.append(" SELECT * FROM (");

            sql.append(" SELECT A.NAME EDITOR_NAME, A.SEX EDITOR_SEX, ");
            sql.append(" C.TITLE, C.COST, C.BLOB_CONTENT, C.TEXT_CONTENT ");
            sql.append(" FROM  EDITOR A, BOOK_EDITOR B, BOOK C ");
            sql.append(" WHERE A.EDITOR_ID = B.EDITOR_ID ");
            sql.append("   AND  B.BOOK_ID =  C.BOOK_ID ");
            // sql.append(" AND A.NAME like 'Richard%' ");
            sql.append(" ORDER BY C.COST ");

            @SuppressWarnings("rawtypes")
            ArrayList list = dao
                    .get(sql.toString(), EditorAndBook.class, 1, 15);

            // 打印出查询结果
            StringBuffer buff = new StringBuffer("查询结果: \n");
            for (int i = 0; i < list.size(); i++) {
                EditorAndBook editorBook = (EditorAndBook) list.get(i);

                // 输出作者姓名、性别、作者、作者所著书的书名、书价、书的内容
                buff.append(editorBook.getEditorName() + "\t");
                buff.append(editorBook.getEditorSex() + "\t");
                buff.append(editorBook.getTitle() + "\t");
                buff.append(editorBook.getCost() + "\t");

                // EasyDB已经从数据库读取到书的内容，现在打印出该文件的全路径
                buff.append(editorBook.getBlobContent() + "\t");
                buff.append(editorBook.getTextContent() + "\t");

                buff.append("\n");
            }

            log.info(buff.toString());

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 查询并展示所有的书
     *
     * @throws Exception
     */
    public void showAllBooks() throws Exception {
        log.info("\nshowAllBooks()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识

        try {
            String sql = " select book_id,title,cost,publish_Time from BOOK order by book_id ";
            ArrayList list = dao.get(sql, Book.class);

            // 打印出查询结果
            StringBuffer buff = new StringBuffer("查询结果: \n");
            for (int i = 0; i < list.size(); i++) {
                Book b = (Book) list.get(i);
                buff.append(b.getBookId() + "\t");
                buff.append(b.getTitle() + "\t");
                buff.append(b.getCost() + "\t");
                //buff.append(b.getBlobContent() + "\t");
                //buff.append(b.getTextContent()+"\t");
                buff.append(b.getPublishTime() + "\n");
            }
            log.info(buff);

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 从某个表中仅取一条记录
     *
     * @throws Exception
     */
    public void getOneBook() throws Exception {
        log.info("\ngetOneBook()>>>>>>>>>>>>>>>>>>>>");
        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {

            Criteria c = Exp.eq("bookId", 102);
            Book book = (Book) dao.getOne(Book.class, c);

            // 输出查询结果
            StringBuffer buff = new StringBuffer("查询结果: \n");
            if (book != null) {
                buff.append(book.getBookId() + "\t");
                buff.append(book.getTitle() + "\t");
                buff.append(book.getCost() + "\t");
                //buff.append(book.getBlobContent() + "\t");
                //buff.append(book.getTextContent() + "\t");
                buff.append(book.getPublishTime() + "\n");
            }
            log.info(buff.toString());

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }


    /**
     * 演示嵌套事务
     *
     * @throws Exception
     */
    public void nestedTransaction() throws Exception {
        log.info("\nnestedTransaction()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // 发起事务
        boolean commit = false;// 事务执行成功的标识
        try {

            this.addOneBook();       //第一个子事务

            this.addMultiBooks();    //第二个子事务

            this.executeSqlToAddBook();


//			 //开放此代码，整个事务包括所有子事务将回滚
//			 if (true) {
//			 	throw new Exception(">>>>>throwed by nestedTransaction() ");
//			 }

            this.executeSqlToAddEditors();

            this.executeSqlToAddBookEditor();

            commit = true;// 标识事务成功执行
        } finally {
            dao.endTrans(commit);// 结束事务
        }
    }

    public void doBeforeTest() throws Exception {

        //创建两个文件
        File file1 = new File("d:/unix.txt");
        File file3 = new File("d:/三国演义.txt");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file1);
            out.write("the history of unix ....".getBytes());
            out.flush();
            out.close();

            out = new FileOutputStream(file3);
            out.write("三国啊，孔明兄啊 ".getBytes());
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
    }


    public void doAfterTest() throws Exception {

        File file1 = new File("d:\\unix.txt");
        File file2 = new File("d:\\unix.chm");
        File file3 = new File("d:\\三国演义.txt");

        file1.delete();
        file2.delete();
        file3.delete();
    }

}

