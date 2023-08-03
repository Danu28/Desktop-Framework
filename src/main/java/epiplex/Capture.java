package epiplex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sikuli.script.Screen;

import actions.ElementFinder;
import actions.Keyboard;
import core.Application;
import core.Driver;
import core.UIElement;
import utils.ProjectConfiguration;
import utils.Settings;
import utils.Timer;
import utils.XML_Library;

/**
 * The Capture class is the entry point and a key component of the Epiplex
 * automation framework. It initializes various components and settings for test
 * case execution in the Epiplex application. The class orchestrates the
 * automation flow by interacting with UI elements, executing test cases, and
 * logging relevant information.
 */
public class Capture {

	/**
	 * An instance of the Logger class from Log4j used for logging messages.
	 */
	private static final Logger log = LogManager.getLogger(Capture.class);

	/**
	 * The path to the Epiplex application.
	 */
	static String epiplexPath = "Epiplex";

	/**
	 * The wait time in milliseconds used in various automation actions. It is
	 * fetched from the Settings.getFindWait() method.
	 */
	static long waitTime = Settings.FIND_WAIT;

	/**
	 * An instance of the Driver class, which provides automation functionality for
	 * the Epiplex application.
	 */
	static Driver driver = new Driver();

	/**
	 * An instance of the XML_Library class, used for working with XML files.
	 */
	static XML_Library xml_Lib = new XML_Library();

	/**
	 * An instance of the ElementFinder class, which is used to find UI elements
	 * within the Epiplex application.
	 */
	static ElementFinder finder = new ElementFinder(driver);

	/**
	 * This method will start the capture process with the given capture file name.
	 * The method launches the Epiplex application, sets up the capture, and begins
	 * recording actions.
	 * 
	 * @param fileName The name of the capture file to be saved.
	 */
	public static void startCapture(String fileName) {

		try {
			// Update capture settings
			new XML_Library().updateCaptureSettings(true);

			// Launch the Epiplex application
			Application.launchApplication(epiplexPath);

			// Wait for the Epiplex main window to appear
			finder.waitToDisplay("name", "WINDOW", "Epiplex500", waitTime);

			// Focus on the Epiplex main window
			Timer.waitTime(1500);
			finder.getWindow("Epiplex500", waitTime).setFocus();

			// Locate and enter the file name in the Edit Box
			UIElement fileNameEditBox = finder.getElement("name", "EDIT",
					"Enter a new file name or select from the list", waitTime);
			fileNameEditBox.click();
			fileNameEditBox.write(fileName);

			// Click on the "Start Capture" button
			finder.getElement("name", "BUTTON", "Start Capture", waitTime).click();

			// Check for file overwrite or pending recovery popup and handle if needed
			boolean fileOverWritePopup = finder.waitToDisplay("name", "Text",
					"File already exists. Do you want to overwrite?", 3);
			boolean pending = finder.waitToDisplay("name", "Text",
					"File Name specified is pending to recover and save. Do you want to overwrite the file?", 3);

			if (fileOverWritePopup || pending) {
				finder.getElement("name", "BUTTON", "Yes", waitTime).click();
			}

			// Handle Data Security Confirmation popup if displayed
			boolean security = finder.waitToDisplay("partialName", "Text", "Data Security Confirmation...", 3);
			if (security) {
				finder.getElement("name", "BUTTON", "Yes", waitTime).click();
			}

			// Handle any other popup if displayed (e.g., Epiplex500 Capture popup)
			boolean popUp = finder.waitToDisplay("name", "Window", "Epiplex500 Capture", waitTime);
			if (popUp) {
				finder.getElement("name", "BUTTON", "OK", waitTime).click();
			}

			// Wait for the capture to start
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);
			while (!status) {
				status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);
			}

			log.info("Capture Started");
		} catch (Exception e) {
			log.error("An error occurred during the capture process:", e);
		}
	}

	/**
	 * This method will start the manual capture mode with the given capture file
	 * name. The method launches the Epiplex application, sets up the manual capture
	 * mode, and waits for the user to start the capture manually.
	 * 
	 * @param fileName The name of the capture file to be saved.
	 */
	public static void startCaptureManual(String fileName) {

		try {
			// Update capture settings
			new XML_Library().updateCaptureSettings(true);

			// Launch the Epiplex application
			Application.launchApplication(epiplexPath);

			// Wait for the Epiplex main window to appear
			finder.waitToDisplay("name", "WINDOW", "Epiplex500", waitTime);

			// Focus on the Epiplex main window
			Timer.waitTime(1500);
			finder.getWindow("Epiplex500", waitTime).setFocus();

			// Locate and enter the file name in the Edit Box
			UIElement fileNameEditBox = finder.getElement("name", "EDIT",
					"Enter a new file name or select from the list", waitTime);
			fileNameEditBox.click();
			fileNameEditBox.write(fileName);

			// Click on the "Start Capture" button
			finder.getElement("name", "BUTTON", "Start Capture", waitTime).click();

			// Check for file overwrite or pending recovery popup and handle if needed
			boolean fileOverwritePopup = finder.waitToDisplay("name", "Text",
					"File already exists. Do you want to overwrite?", 3);
			boolean pending = finder.waitToDisplay("name", "Text",
					"File Name specified is pending to recover and save. Do you want to overwrite the file?", 3);

			if (fileOverwritePopup || pending) {
				finder.getElement("name", "BUTTON", "Yes", waitTime).click();
			}

			// Handle Data Security Confirmation popup if displayed
			boolean security = finder.waitToDisplay("partialName", "Text", "Data Security Confirmation...", 3);
			if (security) {
				finder.getElement("name", "BUTTON", "Yes", waitTime).click();
			}

			// Handle any other popup if displayed (e.g., Epiplex500 Capture popup)
			boolean popUp = finder.waitToDisplay("name", "Window", "Epiplex500 Capture", waitTime);
			if (popUp) {
				finder.getElement("name", "BUTTON", "OK", waitTime).click();
			}

			// Check if the manual capture mode is started successfully
			boolean status = finder.waitToDisplay("partialName", "Button", "start manual capture", waitTime);
			if (status) {
				log.info("Capture Started");
			} else {
				log.error("Capture process did not start as expected.");
			}
		} catch (Exception e) {
			log.error("An error occurred during the capture process:", e);
		}
	}

	/**
	 * This method cancels the ongoing capture process, if any. If a capture is in
	 * progress, the method clicks on the "Capture in progress" button, opens the
	 * context menu, and selects "Cancel". It then confirms the cancel operation by
	 * clicking the "Yes" button on the confirmation dialog. If no capture is in
	 * progress, the method logs that no action is taken.
	 */
	public static void cancelCapture() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();

				// Click on the "Cancel" option in the context menu
				UIElement cancelButton = finder.getElement("name", "MENUITEM", "Cancel", waitTime);
				cancelButton.clickCenter();

				// Confirm the cancel operation by clicking the "Yes" button on the confirmation
				// dialog
				UIElement yesButton = finder.getElement("name", "BUTTON", "Yes", waitTime);
				yesButton.clickCenter();

				log.info("Capture process has been canceled.");
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while canceling the capture process:", e);
		}
	}

	/**
	 * This method saves the running capture in progress. If a capture is in
	 * progress, the method clicks on the "Capture in progress" button, opens the
	 * context menu, and selects "Save". It then checks if the "Properties" window
	 * is displayed and clicks on the "Save" button within the window (if present).
	 * After saving, the method waits for the "Saving capture file" window to
	 * vanish, indicating that the capture saving is complete. The method then
	 * closes any pop-up windows that appear after saving the capture and closes the
	 * Epiplex500 application. If no capture is in progress, the method logs that no
	 * action is taken.
	 */
	public static void saveCapture() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Save" option in the context menu
				UIElement saveMenuItem = finder.getElement("name", "MENUITEM", "Save", waitTime);
				saveMenuItem.clickCenter();

				// Check if the "Properties" window is displayed and click on the "Save" button
				// within the window (if present)
				boolean propertiesWindow = finder.waitToDisplay("name", "Window", "Properties", waitTime);
				log.info("Properties window displayed: {}", propertiesWindow);

				if (propertiesWindow) {
					UIElement saveButton = finder.getElement("name", "BUTTON", "Save", waitTime);
					saveButton.clickCenter();
				}

				// Wait for the "Saving capture file" window to vanish, indicating that the
				// capture saving is complete
				boolean isSavingComplete = finder.waitToVanish("name", "Window", "Saving capture file", 5);
				log.info("Capture saving completed: {}", isSavingComplete);

				// Close any pop-up windows that appear after saving the capture
				finder.waitToDisplay("name", "Window", "Epiplex500", waitTime);
				finder.getWindow("Epiplex500", waitTime).setFocus();
				finder.getElement("name", "BUTTON", "OK", waitTime).clickCenter();
				finder.getElement("name", "BUTTON", "No", waitTime).clickCenter();

				// Close the Epiplex500 application
				Application.closeApplication(epiplexPath);
				log.info("Capture process completed.");
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while saving the capture:", e);
		}
	}

	/**
	 * This method adds a note while the capture is in progress. If a capture is in
	 * progress, the method clicks on the "Capture in progress" button, opens the
	 * context menu, and selects "Add Note". It then checks if the "Epiplex500
	 * Capture - Add Note" window is displayed and adds a note to the capture. The
	 * method also attaches an audio file to the note, if applicable. After adding
	 * the note, the method logs the status of the "Add Note" window and the "Select
	 * The Audio File" window (if displayed). If no capture is in progress, the
	 * method logs that no action is taken.
	 */
	public static void addNote() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Add Note" option in the context menu
				UIElement addNoteMenuItem = finder.getElement("name", "MENUITEM", "Add Note", waitTime);
				addNoteMenuItem.clickCenter();

				// Check if the "Epiplex500 Capture - Add Note" window is displayed and add a
				// note
				boolean addNoteWindowDisplayed = finder.waitToDisplay("name", "Window",
						"Epiplex500 Capture  - Add Note", waitTime);
				log.info("Add Note window displayed: {}", addNoteWindowDisplayed);

				if (addNoteWindowDisplayed) {
					finder.getWindow("Epiplex500 Capture  - Add Note", waitTime).setFocus();
					Keyboard.type("Step Note added while capturing");

					// Attach an audio file to the note, if applicable
					UIElement attachButton = finder.getElement("id", "Button", "tsmiAttach", waitTime);
					attachButton.clickCenter();

					boolean selectAudioFileWindowDisplayed = finder.waitToDisplay("name", "Window",
							"Select The Audio File", waitTime);
					log.info("Select Audio File window displayed: {}", selectAudioFileWindowDisplayed);

					if (selectAudioFileWindowDisplayed) {
						finder.getElement("name", "SplitButton", "All locations", waitTime).clickCenter();
						Keyboard.paste(ProjectConfiguration.baseFilesPath);
						Keyboard.specialKeyPress("ENTER");

						UIElement fileNameEditBox = finder.getElement("name", "Edit", "File name:", waitTime);
						fileNameEditBox.write("Step.mp3");
						Keyboard.specialKeyPress("ENTER");

						UIElement okButton = finder.getElement("name", "Button", "OK", waitTime);
						okButton.clickCenter();
					}
				}
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while adding the note:", e);
		}
	}

	/**
	 * This method adds validations while the capture is in progress. If a capture
	 * is in progress, the method clicks on the "Capture in progress" button, opens
	 * the context menu, and selects "Add Validations & Prerequisites". It then
	 * checks if the "Validations and Prerequisites" window is displayed and adds
	 * the specified validation conditions and attachment. The method logs the
	 * status of the "Validations and Prerequisites" window. If no capture is in
	 * progress, the method logs that no action is taken.
	 */
	public static void addValidations() {

		try {
			String condition = "Alphabets Only"; // or "Numeric Only"
			String attachTo = "Current Step";

			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Add Validations & Prerequisites" option in the context menu
				UIElement addValidationsMenuItem = finder.getElement("name", "MENUITEM",
						"Add Validations & Prerequisites", waitTime);
				addValidationsMenuItem.clickCenter();

				// Check if the "Validations and Prerequisites" window is displayed and add the
				// specified validation conditions and attachment
				boolean validationsWindowDisplayed = finder.waitToDisplay("name", "Window",
						"Validations and Prerequisites", waitTime);
				log.info("Validations and Prerequisites window displayed: {}", validationsWindowDisplayed);

				if (validationsWindowDisplayed) {
					finder.getWindow("Validations and Prerequisites", waitTime).setFocus();

					// Select the specified condition from the condition combo box
					UIElement conditionComboBox = finder.getElement("id", "COMBOBOX", "cmb_condition", waitTime);
					conditionComboBox.clickCenter();
					Keyboard.type(condition);
					UIElement conditionOption = finder.getElement("name", "TEXT", condition, waitTime);
					conditionOption.clickCenter();

					// Select the specified attachment option from the attach to combo box
					UIElement attachToComboBox = finder.getElement("id", "COMBOBOX", "cmd_attch_to", waitTime);
					attachToComboBox.clickCenter();
					Keyboard.type(attachTo);
					UIElement attachToOption = finder.getElement("name", "TEXT", attachTo, waitTime);
					attachToOption.clickCenter();

					// Click on the "Resume Capture" button to continue the capture process
					UIElement resumeCaptureButton = finder.getElement("name", "BUTTON", "Resume Capture", waitTime);
					resumeCaptureButton.clickCenter();
				}
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while adding validations:", e);
		}
	}

	/**
	 * This method adds an exception while the capture is in progress. If a capture
	 * is in progress, the method clicks on the "Capture in progress" button, opens
	 * the context menu, and selects "Add Exception". It then checks if the "Process
	 * Exception" window is displayed and adds the specified exception description.
	 * The method logs the status of the "Process Exception" window. If no capture
	 * is in progress, the method logs that no action is taken.
	 */
	public static void addException() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Add Exception" option in the context menu
				UIElement addExceptionMenuItem = finder.getElement("name", "MENUITEM", "Add Exception", waitTime);
				addExceptionMenuItem.clickCenter();

				// Check if the "Process Exception" window is displayed and add the specified
				// exception description
				boolean exceptionWindowDisplayed = finder.waitToDisplay("name", "Window", "Process Exception",
						waitTime);
				log.info("Exception Information window displayed: {}", exceptionWindowDisplayed);

				if (exceptionWindowDisplayed) {
					finder.getWindow("Process Exception", waitTime).setFocus();

					// Write the specified exception description in the exception description
					// textbox
					UIElement exceptionDescriptionTextbox = finder.getElement("name", "TEXT",
							"Enter the exception description here.", waitTime);
					exceptionDescriptionTextbox.write("Exception Added");

					// Click on the "Recover Exception" button to continue the capture process
					UIElement recoverExceptionButton = finder.getElement("name", "BUTTON", "Recover Exception",
							waitTime);
					recoverExceptionButton.clickCenter();
				}
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while adding the exception:", e);
		}
	}

	/**
	 * This method stops the recovery process when an exception has been added
	 * during the capture. If a capture is in progress, the method clicks on the
	 * "Capture in progress" button, opens the context menu, and selects "Recovery
	 * Stop". The method stops the recovery process if the "Capture in progress"
	 * button is displayed. If no capture is in progress, the method logs that no
	 * action is taken.
	 */
	public static void exceptionRecoveryStop() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Recovery Stop" option in the context menu to stop the recovery
				// process
				UIElement recoveryStopMenuItem = finder.getElement("name", "MENUITEM", "Recovery Stop", waitTime);
				recoveryStopMenuItem.clickCenter();
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while performing exception recovery stop:", e);
		}
	}

	/**
	 * This method adds input data points while performing a capture. If a capture
	 * is in progress, the method clicks on the "Capture in progress" button, opens
	 * the context menu, and selects "Add Input Data". The method displays the
	 * "Input Data Points" window if the "Capture in progress" button is displayed.
	 * After the window is displayed, the method writes the test data into the
	 * "Datapoint_1" textbox and clicks on the "Resume Capture" button to continue
	 * the capture. If no capture is in progress, the method logs that no action is
	 * taken.
	 */
	public static void addInputData() {

		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				UIElement captureInProgressButton = finder.getElement("partialName", "Button", "Capture in progress",
						waitTime);
				captureInProgressButton.clickCenter();
				Timer.waitTime(1000);

				// Click on the "Add Input Data" option in the context menu to open the "Input
				// Data Points" window
				UIElement addInputDataMenuItem = finder.getElement("name", "MENUITEM", "Add Input Data", waitTime);
				addInputDataMenuItem.clickCenter();

				// Check if the "Input Data Points" window is displayed
				boolean inputDataWindowDisplayed = finder.waitToDisplay("name", "Window", "Input Data Points",
						waitTime);
				log.info("Input Data Points window displayed: {}", inputDataWindowDisplayed);

				if (inputDataWindowDisplayed) {
					// Set focus to the "Input Data Points" window
					finder.getWindow("Input Data Points", waitTime).setFocus();

					// Write test data into the "Datapoint_1" textbox
					UIElement datapointTextbox = finder.getElement("name", "TEXT", "Datapoint_1", waitTime);
					datapointTextbox.write("Test Data_1");

					// Click on the "Resume Capture" button to continue the capture
					UIElement resumeCaptureButton = finder.getElement("name", "BUTTON", "Resume Capture", waitTime);
					resumeCaptureButton.clickCenter();
				}
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while adding input data:", e);
		}
	}

	/**
	 * This private helper method adds a capture action based on the provided
	 * parameters. If a capture is in progress, the method clicks on the "Capture in
	 * progress" button, opens the context menu, and selects the specified menu
	 * item. The method then checks if the specified button text is displayed after
	 * selecting the menu item. If it is, the method logs the provided log message.
	 * If the button text is not displayed, the method waits for one second and then
	 * returns.
	 * 
	 * @param menuItemText The text of the menu item to select from the context
	 *                     menu.
	 * @param buttonText   The text of the button to check after selecting the menu
	 *                     item.
	 * @param logMessage   The log message to be logged if the button text is
	 *                     displayed after selecting the menu item.
	 */
	private static void addCaptureAction(String menuItemText, String buttonText, String logMessage) {
		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				finder.getElement("partialName", "Button", "Capture in progress", waitTime).clickCenter();
				Timer.waitTime(1000);

				// Click on the specified menu item in the context menu
				finder.getElement("name", "MENUITEM", menuItemText, waitTime).clickCenter();

				// Check if the specified button text is displayed
				status = finder.waitToDisplay("partialName", "Button", buttonText, waitTime);

				if (status) {
					log.info(logMessage);
				} else {
					// Wait for one second before returning
					Timer.waitTime(1000);
				}
			}
		} catch (Exception e) {
			log.error("An error occurred while performing the action: {}", menuItemText, e);
		}
	}

	/**
	 * This private helper method stops a capture action based on the provided
	 * parameters. If the specified button text is displayed, the method clicks on
	 * the button to stop the capture action. It then clicks on the specified menu
	 * item from the context menu and clicks on the center of the screen. Finally,
	 * the method checks if the "Capture in progress" button is displayed, and if it
	 * is, the method logs the provided log message. If the "Capture in progress"
	 * button is not displayed, the method waits for one second and then returns.
	 * 
	 * @param menuItemText The text of the menu item to select from the context
	 *                     menu.
	 * @param buttonText   The text of the button to click to stop the capture
	 *                     action.
	 * @param logMessage   The log message to be logged if the "Capture in progress"
	 *                     button is displayed after stopping the capture action.
	 */
	private static void stopCaptureAction(String menuItemText, String buttonText, String logMessage) {
		try {
			boolean status = finder.waitToDisplay("partialName", "Button", buttonText, waitTime);

			if (status) {
				// Click on the button to stop the capture action
				finder.getElement("partialName", "Button", buttonText, waitTime).clickCenter();
				Timer.waitTime(1000);

				// Click on the specified menu item in the context menu
				finder.getElement("name", "MENUITEM", menuItemText, waitTime).clickCenter();

				// Click on the center of the screen to deselect any elements
				new Screen().getCenter().click();

				// Check if the "Capture in progress" button is displayed
				status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

				if (status) {
					log.info(logMessage);
				} else {
					// Wait for one second before returning
					Timer.waitTime(1000);
				}
			}
		} catch (Exception e) {
			log.error("An error occurred while performing the action: {}", menuItemText, e);
		}
	}

	/**
	 * This method adds screen video capture action by calling the
	 * {@link #addCaptureAction(String, String, String)} method with appropriate
	 * parameters. It adds a "Start Screen Video" action, which turns on the screen
	 * video recording and logs the message "Screen video started" if the action is
	 * successful.
	 */
	public static void addScreenVideo() {
		addCaptureAction("Start Screen Video", "Screen Video on", "Screen video started");
	}

	/**
	 * This method adds audio capture action by calling the
	 * {@link #addCaptureAction(String, String, String)} method with appropriate
	 * parameters. It adds a "Start Audio" action, which turns on the audio
	 * recording and logs the message "Audio Record started" if the action is
	 * successful.
	 */
	public static void addAudio() {
		addCaptureAction("Start Audio", "Audio on", "Audio Record started");
	}

	/**
	 * This method stops the screen video capture action by calling the
	 * {@link #stopCaptureAction(String, String, String)} method with appropriate
	 * parameters. It adds a "Stop Screen Video" action, which stops the screen
	 * video recording and logs the message "Screen video ended" if the action is
	 * successful.
	 */
	public static void stopScreenVideo() {
		stopCaptureAction("Stop Screen Video", "Screen Video on", "Screen video ended");
	}

	/**
	 * This method stops the audio capture action by calling the
	 * {@link #stopCaptureAction(String, String, String)} method with appropriate
	 * parameters. It adds a "Stop Audio" action, which stops the audio recording
	 * and logs the message "Audio Record ended" if the action is successful.
	 */
	public static void stopAudio() {
		stopCaptureAction("Stop Audio", "Audio on", "Audio Record ended");
	}

	/**
	 * This method adds narration while doing a capture. If a capture is in
	 * progress, the method clicks on the "Capture in progress" button, opens the
	 * context menu, and selects "Add Narration". The method then enters the text
	 * "Narration added" in the "Partner Name" textbox and resumes the capture. If
	 * no capture is in progress, the method logs that no action is taken.
	 */
	public static void addNarration() {
		try {
			boolean status = finder.waitToDisplay("partialName", "Button", "Capture in progress", waitTime);

			if (status) {
				// Click on the "Capture in progress" button to open the context menu
				finder.getElement("partialName", "Button", "Capture in progress", waitTime).clickCenter();
				Timer.waitTime(1000);

				// Click on the "Add Narration" option in the context menu to open the
				// "Narration" window
				finder.getElement("name", "MENUITEM", "Add Narration", waitTime).clickCenter();

				// Check if the "Narration" window is displayed
				boolean narrationWindowDisplayed = finder.waitToDisplay("id", "BUTTON", "BtnAutoRegions", waitTime);
				log.info("Narration window displayed: {}", narrationWindowDisplayed);

				if (narrationWindowDisplayed) {
					// Set focus to the "Narration" window
					finder.getWindow("Epiplex500 Capture  - Add Narration", waitTime).setFocus();

					// Enter the text "Narration added" in the "Partner Name" textbox
					UIElement partnerNameEdit = finder.getElement("name", "EDIT", "Partner Name", waitTime);
					partnerNameEdit.clickCenter();
					partnerNameEdit.doubleClick();
					Keyboard.type("Narration added");

					// Click on the "Resume Capture" button twice to resume the capture
					finder.getElement("id", "BUTTON", "BtnResumeCapture", waitTime).clickCenter();
					finder.getElement("id", "BUTTON", "BtnResumeCapture", waitTime).clickCenter();
				}
			} else {
				log.info("Capture process is not in progress. No action taken.");
			}
		} catch (Exception e) {
			log.error("An error occurred while adding narration:", e);
		}
	}
}
