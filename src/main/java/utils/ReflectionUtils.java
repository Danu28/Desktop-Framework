package utils;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReflectionUtils {
	// Other methods and variables in the class...
	private static final Logger log = LogManager.getLogger(ExecutionUtils.class);

	/**
	 * Get the array of declared methods of a class by providing its full class
	 * name.
	 * 
	 * @param fullClassName The full name of the class (including package) to get
	 *                      the methods from.
	 * @return An array of Method objects representing the declared methods of the
	 *         class.
	 */
	public Method[] getDeclaredMethods(String fullClassName) {
		Class<?> cls = null;
		try {
			cls = Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			log.error("Class not found: " + fullClassName);
			// Handle the exception appropriately or propagate it further if needed.
			// For now, we'll just return an empty array of methods.
			return new Method[0];
		}
		return cls.getDeclaredMethods();
	}
}
