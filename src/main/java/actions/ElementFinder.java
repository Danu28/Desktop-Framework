package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sikuli.script.FindFailed;

import core.Controls;
import core.Driver;
import core.SikuliElement;
import core.UIElement;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.pattern.Window;

/**
 * This class consists of all element finding methods
 * 
 * @author Dhanush
 *
 */
/**
 * The ElementFinder class is responsible for mapping human-readable locator
 * types to their corresponding internal locator types. It provides a mapping of
 * strings to LocatorType enum values for easy lookup. The class also stores a
 * static reference to the Driver instance to be used for locating elements.
 */
public class ElementFinder {

	private static final Logger log = LogManager.getLogger(ElementFinder.class);
	private static HashMap<String, LocatorType> locators = new HashMap<>();
	public static Driver driver;

	/**
	 * Constructor for the ElementFinder class.
	 *
	 * @param driver The Driver instance to be used for locating elements.
	 */
	public ElementFinder(Driver driver) {
		ElementFinder.driver = driver;
	}

	/**
	 * Static initializer block to populate the locators HashMap with human-readable
	 * locator types mapped to their corresponding LocatorType enum values. The
	 * supported locator types are NAME, ID, TEXT, PARTIALNAME, PARTIALID,
	 * PARTIALTEXT, PARTIALVALUE, IMAGE, LOCATION, and OCR.
	 */
	static {
		locators.put("NAME", LocatorType.NAME);
		locators.put("ID", LocatorType.ID);
		locators.put("TEXT", LocatorType.TEXT);
		locators.put("PARTIALNAME", LocatorType.PARTIALNAME);
		locators.put("PARTIALID", LocatorType.PARTIALID);
		locators.put("PARTIALTEXT", LocatorType.PARTIALTEXT);
		locators.put("PARTIALVALUE", LocatorType.PARTIALVALUE);
		locators.put("IMAGE", LocatorType.IMAGE);
		locators.put("LOCATION", LocatorType.LOCATION);
		locators.put("OCR", LocatorType.OCR);
	}

	/**
	 * This method maximizes the window with the specified title.
	 * 
	 * @param windowTitle The title of the window to maximize.
	 * @param findWait    The time to wait for the window to be found, in
	 *                    milliseconds.
	 * @throws RuntimeException If any error occurs during maximizing the window.
	 */
	public void maximizeWindow(String windowTitle, long findWait) throws RuntimeException {
		Element element = getWindow(windowTitle, findWait);
		try {
			new Window(element).maximize();
		} catch (AutomationException e) {
			throw new RuntimeException("Error while maximizing the window: " + e.getMessage());
		}
	}

	/**
	 * This method maximizes the pane with the specified title.
	 * 
	 * @param paneTitle The title of the pane to maximize.
	 * @param findWait  The time to wait for the pane to be found, in milliseconds.
	 * @throws RuntimeException If any error occurs during maximizing the pane.
	 */
	public void maximizePane(String paneTitle, long findWait) throws RuntimeException {
		Element element = getPane(paneTitle, findWait);
		try {
			new Window(element).maximize();
		} catch (AutomationException e) {
			throw new RuntimeException("Error while maximizing the pane: " + e.getMessage());
		}
	}

	/**
	 * This method closes the window with the specified title.
	 * 
	 * @param windowTitle The title of the window to close.
	 * @param findWait    The time to wait for the window to be found, in
	 *                    milliseconds.
	 * @throws RuntimeException If any error occurs during closing the window.
	 */
	public void closeWindow(String windowTitle, long findWait) throws RuntimeException {
		Element element = getWindow(windowTitle, findWait);
		try {
			new Window(element).close();
		} catch (AutomationException e) {
			throw new RuntimeException("Error while closing the window: " + e.getMessage());
		}
	}

	/**
	 * This method closes the pane with the specified title.
	 * 
	 * @param paneTitle The title of the pane to close.
	 * @param findWait  The time to wait for the pane to be found, in milliseconds.
	 * @throws RuntimeException If any error occurs during closing the pane.
	 */
	public void closePane(String paneTitle, long findWait) throws RuntimeException {
		Element element = getPane(paneTitle, findWait);
		try {
			new Window(element).close();
		} catch (AutomationException e) {
			throw new RuntimeException("Error while closing the pane: " + e.getMessage());
		}
	}

	// HashMap to store already found windows and panes to avoid redundant searches
	private Map<String, Element> windowList = new HashMap<>();
	private Map<String, Element> panesList = new HashMap<>();

	/**
	 * Retrieves the Element representing the specified window with the given title
	 * within the specified duration.
	 *
	 * @param windowTitle The title of the window to find.
	 * @param duration    The maximum duration, in seconds, to wait for the window
	 *                    to be found.
	 * @return The Element representing the window if found, or null if the window
	 *         is not found within the specified duration.
	 */
	public Element getWindow(String windowTitle, long duration) {
		Element element = checkWindowList(windowTitle);
		if (element != null)
			return element;

		long start = System.currentTimeMillis();
		long durationMillis = 1000 * duration;

		while (System.currentTimeMillis() - start <= durationMillis) {
			element = ElementFinder.driver.getWindow(windowTitle);
			if (element != null) {
				windowList.put(windowTitle, element);
				return element;
			}
		}

		System.err.println(String.format(windowTitle + " not found within %s s", duration));
		return null;
	}

	/**
	 * Retrieves the Element representing the specified pane with the given title
	 * within the specified duration.
	 *
	 * @param paneTitle The title of the pane to find.
	 * @param duration  The maximum duration, in seconds, to wait for the pane to be
	 *                  found.
	 * @return The Element representing the pane if found, or null if the pane is
	 *         not found within the specified duration.
	 */
	public Element getPane(String paneTitle, long duration) {
		Element element = checkPaneList(paneTitle);
		if (element != null)
			return element;

		long start = System.currentTimeMillis();
		long durationMillis = 1000 * duration;

		while (System.currentTimeMillis() - start <= durationMillis) {
			element = driver.getPane(paneTitle);
			if (element != null) {
				panesList.put(paneTitle, element);
				return element;
			}
		}

		System.err.println(String.format(paneTitle + " not found within %s s", duration));
		return null;
	}

	// Helper methods to check if the window or pane is already found in the
	// respective lists
	private Element checkWindowList(String windowTitle) {
		return windowList.get(windowTitle);
	}

	private Element checkPaneList(String paneTitle) {
		return panesList.get(paneTitle);
	}

	/**
	 * Retrieves the SikuliElement representing the UI element using the given
	 * locator type and parameters, within the specified duration.
	 *
	 * @param locatorType The type of locator to use for finding the UI element
	 *                    (e.g., "NAME", "ID", "TEXT", etc.).
	 * @param parameter1  The first parameter required for the locator (e.g., the
	 *                    name, ID, or text to search for).
	 * @param parameter2  The second parameter (if applicable) required for the
	 *                    locator (e.g., partial text or value).
	 * @param duration    The maximum duration, in seconds, to wait for the UI
	 *                    element to be found.
	 * @return The SikuliElement representing the UI element if found, or null if
	 *         the element is not found within the specified duration.
	 */
	public SikuliElement getSikuliElement(String locatorType, String parameter1, String parameter2, long duration) {
		duration = 1000 * duration;
		long start = System.currentTimeMillis();
		long durationMillis = 1000 * duration;

		while (System.currentTimeMillis() - start <= durationMillis) {
			try {
				return driver.findSikuliElement(locators.get(locatorType.toUpperCase()), parameter1, parameter2);
			} catch (FindFailed e) {
				// Continue to the next iteration if the element is not found.
			}
		}

		System.err.println(String.format(parameter1 + " - " + parameter2 + " not found within %s s", duration));
		log.error(String.format(parameter1 + " - " + parameter2 + " not found within %s s", duration));
		return null;
	}

	/**
	 * Retrieves the UIElement representing the UI control using the given locator
	 * type, control type, and value, within the specified duration.
	 *
	 * @param locatorType The type of locator to use for finding the UI control
	 *                    (e.g., "NAME", "ID", "TEXT", etc.).
	 * @param controlType The type of UI control to search for (e.g., "BUTTON",
	 *                    "TEXTBOX", "LINK", etc.).
	 * @param value       The value or identifier of the UI control to search for
	 *                    (e.g., the name, ID, or text to search for).
	 * @param duration    The maximum duration, in seconds, to wait for the UI
	 *                    control to be found.
	 * @return The UIElement representing the UI control if found, or null if the
	 *         control is not found within the specified duration.
	 */
	public UIElement getUIElement(String locatorType, String controlType, String value, long duration) {
		long durationMillis = 1000 * duration;
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start <= durationMillis) {
			try {
				UIElement element = new UIElement(driver.findElement(locators.get(locatorType.toUpperCase()),
						Controls.getControl(controlType), value));
				return element;
			} catch (NullPointerException | AutomationException e) {
				// Catch specific exceptions only if necessary. Handle or log them accordingly.
			}
		}

		System.err.println(String.format(controlType + " - " + value + " not found within %s s", duration));
		log.error(String.format(controlType + " - " + value + " not found within %s s", duration));
		return null;
	}

	/**
	 * Retrieves a UiElement or SikuliElement based on the provided locator type,
	 * parameters, and duration.
	 *
	 * @param <T>         The type of element to retrieve, which can be either
	 *                    SikuliElement or UIElement.
	 * @param locatorType The type of locator to use for finding the element (e.g.,
	 *                    "NAME", "ID", "TEXT", "IMAGE", "LOCATION", "OCR").
	 * @param parameter1  The first parameter for locating the element, which can be
	 *                    the control type, search area image, or coordinate-X
	 *                    value.
	 * @param parameter2  The second parameter for locating the element, which can
	 *                    be the control value, search element image, or
	 *                    coordinate-Y value.
	 * @param duration    The maximum duration, in seconds, to wait for the element
	 *                    to be found.
	 * @return The retrieved UiElement or SikuliElement if found, or null if the
	 *         element is not found or an exception occurs during retrieval.
	 */
	@SuppressWarnings("unchecked")
	public <T extends SikuliElement> T getElement(String locatorType, String parameter1, String parameter2,
			long duration) {
		locatorType = locatorType.toUpperCase();

		if ("IMAGE".equals(locatorType) || "LOCATION".equals(locatorType) || "OCR".equals(locatorType)) {
			try {
				return (T) getSikuliElement(locatorType, parameter1, parameter2, duration);
			} catch (Exception e) {
				// Handle or log any exceptions related to Sikuli element retrieval.
				e.printStackTrace();
			}
		} else {
			try {
				return (T) getUIElement(locatorType, parameter1, parameter2, duration);
			} catch (Exception e) {
				// Handle or log any exceptions related to UI element retrieval.
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * This method will wait to display the given element
	 * 
	 * @param locatorType
	 * @param parameter1  - control type or search area image or coordinate-X value
	 * @param parameter2  - control value or search element image or coordinate-Y
	 * @param duration
	 * @return
	 */
	public boolean waitToDisplay(String locatorType, String parameter1, String parameter2, long duration) {
		return display(locatorType, parameter1, parameter2, duration);
	}

	/**
	 * This method will wait to vanish the given element
	 * 
	 * @param locatorType
	 * @param parameter1  - control type or search area image or coordinate-X value
	 * @param parameter2  - control value or search element image or coordinate-Y
	 * @param duration
	 * @return
	 */
	public boolean waitToVanish(String locatorType, String parameter1, String parameter2, long duration) {
		return vanish(locatorType, parameter1, parameter2, duration);
	}

	/**
	 * Waits for the element specified by the locator type and parameters to vanish
	 * from the screen within the given duration. The method checks for the
	 * element's presence repeatedly until it vanishes or the specified duration is
	 * exceeded.
	 *
	 * @param locatorType The type of locator to use for finding the element (e.g.,
	 *                    "NAME", "ID", "TEXT", "IMAGE", "LOCATION", "OCR").
	 * @param parameter1  The first parameter for locating the element, which can be
	 *                    the control type, search area image, or coordinate-X
	 *                    value.
	 * @param parameter2  The second parameter for locating the element, which can
	 *                    be the control value, search element image, or
	 *                    coordinate-Y value.
	 * @param duration    The maximum duration, in seconds, to wait for the element
	 *                    to vanish.
	 * @return True if the element vanishes within the specified duration, false
	 *         otherwise.
	 */
	public boolean vanish(String locatorType, String parameter1, String parameter2, long duration) {
		boolean isVanished = false;
		long durationMillis = 1000 * duration;
		long start = System.currentTimeMillis();

		while (!isVanished) {
			try {
				locatorType = locatorType.toUpperCase();
				SikuliElement sikuliElement = null;
				if ("IMAGE".equals(locatorType) || "LOCATION".equals(locatorType) || "OCR".equals(locatorType)) {
					sikuliElement = driver.findSikuliElement(locators.get(locatorType.toUpperCase()), parameter1,
							parameter2);
				} else {
					sikuliElement = new UIElement(driver.findElement(locators.get(locatorType.toUpperCase()),
							Controls.getControl(parameter1), parameter2));
				}

				if (sikuliElement != null && sikuliElement.isVanished()) {
					return true;
				}
			} catch (FindFailed | NullPointerException | AutomationException e) {
				// Handle or log any exceptions appropriately.
				return true;
			}

			if (System.currentTimeMillis() - start > durationMillis) {
				System.err.println(String.format(parameter2 + " not vanished within %s s", duration));
				log.error(String.format(parameter2 + " not vanished within %s s", duration));
				break;
			}
		}
		return false;
	}

	/**
	 * Waits for the element specified by the locator type and parameters to be
	 * displayed on the screen within the given duration. The method checks for the
	 * element's presence and visibility repeatedly until it is displayed or the
	 * specified duration is exceeded.
	 *
	 * @param locatorType The type of locator to use for finding the element (e.g.,
	 *                    "NAME", "ID", "TEXT", "IMAGE", "LOCATION", "OCR").
	 * @param parameter1  The first parameter for locating the element, which can be
	 *                    the control type, search area image, or coordinate-X
	 *                    value.
	 * @param parameter2  The second parameter for locating the element, which can
	 *                    be the control value, search element image, or
	 *                    coordinate-Y value.
	 * @param duration    The maximum duration, in seconds, to wait for the element
	 *                    to be displayed.
	 * @return True if the element is displayed within the specified duration, false
	 *         otherwise.
	 */
	public boolean display(String locatorType, String parameter1, String parameter2, long duration) {
		long durationMillis = 1000 * duration;
		long start = System.currentTimeMillis();

		while (true) {
			try {
				locatorType = locatorType.toUpperCase();
				SikuliElement sikuliElement = null;
				if ("IMAGE".equals(locatorType) || "LOCATION".equals(locatorType) || "OCR".equals(locatorType)) {
					sikuliElement = driver.findSikuliElement(locators.get(locatorType.toUpperCase()), parameter1,
							parameter2);
				} else {
					sikuliElement = new UIElement(driver.findElement(locators.get(locatorType.toUpperCase()),
							Controls.getControl(parameter1), parameter2));
				}

				if (sikuliElement != null && sikuliElement.isDisplayed()) {
					return true;
				}
			} catch (FindFailed | NullPointerException | AutomationException e) {
				// Handle or log any exceptions appropriately.
			}

			if (System.currentTimeMillis() - start > durationMillis) {
				System.err.println(String.format(parameter2 + " not displayed within %s s", duration));
				log.error(String.format(parameter2 + " not displayed within %s s", duration / 1000));
				break;
			}
		}
		return false;
	}

	/**
	 * Waits for the specified element to become enabled within the given duration.
	 * The method checks for the element's presence and whether it is enabled
	 * repeatedly until it is enabled or the specified duration is exceeded.
	 *
	 * @param locatorType The type of locator to use for finding the element (e.g.,
	 *                    "NAME", "ID", "TEXT").
	 * @param controlType The type of the control to use for locating the element
	 *                    (e.g., "BUTTON", "TEXTBOX").
	 * @param value       The value of the element, such as the control value or
	 *                    text to search for.
	 * @param duration    The maximum duration, in seconds, to wait for the element
	 *                    to become enabled.
	 * @return True if the element becomes enabled within the specified duration,
	 *         false otherwise.
	 */
	public boolean waitToEnable(String locatorType, String controlType, String value, long duration) {
		long durationMillis = 1000 * duration;
		long start = System.currentTimeMillis();

		while (true) {
			try {
				Element element = driver.findElement(locators.get(locatorType.toUpperCase()),
						Controls.getControl(controlType), value);

				if (element != null && element.isEnabled()) {
					return true;
				}
			} catch (NullPointerException | AutomationException e) {
				// Handle or log any exceptions appropriately.
			}

			if (System.currentTimeMillis() - start > durationMillis) {
				System.err.println(String.format(value + " not enabled within %s s", duration));
				log.error(String.format(value + " not enabled within %s s", duration / 1000));
				break;
			}
		}
		return false;
	}
}
