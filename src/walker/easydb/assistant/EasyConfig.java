package walker.easydb.assistant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class loads config infomation.
 * 
 * @author HuQingmiao
 * 
 */
public class EasyConfig {
	// database connection config file
	public static String CONFIG_FILENAME = "easydb.properties";

	private static Logger log = LogFactory.getLogger(EasyConfig.class);

	// the variable configCache stores the config info
	private static Properties configCache = new Properties();

	static {
		try {
			InputStream is = EasyConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
			configCache.load(is);

			// 如果设定了重定向文件, 则装载该文件
			String redirectFile = configCache.getProperty("redirect_file");
			if (redirectFile != null) {
				CONFIG_FILENAME = redirectFile;
				// is.close();
				is = EasyConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
				configCache.load(is);
			}

			log.info("Loading the config information from '" + EasyConfig.CONFIG_FILENAME + "'... OK! ");

		} catch (FileNotFoundException e) {
			log.error("Loading the config information from '" + EasyConfig.CONFIG_FILENAME + "'... FAILED! ", e);
		} catch (IOException e) {
			log.error("Loading the config information from '" + EasyConfig.CONFIG_FILENAME + "'... FAILED! ", e);
		}
	}

	private EasyConfig() {
	}

	/**
	 * Retrieves the configuration info by the key.
	 * 
	 * @param key
	 */
	public static String getProperty(String key) {

		if (configCache.containsKey(key)) {
			return configCache.getProperty(key).trim();
		}
		return null;
	}

}