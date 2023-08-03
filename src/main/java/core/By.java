package core;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.ptr.PointerByReference;

import exceptions.CustomImageSearchException;
import exceptions.CustomLocationException;
import exceptions.CustomOCRFailedException;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.ElementNotFoundException;
import mmarquee.automation.PropertyID;
import mmarquee.uiautomation.TreeScope;
import utils.ProjectConfiguration;

/**
 * This class consists of element finding logic methods. It provides various
 * methods to find elements based on different attributes and criteria. It also
 * supports image-based and OCR-based element searches.
 * 
 * Note: The methods in this class use the `Driver` class for element search and
 * automation. Make sure to initialize the `Driver` before using any of the
 * methods.
 * 
 * @author Dhanush
 */
public class By {

	/**
	 * This method will find and return the first matching element based on the
	 * provided criteria.
	 *
	 * @param pointerByReference The reference pointer to search for elements.
	 * @return The first matching element.
	 * @throws ElementNotFoundException If the element is not found after the
	 *                                  specified search attempts.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 */
	private static Element findFirst(PointerByReference pointerByReference)
			throws ElementNotFoundException, AutomationException {
		Element element = null;
		int searchAttempts = Driver.searchAttempts;
		int retryInterval = 100; // Milliseconds

		for (int retryCount = 0; retryCount < searchAttempts; retryCount++) {
			try {
				if (Driver.rootSearch)
					element = Driver.getRootElement().findFirst(new TreeScope(Driver.scope), pointerByReference);
				else
					element = Driver.getSearchContext().findFirst(new TreeScope(Driver.scope), pointerByReference);
			} catch (AutomationException e1) {
				// Log or handle the exception if needed
			}

			if (element == null) {
				try {
					TimeUnit.MILLISECONDS.sleep(retryInterval);
				} catch (InterruptedException e) {
					// Log or handle the exception if needed
				}
			} else {
				return element;
			}
		}

		throw new ElementNotFoundException("Element not found after " + searchAttempts + " attempts.");
	}

	/**
	 * This method will find and return a list of all matching elements based on the
	 * provided criteria.
	 *
	 * @param pointerByReference The reference pointer to search for elements.
	 * @return A list of all matching elements found.
	 * @throws ElementNotFoundException If no elements are found after the specified
	 *                                  search attempts.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 */
	private static List<Element> findAll(PointerByReference pointerByReference)
			throws ElementNotFoundException, AutomationException {
		List<Element> elements = new ArrayList<>();
		int searchAttempts = Driver.searchAttempts;
		int retryInterval = 100; // Milliseconds

		for (int retryCount = 0; retryCount < searchAttempts; retryCount++) {
			try {
				if (Driver.rootSearch)
					elements = Driver.getRootElement().findAll(new TreeScope(Driver.scope), pointerByReference);
				else
					elements = Driver.getSearchContext().findAll(new TreeScope(Driver.scope), pointerByReference);
			} catch (AutomationException e1) {
				// Log or handle the exception if needed
			}

			if (elements.size() == 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(retryInterval);
				} catch (InterruptedException e) {
					// Log or handle the exception if needed
				}
			} else {
				return elements;
			}
		}

		throw new ElementNotFoundException("No elements found after " + searchAttempts + " attempts.");
	}

	/**
	 * This method will retrieve the value of a specific attribute from the given
	 * element.
	 *
	 * @param element The element to extract the attribute value from.
	 * @param locator The attribute name to retrieve the value from (e.g., "NAME",
	 *                "ID", "TEXT", "VALUE").
	 * @return The value of the specified attribute.
	 * @throws IllegalArgumentException If the locator is not recognized.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 */
	private static String getValue(Element element, String locator) throws AutomationException {
		if (element == null || locator == null || locator.isEmpty()) {
			return null;
		}

		String value = null;
		locator = locator.toUpperCase();

		try {
			switch (locator) {
			case "NAME":
				value = element.getName();
				break;
			case "ID":
				value = element.getAutomationId();
				break;
			case "TEXT":
				value = element.getPropertyValue(PropertyID.HelpText.getValue()).toString();
				break;
			case "VALUE":
				value = element.getPropertyValue(PropertyID.LegacyIAccessibleValue.getValue()).toString();
				break;
			default:
				throw new IllegalArgumentException("Invalid locator: " + locator);
			}
		} catch (AutomationException e) {
			// Log or print the exception for debugging purposes
			e.printStackTrace();
			throw e;
		}

		return value;
	}

	/**
	 * Finds elements with partial matches based on the given locator and value.
	 *
	 * @param pointerByReference A PointerByReference object for element search.
	 * @param locator            The locator string used to identify elements.
	 * @param value              The value to be partially matched.
	 * @return A list of elements that contain the specified value partially.
	 * @throws NullPointerException If pointerByReference, locator, or value is
	 *                              null.
	 * @throws AutomationException  If there is an error during the automation
	 *                              process.
	 */
	private static List<Element> findPartial(PointerByReference pointerByReference, String locator, String value)
			throws NullPointerException, AutomationException {
		if (pointerByReference == null || locator == null || locator.isEmpty() || value == null || value.isEmpty()) {
			return Collections.emptyList();
		}

		long startTime = System.currentTimeMillis();
		List<Element> partialMatchedElements = new ArrayList<>();
		List<Element> elements = findAll(pointerByReference);

		for (Element element : elements) {
			if (getValue(element, locator).contains(value)) {
				partialMatchedElements.add(element);
			}
		}
		long endTime = System.currentTimeMillis();

		System.err.println(
				"Time taken to find partial elements: " + (endTime - startTime) + " milliseconds for " + value);

		return partialMatchedElements;
	}

	/**
	 * An enumeration representing different find options for matching elements.
	 */
	enum FindOption {
		/**
		 * Find the FIRST matching element.
		 */
		FIRST,
		/**
		 * Find ALL matching elements.
		 */
		ALL
	}

	/**
	 * Finds elements with a specific help text attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The help text attribute value to match elements against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that match the specified help text and control
	 *         type based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByText(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		WTypes.BSTR sysAllocated = null;

		try {
			// Convert the 'value' String to a BSTR (OLE Automation string).
			sysAllocated = OleAuto.INSTANCE.SysAllocString(value);

			// Create a VARIANT to hold the BSTR.
			Variant.VARIANT.ByValue variant = new Variant.VARIANT.ByValue();
			variant.setValue(Variant.VT_BSTR, sysAllocated);

			// Create an AND condition combining HelpText and ControlType conditions.
			PointerByReference pointerByReference = Driver.automation.createAndCondition(
					Driver.automation.createPropertyCondition(PropertyID.HelpText.getValue(), variant),
					Driver.automation.createControlTypeCondition(controlType));

			if (findOption == FindOption.FIRST) {
				// Find the FIRST matching element.
				Element firstElement = findFirst(pointerByReference);
				if (firstElement != null) {
					elements.add(firstElement);
				}
			} else if (findOption == FindOption.ALL) {
				// Find ALL matching elements.
				List<Element> allElements = findAll(pointerByReference);
				elements.addAll(allElements);
			}
		} catch (Exception e) {
			// Handle any exception that might occur during resource cleanup or processing.
			// Logging or re-throwing the exception could be considered here.
		} finally {
			// Ensure proper cleanup of resources (e.g., releasing the sysAllocated BSTR).
			if (sysAllocated != null) {
				OleAuto.INSTANCE.SysFreeString(sysAllocated);
			}
		}

		return elements;
	}

	/**
	 * Finds elements with a specific value attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The value attribute value to match elements against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that match the specified value and control type
	 *         based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByValue(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		WTypes.BSTR sysAllocated = null;

		try {
			// Convert the 'value' String to a BSTR (OLE Automation string).
			sysAllocated = OleAuto.INSTANCE.SysAllocString(value);

			// Create a VARIANT to hold the BSTR.
			Variant.VARIANT.ByValue variant = new Variant.VARIANT.ByValue();
			variant.setValue(Variant.VT_BSTR, sysAllocated);

			// Create an AND condition combining LegacyIAccessibleValue and ControlType
			// conditions.
			PointerByReference pointerByReference = Driver.automation.createAndCondition(
					Driver.automation.createPropertyCondition(PropertyID.LegacyIAccessibleValue.getValue(), variant),
					Driver.automation.createControlTypeCondition(controlType));

			if (findOption == FindOption.FIRST) {
				// Find the FIRST matching element.
				Element firstElement = findFirst(pointerByReference);
				if (firstElement != null) {
					elements.add(firstElement);
				}
			} else if (findOption == FindOption.ALL) {
				// Find ALL matching elements.
				List<Element> allElements = findAll(pointerByReference);
				elements.addAll(allElements);
			}
		} catch (Exception e) {
			// Handle any exception that might occur during resource cleanup or processing.
			// Logging or re-throwing the exception could be considered here.
		} finally {
			// Ensure proper cleanup of resources (e.g., releasing the sysAllocated BSTR).
			if (sysAllocated != null) {
				OleAuto.INSTANCE.SysFreeString(sysAllocated);
			}
		}

		return elements;
	}

	/**
	 * Finds elements with a specific name attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The name attribute value to match elements against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that match the specified name and control type
	 *         based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByName(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		PointerByReference pointerByReference = Driver.automation.createAndCondition(
				Driver.automation.createNamePropertyCondition(value),
				Driver.automation.createControlTypeCondition(controlType));

		List<Element> elements = new ArrayList<>();

		if (findOption == FindOption.FIRST) {
			Element element = findFirst(pointerByReference);
			if (element != null) {
				elements.add(element);
			}
		} else if (findOption == FindOption.ALL) {
			List<Element> allElements = findAll(pointerByReference);
			elements.addAll(allElements);
		}

		return elements;
	}

	/**
	 * Finds elements with a specific ID attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The ID attribute value to match elements against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that match the specified ID and control type based
	 *         on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsById(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		PointerByReference pointerByReference = Driver.automation.createAndCondition(
				Driver.automation.createAutomationIdPropertyCondition(value),
				Driver.automation.createControlTypeCondition(controlType));

		if (findOption == FindOption.FIRST) {
			Element firstElement = findFirst(pointerByReference);
			if (firstElement != null) {
				elements.add(firstElement);
			}
		} else if (findOption == FindOption.ALL) {
			List<Element> allElements = findAll(pointerByReference);
			elements.addAll(allElements);
		}

		return elements;
	}

	/**
	 * Finds elements with a partial name attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The partial name attribute value to match elements
	 *                    against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that have a partial name match and belong to the
	 *         specified control type based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByPartialName(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		PointerByReference pointerByReference = Driver.automation.createControlTypeCondition(controlType);

		List<Element> partialMatchedElement = findPartial(pointerByReference, "name", value);

		if (findOption == FindOption.FIRST && !partialMatchedElement.isEmpty()) {
			elements.add(partialMatchedElement.get(0));
		} else if (findOption == FindOption.ALL) {
			elements.addAll(partialMatchedElement);
		}

		return elements;
	}

	/**
	 * Finds elements with a partial ID attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The partial ID attribute value to match elements against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that have a partial ID match and belong to the
	 *         specified control type based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByPartialId(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		PointerByReference pointerByReference = Driver.automation.createControlTypeCondition(controlType);

		List<Element> partialMatchedElements = findPartial(pointerByReference, "id", value);

		if (findOption == FindOption.FIRST && !partialMatchedElements.isEmpty()) {
			elements.add(partialMatchedElements.get(0));
		} else if (findOption == FindOption.ALL) {
			elements.addAll(partialMatchedElements);
		}

		return elements;
	}

	/**
	 * Finds elements with a partial text attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The partial text attribute value to match elements
	 *                    against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that have a partial text match and belong to the
	 *         specified control type based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByPartialText(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		PointerByReference pointerByReference = Driver.automation.createControlTypeCondition(controlType);

		List<Element> partialMatchedElements = findPartial(pointerByReference, "text", value);

		if (findOption == FindOption.FIRST && !partialMatchedElements.isEmpty()) {
			elements.add(partialMatchedElements.get(0));
		} else if (findOption == FindOption.ALL) {
			elements.addAll(partialMatchedElements);
		}

		return elements;
	}

	/**
	 * Finds elements with a partial value attribute and control type.
	 *
	 * @param controlType The type of the control to search for (e.g., Button,
	 *                    TextBox, etc.).
	 * @param value       The partial value attribute value to match elements
	 *                    against.
	 * @param findOption  An enumeration specifying whether to find the FIRST
	 *                    matching element or ALL matching elements.
	 * @return A list of elements that have a partial value match and belong to the
	 *         specified control type based on the chosen find option.
	 * @throws AutomationException      If there is an error during the automation
	 *                                  process.
	 * @throws IllegalArgumentException If any of the input parameters (controlType,
	 *                                  value, or findOption) are invalid.
	 */
	public static List<Element> findElementsByPartialValue(ControlType controlType, String value, FindOption findOption)
			throws AutomationException {
		if (controlType == null || value == null || value.isEmpty() || findOption == null) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}

		List<Element> elements = new ArrayList<>();
		PointerByReference pointerByReference = Driver.automation.createControlTypeCondition(controlType);

		List<Element> partialMatchedElements = findPartial(pointerByReference, "value", value);

		if (findOption == FindOption.FIRST && !partialMatchedElements.isEmpty()) {
			elements.add(partialMatchedElements.get(0));
		} else if (findOption == FindOption.ALL) {
			elements.addAll(partialMatchedElements);
		}

		return elements;
	}

	/**
	 * Finds an element with the specified image inside another image or screen.
	 *
	 * @param searchImage The filename of the image where the search for the
	 *                    findImage should happen. Use "SCREEN" to search on the
	 *                    entire screen.
	 * @param findImage   The filename of the image to find inside the searchImage.
	 * @return A SikuliElement representing the found region.
	 * @throws CustomImageSearchException If the image search fails or the image
	 *                                    filenames are null or empty.
	 */
	public static SikuliElement findImageInImage(String searchImage, String findImage)
			throws CustomImageSearchException {
		if (searchImage == null || searchImage.isEmpty() || findImage == null || findImage.isEmpty()) {
			throw new IllegalArgumentException("Image filenames must not be null or empty.");
		}

		String basePath = ProjectConfiguration.sikuliImageBasePath + "\\";
		Region region = null;

		try {
			if (searchImage.equalsIgnoreCase("SCREEN")) {
				region = SikuliElement.screen.find(basePath + findImage);
			} else {
				Match searchMatch = SikuliElement.screen.find(basePath + searchImage);
				Match findMatch = searchMatch.find(basePath + findImage);
				Rectangle foundRect = findMatch.getRect();
				region = new Region(foundRect);
			}
		} catch (FindFailed e) {
			throw new CustomImageSearchException("Image search failed: " + e.getMessage(), e);
		}

		return new SikuliElement(region);
	}

	/**
	 * Finds an element containing the specified text using OCR inside an image or
	 * screen.
	 *
	 * @param searchImage The filename of the image where the OCR search for the
	 *                    text should happen. Use "SCREEN" to search on the entire
	 *                    screen.
	 * @param text        The text to find using OCR.
	 * @return A SikuliElement representing the found region containing the text.
	 * @throws CustomOCRFailedException If the OCR search fails or the image
	 *                                  filename or text is null or empty.
	 */
	public static SikuliElement findTextUsingOCR(String searchImage, String text) throws CustomOCRFailedException {
		if (searchImage == null || searchImage.isEmpty() || text == null || text.isEmpty()) {
			throw new IllegalArgumentException("Image filename and text must not be null or empty.");
		}

		String basePath = ProjectConfiguration.sikuliImageBasePath + "\\";
		Screen screen = new Screen();
		Region region = null;

		try {
			if (searchImage.equalsIgnoreCase("SCREEN")) {
				region = screen.findText(text);
			} else {
				Match searchMatch = screen.find(basePath + searchImage);
				region = searchMatch.findText(text);
			}
		} catch (FindFailed e) {
			throw new CustomOCRFailedException("OCR search failed: " + e.getMessage(), e);
		}

		return new SikuliElement(region);
	}

	/**
	 * Creates a SikuliElement at the specified (x, y) coordinates on the screen.
	 *
	 * @param screenX The x-coordinate of the location to create the SikuliElement.
	 * @param screenY The y-coordinate of the location to create the SikuliElement.
	 * @return A SikuliElement representing the region at the specified coordinates.
	 * @throws CustomLocationException If the (x, y) coordinates are not valid
	 *                                 screen coordinates.
	 */
	public static SikuliElement createSikuliElementAtLocation(int screenX, int screenY) throws CustomLocationException {
		if (!isValidScreenCoordinates(screenX, screenY)) {
			throw new CustomLocationException("Invalid screen coordinates.");
		}

		Region region = new Region(screenX, screenY);
		return new SikuliElement(region);
	}

	/**
	 * Checks whether the provided (x, y) coordinates are valid screen coordinates.
	 *
	 * @param x The x-coordinate to check.
	 * @param y The y-coordinate to check.
	 * @return True if the coordinates are within the screen bounds, false
	 *         otherwise.
	 */
	private static boolean isValidScreenCoordinates(int x, int y) {
		// Assuming your screen size, adjust the values based on your actual screen
		// resolution.
		Dimension screenSize = getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		return (x >= 0 && x < screenWidth) && (y >= 0 && y < screenHeight);
	}

	/**
	 * Gets the screen size of the default screen.
	 *
	 * @return The dimension representing the screen size (width and height).
	 */
	private static Dimension getScreenSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.getScreenSize();
	}

}
