package core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sikuli.script.App;

import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.pattern.PatternNotFoundException;
import mmarquee.automation.pattern.Window;
import utils.Timer;

/**
 * This class consists of application launch and close methods. It provides
 * functionalities to launch and close applications by name, maximize windows,
 * check if an application is closed, and close all running applications.
 * 
 * The class also maintains a list of application paths and maps normalized
 * application names to their actual paths. It uses SikuliX library for window
 * automation.
 * 
 * @author Dhanush
 *
 */
public class Application {

	// A list to maintain application paths that are launched
	public static List<String> applicationPaths = new ArrayList<>();

	// A map to map normalized application names to their actual paths
	public static HashMap<String, String> apps = new HashMap<>();

	// Instance of the Driver class for window automation
	static Driver driver = new Driver();

	private static final Logger log = LogManager.getLogger(Application.class);

	/***
	 * Launch an application with the given app name.
	 * 
	 * @param applicationName The name of the application to be launched.
	 * @throws AutomationException      If there is an issue with window automation.
	 * @throws PatternNotFoundException If a required pattern for window automation
	 *                                  is not found.
	 */
	public static void launchApplication(String applicationName) {
		try {
			applicationName = applicationName.toUpperCase();
			String normalizedAppName = apps.get(applicationName);
			log.info(normalizedAppName + " is launching...");

			App application = new App(normalizedAppName);
			if (applicationName.contains("MSEDGE")) {
				open(normalizedAppName);
			} else if (!application.isRunning()) {
				application.open();
			}

			Timer.waitTime(2000);
			application.focus();
			String title = application.getTitle().trim();
			maximizeWindow(title);
			applicationPaths.add(normalizedAppName);
			log.info(normalizedAppName + " is launched");
		} catch (Exception e) {
			log.error("Error launching application", e);
		}
	}

	// Open an application using Desktop class (mainly for Microsoft Edge)
	private static void open(String cmd) {
		cmd = cmd.trim();
		try {
			Desktop.getDesktop().open(new File(cmd));
		} catch (IOException e) {
			log.error(cmd + " got an error in exec");
		}
	}

	// Maximize a window with the given title using the SikuliX library
	private static void maximizeWindow(String title) {
		title = title.trim();
		Element element = driver.getWindow(title);
		if (element == null) {
			element = driver.getPane(title);
		}
		try {
			Window win = new Window(element);
			if (win.getCanMaximize()) {
				win.maximize();
			}
		} catch (AutomationException | NullPointerException e) {
			log.error("Error maximizing window", e);
		}
	}

	/***
	 * Close an application with the given app name.
	 * 
	 * @param applicationName The name of the application to be closed.
	 */
	public static void closeApplication(String applicationName) {
		try {
			log.info(applicationName + " is closing...");

			String normalizedAppName = apps.get(applicationName.toUpperCase());

			App application = new App(normalizedAppName);
			application.close();

			if (!normalizedAppName.contains("exe")) {
				cleanupApplication(normalizedAppName);
			}

			if (application.isClosing()) {
				Timer.waitTime(1000);
			}

			log.info(applicationName + " is closed.");
		} catch (Exception e) {
			log.error("Error closing application", e);
		}
	}

	// Close all applications that were launched
	public static void closeAllApps() {
		log.info("closeAllApps is started...");
		for (String path : applicationPaths) {
			try {
				App.close(path);
				log.info("Application with path " + path + " is closed.");
			} catch (Exception e) {
				log.error("Error closing application with path: " + path, e);
			}
		}
		log.info("closeAllApps is completed...");
	}

	/**
	 * Check if an application with the given path is closed or not.
	 * 
	 * @param applicationPath The path of the application to check.
	 * @return True if the application is closed, false otherwise.
	 */
	public static boolean isClosed(String applicationPath) {
		String normalizedPath = apps.get(applicationPath.toUpperCase());
		if (normalizedPath == null) {
			throw new IllegalArgumentException("Unknown application name: " + applicationPath);
		}

		App application = new App(normalizedPath);
		return !application.isRunning();
	}

	// Cleanup application by closing its window with the given title using SikuliX
	private static void cleanupApplication(String applicationPath) {
		App application = new App(applicationPath);
		String title = application.getTitle().trim();
		log.info("Closing application with title: " + title);

		Element element = driver.getWindow(title);
		if (element == null) {
			element = driver.getPane(title);
		}

		if (element != null) {
			try {
				Window win = new Window(element);
				win.close();
				log.info("Application with title " + title + " is closed.");
			} catch (AutomationException | NullPointerException e) {
				log.error("Error closing application with title: " + title, e);
			}
		} else {
			log.warn("Application with title " + title + " not found.");
		}
	}
}