package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ProjectConfiguration {
	private static final Logger log = LogManager.getLogger(ProjectConfiguration.class);
	private static final String REPORT_NAME = "Automation_Report.html";

	// Folder paths
	private static final Path REPORT_FOLDER = Paths.get("Report");
	private static final Path CAPTURE_IMAGES_FOLDER = Paths.get("Capture Images");
	private static final Path SCREENSHOTS_FOLDER = Paths.get("Screenshots");
	private static final Path SIKULI_IMAGES_FOLDER = Paths.get("Sikuli-Images");
	private static final Path EXCEL_HOLDER_FOLDER = Paths.get("ExcelHolder");
	private static final Path TRANSFORMED_GPS_FOLDER = Paths.get("TransformedGPS");
	private static final Path BASE_FILES_FOLDER = Paths.get("Base-Files");
	private static final Path LOGS_FOLDER = Paths.get("Logs");
	private static final Path CONFIG_FOLDER = Paths.get("config");

	// File paths
	public static final String baseFilesPath = BASE_FILES_FOLDER.toAbsolutePath().toString();
	public static final String transformedGPS_path = TRANSFORMED_GPS_FOLDER.toAbsolutePath().toString();
	public static final String reportPath = Paths.get(REPORT_FOLDER.toString(), REPORT_NAME).toString();
	public static final String sikuliImageBasePath = SIKULI_IMAGES_FOLDER.toAbsolutePath().toString();
	public static final String mainWorkbookPath = Paths.get(EXCEL_HOLDER_FOLDER.toString(), "main.xlsx").toString();
	public static final String performaceReportExcelPath = Paths.get(REPORT_FOLDER.toString(), "Performance_Report.xlsx").toString();

	public static ExtentReports extentReporter;
	private static ExtentHtmlReporter htmlReporter;

	/**
	 * Create the specified folder if it does not exist.
	 *
	 * @param folder The path of the folder to be created.
	 */
	private void createFolderIfNotExists(Path folder) {
		try {
			if (!Files.exists(folder)) {
				Files.createDirectories(folder);
				log.info(folder.getFileName() + " Folder Created Successfully");
			} else {
				log.info(folder.getFileName() + " Folder Already Exists");
			}
		} catch (IOException e) {
			log.error("Exception while creating folder: " + e.getMessage());
		}
	}

	/**
	 * Clean the specified folder if it exists.
	 *
	 * @param folder The path of the folder to be cleaned.
	 */
	private void cleanFolder(Path folder) {
		try {
			if (Files.exists(folder)) {
				FileUtils.cleanDirectory(folder.toFile());
				log.info(folder.getFileName() + " Folder Cleaned Successfully");
			} else {
				log.info(folder.getFileName() + " Folder Doesn't Exist");
			}
		} catch (IOException e) {
			log.error("Exception while cleaning folder: " + e.getMessage());
		}
	}

	/**
	 * Configure the required folders for the application.
	 * 
	 * @return
	 */
	public boolean folderConfig() {
		Path[] foldersToCreate = { LOGS_FOLDER, REPORT_FOLDER, CAPTURE_IMAGES_FOLDER, SCREENSHOTS_FOLDER,
				SIKULI_IMAGES_FOLDER, EXCEL_HOLDER_FOLDER, TRANSFORMED_GPS_FOLDER, BASE_FILES_FOLDER, CONFIG_FOLDER };

		// Clean specific folders.
		log.info("CleanFolders Started");
		cleanFolder(SCREENSHOTS_FOLDER);
		cleanFolder(TRANSFORMED_GPS_FOLDER);
		log.info("CleanFolders Completed");

		// Create required folders.
		log.info("Folders Creation Started");
		for (Path folder : foldersToCreate) {
			createFolderIfNotExists(folder);
		}
		log.info("Folders Creation Completed");
		return true;
	}

	/**
	 * Check if the required folders exist.
	 *
	 * @return true if all required folders exist; false if any folder is missing.
	 */
	public boolean checkFoldersExist() {
		Path[] requiredFolders = { LOGS_FOLDER, REPORT_FOLDER, CAPTURE_IMAGES_FOLDER, SCREENSHOTS_FOLDER,
				SIKULI_IMAGES_FOLDER, EXCEL_HOLDER_FOLDER, TRANSFORMED_GPS_FOLDER, BASE_FILES_FOLDER, CONFIG_FOLDER };

		for (Path folder : requiredFolders) {
			if (!Files.exists(folder) || !Files.isDirectory(folder)) {
				System.err.println("Missing folder: " + folder.getFileName());
				return false;
			}
		}

		if (!createMainSheet()) {
			log.error("main.xlsx creation failed. Please check.");
			return false;
		}

		return true;
	}

	public boolean createMainSheet() {

		boolean status = true;
		File mainSheet = new File(ProjectConfiguration.mainWorkbookPath);
		if (!mainSheet.exists()) {
			ExcelLibrary excelLib = new ExcelLibrary();
			String[] sheetNames = { "workbooks", "Apps", "settings" };
			String[][] headers = { { "Workbook Paths to execute" }, { "App Name", "App Path" },
					{ "Setting Name", "Setting Value" } };

			String[][] settingsData = { { "MAIL_ID", "Add Value" }, { "MAIL_PASSWORD", "Add Value" },
					{ "BUILD_NO", "Add Value" }, { "HOTFIX_ID", "Add Value" }, { "PATCH_INFO", "Add Value" },
					{ "BUILD_BY", "Add Value" }, { "REPORT_TITLE", "Add Value" }, { "REPORT_NAME", "Add Value" },
					{ "REPO_PATH", "Add Value" }, { "EPIPLEX500_PATH", "Add Value" }, { "FIND_WAIT", "Add Value" },
					{ "MAX_WAIT", "Add Value" }, { "PROGRAM_DATA_PATH", "Add Value" }, { "SCALE", "Add Value" } };

			try {
				for (int i = 0; i < sheetNames.length; i++) {
					status &= excelLib.createExcelWithSheetAndHeader(sheetNames[i], headers[i]);
				}
				status &= excelLib.writeDataToExcel("settings", settingsData);

				// Save and close the workbook
				excelLib.saveWorkbook(ProjectConfiguration.mainWorkbookPath);
				excelLib.closeWorkbook();
			} catch (IOException e) {
				log.error("Exception while creating main.xlsx: " + e.getMessage());
				e.printStackTrace();
				status = false;
			}
		}
		log.info("main.xlsx: created successfully");
		createPerformanceSheet();
		return status;
	}

	private void createPerformanceSheet() {
		String SHEET_NAME = "Performance";
		String[] HEADERS = { "Document Type", "Start Time", "End Time", "Duration in Seconds" };
		ExcelLibrary excelLib = new ExcelLibrary();
		String filePath = ProjectConfiguration.performaceReportExcelPath;
		try {
			if (!new File(filePath).exists()) {
				excelLib.createExcelWithSheetAndHeader(SHEET_NAME, HEADERS);
				excelLib.saveWorkbook(filePath);
				excelLib.closeWorkbook();
			}
		} catch (Exception e) {
			log.error("Exception while creating " + filePath+ e.getMessage());
		}
		log.info(filePath+" created successfully");
	}

	/**
	 * Check if required files exist in specific folders. If any file is missing,
	 * return false; otherwise, return true.
	 *
	 * @return true if all required files exist; false if any file is missing.
	 */
	public boolean checkFiles() {
		boolean status = true;
		Path[] foldersToCheck = { EXCEL_HOLDER_FOLDER, BASE_FILES_FOLDER, CONFIG_FOLDER };
		for (Path folder : foldersToCheck) {
			if (Files.notExists(folder) || !Files.isDirectory(folder) || folder.toFile().list().length == 0) {
				log.error(folder.getFileName() + " Files Missing");
				status = false;
			}
		}
		return status;
	}

	/**
	 * Configure the ExtentReports for logging the test report.
	 */
	public void reportConfig() {
		folderConfig(); // Ensure required folders are created before setting up the report.
		log.info("Report Setup Started");

		// Create and configure the HTML reporter.
		htmlReporter = new ExtentHtmlReporter(reportPath);
		extentReporter = new ExtentReports();
		extentReporter.attachReporter(htmlReporter);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setDocumentTitle(Settings.REPORT_TITLE);
		htmlReporter.config().setReportName(Settings.REPORT_NAME);
		htmlReporter.config().setTheme(Theme.STANDARD);

		// Set system information for the report.
		extentReporter.setSystemInfo("Organization", "Epiance Software");
		extentReporter.setSystemInfo("Build No", Settings.BUILD_NO);
		extentReporter.setSystemInfo("Hotfix Id", Settings.HOTFIX_ID);
		extentReporter.setSystemInfo("Patch Info", Settings.PATCH_INFO);
		extentReporter.setSystemInfo("Build By", Settings.BUILD_BY);
		extentReporter.setSystemInfo("Automation Tester", "Dhanush");

		log.info("Report Setup Completed");
	}

	/**
	 * Get a Markup object for a log message with the pass color.
	 *
	 * @param log The log message.
	 * @return The Markup object representing the log message with the pass color.
	 */
	public Markup getPassMarkUp(String log) {
		return MarkupHelper.createLabel(log, ExtentColor.GREEN);
	}

	/**
	 * Get a Markup object for a log message with the fail color.
	 *
	 * @param log The log message.
	 * @return The Markup object representing the log message with the fail color.
	 */
	public Markup getFailMarkUp(String log) {
		return MarkupHelper.createLabel(log, ExtentColor.RED);
	}

	/**
	 * Get a Markup object for a log message with the retry color.
	 *
	 * @param log The log message.
	 * @return The Markup object representing the log message with the retry color.
	 */
	public Markup getRetryMarkUp(String log) {
		return MarkupHelper.createLabel(log, ExtentColor.ORANGE);
	}

	/**
	 * Flush the ExtentReports to complete logging the test report.
	 */
	public void reportFlush() {
		extentReporter.flush();
		log.info("Report Logging Closed");
	}
}