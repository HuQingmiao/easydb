package com.github.walker.easydb.assistant;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Logger Factory that based on log4j .
 * 
 * @author HuQingmiao
 * 
 */
public class LogFactory {

	// config file of log4j
	private static String CONFIG_FILENAME = "log4j.properties";

	private static URL url = LogFactory.class.getClassLoader().getResource(CONFIG_FILENAME);

	public static final String MODULE_EASYDB = "easydb";

	/**
	 * ��ȡ��־��¼��
	 * 
	 * @param moduleName
	 *            ģ�����ƻ���־ǰ׺����
	 * 
	 * @return the Logger
	 * 
	 */
	public static Logger getLogger(String moduleName) {
		Logger logger = null;

		if (!moduleName.equalsIgnoreCase(MODULE_EASYDB)) {
			logger = Logger.getLogger(MODULE_EASYDB);
		} else {
			logger = Logger.getLogger(moduleName);
		}

		PropertyConfigurator.configure(url);

		return logger;
	}

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz);
		PropertyConfigurator.configure(url);

		return logger;
	}

}