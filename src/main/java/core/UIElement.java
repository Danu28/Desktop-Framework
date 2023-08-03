package core;

import java.awt.Rectangle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.pattern.Toggle;
import mmarquee.uiautomation.ToggleState;
import utils.Settings;

/**
 * The {@code UIElement} class represents a UI element that wraps a SikuliX
 * {@code Element} and provides additional functionality.
 */
public class UIElement extends SikuliElement {

	private Element element;
	private static final Logger log = LogManager.getLogger(UIElement.class);

	/**
	 * Constructs a new {@code UIElement} with the given SikuliX {@code Element}.
	 *
	 * @param inElement The SikuliX {@code Element} to wrap.
	 */
	public UIElement(Element inElement) {
		super(getRegion(inElement));
		this.element = inElement;
	}

	/**
	 * Converts the bounding rectangle of the given element to a region with
	 * adjusted coordinates and dimensions.
	 *
	 * @param element The element to convert to a region.
	 * @return A Region object representing the adjusted region of the element.
	 */
	private static Region getRegion(Element element) {
		Region region = null;
		try {
			Rectangle rect = element.getBoundingRectangle().toRectangle();
			int x = (rect.x * 100) / Settings.SCALE;
			int y = (rect.y * 100) / Settings.SCALE;
			int height = (rect.height * 100) / Settings.SCALE;
			int width = (rect.width * 100) / Settings.SCALE;
			region = new Region(x, y, width, height);
		} catch (AutomationException e) {
			// Log the error using Log4j or any other logger framework.
			// For example, if using Log4j:
			log.error("Error occurred while getting region.", e);
		}
		return region;
	}

	/**
	 * Checks the UI element if it is not already checked.
	 *
	 * @throws AutomationException If an automation error occurs.
	 * @throws FindFailed          If the find operation fails.
	 */
	public void check() throws AutomationException, FindFailed {
		Toggle toggle = new Toggle(element);
		ToggleState state = toggle.currentToggleState();
		int value = state.getValue();
		if (value == 0) {
			click();
		}
	}

	/**
	 * Unchecks the UI element if it is already checked.
	 *
	 * @throws AutomationException If an automation error occurs.
	 * @throws FindFailed          If the find operation fails.
	 */
	public void unCheck() throws AutomationException, FindFailed {
		Toggle toggle = new Toggle(element);
		ToggleState state = toggle.currentToggleState();
		int value = state.getValue();
		if (value == 1) {
			click();
		}
	}

	/**
	 * Toggles the UI element to the specified state.
	 *
	 * @param state The state to toggle. Use 0 to uncheck and 1 to check.
	 * @throws AutomationException If an automation error occurs.
	 * @throws FindFailed          If the find operation fails.
	 */
	public void toggle(String state) throws AutomationException, FindFailed {
		int stat = Integer.parseInt(state);
		Toggle toggle = new Toggle(element);
		ToggleState toggleState = toggle.currentToggleState();
		int value = toggleState.getValue();
		if (value != stat) {
			click();
		}
	}

	/**
	 * Checks if the UI element is enabled.
	 *
	 * @return {@code true} if the UI element is enabled, otherwise {@code false}.
	 * @throws AutomationException If an automation error occurs.
	 */
	public boolean isEnabled() throws AutomationException {
		return element != null && element.isEnabled();
	}

	/**
	 * Gets the name of the UI element.
	 *
	 * @return The name of the UI element.
	 * @throws AutomationException If an automation error occurs.
	 */
	public String getName() throws AutomationException {
		return element.getName();
	}

	/**
	 * Gets the automation ID of the UI element.
	 *
	 * @return The automation ID of the UI element.
	 * @throws AutomationException If an automation error occurs.
	 */
	public String getAutomationId() throws AutomationException {
		return element.getAutomationId();
	}

	/**
	 * Sets focus to the UI element.
	 */
	public void setFocus() {
		element.setFocus();
	}
}