package actions;

import java.io.File;
import java.io.IOException;

import org.sikuli.script.FindFailed;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import core.Application;
import core.Driver;
import core.EventFailException;
import core.SikuliElement;
import core.UIElement;
import epiplex.Capture;
import epiplex.PerformanceUtils;
import epiplex.RemoteClient;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import utils.ProjectConfiguration;
import utils.Settings;
import utils.TakeScreenshot;
import utils.Timer;
import utils.XML_Library;

/**
 * This class consists of all action methods which will be used to automate from
 * excel
 * 
 * @author Dhanush
 *
 */
public class ActionMethods {

	private final Driver driver;
	private final ElementFinder elementFinder;
	private final ProjectConfiguration config;
	private final RemoteClient remoteClient;
	private final XML_Library xmlLib;
	protected static boolean eventStatus = true;
	private static String testcaseName;
	public static ExtentTest reportLogger;

	public ActionMethods(Driver driver) {
		this.driver = driver;
		elementFinder = new ElementFinder(driver);
		config = new ProjectConfiguration();
		xmlLib = new XML_Library();
		remoteClient = new RemoteClient();
	}

	/**
	 * Creates a test in the report with the given name and returns an ExtentTest
	 * instance representing the test case.
	 *
	 * @param testcaseName The name of the test case.
	 * @return The ExtentTest instance representing the test case.
	 */
	public ExtentTest startTest(String testcaseName) {
		// Create a test in the report using the given test case name.
		reportLogger = ProjectConfiguration.extentReporter.createTest(testcaseName);

		// Set the current test case name in the ActionMethods class.
		ActionMethods.testcaseName = testcaseName;

		// Log a message indicating that the test case has started.
		String logMessage = "startTest - " + testcaseName + " started successfully";
		if (reportLogger != null) {
			// If the reportLogger is not null, log a pass message.
			reportLogger.info(config.getPassMarkUp(logMessage));
		} else {
			// If the reportLogger is null, log a failure message.
			reportLogger.info(config.getFailMarkUp(logMessage + " but reportLogger is null."));
		}

		// Return the ExtentTest instance representing the test case.
		return reportLogger;
	}

	/**
	 * Finalizes the report logger and logs a success message if the operation
	 * succeeds, or logs a failure message if the operation throws an exception.
	 */
	public void closeReportLogger() {
		try {
			// Flush the report data by calling the 'reportFlush()' method from 'config'.
			config.reportFlush();

			// Log a success message indicating that the report logger was closed
			// successfully.
			reportLogger.info(config.getPassMarkUp("ReportLogger closed successfully"));
		} catch (Exception e) {
			// If an exception occurs during the 'reportFlush()' operation,
			// log a failure message indicating that the report logger close failed.
			reportLogger.info(config.getFailMarkUp("ReportLogger close failed"));
		}
	}

	/**
	 * Logs the result of a test case execution in the report.
	 *
	 * @param testStatus The status of the test case (true if passed, false if
	 *                   failed).
	 */
	protected void logResult(boolean testStatus) {
		try {
			// Attempt to capture a screenshot of the current state of the application or
			// webpage.
			// The screenshot will be added to the report log along with the test execution
			// details.
			reportLogger.info("Screen shot:- ", MediaEntityBuilder
					.createScreenCaptureFromPath(TakeScreenshot.captureScreenshot(testcaseName)).build());
		} catch (IOException e) {
			// If an IOException occurs during screenshot capture, it will be caught here.
			// However, the exception is not logged or handled further in this method.
			// It is recommended to handle exceptions appropriately in production code.
			// For example, you can log the exception or take further action based on the
			// requirement.
		}

		// Log the test status (pass or fail) in the report based on the provided
		// 'testStatus'.
		if (testStatus) {
			// If the test passed, log a pass message with the test case name in uppercase.
			reportLogger.pass(config.getPassMarkUp(testcaseName.toUpperCase() + " PASSED"));
		} else {
			// If the test failed, log a fail message with the test case name in uppercase.
			reportLogger.fail(config.getFailMarkUp(testcaseName.toUpperCase() + " FAILED"));
		}
	}

	/**
	 * Deletes a file with the specified file name and file path.
	 *
	 * @param fileName The name of the file to be deleted.
	 * @param filePath The path where the file is located, relative to the
	 *                 repository path. This should not include the repository path
	 *                 itself.
	 */
	public void deleteFile(String fileName, String filePath) {
		// Construct the absolute file path by combining the repository path with the
		// provided filePath and fileName.
		String absoluteFilePath = utils.Settings.REPO_PATH + "\\" + filePath.trim() + "\\" + fileName.trim();
		File file = new File(absoluteFilePath);

		try {
			// Check if the file exists before attempting to delete it.
			if (file.exists()) {
				// If the file exists, attempt to delete it.
				if (file.delete()) {
					// If the file is successfully deleted, log a pass message in the report.
					String successMessage = absoluteFilePath + " Deleted completed.";
					reportLogger.info(config.getPassMarkUp(successMessage));
				}
			}
		} catch (Exception e) {
			// If an exception occurs during file deletion, log a failure message in the
			// report.
			String failureMessage = absoluteFilePath + " Deleted got failed";
			reportLogger.info(config.getFailMarkUp(failureMessage));
		}
	}

	/**
	 * Deletes a document (file) from the given path.
	 *
	 * @param docPath The path of the document to be deleted.
	 */
	public void deleteFile(String docPath) {
		try {
			// Invoke the 'deleteFile' method from 'XML_Library' to delete the document.
			docPath = new XML_Library().deleteFile(docPath);

			// Log a pass message indicating that the document was successfully deleted.
			reportLogger.info(config.getPassMarkUp(docPath + " Deleted completed."));
		} catch (Exception e) {
			// If an exception occurs during document deletion, log a failure message in the
			// report.
			reportLogger.info(config.getFailMarkUp(docPath + " Deleted got failed"));
		}
	}

	/**
	 * Checks whether the given string exists in an XML file located in the
	 * repository or program data. Logs the result of the assertion in the report.
	 *
	 * @param documentPath The path of the XML file in the repository or program
	 *                     data.
	 * @param expected     The string to be searched for in the XML file.
	 */
	public void assertXMLExist(String documentPath, String expected) {
		try {
			// Check if the expected string exists in the XML file.
			boolean actualStatus = xmlLib.isTextInFile(documentPath, expected);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp("assertXMLExist completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp("assertXMLExist got failed"));
		}
	}

	/**
	 * Checks whether the given string does not exist in an XML file located in the
	 * repository or program data. Logs the result of the assertion in the report.
	 *
	 * @param documentPath The path of the XML file in the repository or program
	 *                     data.
	 * @param expected     The string not to be found in the XML file.
	 */
	public void assertXMLNotExist(String documentPath, String expected) {
		try {
			// Check if the expected string does not exist in the XML file.
			boolean actualStatus = !xmlLib.isTextInFile(documentPath, expected);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp("assertXMLNotExist completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp("assertXMLNotExist got failed"));
		}
	}

	/**
	 * Checks whether a file exists in the given path from the repository. Logs the
	 * result of the assertion in the report.
	 *
	 * @param fileName The name of the file to check for existence.
	 * @param filePath The path where the file is located, relative to the
	 *                 repository path.
	 */
	public void assertFileExists(String fileName, String filePath) {
		String absoluteFilePath = null;
		try {
			// Construct the absolute file path by combining the repository path with the
			// provided filePath and fileName.
			absoluteFilePath = utils.Settings.REPO_PATH + "\\" + filePath + "\\" + fileName;
			File file = new File(absoluteFilePath);

			// Check if the file exists.
			boolean status = file.exists();

			// Log the result of the assertion in the report.
			logResult(status);
			reportLogger.info(config.getPassMarkUp(absoluteFilePath + " assertFileExists completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp(absoluteFilePath + " assertFileExists got failed"));
		}
	}

	/**
	 * Verifies the 'name' attribute of a UI element identified by the 'controlType'
	 * and 'IDValue' parameters. Logs the result of the assertion in the report.
	 *
	 * @param controlType The type of the UI element (e.g., "id", "name", "text",
	 *                    etc.).
	 * @param IDValue     The value used to identify the UI element (e.g., the ID,
	 *                    name, or text).
	 * @param expected    The expected 'name' attribute value of the UI element.
	 */
	public void assertName(String controlType, String IDValue, String expected) {
		try {
			String actual = null;
			try {
				// Attempt to get the UI element using the provided 'controlType', 'IDValue',
				// and a timeout of 3 seconds.
				actual = elementFinder.getUIElement("id", controlType, IDValue, 3).getName();
			} catch (AutomationException e) {
				// If an AutomationException occurs during the element retrieval, it will be
				// caught here.
				// The 'actual' value will remain null, and the assertion will be handled later.
			}

			// Log the result of the assertion in the report.
			logResult(actual.equals(expected));
			reportLogger.info(config.getPassMarkUp(controlType + " - " + IDValue + " assertName completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp(controlType + " - " + IDValue + " assertName got failed"));
		}
	}

	/**
	 * Verifies whether an element exists and logs the result in the report.
	 *
	 * @param locatorType The type of locator used to find the element (e.g., "id",
	 *                    "name", "text", etc.).
	 * @param parameter1  The first parameter used in the element identification
	 *                    (e.g., the ID, name, or text).
	 * @param parameter2  The second parameter used in the element identification
	 *                    (if required).
	 */
	public void assertExist(String locatorType, String parameter1, String parameter2) {
		try {
			// Check whether the element exists using the provided locator and parameters.
			boolean actualStatus = elementFinder.waitToDisplay(locatorType, parameter1, parameter2, 5);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config
					.getPassMarkUp(locatorType + " - " + parameter1 + " - " + parameter2 + " assertExist completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config
					.getPassMarkUp(locatorType + " - " + parameter1 + " - " + parameter2 + " assertExist got failed"));
		}
	}

	/**
	 * Verifies whether an element does not exist and logs the result in the report.
	 *
	 * @param locatorType The type of locator used to find the element (e.g., "id",
	 *                    "name", "text", etc.).
	 * @param parameter1  The first parameter used in the element identification
	 *                    (e.g., the ID, name, or text).
	 * @param parameter2  The second parameter used in the element identification
	 *                    (if required).
	 */
	public void assertNotExist(String locatorType, String parameter1, String parameter2) {
		try {
			// Check whether the element does not exist using the provided locator and
			// parameters.
			boolean actualStatus = elementFinder.waitToVanish(locatorType, parameter1, parameter2, 5);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertNotExist completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertNotExist got failed"));
		}
	}

	/**
	 * Verifies whether the given element is enabled and logs the result in the
	 * report.
	 *
	 * @param locatorType The type of locator used to find the element (e.g., "id",
	 *                    "name", "text", etc.).
	 * @param parameter1  The first parameter used in the element identification
	 *                    (e.g., the ID, name, or text).
	 * @param parameter2  The second parameter used in the element identification
	 *                    (if required).
	 */
	public void assertEnabled(String locatorType, String parameter1, String parameter2) {
		try {
			// Check whether the element is enabled using the provided locator and
			// parameters.
			boolean actualStatus = elementFinder.waitToEnable(locatorType, parameter1, parameter2, 3);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertEnabled completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getPassMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertEnabled got failed"));
		}
	}

	/**
	 * Verifies whether the given element is disabled and logs the result in the
	 * report.
	 *
	 * @param locatorType The type of locator used to find the element (e.g., "id",
	 *                    "name", "text", etc.).
	 * @param parameter1  The first parameter used in the element identification
	 *                    (e.g., the ID, name, or text).
	 * @param parameter2  The second parameter used in the element identification
	 *                    (if required).
	 */
	public void assertNotEnabled(String locatorType, String parameter1, String parameter2) {
		try {
			// Check whether the element is not enabled using the provided locator and
			// parameters.
			boolean actualStatus = !elementFinder.waitToEnable(locatorType, parameter1, parameter2, 3);

			// Log the result of the assertion in the report.
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertNotEnabled completed."));
		} catch (Exception e) {
			// If an exception occurs during the assertion, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp(
					locatorType + " - " + parameter1 + " - " + parameter2 + " assertNotEnabled got failed"));
		}
	}

	/**
	 * Compares the given GPS file from the repository with the base file present in
	 * the base files folder. Logs the comparison result in the report.
	 *
	 * @param baseFileName    The name of the base GPS file in the base files
	 *                        folder.
	 * @param currentFileName The name of the current GPS file in the repository.
	 */
	public void compareGPS(String baseFileName, String currentFileName) {
		try {
			// Compare the GPS files using the provided base and current file names.
			xmlLib.compareGPSFiles(baseFileName, currentFileName, reportLogger);

			// Log the result of the comparison in the report.
			reportLogger.info(config.getPassMarkUp(baseFileName + " - " + currentFileName + " compareGPS completed."));
		} catch (Exception e) {
			// If an exception occurs during the comparison, log a failure message in the
			// report.
			reportLogger.fail(config.getFailMarkUp(baseFileName + " - " + currentFileName + " compareGPS got failed"));
		}
	}

	/**
	 * Clears the text field. Logs the result of the operation in the report.
	 */
	public void clear() {
		try {
			Keyboard.clear();
			reportLogger.info(config.getPassMarkUp("clear successful."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("clear failed."));
			eventStatus = false;
		}
	}

	/**
	 * Simulates keyboard press to type the given text.
	 *
	 * @param text The text to be typed.
	 */
	public void keyboardType(String text) {
		try {
			Keyboard.type(text);
			reportLogger.info(config.getPassMarkUp("keyboardType " + text + " successful."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("keyboardType " + text + " failed."));
			eventStatus = false;
		}
	}

	/**
	 * Pastes the given text.
	 *
	 * @param text The text to be pasted.
	 */
	public void paste(String text) {
		try {
			Keyboard.paste(text);
			reportLogger.info(config.getPassMarkUp("paste " + text + " successful."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("paste " + text + " failed."));
			eventStatus = false;
		}
	}

	/**
	 * Presses a single special character key (e.g., Control, Shift, Alt).
	 *
	 * @param key The special character key to be pressed.
	 */
	public void shortcut(String key) {
		try {
			Keyboard.specialKeyPress(key);
			reportLogger.info(config.getPassMarkUp("shortcut " + key + " press successful."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("shortcut " + key + " press failed."));
		}
	}

	/**
	 * Presses a combination of two special character keys (e.g., Control + A,
	 * Control + C).
	 *
	 * @param key1 The first special character key in the combination.
	 * @param key2 The second key in the combination.
	 */
	public void shortcut(String key1, String key2) {
		try {
			Keyboard.specialKeyPress(key1, key2);
			reportLogger.info(config.getPassMarkUp("shortcut " + key1 + " - " + key2 + " press successful."));
		} catch (Exception e) {
			reportLogger.info(config.getPassMarkUp("shortcut " + key1 + " - " + key2 + " press failed."));
		}
	}

	/**
	 * Presses a combination of three special character keys (e.g., Control + Shift
	 * + A).
	 *
	 * @param key1 The first special character key in the combination.
	 * @param key2 The second key in the combination.
	 * @param key3 The third key in the combination.
	 */
	public void shortcut(String key1, String key2, String key3) {
		try {
			Keyboard.specialKeyPress(key1, key2, key3);
			reportLogger.info(
					config.getPassMarkUp("shortcut " + key1 + " - " + key2 + " - " + key3 + " press successful."));
		} catch (Exception e) {
			reportLogger
					.info(config.getFailMarkUp("shortcut " + key1 + " - " + key2 + " - " + key3 + " press failed."));
		}
	}

	/**
	 * Scrolls down by the specified number of steps.
	 *
	 * @param steps The number of steps to scroll down.
	 */
	public void scrollDown(String steps) {
		try {
			int step = Integer.parseInt(steps);
			Mouse.scrollDown(step);
			reportLogger.info(config.getPassMarkUp("scrollDown " + steps + " successful."));
		} catch (NumberFormatException e) {
			reportLogger.info(config.getFailMarkUp("Invalid number of steps provided for scrollDown."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("scrollDown " + steps + " failed."));
		}
	}

	/**
	 * Scrolls up by the specified number of steps.
	 *
	 * @param steps The number of steps to scroll up.
	 */
	public void scrollUp(String steps) {
		try {
			int step = Integer.parseInt(steps);
			Mouse.scrollUp(step);
			reportLogger.info(config.getPassMarkUp("scrollUp " + steps + " successful."));
		} catch (NumberFormatException e) {
			reportLogger.info(config.getFailMarkUp("Invalid number of steps provided for scrollUp."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("scrollUp " + steps + " failed."));
		}
	}

	/**
	 * Launches the application specified by the given application path.
	 *
	 * @param applicationPath The path of the application to be launched.
	 */
	public void launchApplication(String applicationPath) {
		try {
			Application.launchApplication(applicationPath);
			reportLogger.info(config.getPassMarkUp(applicationPath + " Launched successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(applicationPath + " Launch failed."));
			eventStatus = false;
//          throw new RuntimeException(e); // Optionally, re-throw the exception if required.
		}
	}

	/**
	 * Closes the running application specified by the given application path.
	 *
	 * @param applicationPath The path of the application to be closed.
	 */
	public void closeApplication(String applicationPath) {
		try {
			Application.closeApplication(applicationPath);
			reportLogger.info(config.getPassMarkUp(applicationPath + " Closed successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(applicationPath + " Close failed."));
			eventStatus = false;
//          throw new RuntimeException(e); // Optionally, re-throw the exception if required.
		}
	}

	/**
	 * Maximizes the window with the specified window title.
	 *
	 * @param windowTitle The title of the window to be maximized.
	 */
	public void maximizeWindow(String windowTitle) {
		try {
			elementFinder.maximizeWindow(windowTitle, Settings.FIND_WAIT);
			reportLogger.info(config.getPassMarkUp(windowTitle + " maximized successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(windowTitle + " maximize failed."));
			eventStatus = false;
		}
	}

	/**
	 * Maximizes the pane with the specified pane title.
	 *
	 * @param paneTitle The title of the pane to be maximized.
	 */
	public void maximizePane(String paneTitle) {
		try {
			elementFinder.maximizePane(paneTitle, Settings.FIND_WAIT);
			reportLogger.info(config.getPassMarkUp(paneTitle + " maximized successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(paneTitle + " maximize failed."));
			eventStatus = false;
		}
	}

	/**
	 * Closes the window with the specified window title.
	 *
	 * @param windowTitle The title of the window to be closed.
	 */
	public void closeWindow(String windowTitle) {
		try {
			elementFinder.closeWindow(windowTitle, Settings.FIND_WAIT);
			reportLogger.info(config.getPassMarkUp(windowTitle + " window closed successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(windowTitle + " window close failed."));
			eventStatus = false;
		}
	}

	/**
	 * Closes the pane with the specified pane title.
	 *
	 * @param paneTitle The title of the pane to be closed.
	 */
	public void closePane(String paneTitle) {
		try {
			elementFinder.closePane(paneTitle, Settings.FIND_WAIT);
			reportLogger.info(config.getPassMarkUp(paneTitle + " pane closed successfully."));
			eventStatus = true;
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(paneTitle + " pane close failed."));
			eventStatus = false;
		}
	}

	/**
	 * Sets the root search behavior. If 'true', it searches the full system; if
	 * 'false', it searches in the given window.
	 *
	 * @param value 'true' to search the full system, 'false' to search in the given
	 *              window.
	 */
	public void setRootSearch(String value) {
		try {
			driver.setRootSearch(Boolean.parseBoolean(value));
			reportLogger.info(config.getPassMarkUp("setRootSearch set to " + value + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("setRootSearch set to " + value + " failed"));
		}
	}

	/**
	 * Focuses on the window with the specified window title.
	 *
	 * @param windowTitle The title of the window to be focused.
	 */
	public void focusWindow(String windowTitle) {
		try {
			Element element = elementFinder.getWindow(windowTitle, Settings.FIND_WAIT);
			if (element == null) {
				waitToDisplay("NAME", "WINDOW", windowTitle, "5");
				element = elementFinder.getWindow(windowTitle, Settings.FIND_WAIT);
			}
			Driver.searchContext = element;
			Driver.searchContext.setFocus();
			new UIElement(element).highlight(1);
			eventStatus = true;
			reportLogger.info(config.getPassMarkUp("focusWindow " + windowTitle + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("focusWindow " + windowTitle + " failed"));
			eventStatus = false;
			throw new RuntimeException(e);
		}
	}

	/**
	 * Focuses on the pane with the specified pane title.
	 *
	 * @param paneTitle The title of the pane to be focused.
	 */
	public void focusPane(String paneTitle) {
		try {
			Element element = elementFinder.getPane(paneTitle, Settings.FIND_WAIT);
			Driver.searchContext = element;
			element.setFocus();
			new UIElement(element).highlight(1);
			eventStatus = true;
			reportLogger.info(config.getPassMarkUp("focusPane " + paneTitle + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("focusPane " + paneTitle + " failed"));
			eventStatus = false;
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens the given URL in a new tab of the web browser.
	 *
	 * @param url The URL to be opened.
	 */
	public void openURL(String url) {
		try {
			// Press CONTROL + T to open a new tab.
			shortcut("CONTROL", "T");
			// Paste the URL into the address bar.
			SikuliElement.screen.paste(url);
			// Press ENTER to navigate to the URL.
			shortcut("ENTER");
			eventStatus = true;
			reportLogger.info(config.getPassMarkUp("openURL " + url + " successful"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("openURL " + url + " failed"));
			eventStatus = false;
		}
	}

	/**
	 * Pauses the execution for the given time period in milliseconds.
	 *
	 * @param milliSeconds The time to wait in milliseconds.
	 */
	public void waitTime(String milliSeconds) {
		try {
			int time = Integer.parseInt(milliSeconds);
			Timer.waitTime(time);
			reportLogger.info(config.getPassMarkUp("waitTime " + milliSeconds + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("waitTime " + milliSeconds + " failed"));
		}
	}

	/**
	 * Waits for the given element to be displayed on the screen for up to the
	 * maximum wait duration.
	 *
	 * @param locatorType The type of locator to identify the element (e.g., "id",
	 *                    "name", "text").
	 * @param parameter1  The first parameter for locating the element (e.g.,
	 *                    control type or image path).
	 * @param parameter2  The second parameter for locating the element (e.g.,
	 *                    control value or image path).
	 * @throws RuntimeException if the element fails to be displayed within the
	 *                          maximum wait duration.
	 */
	public void waitToDisplay(String locatorType, String parameter1, String parameter2) {
		boolean displayStatus = elementFinder.waitToDisplay(locatorType, parameter1, parameter2, Settings.MAX_WAIT);
		if (displayStatus) {
			reportLogger.info(config.getPassMarkUp(parameter2 + " successfully displayed."));
		} else {
			reportLogger.info(config.getFailMarkUp(parameter2 + " failed to display."));
			throw new RuntimeException(parameter2 + " failed to display.");
		}
	}

	/**
	 * Waits for the given element to be displayed on the screen for up to the
	 * specified duration.
	 *
	 * @param locatorType The type of locator to identify the element (e.g., "id",
	 *                    "name", "text").
	 * @param parameter1  The first parameter for locating the element (e.g.,
	 *                    control type or image path).
	 * @param parameter2  The second parameter for locating the element (e.g.,
	 *                    control value or image path).
	 * @param duration    The duration to wait in seconds.
	 * @throws RuntimeException if the element fails to be displayed within the
	 *                          specified duration.
	 */
	public void waitToDisplay(String locatorType, String parameter1, String parameter2, String duration) {
		boolean displayStatus = elementFinder.waitToDisplay(locatorType, parameter1, parameter2,
				Long.parseLong(duration));
		if (displayStatus) {
			reportLogger.info(config.getPassMarkUp(parameter2 + " successfully displayed."));
		} else {
			reportLogger.info(config.getFailMarkUp(parameter2 + " failed to display."));
			throw new RuntimeException(parameter2 + " failed to display.");
		}
	}

	/**
	 * Waits for the given element to vanish from the screen for up to the maximum
	 * wait duration.
	 *
	 * @param locatorType The type of locator to identify the element (e.g., "id",
	 *                    "name", "text").
	 * @param parameter1  The first parameter for locating the element (e.g.,
	 *                    control type or image path).
	 * @param parameter2  The second parameter for locating the element (e.g.,
	 *                    control value or image path).
	 * @throws RuntimeException if the element fails to vanish within the maximum
	 *                          wait duration.
	 */
	public void waitToVanish(String locatorType, String parameter1, String parameter2) {
		boolean vanishStatus = elementFinder.waitToVanish(locatorType, parameter1, parameter2, Settings.MAX_WAIT);
		if (vanishStatus) {
			reportLogger.info(config.getPassMarkUp(parameter2 + " successfully vanished."));
		} else {
			reportLogger.info(config.getFailMarkUp(parameter2 + " failed to vanish."));
			throw new RuntimeException(parameter2 + " failed to vanish.");
		}
	}

	/**
	 * Waits for the given element to vanish from the screen for up to the specified
	 * duration.
	 *
	 * @param locatorType The type of locator to identify the element (e.g., "id",
	 *                    "name", "text").
	 * @param parameter1  The first parameter for locating the element (e.g.,
	 *                    control type or image path).
	 * @param parameter2  The second parameter for locating the element (e.g.,
	 *                    control value or image path).
	 * @param duration    The duration to wait in seconds.
	 * @throws RuntimeException if the element fails to vanish within the specified
	 *                          duration.
	 */
	public void waitToVanish(String locatorType, String parameter1, String parameter2, String duration) {
		boolean vanishStatus = elementFinder.waitToVanish(locatorType, parameter1, parameter2,
				Long.parseLong(duration));
		if (vanishStatus) {
			reportLogger.info(config.getPassMarkUp(parameter2 + " successfully vanished."));
		} else {
			reportLogger.info(config.getFailMarkUp(parameter2 + " failed to vanish."));
			throw new RuntimeException(parameter2 + " failed to vanish.");
		}
	}

	/**
	 * Waits for the specified element to become enabled within the given duration
	 * time.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "text").
	 * @param controlType The type of control used to identify the element (e.g.,
	 *                    "button", "textbox").
	 * @param value       The value of the locator used to find the element (e.g.,
	 *                    the ID, name, or XPath expression).
	 * @param duration    The maximum time (in seconds) to wait for the element to
	 *                    become enabled.
	 * @throws RuntimeException If the element fails to become enabled within the
	 *                          specified duration.
	 */
	public void waitToEnable(String locatorType, String controlType, String value, String duration) {
		boolean enableStatus = elementFinder.waitToEnable(locatorType, controlType, value, Long.parseLong(duration));
		if (enableStatus)
			reportLogger.info(config.getPassMarkUp(value + " successfully Enabled."));
		else {
			reportLogger.info(config.getFailMarkUp(value + " failed to Enable."));
			throw new RuntimeException(value + " failed to Enable.");
		}
	}

	/**
	 * Waits for the specified element to become enabled within the default maximum
	 * duration time.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "text").
	 * @param controlType The type of control used to identify the element (e.g.,
	 *                    "button", "textbox").
	 * @param value       The value of the locator used to find the element (e.g.,
	 *                    the ID, name, or XPath expression).
	 * @throws RuntimeException If the element fails to become enabled within the
	 *                          default maximum duration time.
	 */
	public void waitToEnable(String locatorType, String controlType, String value) {
		boolean enableStatus = elementFinder.waitToEnable(locatorType, controlType, value, Settings.MAX_WAIT);
		if (enableStatus)
			reportLogger.info(config.getPassMarkUp(value + " successfully Enabled."));
		else {
			reportLogger.info(config.getFailMarkUp(value + " failed to Enable."));
			throw new RuntimeException(value + " failed to Enable.");
		}
	}

	/**
	 * Clicks on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value. element.
	 */
	public void click(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getElement(locatorType, parameter1, parameter2, Settings.FIND_WAIT).click();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " click successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " click failed."));
			eventStatus = false;
		}
	}

	/**
	 * Clicks at the center of the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void clickCenter(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getElement(locatorType, parameter1, parameter2, Settings.FIND_WAIT).clickCenter();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " clickCenter successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " clickCenter failed."));
			eventStatus = false;
		}
	}

	/**
	 * Performs a right-click on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void rightClick(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getElement(locatorType, parameter1, parameter2, Settings.FIND_WAIT).rightClick();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " right click successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " right click failed."));
			eventStatus = false;
		}
	}

	/**
	 * Performs a double-click on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void doubleClick(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getElement(locatorType, parameter1, parameter2, Settings.FIND_WAIT).doubleClick();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " double click successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " double click failed."));
			eventStatus = false;
		}
	}

	/**
	 * Hovers over the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void hover(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getElement(locatorType, parameter1, parameter2, Settings.FIND_WAIT).hover();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " hover successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " hover failed."));
			eventStatus = false;
		}
	}

	/**
	 * Writes the given text on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 * @param text        The text to be written on the element.
	 */
	public void write(String locatorType, String parameter1, String parameter2, String text) {
		try {
			click(locatorType, parameter1, parameter2);
			Timer.waitTime(200);
			Keyboard.clear();
			keyboardType(text);
			reportLogger
					.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " write " + text + " successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " write " + text + " failed."));
			eventStatus = false;
		}
	}

	/**
	 * Selects the given check box on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void check(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getUIElement(locatorType, "CHECKBOX", parameter2, Settings.FIND_WAIT).check();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " checked successfully."));
			eventStatus = true;
		} catch (FindFailed | AutomationException | NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " check failed."));
			eventStatus = false;
		}
	}

	/**
	 * Deselects the given check box on the specified element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void unCheck(String locatorType, String parameter1, String parameter2) {
		try {
			elementFinder.getUIElement(locatorType, "CHECKBOX", parameter2, Settings.FIND_WAIT).unCheck();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " unchecked successfully."));
			eventStatus = true;
		} catch (FindFailed | AutomationException | NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " uncheck failed."));
			eventStatus = false;
		}
	}

	/**
	 * Drags the given element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void drag(String locatorType, String parameter1, String parameter2) {
		try {
			SikuliElement element = elementFinder.getElement(locatorType, parameter1, parameter2, 2);
			element.drag();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " dragged successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " drag failed."));
			eventStatus = false;
		}
	}

	/**
	 * Drops the given element.
	 *
	 * @param locatorType The type of locator used to identify the element (e.g.,
	 *                    "id", "name", "xpath").
	 * @param parameter1  The first parameter based on the locator type, which can
	 *                    be the control type, search area image, or X-coordinate
	 *                    value.
	 * @param parameter2  The second parameter based on the locator type, which can
	 *                    be the control value, search element image, or
	 *                    Y-coordinate value.
	 */
	public void drop(String locatorType, String parameter1, String parameter2) {
		try {
			SikuliElement element = elementFinder.getElement(locatorType, parameter1, parameter2, 2);
			element.dropAt();
			reportLogger.info(config.getPassMarkUp(parameter1 + " " + parameter2 + " drop successfully."));
			eventStatus = true;
		} catch (NullPointerException e) {
			reportLogger.info(config.getFailMarkUp(parameter1 + " " + parameter2 + " drop failed."));
			eventStatus = false;
		}
	}

	/**
	 * Starts the Remote Capture (RC) by launching the specified application.
	 *
	 * @param applicationPath The path to the application executable or file to be
	 *                        captured by Remote Capture. The applicationPath should
	 *                        point to the executable or file associated with the
	 *                        application that you want to capture remotely. Note:
	 *                        The behavior of this method depends on the
	 *                        implementation of the 'remoteClient' and its
	 *                        compatibility with the application type specified by
	 *                        'applicationPath'.
	 * @throws SomeException (e.g., RemoteCaptureException) if there is an error
	 *                       while starting the Remote Capture.
	 */
	public void startRC(String applicationPath) {
		try {
			remoteClient.startRecording(applicationPath);
			reportLogger.info(config.getPassMarkUp("RC started successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("RC start failed"));
		}
	}

	/**
	 * Stops the Remote Capture (RC) that was previously started.
	 *
	 * @throws SomeException (e.g., RemoteCaptureException) if there is an error
	 *                       while stopping the Remote Capture. This may happen if
	 *                       the Remote Capture was not successfully started or if
	 *                       there are connectivity issues with the remote capturing
	 *                       system.
	 */
	public void stopRC() {
		try {
			remoteClient.stopRecording();
			reportLogger.info(config.getPassMarkUp("RC stopped successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("RC stop failed"));
		}
	}

	/**
	 * Updates Epiplex settings for automation with the specified status.
	 *
	 * @param status The status to be set for automation. Should be represented as a
	 *               boolean value (e.g., "true" or "false").
	 * @throws SomeException (e.g., XMLParsingException) if there is an error while
	 *                       updating the Epiplex capture settings. The exception
	 *                       may occur due to issues with XML parsing or updating
	 *                       the settings file.
	 */
	public void updateSetting(String status) {
		try {
			new XML_Library().updateCaptureSettings(Boolean.parseBoolean(status));
			reportLogger.info(config.getPassMarkUp("updateSetting " + status + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("updateSetting " + status + " failed"));
		}
	}

	/**
	 * Updates Epiplex capture settings for automation with the specified setting.
	 *
	 * @param setting The setting to be updated. The valid options are "SCREEN
	 *                VIDEO", "CAPTURE AUDIO", or "AUDIO VIDEO". Use one of these
	 *                values to enable or disable the corresponding capture feature
	 *                in Epiplex.
	 * @throws SomeException (e.g., XMLParsingException) if there is an error while
	 *                       updating the Epiplex capture settings. The exception
	 *                       may occur due to issues with XML parsing or updating
	 *                       the settings file.
	 */
	public void updateCaptureSetting(String setting) {
		try {
			new XML_Library().updateCaptureSetting(setting);
			reportLogger.info(config.getPassMarkUp("updateCaptureSetting " + setting + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("updateCaptureSetting " + setting + " failed"));
		}
	}

	/**
	 * Asserts that a node with the specified tag name and attribute value exists in
	 * the XML file located at the given document path.
	 *
	 * @param documentPath The path to the XML document file (e.g., REPOPATH or
	 *                     PROGRAMDATA) in which to search for the node.
	 * @param tagName      The name of the XML tag representing the node to be
	 *                     searched for.
	 * @param attribute    The attribute type to be used for the search. It can be
	 *                     either "TEXT" or an attribute name.
	 * @param value        The attribute value to be matched in the node.
	 * @throws SomeException (e.g., XMLParsingException) if there is an error while
	 *                       parsing the XML file or if the node is not found. The
	 *                       exception may occur due to issues with XML parsing or
	 *                       when the node with the specified tag and attribute
	 *                       value is not found in the XML file.
	 */
	public void assertNodeExist(String documentPath, String tagName, String attribute, String value) {
		try {
			boolean actualStatus = new XML_Library().getAttribute(documentPath, tagName, attribute, value);
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp("assertNodeExist completed."));
		} catch (Exception e) {
			reportLogger.fail(config.getFailMarkUp("assertNodeExist failed"));
		}
	}

	/**
	 * Asserts that a node with the specified tag name and attribute value does not
	 * exist in the XML file located at the given document path.
	 *
	 * @param documentPath The path to the XML document file (e.g., REPOPATH or
	 *                     PROGRAMDATA) in which to search for the node.
	 * @param tagName      The name of the XML tag representing the node to be
	 *                     searched for.
	 * @param attribute    The attribute type to be used for the search. It can be
	 *                     either "TEXT" or an attribute name.
	 * @param value        The attribute value to be matched in the node.
	 * @throws SomeException (e.g., XMLParsingException) if there is an error while
	 *                       parsing the XML file or if the node is found. The
	 *                       exception may occur due to issues with XML parsing or
	 *                       when the node with the specified tag and attribute
	 *                       value is found in the XML file.
	 */
	public void assertNodeNotExist(String documentPath, String tagName, String attribute, String value) {
		try {
			boolean actualStatus = !new XML_Library().getAttribute(documentPath, tagName, attribute, value);
			logResult(actualStatus);
			reportLogger.info(config.getPassMarkUp("assertNodeNotExist completed."));
		} catch (Exception e) {
			reportLogger.fail(config.getFailMarkUp("assertNodeNotExist failed"));
		}
	}

	/**
	 * Updates the Window mode settings for automation to the specified mode.
	 *
	 * @param mode The mode to be set for automation. Valid options are: "DESKTOP",
	 *             "ACTIVE WINDOW", "FULL SCREEN", "DOCUMENT AREA", or "CUSTOM
	 *             SIZE".
	 * @throws SomeException (e.g., XMLParsingException) if there is an error while
	 *                       updating the Window mode settings. The exception may
	 *                       occur due to issues with XML parsing or updating the
	 *                       settings file.
	 */
	public void selectWindow(String mode) {
		try {
			new XML_Library().selectWindow(mode);
			reportLogger.info(config.getPassMarkUp("selectWindow " + mode + " successfully"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("selectWindow " + mode + " failed"));
		}
	}

	/**
	 * Starts the capture process with the specified capture file name.
	 *
	 * @param captureFileName The name of the capture file to be created for the
	 *                        recording.
	 * @throws RuntimeException if there is an error while starting the capture
	 *                          process. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur during
	 *                          the capture process.
	 */
	public void startCapture(String captureFileName) {
		try {
			Capture.startCapture(captureFileName);
			reportLogger.info(config.getPassMarkUp(captureFileName + " startCapture started..."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(captureFileName + " startCapture failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts the manual capture process with the specified capture file name.
	 *
	 * @param captureFileName The name of the capture file to be created for the
	 *                        manual recording.
	 * @throws RuntimeException if there is an error while starting the manual
	 *                          capture process. This exception is thrown as a
	 *                          runtime exception to propagate any errors that occur
	 *                          during the manual capture process.
	 */
	public void startCaptureManual(String captureFileName) {
		try {
			Capture.startCaptureManual(captureFileName);
			reportLogger.info(config.getPassMarkUp(captureFileName + " startCapture started..."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(captureFileName + " startCapture failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts the performance capture process with the default capture settings.
	 *
	 * @param captureFileName The name of the capture file to be created for the
	 *                        performance recording.
	 * @throws RuntimeException if there is an error while starting the performance
	 *                          capture process. This exception is thrown as a
	 *                          runtime exception to propagate any errors that occur
	 *                          during the performance capture process.
	 */
	public void startCapturePerformance(String captureFileName) {
		try {
			PerformanceUtils.startCapture(captureFileName);
			reportLogger.info(config.getPassMarkUp(captureFileName + " startCapturePerformance started..."));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp(captureFileName + " startCapturePerformance failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a note during the capture process.
	 *
	 * @throws RuntimeException if there is an error while adding the note. This
	 *                          exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while adding the
	 *                          note.
	 */
	public void addNote() {
		try {
			Capture.addNote();
			reportLogger.info(config.getPassMarkUp("add note completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("add note failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds validations during the capture process.
	 *
	 * @throws RuntimeException if there is an error while adding the validations.
	 *                          This exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while adding the
	 *                          validations.
	 */
	public void addValidations() {
		try {
			Capture.addValidations();
			reportLogger.info(config.getPassMarkUp("add Validations completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("add Validations failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an exception during the capture process.
	 *
	 * @throws RuntimeException if there is an error while adding the exception.
	 *                          This exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while adding the
	 *                          exception.
	 */
	public void addException() {
		try {
			Capture.addException();
			reportLogger.info(config.getPassMarkUp("add Exception completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("add Exception failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops the recovery mechanism for exceptions during the capture process.
	 *
	 * @throws RuntimeException if there is an error while stopping the exception
	 *                          recovery. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          stopping the exception recovery.
	 */
	public void exceptionRecoveryStop() {
		try {
			Capture.exceptionRecoveryStop();
			reportLogger.info(config.getPassMarkUp("Exception recovery stop completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("Exception recovery stop failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds input data during the capture process.
	 *
	 * @throws RuntimeException if there is an error while adding the input data.
	 *                          This exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while adding the
	 *                          input data.
	 */
	public void addInputData() {
		try {
			Capture.addInputData();
			reportLogger.info(config.getPassMarkUp("addInputData completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("addInputData got error"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a narration to the capture process.
	 *
	 * @throws RuntimeException if there is an error while adding the narration.
	 *                          This exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while adding the
	 *                          narration.
	 */
	public void addNarration() {
		try {
			Capture.addNarration();
			reportLogger.info(config.getPassMarkUp("add Narration completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("add Narration failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts recording the screen video during the capture process.
	 *
	 * @throws RuntimeException if there is an error while starting the screen video
	 *                          recording. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          starting the screen video recording.
	 */
	public void addScreenVideo() {
		try {
			Capture.addScreenVideo();
			reportLogger.info(config.getPassMarkUp("add Screen Video completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("add Screen Video failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops recording the screen video during the capture process.
	 *
	 * @throws RuntimeException if there is an error while stopping the screen video
	 *                          recording. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          stopping the screen video recording.
	 */
	public void stopScreenVideo() {
		try {
			Capture.stopScreenVideo();
			reportLogger.info(config.getPassMarkUp("stop Screen Video completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("stop Screen Video failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts recording audio during the capture process.
	 *
	 * @throws RuntimeException if there is an error while starting the audio
	 *                          recording. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          starting the audio recording.
	 */
	public void addAudio() {
		try {
			Capture.addAudio();
			reportLogger.info(config.getPassMarkUp("Add audio completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("Add audio failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops recording audio during the capture process.
	 *
	 * @throws RuntimeException if there is an error while stopping the audio
	 *                          recording. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          stopping the audio recording.
	 */
	public void stopAudio() {
		try {
			Capture.stopAudio();
			reportLogger.info(config.getPassMarkUp("stop audio completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("stop audio failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves the capture file during the capture process.
	 *
	 * @throws RuntimeException if there is an error while saving the capture file.
	 *                          This exception is thrown as a runtime exception to
	 *                          propagate any errors that occur while saving the
	 *                          capture file.
	 */
	public void saveCapture() {
		try {
			Capture.saveCapture();
			reportLogger.info(config.getPassMarkUp("saveCapture save completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("saveCapture save failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves the performance capture file.
	 *
	 * @throws RuntimeException if there is an error while saving the performance
	 *                          capture file. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          saving the performance capture file.
	 */
	public void saveCapturePerformance() {
		try {
			PerformanceUtils.saveCapture();
			reportLogger.info(config.getPassMarkUp("saveCapturePerformance save completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("saveCapturePerformance save failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Cancels the ongoing capture process.
	 *
	 * @throws RuntimeException if there is an error while canceling the capture
	 *                          process. This exception is thrown as a runtime
	 *                          exception to propagate any errors that occur while
	 *                          canceling the capture process.
	 */
	public void cancelCapture() {
		try {
			Capture.cancelCapture();
			reportLogger.info(config.getPassMarkUp("Cancel Capture completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("Cancel Capture failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Cancels the ongoing performance capture process.
	 *
	 * @throws RuntimeException if there is an error while canceling the performance
	 *                          capture process. This exception is thrown as a
	 *                          runtime exception to propagate any errors that occur
	 *                          while canceling the performance capture process.
	 */
	public void cancelCapturePerformance() {
		try {
			PerformanceUtils.cancelCapture();
			reportLogger.info(config.getPassMarkUp("cancelCapturePerformance completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("cancelCapturePerformance failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs a performance test on document generation using the specified
	 * capture file.
	 *
	 * @param captureFileName The name of the capture file to be used for the
	 *                        performance test.
	 * @throws RuntimeException if there is an error while performing the
	 *                          performance test on document generation. This
	 *                          exception is thrown as a runtime exception to
	 *                          propagate any errors that occur during the
	 *                          performance test.
	 */
	public void performanceTestDoc(String captureFileName) {
		PerformanceUtils performance = new PerformanceUtils();
		try {
			performance.performanceTestDoc(captureFileName);
			reportLogger.info(config.getPassMarkUp("performanceTestDoc completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("performanceTestDoc failed"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Selects the display file type in the home page.
	 *
	 * @param fileType The file type to be selected for display. (e.g., "PDF",
	 *                 "DOCX", "PPTX", "XLS", "XLSX", etc.)
	 * @throws EventFailException if there is an error while selecting the display
	 *                            file type. This exception is thrown to handle any
	 *                            specific event failure that may occur during the
	 *                            display file type selection.
	 */
	public void displayFileType(String fileType) {
		try {
			focusWindow("Epiplex500");
			Timer.waitTime(3000);
			click("Image", "Screen", "ToolBar");
			click("name", "MENUITEM", "Display File Type");
			click("name", "MENUITEM", fileType);
			reportLogger.info(config.getPassMarkUp("displayFileType - " + fileType + " - " + " completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("displayFileType - " + fileType + " - " + " failed"));
		}
	}

	/**
	 * Generates a document of the specified file type with the given name and saves
	 * it to the specified file path.
	 *
	 * @param fileType The type of document to be generated. (e.g., "PDF", "DOCX",
	 *                 "PPTX", "XLS", "XLSX", etc.)
	 * @param fileName The name of the generated document file.
	 * @param filePath The path where the generated document file should be saved.
	 */
	public void generateDocument(String fileType, String fileName, String filePath) {
		try {
			startTest("Generate " + fileName);
			deleteFile(fileName, filePath);
			setRootSearch("false");
			focusWindow("Developer Editor - ");
			click("name", "MENUITEM", "File");
			click("name", "MENUITEM", "Generate Document...");
			focusWindow("Generate Document");
			click("id", "COMBOBOX", "8");
			click("name", "LISTITEM", fileType);

			if (fileName.endsWith("docx"))
				check("name", "CHECKBOX", "Save in .docx format");

			else if (fileName.endsWith("doc"))
				unCheck("name", "CHECKBOX", "Save in .docx format");

			else if (fileName.endsWith("pptx"))
				check("name", "CHECKBOX", "Save in .pptx format");

			else if (fileName.endsWith("ppt"))
				unCheck("name", "CHECKBOX", "Save in .pptx format");

			else if (fileName.endsWith("xls")) {
				unCheck("name", "CHECKBOX", "Use HTML Template");
				unCheck("name", "CHECKBOX", "Save in .xlsx format");
			} else if (fileName.endsWith("xlsx")) {
				unCheck("name", "CHECKBOX", "Use HTML Template");
				check("name", "CHECKBOX", "Save in .xlsx format");
			} else if (fileName.endsWith("pdf"))
				check("name", "CHECKBOX", "Use MS Word Template");

			unCheck("name", "CHECKBOX", "Launch the document after successful generation");
			click("name", "BUTTON", "Generate");
			focusWindow("Developer Editor");
			waitToDisplay("name", "Text", "Do you want to launch the generated document?");
			click("name", "BUTTON", "No");
			waitTime("500");
			assertFileExists(fileName, filePath);
			reportLogger.info(config.getPassMarkUp("GenerateDocument - " + fileType + " - " + fileName + " completed"));
		} catch (Exception e) {
			reportLogger.info(config.getFailMarkUp("GenerateDocument - " + fileType + " - " + fileName + " failed"));
		}
	}
}
