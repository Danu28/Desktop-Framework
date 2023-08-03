package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Timer {

	private static long start;
	private static String startTime;
	private static int rowNo = 1;
	private static final String SHEET_NAME = "Performance";
//	private static final String[] HEADERS = { "Document Type", "Start Time", "End Time", "Duration in Seconds" };
	private static final ExcelLibrary excelLib = new ExcelLibrary();

	/**
	 * This method will wait for the given time duration (in milliseconds).
	 *
	 * @param time The time duration to wait in milliseconds.
	 */
	public static void waitTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * This method will return the current time in HH:mm:ss format.
	 *
	 * @return The formatted current time.
	 */
	private static String getTimeFormat() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		return dtf.format(LocalDateTime.now());
	}

	
	/**
	 * This method will return the current time, acting as the start button of the
	 * stopwatch.
	 *
	 * @return The formatted start time.
	 */
	public static void startTimer() {
		start = System.currentTimeMillis();
		startTime = getTimeFormat();
	}

	/**
	 * This method will get the current time and log test name, start time, end
	 * time, and duration.
	 *
	 * @param testName The name of the test.
	 * @return The formatted stop time.
	 */
	public static String stopTimer(String testName) {
		String currentTime = getTimeFormat();
		long stop = System.currentTimeMillis();
		long timeTaken = (stop - start) / 1000;
		String duration = String.valueOf(timeTaken);

		writeExcelPerformance(testName, startTime, currentTime, duration);
		rowNo++;
		return getTimeFormat();
	}

	/**
	 * This method will write data to the performance report Excel.
	 *
	 * @param testName The name of the test.
	 * @param start    The start time of the test.
	 * @param stop     The stop time of the test.
	 * @param time     The duration of the test in seconds.
	 */
	public static void writeExcelPerformance(String testName, String start, String stop, String time) {
		String filePath = ProjectConfiguration.performaceReportExcelPath;
		Workbook workbook = null;
		try {
//			if (!new File(filePath).exists()) {
//				excelLib.createExcelWithSheetAndHeader(SHEET_NAME, HEADERS);
//				excelLib.saveWorkbook(filePath);
//				excelLib.closeWorkbook();
//			}

			workbook = excelLib.getWorkbook(filePath);
			Sheet sheet = workbook.getSheet(SHEET_NAME);
			Row row = sheet.createRow(rowNo);
			CellStyle cellStyle = excelLib.createDataCellStyle(workbook);

			Cell cell0 = row.createCell(0);
			cell0.setCellStyle(cellStyle);
			cell0.setCellValue(testName);

			Cell cell1 = row.createCell(1);
			cell1.setCellStyle(cellStyle);
			cell1.setCellValue(start);

			Cell cell2 = row.createCell(2);
			cell2.setCellStyle(cellStyle);
			cell2.setCellValue(stop);

			Cell cell3 = row.createCell(3);
			cell3.setCellStyle(cellStyle);
			cell3.setCellValue(time);

			try (FileOutputStream fos = new FileOutputStream(filePath)) {
				workbook.write(fos);
				workbook.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}