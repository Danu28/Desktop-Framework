package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class to work with Excel files.
 */
public class ExcelLibrary {

	private static final Logger log = LogManager.getLogger(ExcelLibrary.class);
	
	private Workbook workbook;

    public ExcelLibrary() {
        this.workbook = new XSSFWorkbook();
    }

	/**
	 * Creates and returns a custom cell style with the specified indexed color for
	 * the font.
	 *
	 * @param workbook     The Workbook object.
	 * @param indexedColor The indexed color code to set for the font.
	 * @return The custom CellStyle with the specified indexed color for the font.
	 */
	protected CellStyle getCellStyle(Workbook workbook, short indexedColor) {
		Font font = workbook.createFont();
		font.setColor(indexedColor);
		font.setBold(true);
		font.setItalic(true);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBorderTop((short) 1);
		cellStyle.setBorderLeft((short) 1);
		cellStyle.setBorderRight((short) 1);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * Reads the value of the cell in the specified row and column of the given
	 * sheet.
	 *
	 * @param sheet  The Sheet object.
	 * @param rowNum The row number (0-based) of the cell.
	 * @param colNum The column number (0-based) of the cell.
	 * @return The value of the cell as a String, or null if the cell is empty.
	 */
	public String readCell(Sheet sheet, int rowNum, int colNum) {
		String cellValue = null;
		Row row = sheet.getRow(rowNum);
		if (row != null) {
			Cell cell = row.getCell(colNum);
			if (cell != null) {
				DataFormatter dataFormatter = new DataFormatter();
				String data = dataFormatter.formatCellValue(cell);
				if (!data.isEmpty())
					cellValue = data.trim();
			}
		}
		return cellValue;
	}

	/**
	 * Gets the number of columns in the specified row of the given sheet.
	 *
	 * @param sheet The Sheet object.
	 * @param rowNo The row number (0-based).
	 * @return The number of columns in the specified row.
	 */
	public int getColumns(Sheet sheet, int rowNo) {
		Row row = sheet.getRow(rowNo);
		return row.getLastCellNum();
	}

	/**
	 * Gets the number of rows in the given sheet.
	 *
	 * @param sheet The Sheet object.
	 * @return The number of rows in the sheet.
	 */
	public int getRows(Sheet sheet) {
		return sheet.getLastRowNum();
	}

	/**
	 * Loads a list of Excel file paths from the specified folder.
	 *
	 * @param excelFolderPath The path of the folder containing Excel files.
	 * @return A List of String representing the absolute file paths of Excel files
	 *         in the folder.
	 */
	public List<String> loadExcelFiles(String excelFolderPath) {
		List<String> workbookPaths = new ArrayList<>();
		File folder = new File(excelFolderPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile() && isExcelFile(file.getName())) {
					workbookPaths.add(file.getAbsolutePath());
				}
			}
		}

		return workbookPaths;
	}

	/**
	 * Checks if the given file name has an Excel file extension.
	 *
	 * @param fileName The name of the file to check.
	 * @return true if the file has an Excel file extension (either .xlsx or .xls),
	 *         otherwise false.
	 */
	private boolean isExcelFile(String fileName) {
		return fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls");
	}

	/**
	 * Gets a list of sheets present in the given workbook based on the "Index"
	 * sheet.
	 *
	 * @param workbook The Workbook object from which to retrieve sheets.
	 * @return A List of Sheet objects that are marked as "YES" in the "Index"
	 *         sheet.
	 */
	public List<Sheet> getSheetsInWorkbook(Workbook workbook) {
		List<Sheet> sheets = new ArrayList<>();
		Sheet indexSheet = workbook.getSheet("Index");
		int rowCount = indexSheet.getPhysicalNumberOfRows();
		for (int rowNo = 1; rowNo < rowCount; rowNo++) {
			String sheetStatus = indexSheet.getRow(rowNo).getCell(1).getStringCellValue();
			if (sheetStatus != null && sheetStatus.equalsIgnoreCase("YES")) {
				String sheetName = indexSheet.getRow(rowNo).getCell(0).getStringCellValue();
				sheets.add(workbook.getSheet(sheetName));
			}
		}
		return sheets;
	}

	/**
	 * Reads and returns a Workbook object from the given workbook file path.
	 *
	 * @param workbookPath The file path of the workbook to read.
	 * @return The Workbook object read from the file path, or null if an exception
	 *         occurs.
	 */
	public Workbook getWorkbook(String workbookPath) {
		Workbook workbook = null;
		try (FileInputStream fis = new FileInputStream(new File(workbookPath))) {
			workbook = WorkbookFactory.create(fis);
		} catch (IOException | InvalidFormatException e) {
			log.error("Exception in getWorkbook: " + e.getMessage());
		}
		return workbook;
	}

	/**
	 * Writes the given Workbook object to the specified workbook file path.
	 *
	 * @param workbook     The Workbook object to write.
	 * @param workbookPath The file path to which the Workbook should be written.
	 */
	protected void writeWorkbook(Workbook workbook, String workbookPath) {
		try (FileOutputStream fos = new FileOutputStream(workbookPath)) {
			workbook.write(fos);
		} catch (IOException e) {
			log.error("Exception in writeWorkbook: " + e.getMessage());
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				log.error("Exception while closing workbook: " + e.getMessage());
			}
		}
		log.info(workbookPath + " - file " + " created successfully");
	}

//	public void createExcelWithSheetAndHeader(String filePath, String sheetName, String[] headers)
//			throws IOException {
//		// Create a new Workbook
//		Workbook workbook = new XSSFWorkbook();
//
//		// Create a new Sheet
//		Sheet sheet = workbook.createSheet(sheetName);
//
//		// Apply cell styles for header
//		CellStyle headerCellStyle = createHeaderCellStyle(workbook);
//
//		// Create header row
//		Row headerRow = sheet.createRow(0);
//		for (int i = 0; i < headers.length; i++) {
//			Cell cell = headerRow.createCell(i);
//			cell.setCellValue(headers[i]);
//			cell.setCellStyle(headerCellStyle);
//		}
//
//		// Adjust column widths to fit the content
//		for (int i = 0; i < headers.length; i++) {
//			sheet.autoSizeColumn(i);
//		}
//
//		// Write the workbook to the file
//		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
//			workbook.write(fileOut);
//		}
//		catch (Exception e) {
//			log.error(filePath + " - file generated with sheet" + sheetName + " creation failed");
//		} finally {
//			// Close the workbook to release resources
//			workbook.close();
//		}
//		log.info(filePath + " - file generated with sheet" + sheetName + " created successfully");
//
//	}
//	
//	public void writeDataToExcel(String filePath, String sheetName, String[][] data) throws IOException {
//	    if (data == null || data.length == 0) {
//	        log.error("No data provided. Skipping Excel generation for sheet: " + sheetName);
//	        return;
//	    }
//
//	    Workbook workbook = getWorkbook(filePath);
//	    if (workbook == null) {
//	        return;
//	    }
//
//	    try {
//	        Sheet sheet = workbook.getSheet(sheetName);
//
//	        // Apply cell styles for data
//	        CellStyle dataCellStyle = createDataCellStyle(workbook);
//
//	        // Populate the Sheet with the data
//	        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
//	            Row dataRow = sheet.createRow(rowIndex + 1);
//	            for (int cellIndex = 0; cellIndex < data[rowIndex].length; cellIndex++) {
//	                Cell cell = dataRow.createCell(cellIndex);
//	                cell.setCellValue(data[rowIndex][cellIndex]);
//	                cell.setCellStyle(dataCellStyle);
//	            }
//	        }
//
//	        // Adjust column widths to fit the content
//	        for (int i = 0; i < data[0].length; i++) {
//	            sheet.autoSizeColumn(i);
//	        }
//
//	        // Write the workbook to the file
//	        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
//	            workbook.write(fileOut);
//	        }
//	        log.info(filePath + " - file generated with sheet " + sheetName + " data added successfully");
//	    } catch (Exception e) {
//	        log.error(filePath + " - file generated with sheet " + sheetName + " data added failed", e);
//	    } finally {
//	        // Close the workbook to release resources
//	        workbook.close();
//	    }
//	}

	/**
	 * Creates a new Excel workbook with a single sheet containing the specified
	 * headers. The sheet will be styled with a header cell style that has a bold
	 * font, blue-grey background, and thin border around each cell.
	 *
	 * @param filePath  The path of the output Excel file to be created.
	 * @param sheetName The name of the sheet to be created.
	 * @param headers   The array of header strings to be added as the first row of
	 *                  the sheet.
	 * @return
	 * @throws IOException If an I/O error occurs while creating the Excel file.
	 */
	public boolean createExcelWithSheetAndHeader(String sheetName, String[] headers) {
		
        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerCellStyle = createHeaderCellStyle(workbook);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Adjust column widths to fit the content
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return true;
    }

	/**
	 * Writes the provided data to the specified sheet in an existing Excel
	 * workbook. The data will be styled with a cell style that has a black font and
	 * a thin border around each cell. Column widths will be adjusted to fit the
	 * content.
	 *
	 * @param filePath  The path of the output Excel file containing the workbook.
	 * @param sheetName The name of the sheet to which the data will be written.
	 * @param data      The 2D array of data to be added to the sheet.
	 * @return
	 * @throws IOException If an I/O error occurs while writing the data to the
	 *                     Excel file.
	 */
	public boolean writeDataToExcel(String sheetName, String[][] data) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return false; // Sheet not found
        }

        CellStyle dataCellStyle = createDataCellStyle(workbook);

        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            Row dataRow = sheet.createRow(rowIndex + 1);
            for (int cellIndex = 0; cellIndex < data[rowIndex].length; cellIndex++) {
                Cell cell = dataRow.createCell(cellIndex);
                cell.setCellValue(data[rowIndex][cellIndex]);
                cell.setCellStyle(dataCellStyle);
            }
        }

        // Adjust column widths to fit the content
        for (int i = 0; i < data[0].length; i++) {
            sheet.autoSizeColumn(i);
        }

        return true;
    }
	
	 // Method to save the workbook to the file
    public void saveWorkbook(String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
    }

    // Method to close the workbook and release resources
    public void closeWorkbook() throws IOException {
        workbook.close();
    }

	/**
	 * Creates a CellStyle for the header cells with the specified font and cell
	 * border style.
	 *
	 * @param workbook The Workbook instance used to create the CellStyle.
	 * @return The CellStyle for header cells with the specified style settings.
	 */
	private CellStyle createHeaderCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
		style.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		style.setAlignment((short) HorizontalAlignment.CENTER.ordinal());
		style.setVerticalAlignment((short) VerticalAlignment.CENTER.ordinal());
		style.setBorderTop((short) BorderStyle.THIN.ordinal());
		style.setBorderBottom((short) BorderStyle.THIN.ordinal());
		style.setBorderLeft((short) BorderStyle.THIN.ordinal());
		style.setBorderRight((short) BorderStyle.THIN.ordinal());
		return style;
	}

	/**
	 * Creates a CellStyle for the data cells with the specified font and cell
	 * border style.
	 *
	 * @param workbook The Workbook instance used to create the CellStyle.
	 * @return The CellStyle for data cells with the specified style settings.
	 */
	public CellStyle createDataCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		style.setFont(font);
		style.setVerticalAlignment((short) VerticalAlignment.CENTER.ordinal());
		style.setBorderTop((short) BorderStyle.THIN.ordinal());
		style.setBorderBottom((short) BorderStyle.THIN.ordinal());
		style.setBorderLeft((short) BorderStyle.THIN.ordinal());
		style.setBorderRight((short) BorderStyle.THIN.ordinal());
		return style;
	}

}