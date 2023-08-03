package epiplex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import actions.ActionMethods;
import actions.ElementFinder;
import core.Application;
import core.Driver;
import utils.Settings;
import utils.Timer;
import utils.XML_Library;

/**
 * The PerformanceUtils class provides utility methods to handle
 * performance-related actions in the Epiplex500 application. It contains
 * methods for starting a capture, clicking elements and waiting for specific
 * elements to be displayed.
 */

public class PerformanceUtils {

	private static final Logger log = LogManager.getLogger(PerformanceUtils.class);
	private static final Driver driver = new Driver();
	private static final ElementFinder finder = new ElementFinder(driver);
	private static final ActionMethods action = new ActionMethods(driver);
	private static final String epiplexPath = "Epiplex";

	private static final long WAIT_TIME = Settings.FIND_WAIT;
	private static final long MAX_WAIT = Settings.MAX_WAIT;

	/**
	 * Clicks an element and waits for another element to be displayed.
	 * 
	 * @param elementPartialName The partial name of the element to be clicked.
	 * @param elementType        The type of the element to be clicked.
	 * @param elementName        The name of the element to be clicked.
	 * @param waitTime           The maximum time to wait for the second element to
	 *                           be displayed.
	 */
	private static void clickAndWait(String elementPartialName, String elementType, String elementName, long waitTime) {
		boolean status = finder.waitToDisplay("partialName", elementType, elementPartialName, waitTime);
		if (status) {
			finder.getElement("partialName", elementType, elementPartialName, waitTime).clickCenter();
			Timer.waitTime(1000);
			finder.getElement("name", "MENUITEM", elementName, waitTime).clickCenter();
		}
	}

	/**
	 * Starts the capture process in the Epiplex500 application with the specified
	 * file name.
	 *
	 * @param fileName The name of the capture file to be created.
	 * @throws RuntimeException If an error occurs during the capture process.
	 */
	public static void startCapture(String fileName) {
		Application.closeApplication(epiplexPath);
		new XML_Library().updateCaptureSettings(false);
		log.info(fileName + " Capture - initializing...");

		try {
			Timer.startTimer();
			Application.launchApplication(epiplexPath);
			action.focusWindow("Epiplex500");
			Timer.stopTimer("Launch Epiplex500");

			// Enter the file name and start the capture
			finder.getElement("name", "EDIT", "Enter a new file name or select from the list", WAIT_TIME)
					.write(fileName);
			finder.getElement("name", "BUTTON", "Start Capture", WAIT_TIME).click();

			// Handle file overwrite and data security confirmation
			boolean fileOverWritePopup = finder.waitToDisplay("name", "Text",
					"File already exists. Do you want to overwrite?", 3);
			if (fileOverWritePopup) {
				finder.getElement("name", "BUTTON", "Yes", WAIT_TIME).click();
			} else {
				boolean pending = finder.waitToDisplay("name", "Text",
						"File Name specified is pending to recover and save. Do you want to overwrite the file?", 3);
				if (pending) {
					finder.getElement("name", "BUTTON", "Yes", WAIT_TIME).click();
				}
			}

			boolean security = finder.waitToDisplay("partialName", "Text", "Data Security Confirmation...", 3);
			if (security) {
				finder.getElement("name", "BUTTON", "Yes", WAIT_TIME).click();
			}

			// Close the popup if it appears
			boolean popUp = finder.waitToDisplay("name", "Window", "Epiplex500 Capture", WAIT_TIME);
			if (popUp) {
				finder.getElement("name", "BUTTON", "OK", WAIT_TIME).click();
			}

			// Wait for the capture to start
			Timer.startTimer();
			finder.waitToDisplay("partialName", "Button", "Capture in progress", WAIT_TIME);
			Timer.stopTimer("Message to System Tray");

		} catch (Exception e) {
			String errorMessage = fileName + " Capture - error. " + e.getMessage();
			log.error(errorMessage);
			throw new RuntimeException(errorMessage, e);
		}

		log.info(fileName + " Capture - started...");
	}

	/**
	 * Cancels the ongoing capture process in the Epiplex500 application, if it is
	 * in progress.
	 *
	 * <p>
	 * This method cancels the ongoing capture by clicking the "Cancel" button on
	 * the capture progress screen and confirming the cancellation. If the capture
	 * process is not in progress, no action is taken.
	 *
	 * @throws RuntimeException If an error occurs while canceling the capture
	 *                          process.
	 */
	public static void cancelCapture() {
		log.info("Capture cancel initializing...");

		clickAndWait("Capture in progress", "Button", "Cancel", WAIT_TIME);
		Timer.startTimer();

		try {
			// Click the "Yes" button to confirm the cancellation
			finder.getElement("name", "BUTTON", "Yes", WAIT_TIME).clickCenter();

			// Wait for the Epiplex500 window to reappear and focus on it
			finder.waitToDisplay("name", "window", "Epiplex500", WAIT_TIME);
			action.focusWindow("Epiplex500");

			Timer.stopTimer("Cancel Capture");
		} catch (Exception e) {
			log.error("Error while canceling capture. " + e.getMessage());
			throw new RuntimeException(e);
		}

		log.info("Capture cancel completed.");
	}

	/**
	 * Saves the ongoing capture in the Epiplex500 application, if it is in
	 * progress.
	 *
	 * <p>
	 * This method saves the ongoing capture by clicking the "Save" button on the
	 * capture progress screen, handling any properties window that may appear, and
	 * waiting for the capture file to be saved. After the capture file is saved, it
	 * closes any pop-up windows that may appear and closes the Epiplex500
	 * application.
	 *
	 * @throws RuntimeException If an error occurs while saving the capture.
	 */
	public static void saveCapture() {
		log.info("Capture save initializing...");

		// Click the "Save" button on the capture progress screen
		clickAndWait("Capture in progress", "Button", "Save", WAIT_TIME);

		// Check if the "Properties" pop-up window is displayed and click the "Save"
		// button if it is
		boolean popUp = finder.waitToDisplay("name", "Window", "Properties", WAIT_TIME);
		if (popUp) {
			finder.getElement("name", "BUTTON", "Save", WAIT_TIME).click();
		}

		// Wait for the capture file to be saved
		Timer.startTimer();
		finder.waitToDisplay("name", "Window", "Saving capture file", WAIT_TIME);
		while (finder.waitToDisplay("name", "Window", "Saving capture file", 3)) {
		}
		Timer.stopTimer("Save Capture");

		try {
			// Close any pop-up windows that may appear
			finder.getElement("name", "BUTTON", "OK", WAIT_TIME).click();
			finder.getElement("name", "BUTTON", "No", WAIT_TIME).click();

			// Close the Epiplex500 application
			Application.closeApplication(epiplexPath);
		} catch (Exception e) {
			log.error("Error while saving capture. " + e.getMessage());
			throw new RuntimeException(e);
		}

		log.info("Capture save completed.");
	}

	/**
	 * Finalizes the capture file with the given name from the Epiplex500
	 * application's home screen.
	 *
	 * <p>
	 * This method launches the Epiplex500 application, sets root search to true,
	 * and focuses on the Epiplex500 window. It then performs the necessary steps to
	 * finalize the capture file specified by the given {@code fileName}. The method
	 * searches for the capture file using the search functionality, right-clicks on
	 * the file, and selects the "Finalize" option. After finalizing the capture, it
	 * waits for the "Finalize Settings" window to be displayed and clicks the "OK"
	 * button. If prompted, it clicks the "Yes" button to confirm the action. After
	 * the capture file is finalized, it waits for the Developer Editor window to
	 * open and focuses on it.
	 *
	 * @param fileName The name of the capture file to be finalized.
	 *
	 * @throws RuntimeException If an error occurs while finalizing the capture file
	 *                          from the home screen.
	 */
	public void finalizeFromHome(String fileName) {
		log.info("Finalize capture file " + fileName + " from home initializing...");

		try {
			// Launch the Epiplex500 application
			Application.launchApplication(epiplexPath);
			driver.setRootSearch(true);
			finder.getUIElement("name", "Window", "Epiplex500", WAIT_TIME).setFocus();

			// Search for the capture file using the search functionality
			finder.getUIElement("id", "PANE", "picBxRefresh", WAIT_TIME).click();
			finder.getUIElement("id", "Edit", "txtBxSearch", WAIT_TIME).click();
			finder.getUIElement("id", "Edit", "txtBxSearch", WAIT_TIME).write(fileName);
			action.shortcut("Enter");

			// Right-click on the capture file and select "Finalize"
			finder.getUIElement("name", "TEXT", fileName, WAIT_TIME).rightClick();
			finder.getUIElement("name", "MENUITEM", "Finalize", WAIT_TIME).click();

			// Wait for the "Finalize Settings" window to be displayed and click the "OK"
			// button
			finder.waitToDisplay("name", "Window", "Finalize Settings", WAIT_TIME);
			finder.getUIElement("name", "BUTTON", "OK", WAIT_TIME).click();

			// Check if prompted to confirm and click the "Yes" button if necessary
			boolean status = finder.waitToDisplay("name", "Button", "Yes", 1);
			if (status) {
				finder.getUIElement("name", "Button", "Yes", WAIT_TIME).click();
			}

			Timer.startTimer();
			// Wait for the Developer Editor window to open and focus on it
			finder.waitToEnable("id", "Pane", "picBxDelete", MAX_WAIT);
			Timer.stopTimer("Finalize from Home");
			Timer.waitTime(2000);
			action.focusWindow("Epiplex500");
			Timer.waitTime(2000);

			// Wait for Developer Editor to open
			boolean statusWin = finder.waitToDisplay("partialName", "Window", "Developer Editor - [", 2);
			while (!statusWin) {
				statusWin = finder.waitToDisplay("partialName", "Window", "Developer Editor - [", 2);
			}
			action.focusWindow("Developer Editor - [");
			driver.setRootSearch(true);

			log.info("Finalize capture file " + fileName + " from home completed");
		} catch (Exception e) {
			log.error("Error while finalizing capture from home. " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generates a document in the Developer Editor and logs data during the
	 * process.
	 *
	 * <p>
	 * This method generates a document in the Developer Editor with the given
	 * {@code fileName} and {@code testCaseName}. It focuses on the Developer Editor
	 * window and performs the following steps:
	 * <ol>
	 * <li>Clicks on the "File" menu and selects "Generate Document..."</li>
	 * <li>Focuses on the "Generate Document" window</li>
	 * <li>Clicks on the "Settings" menu and selects "Load Settings..."</li>
	 * <li>Focuses on the "Open" window</li>
	 * <li>Enters the file name in the "File name:" field and presses Enter</li>
	 * <li>Focuses back on the "Generate Document" window</li>
	 * <li>Clicks on the "Generate" button to generate the document</li>
	 * <li>If a file overwrite confirmation dialog is displayed, clicks "Yes" to
	 * replace the file</li>
	 * <li>Waits for the document to be generated</li>
	 * <li>Stops the timer for logging the time taken for document generation</li>
	 * <li>Clicks on the "No" button to close any additional dialog boxes (if
	 * present)</li>
	 * <li>Sets root search to true</li>
	 * </ol>
	 *
	 * @param fileName     The name of the document file to be generated.
	 * @param testCaseName The name of the test case being executed for logging
	 *                     purposes.
	 *
	 * @throws RuntimeException If an error occurs while generating the document in
	 *                          the Developer Editor.
	 */
	public void generateDocument(String fileName, String testCaseName) {
		log.info(testCaseName + " started");

		try {
			driver.setRootSearch(false);
			action.focusWindow("Developer Editor - [");

			// Click on the "File" menu and select "Generate Document..."
			finder.getUIElement("name", "MENUITEM", "File", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Generate Document...", WAIT_TIME).click();

			action.focusWindow("Generate Document");

			// Click on the "Settings" menu and select "Load Settings..."
			finder.getUIElement("name", "BUTTON", "Settings", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Load Settings...", WAIT_TIME).click();

			action.focusWindow("Open");

			// Enter the file name in the "File name:" field and press Enter
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).click();
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).write(fileName);
			action.shortcut("Enter");

			action.focusWindow("Generate Document");

			// Click on the "Generate" button to generate the document
			finder.getUIElement("name", "BUTTON", "Generate", WAIT_TIME).click();

			// Check if a file overwrite confirmation dialog is displayed and click "Yes" if
			// present
			boolean status = finder.waitToDisplay("partialName", "Text",
					"The following file already exists do you want to replace it?", 10);
			if (status) {
				action.focusWindow("Developer Editor");
				finder.getUIElement("name", "BUTTON", "Yes", WAIT_TIME).click();
			}

			Timer.startTimer();

			action.focusWindow("Developer Editor");

			// Wait for the document to be generated
			finder.waitToDisplay("IMAGE", "SCREEN", "launchDocument", MAX_WAIT);

			Timer.stopTimer(testCaseName);

			// Click on the "No" button to close any additional dialog boxes (if present)
			action.focusWindow("Developer Editor");
			finder.getUIElement("name", "BUTTON", "No", WAIT_TIME).click();

			driver.setRootSearch(true);

			log.info(testCaseName + " completed");
		} catch (Exception e) {
			log.error("Error while generating the document. " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generates a simulation in the Developer Editor and logs data during the
	 * process.
	 *
	 * <p>
	 * This method generates a simulation in the Developer Editor with the given
	 * {@code fileName} and {@code testCaseName}. It focuses on the Developer Editor
	 * window and performs the following steps:
	 * <ol>
	 * <li>Clicks on the "File" menu and selects "Generate Simulation..."</li>
	 * <li>Focuses on the "Simulation Wizard" window</li>
	 * <li>Clicks on the "Settings" menu and selects "Load Settings..."</li>
	 * <li>Focuses on the "Open" window</li>
	 * <li>Enters the file name in the "File name:" field and presses Enter</li>
	 * <li>Focuses back on the "Simulation Wizard" window</li>
	 * <li>Clicks on the "Generate" button to generate the simulation</li>
	 * <li>If a file overwrite confirmation dialog is displayed, clicks "Yes" to
	 * replace the file</li>
	 * <li>Waits for the simulation to be generated</li>
	 * <li>Stops the timer for logging the time taken for simulation generation</li>
	 * <li>Clicks on the "No" button to close any additional dialog boxes (if
	 * present)</li>
	 * <li>Sets root search to true</li>
	 * </ol>
	 *
	 * @param fileName     The name of the simulation file to be generated.
	 * @param testCaseName The name of the test case being executed for logging
	 *                     purposes.
	 *
	 * @throws RuntimeException If an error occurs while generating the simulation
	 *                          in the Developer Editor.
	 */
	public void generateSimulation(String fileName, String testCaseName) {
		log.info(testCaseName + " started...");

		try {
			driver.setRootSearch(false);
			action.focusWindow("Developer Editor - [");

			// Click on the "File" menu and select "Generate Simulation..."
			finder.getUIElement("name", "MENUITEM", "File", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Generate Simulation...", WAIT_TIME).click();

			action.focusWindow("Simulation Wizard");

			// Click on the "Settings" menu and select "Load Settings..."
			finder.getUIElement("name", "BUTTON", "Settings", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Load Settings...", WAIT_TIME).click();

			action.focusWindow("Open");

			// Enter the file name in the "File name:" field and press Enter
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).click();
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).write(fileName);
			action.shortcut("Enter");

			action.focusWindow("Simulation Wizard");

			// Click on the "Generate" button to generate the simulation
			finder.getUIElement("name", "BUTTON", "Generate", WAIT_TIME).click();

			// Check if a file overwrite confirmation dialog is displayed and click "Yes" if
			// present
			boolean status = finder.waitToDisplay("partialName", "Text",
					"The following file already exists do you want to replace it?", 5);
			if (status) {
				action.focusWindow("Developer Editor");
				finder.getUIElement("name", "BUTTON", "Yes", WAIT_TIME).click();
			}

			Timer.startTimer();

			action.focusWindow("Developer Editor");

			// Wait for the simulation to be generated
			finder.waitToDisplay("IMAGE", "SCREEN", "launchSimulation", MAX_WAIT);

			Timer.stopTimer(testCaseName);

			// Click on the "No" button to close any additional dialog boxes (if present)
			action.focusWindow("Developer Editor");
			finder.getUIElement("name", "BUTTON", "No", WAIT_TIME).click();

			driver.setRootSearch(true);

			log.info(testCaseName + " completed");
		} catch (Exception e) {
			log.error("Error while generating the simulation. " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generates a cue card in the Developer Editor and logs data during the
	 * process.
	 *
	 * <p>
	 * This method generates a cue card in the Developer Editor with the given
	 * {@code fileName} and {@code testCaseName}. It focuses on the Developer Editor
	 * window and performs the following steps:
	 * <ol>
	 * <li>Clicks on the "File" menu and selects "Generate Cue card..."</li>
	 * <li>Focuses on the "Cue card Wizard" window</li>
	 * <li>Clicks on the "Settings" menu and selects "Load Settings..."</li>
	 * <li>Focuses on the "Open" window</li>
	 * <li>Enters the file name in the "File name:" field and presses Enter</li>
	 * <li>Focuses back on the "Cue card Wizard" window</li>
	 * <li>Clicks on the "Generate" button to generate the cue card</li>
	 * <li>If a file overwrite confirmation dialog is displayed, clicks "Yes" to
	 * replace the file</li>
	 * <li>Waits for the cue card to be generated</li>
	 * <li>Stops the timer for logging the time taken for cue card generation</li>
	 * <li>Clicks on the "OK" button to close any additional dialog boxes (if
	 * present)</li>
	 * <li>Sets root search to true</li>
	 * </ol>
	 *
	 * @param fileName     The name of the cue card file to be generated.
	 * @param testCaseName The name of the test case being executed for logging
	 *                     purposes.
	 *
	 * @throws RuntimeException If an error occurs while generating the cue card in
	 *                          the Developer Editor.
	 */
	public void generateCueCard(String fileName, String testCaseName) {
		log.info(testCaseName + " started");

		try {
			driver.setRootSearch(false);
			action.focusWindow("Developer Editor - [");

			// Click on the "File" menu and select "Generate Cue card..."
			finder.getUIElement("name", "MENUITEM", "File", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Generate Cue card...", WAIT_TIME).click();

			action.focusWindow("Cue card Wizard");

			// Click on the "Settings" menu and select "Load Settings..."
			finder.getUIElement("name", "BUTTON", "Settings", WAIT_TIME).click();
			finder.getUIElement("name", "MENUITEM", "Load Settings...", WAIT_TIME).click();

			action.focusWindow("Open");

			// Enter the file name in the "File name:" field and press Enter
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).click();
			finder.getUIElement("name", "EDIT", "File name:", WAIT_TIME).write(fileName);
			action.shortcut("Enter");

			action.focusWindow("Cue card Wizard");

			// Click on the "Generate" button to generate the cue card
			finder.getUIElement("name", "BUTTON", "Generate", WAIT_TIME).click();

			// Check if a file overwrite confirmation dialog is displayed and click "Yes" if
			// present
			boolean status = finder.waitToDisplay("partialName", "Text",
					"The following file already exists do you want to replace it?", 5);
			if (status) {
				action.focusWindow("Developer Editor");
				finder.getUIElement("name", "BUTTON", "Yes", WAIT_TIME).click();
			}

			Timer.startTimer();

			action.focusWindow("Developer Editor");

			// Wait for the cue card to be generated
			finder.waitToDisplay("IMAGE", "SCREEN", "launchCueCard", MAX_WAIT);

			Timer.stopTimer(testCaseName);

			// Click on the "OK" button to close any additional dialog boxes (if present)
			action.focusWindow("Developer Editor");
			finder.getUIElement("name", "BUTTON", "OK", WAIT_TIME).click();

			driver.setRootSearch(true);

			log.info(testCaseName + " completed");
		} catch (Exception e) {
			log.error("Error while generating the cue card. " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs document files performance testing.
	 *
	 * <p>
	 * This method performs performance testing on document files using the
	 * Epiplex500 application. It takes the {@code captureFileName} as input, which
	 * represents the name of the capture file to be used for the performance test.
	 * The performance test is divided into the following steps:
	 * <ol>
	 * <li>Step 1: Finalize the capture from home using the
	 * {@link #finalizeFromHome(String)} method.</li>
	 * <li>Step 2: Generate various documents and simulations using the
	 * {@link #generateDocument(String, String)},
	 * {@link #generateSimulation(String, String)}, and
	 * {@link #generateCueCard(String, String)} methods.</li>
	 * <li>Step 3: Close the Developer Editor and Epiplex500 applications using the
	 * {@link core.Application#closeApplication(String)} method.</li>
	 * </ol>
	 *
	 * @param captureFileName The name of the capture file used for performance
	 *                        testing.
	 *
	 * @throws RuntimeException If an error occurs during the performance test.
	 */
	public void performanceTestDoc(String captureFileName) {
		log.info("PerformanceTest started on " + captureFileName);

		try {
			// Step 1: Finalize the capture from home
			finalizeFromHome(captureFileName);

			// Step 2: Generate various documents and simulations
			generateDocument("HTML_BRD.dws", "Generate HTML_BRD");
			generateDocument("HTML_4img.dws", "Generate HTML_4img");
			generateDocument("Word_BRD.dws", "Generate Word_BRD");
			generateDocument("Word_4img.dws", "Generate Word_4img");
			generateDocument("Excel_BRD.dws", "Generate Excel_BRD");
			generateDocument("Excel_Dump.dws", "Generate Excel_Dump");
			generateSimulation("Simulation_MB1.ews", "Generate Simulation_MB1");
			generateCueCard("Live_cuecard.cws", "Generate Live_cuecard");

			// Step 3: Close the Developer Editor and Epiplex500 applications
			action.focusWindow("Developer Editor");
			String developerEditorPath = Settings.EPIPLEX500_PATH.replace("epiplex.exe", "epiSimDoc_Editor.exe");
			Application.closeApplication(developerEditorPath);
			Application.closeApplication(epiplexPath);

			log.info("PerformanceTest completed on " + captureFileName);
		} catch (Exception e) {
			log.error("Error during the performance test. " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
