package core;

import java.util.List;

import org.sikuli.script.FindFailed;

import actions.LocatorType;
import core.By.FindOption;
import exceptions.CustomImageSearchException;
import exceptions.CustomLocationException;
import exceptions.CustomOCRFailedException;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.Window;
import mmarquee.uiautomation.TreeScope;

/**
 * The Driver class provides methods for UI element search and manipulation
 * using UIAutomation and Sikuli. It is responsible for managing the search
 * context, scope, and search attempts. The class also includes utility methods
 * to find Pane and Window elements.
 */
public class Driver {

	public static final UIAutomation automation = UIAutomation.getInstance();
	public static int searchAttempts = 1;
	public static boolean rootSearch = true;
	public static int scope = TreeScope.SUBTREE;

	private static Element rootElement;
	public static Element searchContext;

	static {
		rootElement = automation.getDesktop().getElement();
		searchContext = rootElement;
	}

	/**
	 * Get the root element of the automation tree.
	 *
	 * @return The root Element representing the desktop.
	 */
	public static Element getRootElement() {
		return rootElement;
	}

	/**
	 * Get the current search context element.
	 *
	 * @return The search context Element.
	 */
	public static Element getSearchContext() {
		return searchContext;
	}

	/**
	 * Set the number of search attempts for By class.
	 *
	 * @param searchAttempts The number of search attempts to set.
	 */
	public void setSearchAttempts(int searchAttempts) {
		Driver.searchAttempts = searchAttempts;
	}

	/**
	 * Set whether the search should start from the root level.
	 *
	 * @param searchStatus True if the search should start from the root level,
	 *                     false otherwise.
	 */
	public void setRootSearch(boolean searchStatus) {
		Driver.rootSearch = searchStatus;
	}

	/**
	 * Set the scope of searching.
	 *
	 * @param scope The scope of searching (e.g., TreeScope.SUBTREE,
	 *              TreeScope.CHILDREN).
	 */
	public void setTreeScope(int scope) {
		Driver.scope = scope;
	}

	/**
	 * Set the search context (Search area in the automation tree).
	 *
	 * @param element The Element to set as the search context.
	 */
	public void setSearchContext(Element element) {
		Driver.searchContext = element;
	}


	private boolean checkNameCondition(Element element, String name)
	{
		if(element == null)
			return false;
		else
		{
			try {
				if(element.getName().contains(name))
				{
					return true;
				}
			} catch (AutomationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Finds a window element with the given name.
	 *
	 * @param name The name of the window to find.
	 * @return The Element representing the found window, or null if not found.
	 */
	public Element getWindow(String name) {
	    try {
	        List<Window> windows = automation.getDesktopWindows();

	        for (int attempt = 0; attempt < 5; attempt++) {
	            Window window = windows.get(attempt);
	            Element winElement = findWindowElement(window, name);
	            if (winElement != null) {
	                winElement.setFocus();
	                return winElement;
	            }
	        }
	    } catch (AutomationException e) {
	        // Log or handle AutomationException specifically
	        e.printStackTrace();
	    }
	    return null;
	}

	private Element findWindowElement(Window window, String name) {
	    Element windowElement = window.getElement();
	    if (checkNameCondition(windowElement, name)) {
	        return windowElement;
	    }

	    Window child = getChildWindow(window);
	    if (child != null) {
	        Element childElement = child.getElement();
	        if (checkNameCondition(childElement, name)) {
	            return childElement;
	        }
	    }
	    return null;
	}
	
	/**
	 * Finds a pane element with the given name.
	 *
	 * @param name The name of the pane to find.
	 * @return The Element representing the found pane, or null if not found.
	 */
	public Element getPane(String name) {
	    try {
	    	List<Panel> panes = automation.getDesktopObjects();

	        for (int attempt = 0; attempt < 5; attempt++) {
	            Panel window = panes.get(attempt);
	            Element winElement = findPaneElement(window, name);
	            if (winElement != null) {
	                winElement.setFocus();
	                return winElement;
	            }
	        }
	    } catch (AutomationException e) {
	        // Log or handle AutomationException specifically
	        e.printStackTrace();
	    }
	    return null;
	}

	private Element findPaneElement(Panel pane, String name) {
	    Element paneElement = pane.getElement();
	    if (checkNameCondition(paneElement, name)) {
	        return paneElement;
	    }

	    Panel child = getChildPane(pane);
	    if (child != null) {
	        Element childElement = child.getElement();
	        if (checkNameCondition(childElement, name)) {
	            return childElement;
	        }
	    }
	    return null;
	}

	/**
	 * Gets the child window of the given window.
	 *
	 * @param window The parent window to get the child window from.
	 * @return The child window Element, or null if not found or an error occurred.
	 */
	private Window getChildWindow(Window window) {
		Window winElement = null;
		try {
			List<AutomationBase> childWindows = window.getChildren(false);
			if (!childWindows.isEmpty()) {
				Element element = childWindows.get(0).getElement();
				winElement = new Window(new ElementBuilder(element));
			}
		} catch (AutomationException e) {
			// Log or handle AutomationException specifically
			e.printStackTrace();
		} catch (Exception e) {
			// Log or handle other exceptions specifically
			e.printStackTrace();
		}
		return winElement;
	}
	
	/**
	 * Gets the child pane of the given pane.
	 *
	 * @param pane The parent pane to get the child pane from.
	 * @return The child pane Element, or null if not found or an error occurred.
	 */
	public Panel getChildPane(Panel pane) {
		Panel paneElement = null;
		try {
			List<AutomationBase> childPanes = pane.getChildren(false);
			if (!childPanes.isEmpty()) {
				Element element = childPanes.get(0).getElement();
				paneElement = new Panel(new ElementBuilder(element));
			}
		} catch (AutomationException e) {
			// Log or handle AutomationException specifically
			e.printStackTrace();
		} catch (Exception e) {
			// Log or handle other exceptions specifically
			e.printStackTrace();
		}
		return paneElement;
	}

	/**
	 * Returns a UIElement for the given Element.
	 * 
	 * @param element The Element for which to get the UIElement.
	 * @return The UIElement representing the given Element.
	 * @throws NullPointerException If the provided Element is null.
	 * @throws AutomationException  If an error occurs during UIElement creation.
	 */
	public UIElement getUIElement(Element element) throws NullPointerException, AutomationException {
		UIElement uiElement = new UIElement(element);
		return uiElement;
	}

	/**
	 * Finds an element with the given locator and value.
	 * 
	 * @param locatorType The type of locator to use for finding the element.
	 * @param controlType The ControlType of the element to find.
	 * @param value       The value to search for in the element's properties.
	 * @return The Element representing the found element.
	 * @throws NullPointerException     If the provided locatorType, controlType, or
	 *                                  value is null.
	 * @throws AutomationException      If an error occurs during element search.
	 * @throws IllegalArgumentException If the provided locatorType is not
	 *                                  supported.
	 */
	public Element findElement(LocatorType locatorType, ControlType controlType, String value)
			throws NullPointerException, AutomationException {
		if (locatorType == null || controlType == null || value == null) {
			throw new NullPointerException("LocatorType, ControlType, and value cannot be null.");
		}

		Element element = null;

		switch (locatorType) {
		case NAME:
			element = By.findElementsByName(controlType, value, FindOption.FIRST).get(0);
			break;
		case ID:
			element = By.findElementsById(controlType, value, FindOption.FIRST).get(0);
			break;
		case TEXT:
			element = By.findElementsByText(controlType, value, FindOption.FIRST).get(0);
			break;
		case VALUE:
			element = By.findElementsByValue(controlType, value, FindOption.FIRST).get(0);
			break;
		case PARTIALNAME:
			element = By.findElementsByPartialName(controlType, value, FindOption.FIRST).get(0);
			break;
		case PARTIALID:
			element = By.findElementsByPartialId(controlType, value, FindOption.FIRST).get(0);
			break;
		case PARTIALTEXT:
			element = By.findElementsByPartialText(controlType, value, FindOption.FIRST).get(0);
			break;
		case PARTIALVALUE:
			element = By.findElementsByPartialValue(controlType, value, FindOption.FIRST).get(0);
			break;
		default:
			// Log the error at the caller's level with more context.
			throw new IllegalArgumentException("Locator type not exist: " + locatorType);
		}

		return element;
	}

	/**
	 * Finds elements with the given locator and value.
	 * 
	 * @param locatorType The type of locator to use for finding the elements.
	 * @param controlType The ControlType of the elements to find.
	 * @param value       The value to search for in the elements' properties.
	 * @return The list of Elements representing the found elements.
	 * @throws NullPointerException     If the provided locatorType, controlType, or
	 *                                  value is null.
	 * @throws AutomationException      If an error occurs during element search.
	 * @throws IllegalArgumentException If the provided locatorType is not
	 *                                  supported.
	 */
	public List<Element> findElements(LocatorType locatorType, ControlType controlType, String value)
			throws NullPointerException, AutomationException {
		if (locatorType == null || controlType == null || value == null) {
			throw new NullPointerException("LocatorType, ControlType, and value cannot be null.");
		}

		List<Element> elements = null;

		switch (locatorType) {
		case NAME:
			elements = By.findElementsByName(controlType, value, FindOption.ALL);
			break;
		case ID:
			elements = By.findElementsById(controlType, value, FindOption.ALL);
			break;
		case TEXT:
			elements = By.findElementsByText(controlType, value, FindOption.ALL);
			break;
		case VALUE:
			elements = By.findElementsByValue(controlType, value, FindOption.ALL);
			break;
		case PARTIALNAME:
			elements = By.findElementsByPartialName(controlType, value, FindOption.ALL);
			break;
		case PARTIALID:
			elements = By.findElementsByPartialId(controlType, value, FindOption.ALL);
			break;
		case PARTIALTEXT:
			elements = By.findElementsByPartialText(controlType, value, FindOption.ALL);
			break;
		case PARTIALVALUE:
			elements = By.findElementsByPartialValue(controlType, value, FindOption.ALL);
			break;
		default:
			// Log the error at the caller's level with more context.
			throw new IllegalArgumentException("Locator type not exist: " + locatorType);
		}

		return elements;
	}

	/**
	 * Finds a SikuliElement based on the given locator type and parameters.
	 * 
	 * @param locatorType The type of locator to use for finding the SikuliElement.
	 * @param parameter1  The first parameter for the locator (e.g., image path,
	 *                    x-coordinate).
	 * @param parameter2  The second parameter for the locator (e.g., y-coordinate,
	 *                    OCR text).
	 * @return The found SikuliElement.
	 * @throws FindFailed               If the SikuliElement is not found or if
	 *                                  there is an error during the search.
	 * @throws IllegalArgumentException If the provided locatorType is not
	 *                                  supported.
	 */
	public SikuliElement findSikuliElement(LocatorType locatorType, String parameter1, String parameter2)
			throws FindFailed, IllegalArgumentException {
		if (locatorType == null || parameter1 == null || parameter2 == null) {
			throw new NullPointerException("LocatorType, parameter1, and parameter2 cannot be null.");
		}

		SikuliElement sikuliElement = null;

		try {
			switch (locatorType) {
			case IMAGE:
				sikuliElement = By.findImageInImage(parameter1, parameter2);
				break;
			case LOCATION:
				int x = Integer.parseInt(parameter1);
				int y = Integer.parseInt(parameter2);
				sikuliElement = By.createSikuliElementAtLocation(x, y);
				break;
			case OCR:
				sikuliElement = By.findTextUsingOCR(parameter1, parameter2);
				break;
			default:
				// Log the error at the caller's level with more context.
				throw new IllegalArgumentException("Locator type not exist: " + locatorType);
			}
		} catch (NumberFormatException e) {
			// Log the error at the caller's level with more context.
			throw new IllegalArgumentException("Invalid parameter values for locator type: " + locatorType);
		} catch (CustomImageSearchException e) {
			// Log or handle CustomImageSearchException specifically
			e.printStackTrace();
		} catch (CustomLocationException e) {
			// Log or handle CustomLocationException specifically
			e.printStackTrace();
		} catch (CustomOCRFailedException e) {
			// Log or handle CustomOCRFailedException specifically
			e.printStackTrace();
		}

		return sikuliElement;
	}

}
