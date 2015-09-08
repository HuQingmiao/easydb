package com.github.walker.easydb.assistant;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class provides multiple languages for message.
 * 
 * @author Huqingmiao
 * 
 */
public class LingualResource {

	private static HashMap<String, LingualResource> instances = new HashMap<String, LingualResource>(2); // instance
																											// pool

	private String baseFileName = null;

	private ResourceBundle resourceBundle = null;

	/**
	 * 
	 * @param languageCode
	 *            ���ִ���
	 * @param regionCode
	 *            ��������
	 */
	private LingualResource(String languageCode, String regionCode) {

		String localeCode = makeLocaleCode(languageCode, regionCode);
		Locale locale = new Locale(languageCode, regionCode);

		baseFileName = "res";

		// load the resource file, such as��res_en_US.properties
		resourceBundle = ResourceBundle.getBundle(baseFileName, locale);

		// put the instance into pool
		instances.put(localeCode, this);
	}

	/**
	 * ȡ��ĳ���ֵ���Դ��Ϣ��ȡ��
	 * 
	 * @param language
	 *            ���ִ���
	 * @param region
	 *            ��������
	 */
	public synchronized static LingualResource getInstance(String language, String region) {
		String tmpCode = makeLocaleCode(language, region);

		if (instances.containsKey(tmpCode)) {
			return (LingualResource) instances.get(tmpCode);
		}

		return new LingualResource(language, region);

	}

	public String getLocaleString(String code) {
		return resourceBundle.getString(code);
	}

	private static String makeLocaleCode(String language, String region) {
		return language + "_" + region;
	}
}