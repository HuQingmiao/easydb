package com.github.walker.easydb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.github.walker.easydb.criterion.Exp;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.ETimestamp;
import com.github.walker.easydb.datatype.ETxtFile;
import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.DateTimeUtil;
import com.github.walker.easydb.assistant.LogFactory;
import com.github.walker.easydb.criterion.Criteria;
import com.github.walker.easydb.dao.EasyDao;
import com.github.walker.easydb.dao.SqlParamMap;
import com.github.walker.easydb.datatype.EString;


/**
 * 
 * Demostrates the using of EasyDB component.
 * 
 * @author HuQingmiao
 * 
 */
public class DemoBuiness {

	private EasyDao dao = EasyDao.getInstance();

	private Logger log = LogFactory.getLogger(getClass());
	


	/**
	 * ��ղ������ݣ��Ա�����
	 * 
	 * 
	 * @throws Exception
	 */
	public void clearTestData() throws Exception {
	    log.info("\nclearTestData()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ

		try {				    
			dao.deleteAll(Book.class);

			dao.deleteAll(Editor.class);

			dao.deleteAll(BookEditor.class);

			// ���Ŵ˴���, ��ͼɾ��һ�������ڵı�, ���׳��쳣���������񽫻ع�
			// dao.deleteAll(Boek.class);


			commit = true;// ��ʶ����ɹ�ִ��

		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	/**
	 * ����һ����;
	 * 
	 * @throws Exception
	 */
	public void addOneBook() throws Exception {
	    log.info("\naddOneBook()>>>>>>>>>>>>>>>>>>>>");

	    
	    
		 StringBuilder sb = new StringBuilder();
		 String s = "����";
		 for (int i = 0; i < 500; i++) {
		 sb.append(s);
		 }
		
		 System.out.println(sb.toString().length());
		 
		 System.out.println(sb.toString().getBytes().length);
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			Book book = new Book();
			book = new Book();
			book.setBookId(new ELong(1));
			book.setTitle(new EString(sb.toString()));
			book.setCost(new EFloat(30.0f));
			book.setPublishTime(new ETimestamp(DateTimeUtil.getCurrentTime()));
			// ���Ŵ˴���, ��ͼ��һ�������ڵ��ļ�д�����ݿ�, ���׳��쳣
			book.setTextContent(new ETxtFile("d:\\��������.txt"));

			// ���Ŵ˴���, ��ͼ�򲻴���������Զ�Ӧ����д����, EasyDB��������
			//book.setANotExistCol(new EString("asdff"));

			dao.save(book); // �־û�ʵ��
			
			
			//book.setCost(new EFloat(40.0f));
			//dao.save(book);
			
			//book.setCost(new EFloat(80.0f));
			//dao.save(book);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	/**
	 * �������Ӷ౾��;
	 * 
	 * @throws Exception
	 */
	public void addMultiBooks() throws Exception {
	    log.info("\naddMultiBooks()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {

			int size = 4;
			Book[] BookArray = new Book[size];

			BookArray[0] = new Book();
			BookArray[0].setBookId(new ELong(101));
			BookArray[0].setTitle(new EString("UNIX-�ϲ�"));
			BookArray[0].setCost(new EFloat(100.0f));

			// ���Ŵ˴��룬���׳��쳣����������ʱ��������Դ��ֶ��н�������д��
			// BookArray[0].setBlobContent(new EBinFile("d:\\unix.chm"));

			BookArray[1] = new Book();
			BookArray[1].setBookId(new ELong(105));
			BookArray[1].setTitle(new EString("UNIX-�в�"));

			// ���δ˴��룬���׳��쳣������Entity��������ֵ���λ�ñ���һ�£�
			BookArray[1].setCost(new EFloat(52.0f));

			BookArray[2] = new Book();
			BookArray[2].setBookId(new ELong(103));
			BookArray[2].setTitle(new EString("UNIX-�²�"));
			BookArray[2].setCost(new EFloat(35.0f));
			
			
			
			dao.save(BookArray); // �־û�ʵ��

			commit = true;// ��ʶ����ɹ�ִ��

		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	/**
	 * ����ָ����������ʾ���Ǳ������Ϣ
	 * 
	 * 
	 * @throws Exception
	 */
	public void updateOneBook() throws Exception {
	    log.info("\nupdateOneBook()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
		    
			// ����Ҫ���µ���:����,��Ķ���������,����ı�����
			Book newBookInfo = new Book();
			newBookInfo.setTitle(new EString("��������(�ڶ���)"));
			//newBookInfo.setTextContent(new ETxtFile("d:\\��������.txt"));
			//newBookInfo.setBlobContent(new EBinFile());
				
			// ���Ҫ�����ֶ����ÿ�,��..
			// newBook.setBlobContent(new EBinFile());//�Զ����Ʒ�ʽ��������
			// newBook.setTextContent(new ETxtFile());//���ı���ʽ��������
			
			//����ָ������, ���Ǹ��µ�����
			newBookInfo.setBookId(new ELong(1));
			dao.update(newBookInfo);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	
	/**
	 * �ȴ����ݿ����һ����, �ٶ���Щ�����Ϣ������������
	 * 
	 * @throws Exception
	 */
	public void updateMulitBooks() throws Exception {
	    log.info("\nupdateMulitBooks()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			String sql = " select book_id,title,cost,publish_time from book ";

			@SuppressWarnings("rawtypes")
			ArrayList list = dao.get(sql, Book.class);
	
			Book[] books = new Book[ list.size()];
			for (int i = 0; i < list.size(); i++) {
				Book b = (Book) list.get(i);
				
				//�����µĳ���ʱ��ͼ۸�
				b.setPublishTime(new ETimestamp(DateTimeUtil.parse("2008-01","yyyy-MM")));
				b.setCost(new EFloat(b.getCost().floatValue()-10.0f));
				books[i]=b;
			}

			dao.update(books);	
			
			commit = true;// ��ʶ����ɹ�ִ��
			
		} finally {
			dao.endTrans(commit);// ��������
		}
	}
	

	/**
	 * ָ������, �Բ�������и���
	 * 
	 * @throws Exception
	 */
	public void updateBookByCriteria() throws Exception {	    
	    log.info("\nupdateBooksByCriteria()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			// ����Ҫ���µ���:�۸�,����,��Ķ���������,����ı�����
			Book newBookInfo = new Book();
			newBookInfo.setCost(new EFloat("65.0"));// ���¼۸�
			newBookInfo.setTitle(new EString("��Զ��UNIX_�ϲ�(����ƪ)"));

			// ���Ŵ˴���,�������쳣:��BLOB/CLOB��д�룬ÿ��ֻ��������������ֵ��ʶ��Ψһ��¼�ϣ�
			 //newBookInfo.setBlobContent(new EBinFile("d:\\unix.chm"));
			// newBookInfo.setTextContent(new ETxtFile("d:\\unix.txt"));

			// ���Ҫ�����ֶ����ÿ�,��..
			// newBookInfo.setBlobContent(new EBinFile());//�Զ����Ʒ�ʽ��������
			// newBookInfo.setTextContent(new ETxtFile());//���ı���ʽ��������

			// ��������"UNIX"��ͷ���Ҽ۸�>=70.0Ԫ������и���
			Criteria c1 = Exp.like("title", "UNIX%");
			Criteria c2 = Exp.ge("cost",70);

			dao.update(newBookInfo,Exp.and(c1,c2));

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}


	
	/**
	 * ɾ��ָ����������ʾ���Ǳ���
	 * 
	 * 
	 * @throws Exception
	 */
	public void deleteOneBook() throws Exception {
	    log.info("\ndeleteOneBook()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
		    
			Book book = new Book();
			book.setBookId(new ELong(101));
			
			//�ȴ����ݿ�����, ��ɾ��
			if(dao.loadByPK(book)){
			    dao.delete(book);
			}
						
			//����ָ����������ɾ��
//			Book book2 = new Book();
//			book2.setBookId(new ELong(101));
//			dao.delete(book2);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	
	/**
	 * �ȴ����ݿ����һ����, ��ɾ����Щ��
	 * 
	 * @throws Exception
	 */
	public void deleteMulitBooks() throws Exception {
	    log.info("\ndeleteMulitBooks()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			String sql = " select book_id,title,cost,publish_time from book where cost>20";
			
			@SuppressWarnings("rawtypes")
			ArrayList list = dao.get(sql, Book.class);

			//��listתΪ�����ɾ�� 
			@SuppressWarnings("unchecked")
			Book[] books = (Book[])list.toArray(new Book[0]);
			dao.delete(books);			
			list.clear();
			
			commit = true;// ��ʶ����ɹ�ִ��
			
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

    
    public void deleteMulitBooks2() throws Exception {
        log.info("\ndeleteMulitBooks()>>>>>>>>>>>>>>>>>>>>");
        
        dao.beginTrans(); // ��������
        boolean commit = false;// ����ִ�гɹ��ı�ʶ
        try {
            
            Set<String> set = new HashSet<String>();
            set.add("UNIX-�ϲ�");
            set.add("UNIX-�²�");
            

            dao.delete(Book.class,Exp.in("title",set));          
            //list.clear();
            
            commit = true;// ��ʶ����ɹ�ִ��
            
        } finally {
            dao.endTrans(commit);// ��������
        }
    }
	
	/**
	 * ָ������, ɾ��һ������
	 * 
	 * @throws Exception
	 */
	public void deleteBookByCriteria() throws Exception {
	    log.info("\ndeleteBookByCriteria()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {

			Criteria c1 = Exp.like("title", "����%");
			dao.delete(Book.class, c1);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * ��SQL�ķ�ʽ���������Ϣ
	 * 
	 * 
	 * @throws Exception
	 */
	public void executeSqlToUpdateBooks() throws Exception {
	    log.info("\nexecuteSqlToUpdateBooks()>>>>>>>>>>>>>>>>>>>>");

	    dao.beginTrans(); // ��������
        boolean commit = false;// ����ִ�гɹ��ı�ʶ
        try {

            //Oracle�汾��Ӧ
            //String sql = " UPDATE BOOK SET TITLE = '��Զ��'||TITLE WHERE TITLE LIKE ? ";

            //mySql�汾��Ӧ
            String sql = " UPDATE BOOK SET TITLE = CONCAT('��Զ��',TITLE) WHERE TITLE LIKE ? ";

            SqlParamMap pMap = new SqlParamMap();
            pMap.put(1, "UNIX%");

            dao.exec(sql, pMap);
            commit = true;// ��ʶ����ɹ�ִ��

        } finally {
            dao.endTrans(commit);// ��������
        }
    }

	/**
	 * �ֱ�������SQL�ķ�ʽ���������;
	 * 
	 * @throws Exception
	 */
	public void executeSqlToAddBook() throws Exception {
	    log.info("\nexecuteSqlToAddBook()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {

			// ��ʽ1����ִ��SQL
//			String sql = " INSERT INTO BOOK (BOOK_ID,TITLE,COST,PUBLISH_TIME) "
//			           + " VALUES (3, '³��ѷƯ����', 20.0,TO_DATE('2007-01-10', 'yyyy-MM-dd')) ";
		    
			String sql = " INSERT INTO BOOK (BOOK_ID,TITLE,COST) "
	           + " VALUES (3, '³��ѷƯ����', 20.0) ";
			dao.exec(sql);

			// ��ʽ2��������SQL
			sql = " INSERT INTO BOOK (BOOK_ID,TITLE,COST,PUBLISH_TIME) VALUES (?,?,?,?) ";
			SqlParamMap pMap = new SqlParamMap();
			pMap.put(1, "4");
			pMap.put(2, "�ʱ���");
			pMap.put(3, 22.0f);
			pMap.put(4, DateTimeUtil.parse("2007-0-10", "yyyy-MM-dd"));

			dao.exec(sql, pMap);

			commit = true;// ��ʶ����ɹ�ִ��

		} finally {
			dao.endTrans(commit);// ��������
		}

	}

	/**
	 * ��SQL�ķ�ʽ��Ӽ�λ������Ϣ
	 * 
	 * @throws Exception
	 */
	public void executeSqlToAddEditors() throws Exception {
	    log.info("\nexecuteSqlToAddEditors()>>>>>>>>>>>>>>>>>>>>");

		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			String sql = " INSERT INTO EDITOR (EDITOR_ID,NAME,SEX) VALUES (?,?,?) ";

			SqlParamMap[] pMapArray = new SqlParamMap[4];

			int k = 1;
			pMapArray[0] = new SqlParamMap();
			pMapArray[0].put(k++, 1);
			pMapArray[0].put(k++, "Richard Stevens");

			// ���δ˴���,���׳��쳣����������ʱ, ĳ�������ֵ��'?'�٣�
			pMapArray[0].put(k++, "M");

			// ���Ŵ˴���,���׳��쳣����������ʱ, ĳ�������ֵ��'?'�࣡
			//pMapArray[0].put(k++, "M");

			k = 1;
			pMapArray[1] = new SqlParamMap();
			pMapArray[1].put(k++, 2);
			pMapArray[1].put(k++, "������");
			pMapArray[1].put(k++, "M");

			k = 1;
			pMapArray[2] = new SqlParamMap();
			pMapArray[2].put(k++, 3);
			pMapArray[2].put(k++, "�޹���");
			pMapArray[2].put(k++, "M");

			k = 1;
			pMapArray[3] = new SqlParamMap();
			pMapArray[3].put(k++, 4);
			pMapArray[3].put(k++, "�Ѹ�");
			pMapArray[3].put(k++, "M");

			dao.exec(sql, pMapArray);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	/**
	 * ��SQL�ķ�ʽ��Ӽ���������ߵĹ�����Ϣ;
	 * 
	 * @throws Exception
	 */
	public void executeSqlToAddBookEditor() throws Exception {
	    log.info("\nexecuteSqlToAddBookEditor()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			String sql = " INSERT INTO BOOK_EDITOR (EDITOR_ID,BOOK_ID) VALUES (?,?) ";

			SqlParamMap[] pMapArray = new SqlParamMap[6];

			// Richard Stevens -> ��Զ��UNIX_�ϲ�
			pMapArray[0] = new SqlParamMap();
			pMapArray[0].put(1, 1);
			pMapArray[0].put(2, 101);

			// Richard Stevens -> ��Զ��UNIX_�в�
			pMapArray[1] = new SqlParamMap();
			pMapArray[1].put(1, 1);
			pMapArray[1].put(2, 102);

			// Richard Stevens -> ��Զ��UNIX_�²�
			pMapArray[2] = new SqlParamMap();
			pMapArray[2].put(1, 1);
			pMapArray[2].put(2, 103);

			// �޹��� -> ��������
			pMapArray[4] = new SqlParamMap();
			pMapArray[4].put(1, 3);
			// ����ϸ�۲죬���δ��к��ִ��Ч���� ������JDBC��Ч��һ�£� ���̳��������ֵ
			pMapArray[4].put(2, 1);

			// �޹��� -> ��������
			pMapArray[5] = new SqlParamMap();
			pMapArray[5].put(1, 4);
			// ����ϸ�۲죬���δ��к��ִ��Ч���� ������JDBC��Ч��һ�£� ���̳��������ֵ
			pMapArray[5].put(2, 3);

			dao.exec(sql, pMapArray);

			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}



	/**
	 * ��ʾǶ������
	 * 
	 * 
	 * @throws Exception
	 */
	public void nestedTransaction() throws Exception {
	    log.info("\nnestedTransaction()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {

           
			this.addOneBook();       //��һ��������
			
			//this.addMultiBooks();    //�ڶ���������
			
			this.executeSqlToAddBook();
			
			this.executeSqlToUpdateBooks();
			
//			 //���Ŵ˴��룬��������������������񽫻ع�
//			 if (true) {
//			 	throw new Exception(">>>>>throwed by nestedTransaction() ");
//			 }
			
			this.executeSqlToAddEditors();
			
			//this.executeSqlToAddBookEditor();//������������


			commit = true;// ��ʶ����ɹ�ִ��
		} finally {
			dao.endTrans(commit);// ��������
		}
	}

	/**
	 * �ҳ�ĳλ���ߵ����ϼ�������д����
	 * 
	 * @throws Exception
	 */
	public void getEditorAndBook() throws Exception {
	    log.info("\ngetEditorAndBook()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
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

			// ��ӡ����ѯ���
			StringBuffer buff = new StringBuffer("��ѯ���: \n");
			for (int i = 0; i < list.size(); i++) {
				EditorAndBook editorBook = (EditorAndBook) list.get(i);

				// ��������������Ա����ߡ��������������������ۡ��������
				buff.append(editorBook.getEditorName() + "\t");
				buff.append(editorBook.getEditorSex() + "\t");
				buff.append(editorBook.getTitle() + "\t");
				buff.append(editorBook.getCost() + "\t");

				// EasyDB�Ѿ������ݿ��ȡ��������ݣ� �����ļ�����ʽ�����JAVA��ʱĿ
				// ¼(java.io.tmp), ���ڴ�ӡ�����ļ���ȫ·����
				buff.append(editorBook.getBlobContent() + "\t");
				buff.append(editorBook.getTextContent() + "\t");

				buff.append("\n");
			}

			log.info(buff.toString());

			commit = true;// ��ʶ����ɹ�ִ��

//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());

		} finally {
			dao.endTrans(commit);// ��������
		}
	}
	
	


	/**
	 * ��ҳ��ѯ, ���ÿ2-3����¼
	 * 
	 * �ҳ�ĳλ���ߵ����ϼ�������д���� Demostrates the method: exec(String pSql, ParameterMap[]
	 * paraMapArray).
	 * 
	 * @throws Exception
	 */
	public void getEditorAndBookByPager() throws Exception {

	    log.info("\ngetEditorAndBookByPager()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
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

			// ��ӡ����ѯ���
			StringBuffer buff = new StringBuffer("��ѯ���: \n");
			for (int i = 0; i < list.size(); i++) {
				EditorAndBook editorBook = (EditorAndBook) list.get(i);

				// ��������������Ա����ߡ��������������������ۡ��������
				buff.append(editorBook.getEditorName() + "\t");
				buff.append(editorBook.getEditorSex() + "\t");
				buff.append(editorBook.getTitle() + "\t");
				buff.append(editorBook.getCost() + "\t");

				// EasyDB�Ѿ������ݿ��ȡ��������ݣ����ڴ�ӡ�����ļ���ȫ·��
				buff.append(editorBook.getBlobContent() + "\t");
				buff.append(editorBook.getTextContent() + "\t");
				
				buff.append("\n");
			}

			log.info(buff.toString());

			commit = true;// ��ʶ����ɹ�ִ��

//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());

		} finally {
			dao.endTrans(commit);// ��������
		}
	}
	
	
	/**
	 * ��ҳ��ѯ, ���ÿ2-3����¼
	 * 
	 * �ҳ�ĳλ���ߵ����ϼ�������д���� Demostrates the method: exec(String pSql, ParameterMap[]
	 * paraMapArray).
	 * 
	 * @throws Exception
	 */
	public void getEditorAndBookByPager2() throws Exception {
	    log.info("\ngetEditorAndBookByPager2()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		try {
			StringBuffer sql = new StringBuffer();
			// sql.append(" SELECT * FROM (");

			sql.append(" SELECT A.NAME EDITOR_NAME, A.SEX EDITOR_SEX, ");
			sql.append(" C.TITLE, C.COST, C.BLOB_CONTENT, C.TEXT_CONTENT ");
			sql.append(" FROM  EDITOR A, BOOK_EDITOR B, BOOK C ");
			sql.append(" WHERE A.EDITOR_ID = B.EDITOR_ID ");
			sql.append("   AND  B.BOOK_ID =  C.BOOK_ID ");
		    sql.append(" AND A.NAME like ? ");
			sql.append(" ORDER BY C.COST ");

			SqlParamMap map = new SqlParamMap();
			map.put(1,"%Rich%");
			
			@SuppressWarnings("rawtypes")
			ArrayList list = dao.get(sql.toString(),map, EditorAndBook.class, 1, 4);


			// ��ӡ����ѯ���
			StringBuffer buff = new StringBuffer("��ѯ���: \n");
			for (int i = 0; i < list.size(); i++) {
				EditorAndBook editorBook = (EditorAndBook) list.get(i);

				// ��������������Ա����ߡ��������������������ۡ��������
				buff.append(editorBook.getEditorName() + "\t");
				buff.append(editorBook.getEditorSex() + "\t");
				buff.append(editorBook.getTitle() + "\t");
				buff.append(editorBook.getCost() + "\t");

				// EasyDB�Ѿ������ݿ��ȡ��������ݣ����ڴ�ӡ�����ļ���ȫ·��
				buff.append(editorBook.getBlobContent() + "\t");
				buff.append(editorBook.getTextContent() + "\t");
				
				buff.append("\n");
			}

			log.info(buff.toString());

			commit = true;// ��ʶ����ɹ�ִ��

		} finally {
			dao.endTrans(commit);// ��������
		}
	}	
	

	/**
	 * ��ѯ��չʾ���е���
	 * 
	 * @throws Exception
	 */
	public void showAllBooks() throws Exception {
	    log.info("\nshowAllBooks()>>>>>>>>>>>>>>>>>>>>");
	    
		dao.beginTrans(); // ��������
		boolean commit = false;// ����ִ�гɹ��ı�ʶ
		
		try {
			//String sql = " update book set title='as' ";
			String sql = " select book_id,title,cost,publish_Time from BOOK order by book_id ";
			@SuppressWarnings("rawtypes")
			ArrayList list = dao.get(sql, Book.class);

			// ��ӡ����ѯ���			
			StringBuffer buff = new StringBuffer("��ѯ���: \n");
			for (int i = 0; i < list.size(); i++) {
				Book b = (Book) list.get(i);
				buff.append(b.getBookId() + "\t");
				buff.append(b.getTitle() + "\t");
				buff.append(b.getCost() + "\t");
				//buff.append(b.getBlobContent() + "\t");
				//buff.append(b.getTextContent()+"\t");
				buff.append(b.getPublishTime()+"\n");
			}
			log.info(buff);

			commit = true;// ��ʶ����ɹ�ִ��
			
		} finally {
			dao.endTrans(commit);// ��������
		}
	}
	

	/**
	 * ��ĳ�����н�ȡһ����¼
	 * 
	 * @throws Exception
	 */
	public void getOneBook() throws Exception {
        log.info("\ngetOneBook()>>>>>>>>>>>>>>>>>>>>");
        dao.beginTrans(); // ��������
        boolean commit = false;// ����ִ�гɹ��ı�ʶ
        try {

            Criteria c = Exp.eq("bookId", 102);
            Book book = (Book) dao.getOne(Book.class, c);

            // �����ѯ���
            StringBuffer buff = new StringBuffer("��ѯ���: \n");
            if (book != null) {
                buff.append(book.getBookId() + "\t");
                buff.append(book.getTitle() + "\t");
                buff.append(book.getCost() + "\t");
                //buff.append(book.getBlobContent() + "\t");
                //buff.append(book.getTextContent() + "\t");
                buff.append(book.getPublishTime() + "\n");
            }
            log.info(buff.toString());

            commit = true;// ��ʶ����ɹ�ִ��
        } finally {
            dao.endTrans(commit);// ��������
        }
    }

	
	public void prepareTest() throws Exception {

		File file1 = new File("d:\\unix.txt");
		File file2 = new File("d:\\unix.chm");
		File file3 = new File("d:\\��������.txt");

		FileOutputStream out;
		try {
			out = new FileOutputStream(file1);
			out.write("�ı��ı��ı��ı�".getBytes());
			out.flush();
			out.close();
			
			out = new FileOutputStream(file2);
			out.write("������".getBytes());
			out.flush();
			out.close();
			
			out = new FileOutputStream(file3);
			out.write("����������������".getBytes());
			out.flush();
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void endTest() throws Exception {

		File file1 = new File("d:\\unix.txt");
		File file2 = new File("d:\\unix.chm");
		File file3 = new File("d:\\��������.txt");
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * �������Ӷ౾��;
	 * 
	 * @throws Exception
	 */
	public void addMultiBooks2() throws Exception {
        log.info("\naddMultiBooks2()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // ��������
        boolean commit = false;// ����ִ�гɹ��ı�ʶ
        try {

            int size = 1;
            Book[] BookArray = new Book[size];

            for (int i = 0; i < size; i++) {
                BookArray[i] = new Book();
                BookArray[i].setBookId(new ELong(100000+i));
                BookArray[i].setTitle(new EString("UNIX-�ϲ�"+i));
                BookArray[i].setCost(new EFloat(100.0f));
            }

            dao.save(BookArray); // �־û�ʵ��

            commit = true;// ��ʶ����ɹ�ִ��

        } finally {
            dao.endTrans(commit);// ��������
        }
    }
	
	
	@SuppressWarnings("unchecked")
	public void finalTest ()throws Exception {
        log.info("\nfinalTest()>>>>>>>>>>>>>>>>>>>>");

        dao.beginTrans(); // ��������
        boolean commit = false;// ����ִ�гɹ��ı�ʶ
        try {
            
            @SuppressWarnings("rawtypes")
			ArrayList list = dao.get("select * from book",Book.class);
            
            log.info(">>>>>>>>>>>>>"+list.size());
            Book[] books = new Book[list.size()];

            list.toArray(books);
            books[1].setCost(new EFloat("20000"));

            dao.update(books); // �־û�ʵ��

            commit = true;// ��ʶ����ɹ�ִ��

        } finally {
            dao.endTrans(commit);// ��������
        }
	}
}
