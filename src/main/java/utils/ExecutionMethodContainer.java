package utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExecutionMethodContainer {
	private static List<Method> methods = new ArrayList<>();
	private static List<Object[]> argumentsObjectList = new ArrayList<>();

	/**
	 * Add the method and arguments to the list for execution
	 *
	 * @param method    The method to add
	 * @param arguments The arguments to add (as a String list)
	 */
	public void addExecutionMethod(Method method, List<String> arguments) {
		methods.add(method);
		Object[] argumentsArray = arguments.toArray();
		argumentsObjectList.add(argumentsArray);
	}

	/**
	 * Return the list of methods.
	 *
	 * @return List of Method objects
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * Return the list of argument objects (Object arrays).
	 *
	 * @return List of Object arrays
	 */
	public List<Object[]> getArgumentObjects() {
		return argumentsObjectList;
	}

	/**
	 * Reset the stored data by clearing both the methods and argumentsObjectList
	 * lists.
	 */
	public void resetData() {
		methods.clear();
		argumentsObjectList.clear();
	}
}
