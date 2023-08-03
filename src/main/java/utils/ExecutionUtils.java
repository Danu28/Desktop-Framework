package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import core.Application;

/**
 * Utility class containing methods related to test execution and workbook
 * handling.
 */
public class ExecutionUtils {

	private static final Logger log = LogManager.getLogger(ExecutionUtils.class);
	private ExcelLibrary excelLibrary = new ExcelLibrary();

	/**
	 * Retrieves the list of workbook paths that need to be executed.
	 *
	 * @return A List of Strings containing the paths of workbooks to execute.
	 */
	public List<String> getExecutionWorkbooks() {
		log.info("Get execution sheets started");
		List<String> workbookPaths = new ArrayList<>();

		try (Workbook mainWorkbook = excelLibrary.getWorkbook(ProjectConfiguration.mainWorkbookPath)) {
			Sheet workbooksSheet = mainWorkbook.getSheet("workbooks");
			int max_rows = workbooksSheet.getPhysicalNumberOfRows();

			for (int row = 1; row < max_rows; row++) {
				String path = excelLibrary.readCell(workbooksSheet, row, 0);
				if (path == null) {
					log.error("workbookPath is null at row " + row);
					break; // Exit the loop on encountering null path
				}
				workbookPaths.add(path);
			}
		} catch (Exception e) {
			log.error("Error while reading workbook: " + e.getMessage());
		}

		log.info("Get execution sheets completed");
		return workbookPaths;
	}

	/**
	 * Loads application paths to a list.
	 *
	 * @param workbook The Workbook from which the application paths are to be
	 *                 loaded.
	 * @return true if all the application paths were successfully loaded, false
	 *         otherwise.
	 */
	public boolean loadApps(Workbook workbook) {
		boolean status = true;
		Sheet appSheet = workbook.getSheet("Apps");
		int max_rows = appSheet.getLastRowNum();
		for (int row = 1; row <= max_rows; row++) {
			String appName = excelLibrary.readCell(appSheet, row, 0);
			String path = excelLibrary.readCell(appSheet, row, 1);
			if (appName == null || path == null) {
				String errorMessage = appName + " - " + path + " has an issue";
				System.err.println(errorMessage);
				log.error(errorMessage);
				status = false;
			} else {
				Application.apps.put(appName.toUpperCase(), path);
			}
		}
		return status;
	}

//	/**
//	 * This method will load settings to a list
//	 * 
//	 * @return
//	 */
//	public boolean loadSettings() {
//		boolean status = true;
//		log.info("loadSettings started");
//		File mainExcel = new File(ProjectConfiguration.mainWorkbookPath);
//		if (!mainExcel.exists())
//			createMainExcel(ProjectConfiguration.mainWorkbookPath);
//		Timer.waitTime(1000);
//		try (Workbook mainWorkbook = excelLibrary.getWorkbook(ProjectConfiguration.mainWorkbookPath)) {
//			status = loadApps(mainWorkbook);
//			if (!status)
//				return status;
//
//			Sheet settingsSheet = mainWorkbook.getSheet("settings");
//			int max_rows = settingsSheet.getLastRowNum();
//
//			// Create a map to hold the settings and their corresponding fields
//			Map<String, Consumer<String>> settingsMap = new HashMap<>();
//			settingsMap.put("MAIL_ID", value -> Settings.MAIL_ID = value);
//			settingsMap.put("MAIL_PASSWORD", value -> Settings.MAIL_PASSWORD = value);
//			settingsMap.put("BUILD_NO", value -> Settings.BUILD_NO = value);
//			settingsMap.put("HOTFIX_ID", value -> Settings.HOTFIX_ID = value);
//			settingsMap.put("PATCH_INFO", value -> Settings.PATCH_INFO = value);
//			settingsMap.put("BUILD_BY", value -> Settings.BUILD_BY = value);
//			settingsMap.put("REPORT_TITLE", value -> Settings.REPORT_TITLE = value);
//			settingsMap.put("REPORT_NAME", value -> Settings.REPORT_NAME = value);
//			settingsMap.put("REPO_PATH", value -> Settings.REPO_PATH = value);
//			settingsMap.put("EPIPLEX500_PATH", value -> Settings.EPIPLEX500_PATH = value);
//			settingsMap.put("FIND_WAIT", value -> Settings.FIND_WAIT = Long.parseLong(value));
//			settingsMap.put("MAX_WAIT", value -> Settings.MAX_WAIT = Long.parseLong(value));
//			settingsMap.put("PROGRAM_DATA_PATH", value -> Settings.PROGRAM_DATA_PATH = value);
//			settingsMap.put("SCALE", value -> Settings.SCALE = Integer.parseInt(value));
//			// Add more settings here
//
//			for (int row = 1; row <= max_rows; row++) {
//				String key = excelLibrary.readCell(settingsSheet, row, 0);
//				String value = excelLibrary.readCell(settingsSheet, row, 1);
//				if (key == null || value == null) { // Use || for logical OR
//					String errorMessage = key + " - " + value + " has an issue";
//					System.err.println(errorMessage);
//					log.error(errorMessage);
//					status = false;
//				} else {
//					// Lookup the setting in the map and update the corresponding field
//					Consumer<String> settingHandler = settingsMap.get(key.toUpperCase());
//					if (settingHandler != null) {
//						settingHandler.accept(value);
//					} else {
//						String errorMessage = key + " - is invalid";
//						System.err.println(errorMessage);
//						log.error(errorMessage);
//						status = false;
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error("Error while reading the workbook: " + e.getMessage());
//			status = false;
//		}
//
//		log.info("loadSettings completed");
//		return status;
//	}

	/**
	 * Loads settings to the {@link Settings} class from the main workbook.
	 *
	 * @return {@code true} if all settings are successfully loaded, otherwise
	 *         {@code false}.
	 */
	public boolean loadSettings() {
		log.info("loadSettings started");
		File mainExcel = new File(ProjectConfiguration.mainWorkbookPath);
		if (!mainExcel.exists())
			createMainExcel(ProjectConfiguration.mainWorkbookPath);
		Timer.waitTime(1000);

		boolean status = true;

		try (Workbook mainWorkbook = excelLibrary.getWorkbook(ProjectConfiguration.mainWorkbookPath)) {
			loadApps(mainWorkbook);

			Sheet settingsSheet = mainWorkbook.getSheet("settings");
			int maxRows = settingsSheet.getLastRowNum();

			for (int row = 1; row <= maxRows; row++) {
				String key = excelLibrary.readCell(settingsSheet, row, 0);
				String value = excelLibrary.readCell(settingsSheet, row, 1);
				if (key != null && value != null) {
					String errorMessage = key + " - " + value + "values";
					System.err.println(errorMessage);
					updateSetting(key.toUpperCase(), value);
				} else {
					String errorMessage = key + " - " + value + " has an issue";
					System.err.println(errorMessage);
					log.error(errorMessage);
					status = false;
				}
			}
		} catch (Exception e) {
			log.error("Error while reading the workbook: " + e.getMessage());
			status = false;
		}

		log.info("loadSettings completed");
		return status;
	}

	/**
	 * Updates the {@link Settings} class with the given setting key and value.
	 *
	 * @param key   The setting key.
	 * @param value The setting value.
	 */
	private void updateSetting(String key, String value) {
		switch (key) {
		case "MAIL_ID":
			Settings.MAIL_ID = value;
			break;
		case "MAIL_PASSWORD":
			Settings.MAIL_PASSWORD = value;
			break;
		case "BUILD_NO":
			Settings.BUILD_NO = value;
			break;
		case "HOTFIX_ID":
			Settings.HOTFIX_ID = value;
			break;
		case "PATCH_INFO":
			Settings.PATCH_INFO = value;
			break;
		case "BUILD_BY":
			Settings.BUILD_BY = value;
			break;
		case "REPORT_TITLE":
			Settings.REPORT_TITLE = value;
			break;
		case "REPORT_NAME":
			Settings.REPORT_NAME = value;
			break;
		case "REPO_PATH":
			Settings.REPO_PATH = value;
			break;
		case "EPIPLEX500_PATH":
			Settings.EPIPLEX500_PATH = value;
			break;
		case "FIND_WAIT":
			Settings.FIND_WAIT = Long.parseLong(value);
			break;
		case "MAX_WAIT":
			Settings.MAX_WAIT = Long.parseLong(value);
			break;
		case "PROGRAM_DATA_PATH":
			Settings.PROGRAM_DATA_PATH = value;
			break;
		case "SCALE":
			Settings.SCALE = Integer.parseInt(value);
			break;
		// Add more settings here
		default:
			String errorMessage = key + " - is invalid";
			System.err.println(errorMessage);
			log.error(errorMessage);
			break;
		}
	}

	/**
	 * Validate Excel sheet steps
	 * 
	 * @param workbookPath
	 * @param declaredMethods
	 * @return
	 * @throws IOException
	 */
	public boolean validateWorkbookSteps(String workbookPath, Method[] declaredMethods) {
		boolean workbookStatus = true;
		log.info(workbookPath + " Excel File check started...");

		try (Workbook workbook = excelLibrary.getWorkbook(workbookPath)) {
			List<Sheet> sheets = excelLibrary.getSheetsInWorkbook(workbook);

			for (Sheet sheet : sheets) {
				boolean sheetStatus = true;
				log.info(sheet.getSheetName() + " Sheet Step validation started...");
				int lastRow = excelLibrary.getRows(sheet);

				for (int row = 1; row <= lastRow; row++) {
					List<String> stepParameters = loadStepParameters(sheet, row);
					boolean stepStatus = validateStep(declaredMethods, stepParameters);
					markCell(workbook, sheet, row, stepStatus);

					if (!stepStatus) {
						workbookStatus = sheetStatus = false;
					}

					stepParameters.clear();
				}

				excelLibrary.writeWorkbook(workbook, workbookPath);
				log.info(sheet.getSheetName() + " Sheet Step validation Ended");

				if (!sheetStatus) {
					String errorMessage = "Excel " + workbookPath + "- Sheet " + sheet.getSheetName() + " - Has issue";
					System.err.println(errorMessage);
					log.error(errorMessage);
				}
			}
		} catch (IOException e) {
			String errorMessage = "Error while reading/writing the workbook: " + e.getMessage();
			System.err.println(errorMessage);
			log.error(errorMessage);
			workbookStatus = false;
		}

		log.info(workbookPath + " Excel File Check ended");
		return workbookStatus;
	}

	/**
	 * Validates the step by checking its parameters and method availability.
	 * 
	 * @param declaredMethods The array of declared methods in the class.
	 * @param stepParameters  The list of parameters for the step, where the first
	 *                        element is the method name and the rest are the method
	 *                        arguments.
	 * @return True if the step is valid, False otherwise.
	 */
	private boolean validateStep(Method[] declaredMethods, List<String> stepParameters) {
		if (stepParameters.isEmpty()) {
			return false; // Empty stepParameters indicate an invalid step
		}

		String methodName = stepParameters.get(0);
		int argumentLength = stepParameters.size() - 1;
		Method method = findMethod(methodName, declaredMethods, argumentLength);

		if (method == null) {
			return false; // Method not found, invalid step
		}

		int parameterLength = method.getParameterCount();
		if (parameterLength != argumentLength) {
			return false; // Invalid number of arguments
		}

		// Validation specific to "IMAGE" locators
		if (stepParameters.size() == 4) {
			String locator = stepParameters.get(1).toUpperCase();
			if (locator.equals("IMAGE")) {
				if (locator.equals("IMAGE")) {
					validateImageLocator(stepParameters);
				}
			}
		}

		return true; // All conditions passed, step is valid
	}

	public LinkedHashSet<String> images = new LinkedHashSet<>();

	/**
	 * Validates the parameters specific to "IMAGE" locators and adds the image
	 * paths to the images set. If the locator type is "SCREEN," the method does not
	 * add the image path to the images set.
	 * 
	 * @param stepParameters The list of parameters for the step, where the first
	 *                       element is the method name, the second element is the
	 *                       locator type, and the third and fourth elements are the
	 *                       image file names.
	 */
	private void validateImageLocator(List<String> stepParameters) {
		if (stepParameters.size() < 4) {
			return; // Invalid "IMAGE" locator, not enough parameters
		}

		String parameter1 = stepParameters.get(2).toUpperCase();
		if (!parameter1.equals("SCREEN")) {
			// Add the image path for the first image to the images set
			images.add(ProjectConfiguration.sikuliImageBasePath + "\\" + stepParameters.get(2) + ".PNG");
		}

		// Add the image path for the second image to the images set
		images.add(ProjectConfiguration.sikuliImageBasePath + "\\" + stepParameters.get(3) + ".PNG");
	}

	/**
	 * Writes the updated step parameters to the workbook sheets and validates the
	 * steps against the declared methods. If a step is valid, the updated
	 * parameters are written to the workbook. If a step is invalid, the method
	 * updates the workbookStatus accordingly.
	 * 
	 * @param workbookPath    The path of the workbook to be updated.
	 * @param declaredMethods An array of declared methods from the class containing
	 *                        the test steps.
	 * @return True if the workbook is updated successfully without any issues,
	 *         false otherwise.
	 */
	public boolean writeNewWorkbook(String workbookPath, Method[] declaredMethods) {
		boolean workbookStatus = true;
		log.info(workbookPath + " Excel File check started...");

		try (Workbook workbook = excelLibrary.getWorkbook(workbookPath)) {
			List<Sheet> sheets = excelLibrary.getSheetsInWorkbook(workbook);

			for (Sheet sheet : sheets) {
				boolean sheetStatus = true;
				log.info(sheet.getSheetName() + " Sheet Step validation started...");
				int lastRow = excelLibrary.getRows(sheet);

				for (int row = 1; row <= lastRow; row++) {
					List<String> stepParameters = loadStepParameters(sheet, row);
					boolean stepStatus = validateStep(declaredMethods, stepParameters);

					if (stepStatus) {
						writeExcel(sheet, row, stepParameters);
					} else {
						workbookStatus = sheetStatus = false;
					}

					stepParameters.clear();
				}

				if (!sheetStatus) {
					String errorMessage = "Excel " + workbookPath + "- Sheet " + sheet.getSheetName() + " - Has issue";
					System.err.println(errorMessage);
					log.error(errorMessage);
				}
			}

			excelLibrary.writeWorkbook(workbook, workbookPath);
			log.info(workbookPath + " Excel File Check ended");
		} catch (IOException e) {
			String errorMessage = "Error while reading/writing the workbook: " + e.getMessage();
			System.err.println(errorMessage);
			log.error(errorMessage);
			workbookStatus = false;
		}

		return workbookStatus;
	}

	/**
	 * Loads and validates methods from the specified workbook path. This method
	 * reads each sheet in the workbook, validates the steps against the declared
	 * methods, and loads the valid methods for execution. If a step is invalid, the
	 * sheetStatus is updated accordingly, and the method prints an error message.
	 * 
	 * @param workbookPath    The path of the workbook to be loaded and validated.
	 * @param declaredMethods An array of declared methods from the class containing
	 *                        the test steps.
	 * @return True if all steps in the workbook are valid and successfully loaded,
	 *         false otherwise.
	 * @throws IOException If an I/O error occurs while reading the workbook.
	 */
	public boolean loadMethodsFromWorkbook(String workbookPath, Method[] declaredMethods) {
		boolean workbookStatus = true;
		log.info(workbookPath + " Excel File check started...");

		try (Workbook workbook = excelLibrary.getWorkbook(workbookPath)) {
			List<Sheet> sheets = excelLibrary.getSheetsInWorkbook(workbook);
			for (Sheet currentSheet : sheets) {
				boolean sheetStatus = true;
				log.info(currentSheet.getSheetName() + " Sheet Step validation started...");
				int lastRow = excelLibrary.getRows(currentSheet);
				for (int currentRow = 1; currentRow <= lastRow; currentRow++) {
					List<String> stepParameters = loadStepParameters(currentSheet, currentRow);
					boolean stepStatus = validateStep(declaredMethods, stepParameters);
					if (stepStatus) {
						loadExecutionMethods(declaredMethods, stepParameters);
					} else {
						workbookStatus = sheetStatus = false;
						String methodName = currentSheet.getRow(currentRow).getCell(0).getStringCellValue();
						System.err.println(methodName + " method has an issue " + stepParameters.size());
					}
					stepParameters.clear();
				}
				excelLibrary.writeWorkbook(workbook, workbookPath);
				log.info(currentSheet.getSheetName() + " Sheet Step validation Ended");
				if (!sheetStatus) {
					logSheetIssue(workbookPath, currentSheet.getSheetName());
				}
			}
			log.info(workbookPath + " Excel File Check ended");
		} catch (IOException e) {
			e.printStackTrace();
			workbookStatus = false;
		}
		return workbookStatus;
	}

	/**
	 * Logs an issue with a specific sheet in the Excel workbook. This method prints
	 * an error message to the standard error stream and logs the same error message
	 * using the configured logging framework.
	 * 
	 * @param workbookPath The path of the workbook that contains the sheet with the
	 *                     issue.
	 * @param sheetName    The name of the sheet that has the issue.
	 */
	private void logSheetIssue(String workbookPath, String sheetName) {
		String errorMessage = "Excel " + workbookPath + "- Sheet " + sheetName + " - Has issue";
		System.err.println(errorMessage);
		log.error(errorMessage);
	}

	/**
	 * Loads step parameters from a specific row in the Excel sheet and returns them
	 * as a list. Each cell value in the row is retrieved, trimmed, and added to the
	 * list if it is not empty.
	 * 
	 * @param sheet The sheet from which to load step parameters.
	 * @param rowNo The row number from which to load step parameters.
	 * @return A list containing the step parameters extracted from the specified
	 *         row in the Excel sheet.
	 */
	private List<String> loadStepParameters(Sheet sheet, int rowNo) {
		Row row = sheet.getRow(rowNo);
		int cellCount = row.getLastCellNum();
		List<String> stepParameters = new ArrayList<>();

		DataFormatter dataFormatter = new DataFormatter();
		for (int col = 0; col < cellCount; col++) {
			Cell cell = row.getCell(col);
			String data = dataFormatter.formatCellValue(cell).trim();
			if (!data.isEmpty()) {
				stepParameters.add(data);
			}
		}

		return stepParameters;
	}

	/**
	 * Marks the cell in the specified row of the given sheet based on the provided
	 * method status. If the methodStatus is false, indicating an issue with the
	 * method, the cell is marked with a red background and a log error message is
	 * generated. If the methodStatus is true, indicating a successful method
	 * validation, the cell is not marked and no log message is generated.
	 *
	 * @param workbook     The workbook containing the sheet to mark.
	 * @param sheet        The sheet in which the cell needs to be marked.
	 * @param row          The row number of the cell to be marked.
	 * @param methodStatus The status of the method validation (true if successful,
	 *                     false if an issue is found).
	 */
	private void markCell(Workbook workbook, Sheet sheet, int row, boolean methodStatus) {
		Cell cell = sheet.getRow(row).getCell(0);
		cell.removeCellComment();

		CellStyle redCellStyle = null;
		if (redCellStyle == null) {
			redCellStyle = createRedCellStyle(workbook);
		}

		if (!methodStatus) {
			String errorMessage = sheet.getSheetName() + " - " + cell.getStringCellValue() + " has an issue";
			System.err.println(errorMessage);
			log.error(errorMessage);
			cell.setCellStyle(redCellStyle);
		}
	}

	/**
	 * Creates and returns a CellStyle with a red background for marking cells with
	 * issues.
	 *
	 * @param workbook The workbook to create the CellStyle for.
	 * @return The created CellStyle with a red background.
	 */
	private CellStyle createRedCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.RED.index);
		cellStyle.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		return cellStyle;
	}

	ExecutionMethodContainer methodContainer = new ExecutionMethodContainer();

	/**
	 * Loads the execution methods by finding the corresponding Java method based on
	 * the provided step parameters and adds them to the ExecutionMethodContainer.
	 * The method name and arguments are extracted from the stepParameters list, and
	 * the method is located using reflection from the declaredMethods array. The
	 * found method, along with its arguments, is then added to the
	 * ExecutionMethodContainer for later execution during test automation. If the
	 * method corresponding to the provided step parameters is not found in the
	 * declaredMethods array, an error message is printed, and the method is not
	 * added.
	 *
	 * @param declaredMethods The array of declared methods in the test automation
	 *                        class.
	 * @param stepParameters  The list containing the method name and its arguments
	 *                        as extracted from the test automation Excel sheet.
	 */
	private void loadExecutionMethods(Method[] declaredMethods, List<String> stepParameters) {
		int argumentLength = stepParameters.size() - 1;
		Method method = findMethod(stepParameters.get(0), declaredMethods, argumentLength);
		if (method == null) {
			// Method not found, handle the error gracefully
			System.err.println("Method not found: " + stepParameters.get(0));
			return;
		}

		// Prepare the arguments (excluding the method name)
		List<String> argumentsList = stepParameters.subList(1, stepParameters.size());

		methodContainer.addExecutionMethod(method, argumentsList);
	}

	/**
	 * Find the corresponding method in the array of existing methods based on the
	 * method name and the number of required arguments.
	 * 
	 * @param methodName     The name of the method to find.
	 * @param methods        The array of existing methods to search through.
	 * @param argumentLength The number of arguments required by the method.
	 * @return The Method object if found, or null if not found.
	 */
	private static Method findMethod(String methodName, Method[] methods, int argumentLength) {
		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == argumentLength) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Write step parameters to the Excel sheet based on the method name.
	 * 
	 * @param sheet          The Excel sheet to write data into.
	 * @param row            The row number to write the data.
	 * @param stepParameters The list of step parameters, with the method name at
	 *                       index 0.
	 */
	private void writeExcel(Sheet sheet, int row, List<String> stepParameters) {
		String methodName = stepParameters.get(0).toLowerCase();
		Map<String, Object[]> methodDataMap = new HashMap<>();

		// Define the mappings for each method.
		methodDataMap.put("clickbutton",
				new Object[] { "click", stepParameters.get(1), "BUTTON", stepParameters.get(2) });
		methodDataMap.put("clicktoolbar",
				new Object[] { "click", stepParameters.get(1), "TOOLBAR", stepParameters.get(2) });
		// Add more mappings for other methods here.

		Object[] methodData = methodDataMap.get(methodName);
		if (methodData != null) {
			// Write the method data to the row in the Excel sheet.
			for (int i = 0; i < methodData.length; i++) {
				sheet.getRow(row).createCell(i).setCellValue(String.valueOf(methodData[i]));
			}

			// Clear remaining cells in the row if there are any.
			for (int i = methodData.length; i < 6; i++) {
				sheet.getRow(row).createCell(i).setCellType(Cell.CELL_TYPE_BLANK);
			}
		} else {
			System.err.println("Invalid method name: " + methodName);
		}
	}

	/**
	 * Create the main Excel workbook with two sheets: "workbooks" and "Settings".
	 * The "workbooks" sheet contains a header "Workbook Path" to store paths to
	 * other workbooks. The "Settings" sheet contains key-value pairs for various
	 * settings required by the application.
	 * 
	 * @param filePath The file path where the main Excel workbook will be created.
	 * @return The XSSFWorkbook object representing the created workbook.
	 */
	private static XSSFWorkbook createMainExcel(String filePath) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		CellStyle cellstyle = workbook.createCellStyle();
		cellstyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		cellstyle.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());

		// Create the "workbooks" sheet
		XSSFSheet sheet1 = workbook.createSheet("workbooks");
		Row row1 = sheet1.createRow(0);
		Cell cell1 = row1.createCell(0);
		cell1.setCellStyle(cellstyle);
		cell1.setCellValue("Workbook Path");

		// Create the "Settings" sheet
		XSSFSheet sheet2 = workbook.createSheet("Settings");
		String[] headers = { "Key", "Value" };
		Row row2 = sheet2.createRow(0);
		for (int col = 0; col < headers.length; col++) {
			Cell cell = row2.createCell(col);
			cell.setCellStyle(cellstyle);
			cell.setCellValue(headers[col]);
		}

		String[] settings = { "MAIL_ID", "MAIL_PASSWORD", "BUILD_NO", "HOTFIX_ID", "PATCH_INFO", "BUILD_BY",
				"REPORT_TITLE", "REPORT_NAME", "REPO_PATH", "EPIPLEX500_PATH", "FIND_WAIT", "MAX_WAIT",
				"PROGRAM_DATA_PATH", "SCALE" };
		for (int r = 0; r < settings.length; r++) {
			Row dataRow = sheet2.createRow(r + 1);
			Cell keyCell = dataRow.createCell(0);
			keyCell.setCellStyle(cellstyle);
			keyCell.setCellValue(settings[r]);

			Cell valueCell = dataRow.createCell(1);
			valueCell.setCellStyle(cellstyle);
			valueCell.setCellValue("Enter data");
		}

		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}
}
