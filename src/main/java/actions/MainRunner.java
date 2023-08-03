package actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import core.Application;
import core.Driver;
import utils.ExecutionMethodContainer;
import utils.ExecutionUtils;
import utils.ProjectConfiguration;
import utils.ProjectSetupManager;
import utils.ReflectionUtils;
import utils.Settings;
import utils.Timer;

/**
 * The Runner class handles different actions related to project setup,
 * validation, and execution.
 */
public class MainRunner {
	private static final Logger log = LogManager.getLogger(MainRunner.class);
	private ProjectSetupManager projectSetup;
	private ExecutionUtils executionUtil;
	private List<String> workbookPaths;
	private ReflectionUtils reflectionUtils;
	private ActionMethods action;
	private ProjectConfiguration config;
	public int stepDelay = 100;
	public boolean retryFlag = true;

	public static void main(String[] args) {
		MainRunner mainRunner = new MainRunner();
		mainRunner.run(Action.EXECUTE);

	}

	/**
	 * Constructor to initialize the Runner with required dependencies.
	 */
	public MainRunner() {
		this.config = new ProjectConfiguration();
		this.executionUtil = new ExecutionUtils();
		this.projectSetup = new ProjectSetupManager(this.config, this.executionUtil);
		this.reflectionUtils = new ReflectionUtils();
		this.action = new ActionMethods(new Driver());
	}

	public enum Action {
		SETUP, VALIDATE, EXECUTE
	}

	/**
	 * This method performs actions based on the specified action type.
	 *
	 * @param action The action type. Possible values: SETUP, VALIDATE, or EXECUTE.
	 */
	public void run(Action action) {
		Keyboard.releaseAllKeys();

		boolean foldersExist = config.checkFoldersExist();

		switch (action) {
		case SETUP:
			if (!foldersExist) {
				projectSetup.createFolderStructure();
			}
			break;

		case VALIDATE:
			if (!foldersExist) {
				projectSetup.createFolderStructure();
			}
			setupIfWorkbookPathsNull();
			validateExcelSheets(workbookPaths);
			break;

		case EXECUTE:
			if (!foldersExist) {
				projectSetup.createFolderStructure();
			}
			setupIfWorkbookPathsNull();
			executeTests();
			break;

		default:
			System.err.println("Invalid action: " + action);
			break;
		}

		Keyboard.releaseAllKeys();
	}

	/**
	 * Helper method to perform setup if the workbookPaths are null.
	 */
	private void setupIfWorkbookPathsNull() {
		if (workbookPaths == null) {
			projectSetup.setupProject();
			workbookPaths = projectSetup.getWorkbookPaths();
		}
	}

	/**
	 * Validate all the Excel sheets based on the provided workbook paths.
	 *
	 * @param workbookPaths A list of paths to the Excel workbooks to be validated.
	 * @return True if all the Excel sheets are successfully validated; otherwise,
	 *         false.
	 */
	public boolean validateExcelSheets(List<String> workbookPaths) {
		log.info("validateExcelSheets started...");

		String targetClassName = "actions.ActionMethods";
		Method[] targetClassMethods = reflectionUtils.getDeclaredMethods(targetClassName);

		boolean overallExecutionStatus = workbookPaths.stream()
				.map(workbookPath -> executionUtil.validateWorkbookSteps(workbookPath, targetClassMethods))
				.reduce(true, (accumulator, validationStatus) -> accumulator && validationStatus);

		log.info("validateExcelSheets completed.");
		return overallExecutionStatus;
	}

	/**
	 * Execute tests based on loaded methods and checked images.
	 */
	private void executeTests() {

		// Clear the list of images and load images from the project.
		executionUtil.images.clear();
		LinkedHashSet<String> imageFiles = loadImages();

		// Load methods from the project and check images.
		boolean status = loadMethods(workbookPaths);
		status = checkImages(imageFiles);

		// Print the image status and start the execution.
		System.err.println("image status - " + status);
		startExecution(status);
	}

	/**
	 * Load images from the specified Sikuli image base path.
	 *
	 * @return A LinkedHashSet containing the absolute paths of the loaded image
	 *         files.
	 */
	private LinkedHashSet<String> loadImages() {
		LinkedHashSet<String> imagesList = new LinkedHashSet<>();
		File[] imageFiles = new File(ProjectConfiguration.sikuliImageBasePath).listFiles();

		for (File file : imageFiles) {
			String path = file.getAbsolutePath();
			imagesList.add(path);
		}
		return imagesList;
	}

	/**
	 * Load methods from the specified Excel workbooks based on the provided
	 * workbook paths.
	 *
	 * @param workbookPaths A list of paths to the Excel workbooks to load methods
	 *                      from.
	 * @return True if all methods are successfully loaded from the workbooks;
	 *         otherwise, false.
	 */
	public boolean loadMethods(List<String> workbookPaths) {
		log.info("loadMethods started...");
		String targetClassName = "actions.ActionMethods";
		Method[] targetClassMethods = reflectionUtils.getDeclaredMethods(targetClassName);

		boolean overallExecutionStatus = workbookPaths.stream()
				.map(workbookPath -> executionUtil.loadMethodsFromWorkbook(workbookPath, targetClassMethods))
				.reduce(true, (accumulator, loadingStatus) -> accumulator && loadingStatus);

		log.info("loadMethods completed.");
		return overallExecutionStatus;
	}

	/**
	 * Check if all images in the executionUtil.images list are present in the
	 * provided set of images.
	 *
	 * @param images A LinkedHashSet containing the absolute paths of images to
	 *               check against.
	 * @return True if all images in the executionUtil.images list are present in
	 *         the provided set; otherwise, false.
	 */
	private boolean checkImages(LinkedHashSet<String> images) {
		boolean status = true;
		for (String imagePath : executionUtil.images) {
			boolean imagePresent = images.contains(imagePath);
			System.out.println(imagePath + (imagePresent ? " - image present" : " - image missing"));
			status &= imagePresent;
		}
		return status;
	}

	/**
	 * Perform the execution process, which includes checking files, gathering
	 * methods, and invoking methods using reflection.
	 *
	 * @param sheetStatus A boolean value indicating whether the sheet status is
	 *                    valid for execution.
	 */
	private void startExecution(boolean sheetStatus) {
		if (!sheetStatus) {
			return; // If sheetStatus is false, do not proceed with execution.
		}

		try {
			log.info("startExecution started...");

			ExecutionMethodContainer methodContainer = new ExecutionMethodContainer();
			List<Method> methods = methodContainer.getMethods();
			List<Object[]> arguments = methodContainer.getArgumentObjects();

//			StringBuilder logString = new StringBuilder();
			for (int i = 0; i < methods.size(); i++) {
				Method method = methods.get(i);
				Object[] methodArguments = arguments.get(i);

				String logString = "";
				try {
					for (int itr = 0; itr < methodArguments.length; itr++) {
						logString = logString + " - " + methodArguments[itr];
					}

					invoker(method, methodArguments, logString.toString());
				} catch (Exception e) {
					action.logResult(false);
					log.error("Exception occurred during method execution: " + method.getName() + logString, e);
					ActionMethods.reportLogger
							.info(config.getFailMarkUp(method.getName() + logString + " action thrown exception."));
					break;
				}

				Timer.waitTime(stepDelay);
			}

			action.closeReportLogger();
			log.info("startExecution completed.");

		} catch (Exception e) {
			log.error("An unexpected error occurred during execution.", e);
		} finally {
			log.info("Close all Application started...");
			Application.closeAllApps();
			log.info("Close all Application completed.");
			Application.applicationPaths.clear();
		}
	}

	/**
	 * Invoke the specified method with the given arguments and perform a retry if
	 * required.
	 *
	 * @param method    The method to invoke.
	 * @param arguments The arguments to be passed to the method.
	 * @param logString A string used for logging purposes.
	 */
	private void invoker(Method method, Object[] arguments, String logString) {
		try {
			// Invoke the method with the given arguments.
			method.invoke(action, arguments);

			// Check if retry is needed and call retryEvent.
			if (retryFlag) {
				retryEvent(method, arguments, logString);
			}
		} catch (IllegalAccessException e) {
			log.error("Error invoking method: " + method.getName() + " - " + logString, e);
		} catch (IllegalArgumentException e) {
			log.error("Invalid arguments for method: " + method.getName() + " - " + logString, e);
		} catch (InvocationTargetException e) {
			log.error("Error executing method: " + method.getName() + " - " + logString, e);
		}
	}

	/**
	 * Perform a retry for the specified method with the given arguments. The retry
	 * will only occur if the eventStatus in ActionMethods is false.
	 *
	 * @param method    The method to retry.
	 * @param arguments The arguments to be passed to the method during retry.
	 * @param logString A string used for logging purposes.
	 */
	private void retryEvent(Method method, Object[] arguments, String logString) {
		if (!ActionMethods.eventStatus) {
			// Log retry attempt.
			ActionMethods.reportLogger.info(config.getRetryMarkUp(method.getName() + " - " + logString + " action retried."));

			// Temporarily reduce the FIND_WAIT setting for retry.
			long temp = Settings.FIND_WAIT;
			if (Settings.FIND_WAIT > 5) {
				Settings.FIND_WAIT = 5;
			}

			try {
				// Retry by invoking the method with the given arguments.
				method.invoke(action, arguments);
			} catch (IllegalAccessException e) {
				log.error("Error retrying method: " + method.getName() + " - " + logString, e);
			} catch (IllegalArgumentException e) {
				log.error("Invalid arguments for method: " + method.getName() + " - " + logString, e);
			} catch (InvocationTargetException e) {
				log.error("Error executing method: " + method.getName() + " - " + logString, e);
			} finally {
				// Restore the original FIND_WAIT setting after retry.
				Settings.FIND_WAIT = temp;
			}
		}
	}
}