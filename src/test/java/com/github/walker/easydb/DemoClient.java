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

		DemoService app = new DemoService();

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

			app.deleteOneBook();
			app.showAllBooks();

			app.deleteBookByCriteria();
			app.showAllBooks();

			app.nestedTransaction();

			app.getEditorAndBookByPager();

			app.getOneBook();

			app.clearTestData();
			app.endTest();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
