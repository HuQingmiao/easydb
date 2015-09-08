package com.github.walker.easydb;

import org.apache.log4j.Logger;

import com.github.walker.easydb.assistant.LogFactory;

/**
 * 
 * Demostrates the using of EasyDB component.
 * 
 * @author HuQingmiao
 * 
 */
public class DemoClient {

	private static Logger log = LogFactory.getLogger(DemoClient.class);

	public static void main(String[] args) {

		DemoBuiness app = new DemoBuiness();

		try {
			app.prepareTest();

			app.clearTestData();

			app.showAllBooks();

			app.addOneBook();
			app.showAllBooks();

			app.addMultiBooks();
			app.showAllBooks();

			app.updateOneBook();
			app.showAllBooks();

			app.updateMulitBooks();
			app.showAllBooks();

			app.updateBookByCriteria();
			app.showAllBooks();

			app.deleteOneBook();
			app.showAllBooks();

			app.deleteMulitBooks2();
			app.showAllBooks();

			app.deleteBookByCriteria();
			app.showAllBooks();

			app.nestedTransaction();

			app.getEditorAndBookByPager();
			app.getEditorAndBookByPager2();

			app.getOneBook();
			app.addMultiBooks2();

			app.showAllBooks();

			app.finalTest();

			app.showAllBooks();
			
			app.clearTestData();
			app.endTest();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
