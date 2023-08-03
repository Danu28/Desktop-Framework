package epiplex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import actions.ElementFinder;
import core.Application;
import core.Driver;

public class RemoteClient {
	private final Logger log = LogManager.getLogger(RemoteClient.class);

	private final int waitTime = 60;
	private final Driver driver;
	private final ElementFinder finder;

	public RemoteClient() {
		this.driver = new Driver();
		this.finder = new ElementFinder(driver);
	}

	/**
	 * Starts the remote capture.
	 *
	 * @param path The path to launch the application.
	 */
	public void startRecording(String path) {
		log.info("RC start initiated...");
		Application.launchApplication(path);

		finder.waitToDisplay("name", "BUTTON", "Remote Client", waitTime);
		finder.getElement("name", "BUTTON", "Remote Client", waitTime).rightClick();
		finder.getElement("name", "MENUITEM", "Start Recording", waitTime).click();
		finder.waitToDisplay("partialName", "BUTTON", "Capture in progress", waitTime);
		log.info("RC Started...");
	}

	/**
	 * Stops the remote capture.
	 */
	public void stopRecording() {
		log.info("RC Stop initiated...");
		boolean status = finder.waitToDisplay("partialName", "BUTTON", "Capture in progress", waitTime);
		if (status) {
			finder.getElement("partialName", "BUTTON", "Remote Client", waitTime).rightClick();
			finder.getElement("name", "MENUITEM", "Stop Recording", waitTime).click();
			finder.waitToVanish("partialName", "BUTTON", "Capture in progress", waitTime);
			finder.getElement("name", "BUTTON", "Remote Client", waitTime).rightClick();
			finder.getElement("name", "MENUITEM", "Exit", waitTime).click();
		}
		log.info("RC Stop completed...");
	}
}