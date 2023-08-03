package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The ProjectSetupManager class handles the setup process for a project.
 */
public class ProjectSetupManager {
	private static final Logger log = LogManager.getLogger(ProjectSetupManager.class);
	private List<String> workbookPaths;
	private ProjectConfiguration config;
	private ExecutionUtils executionUtil;

	public ProjectSetupManager(ProjectConfiguration config, ExecutionUtils executionUtil) {
		this.config = config;
		this.executionUtil = executionUtil;
	}

	/**
	 * Creates the project setup by executing various actions.
	 *
	 * @return true if the project setup is successful; otherwise, false.
	 */
	public boolean setupProject() {
		log.info("createProjectSetup started...");

		if (!loadSettings()) {
			log.error("Failed to load settings. Please check.");
			return false;
		}

		if (!configureReport()) {
			log.error("Failed to configure report settings. Please check.");
			return false;
		}

		if (!fetchExecutionWorkbooks()) {
			log.error("Failed to fetch execution workbooks. Please check.");
			return false;
		}

		if (!copyPreferencesFiles()) {
			log.error("Failed to copy and paste Preferences files. Please check.");
			return false;
		}

		if (!config.checkFiles()) {
			log.error("files missing in specific folders. Please check.");
			return false;
		}

		if (!config.createMainSheet()) {
			log.error("main.xlsx creation failed. Please check.");
			return false;
		}

		log.info("createProjectSetup completed.");
		return true;
	}

	/**
	 * Creates the folder structure required for the project.
	 *
	 * @return true if the folder structure is created successfully; otherwise,
	 *         false.
	 */
	public boolean createFolderStructure() {
		try {
			boolean status = config.folderConfig();
			return status;
		} catch (Exception e) {
			log.error("An error occurred during folder structure creation.", e);
			return false;
		}
	}

	/**
	 * Loads settings for the project execution.
	 *
	 * @return true if settings are loaded successfully; otherwise, false.
	 */
	private boolean loadSettings() {
		// Return true if successful, false otherwise.
		return executionUtil.loadSettings();
	}

	/**
	 * Configures the report settings for the project.
	 *
	 * @return true if report settings are configured successfully; otherwise,
	 *         false.
	 */
	private boolean configureReport() {
		try {
			// Implement your code for report configuration here.
			config.reportConfig();
			return true;
		} catch (Exception e) {
			log.error("An error occurred during report configuration.", e);
			return false;
		}
	}

	/**
	 * Fetches the execution workbooks for the project.
	 *
	 * @return true if execution workbooks are fetched successfully; otherwise,
	 *         false.
	 */
	private boolean fetchExecutionWorkbooks() {
		try {
			// Implement your code for fetching execution workbooks here.
			workbookPaths = executionUtil.getExecutionWorkbooks();
			return true;
		} catch (Exception e) {
			log.error("An error occurred while fetching execution workbooks.", e);
			return false;
		}
	}

	/**
	 * Copies and pastes the Preferences files to the appropriate destination.
	 *
	 * @return true if Preferences files are copied and pasted successfully;
	 *         otherwise, false.
	 */
	private boolean copyPreferencesFiles() {
		try {
			Path srcPref = Paths.get(ProjectConfiguration.baseFilesPath, "Preferences");
			Path destPref = Paths.get(Settings.REPO_PATH, "Preferences");
			copyPasteFiles(srcPref, destPref);
			return true;
		} catch (Exception e) {
			log.error("An error occurred while copying and pasting Preferences files.", e);
			return false;
		}
	}

	/**
	 * Helper method to copy and paste files from source to destination.
	 *
	 * @param source      The source path of the file.
	 * @param destination The destination path where the file will be copied.
	 * @throws IOException if an I/O error occurs during file copying.
	 */
	private void copyPasteFiles(Path source, Path destination) throws IOException {
		// Create the destination directory if it doesn't exist.
		if (!Files.exists(destination)) {
			Files.createDirectories(destination);
		}

		// If the source is a directory, recursively copy its contents.
		if (Files.isDirectory(source)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
				for (Path entry : stream) {
					copyPasteFiles(entry, destination);
				}
			}
		} else {
			// If the source is a file, copy it to the destination.
			Files.copy(source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public List<String> getWorkbookPaths() {
		return workbookPaths;
	}
}
