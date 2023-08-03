package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ReadConfig class is used to read the configuration details from the
 * config properties file
 * 
 * @author Dhanush
 *
 */
public class ReadConfig {

	private static final Logger log = LogManager.getLogger(ReadConfig.class);
	private static final String CONFIG_PATH = "./config/config.properties";
	private static Properties properties = null;

	/**
	 * Get value from config properties file by providing the key
	 * 
	 * @param key The key to retrieve the value
	 * @return The value associated with the key, or null if the key is not found
	 */
	public static String getConfigValue(String key) {
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(key);
	}

	/**
	 * Get Workbook paths in config properties file which contains key name (Not
	 * using Main.xlsx is replaced for this) TestSuite_SHEETPATH
	 * 
	 * @return A map containing TestSuite_SHEETPATH keys and their corresponding
	 *         values
	 */
	public static Map<String, String> getTestSuiteSheetPath() {
		if (properties == null) {
			loadProperties();
		}

		Map<String, String> map = new HashMap<>();

		for (String key : properties.stringPropertyNames()) {
			if (key.contains("TestSuite_SHEETPATH")) {
				map.put(key, properties.getProperty(key));
			}
		}
		return map;
	}

	private static void loadProperties() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(CONFIG_PATH);
			properties = new Properties();
			properties.load(fis);
		} catch (IOException e) {
			log.error("Exception in loadProperties: " + e.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				log.error("Exception in closing FileInputStream: " + e.getMessage());
			}
		}
	}
}