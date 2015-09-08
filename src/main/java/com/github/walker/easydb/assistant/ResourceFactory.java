package com.github.walker.easydb.assistant;

import java.util.Properties;

/**
 * This class is the factory of LingualResource Class.
 * 
 * @author HuQingmiao
 * 
 */
public class ResourceFactory {

	/**
	 * Retrives the local language enviroment by static method.
	 * 
	 * @return LingualResource class
	 */
	public static LingualResource getResource() {

		// Retrieve the the language code and country code of current user.
		Properties env = System.getProperties();
		String language = env.getProperty("user.language");
		String country = env.getProperty("user.country");

		return LingualResource.getInstance(language, country);
	}

	// test
	public static void main(String[] args) {
//		Properties env = System.getProperties();
//
//		for (Iterator<Object> it = env.keySet().iterator(); it.hasNext();) {
//			String a = it.next().toString();
//			System.out.println(a + ": " + env.getProperty(a));
//		}

		LingualResource ling = getResource();
		String str = ling.getLocaleString("db.connect.failed");
		System.out.println("db.connect.failed=" + str);
	}
}
