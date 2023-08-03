package core;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import actions.Keyboard;
import actions.Mouse;

/**
 * The {@code SikuliElement} class is a wrapper around the SikuliX
 * {@code Region} class, providing various convenience methods for interacting
 * with regions on the screen.
 */
public class SikuliElement {

	/**
	 * The SikuliX {@code Screen} instance used for screen actions.
	 */
	public static Screen screen = new Screen();

	/**
	 * The underlying SikuliX {@code Region} associated with this
	 * {@code SikuliElement}.
	 */
	private Region region;

	/**
	 * Constructs a new {@code SikuliElement} with the given SikuliX {@code Region}.
	 *
	 * @param region The SikuliX {@code Region} to wrap.
	 */
	public SikuliElement(Region region) {
		this.region = region;
	}

	/**
	 * Returns the underlying SikuliX {@code Region} associated with this
	 * {@code SikuliElement}.
	 *
	 * @return The SikuliX {@code Region} instance.
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * Performs a click action on the {@code SikuliElement}. The click is done on
	 * the bottom-left area of the region, slightly adjusted by 5 pixels to the
	 * right and 5 pixels above the original position.
	 */
	public void click() {
		region.getBottomLeft().right(5).above(5).click();
	}

	/**
	 * Performs a click action on the center of the {@code SikuliElement}.
	 */
	public void clickCenter() {
		region.getCenter().click();
	}

	/**
	 * Performs a right-click action on the {@code SikuliElement}.
	 */
	public void rightClick() {
		region.rightClick();
	}

	/**
	 * Performs a double-click action on the {@code SikuliElement}.
	 */
	public void doubleClick() {
		region.doubleClick();
	}

	/**
	 * Performs a hover action over the {@code SikuliElement}.
	 */
	public void hover() {
		region.hover();
	}

	/**
	 * Drags the {@code SikuliElement} to another location on the screen. The drag
	 * action starts from the current location of the element.
	 */
	public void drag() {
		try {
			screen.drag(region);
		} catch (FindFailed e) {
			System.out.println("Exception in Drag :- " + e.getMessage());
		}
	}

	/**
	 * Drops the {@code SikuliElement} at its current location.
	 */
	public void dropAt() {
		try {
			screen.dropAt(region);
		} catch (FindFailed e) {
			System.out.println("Exception in Drag :- " + e.getMessage());
		}
	}

	/**
	 * Highlights the {@code SikuliElement} for the specified duration (in seconds).
	 *
	 * @param duration The duration (in seconds) for which the element will be
	 *                 highlighted.
	 */
	public void highlight(double duration) {
		region.highlight(duration);
	}

	/**
	 * Checks if the {@code SikuliElement} is currently displayed on the screen.
	 *
	 * @return {@code true} if the element is displayed, {@code false} otherwise.
	 */
	public boolean isDisplayed() {
		return region.isValid();
	}

	/**
	 * Checks if the {@code SikuliElement} is currently vanished (not visible) on
	 * the screen.
	 *
	 * @return {@code true} if the element is vanished, {@code false} otherwise.
	 */
	public boolean isVanished() {
		return screen.isVirtual();
	}

	/**
	 * Clears the content of the {@code SikuliElement} by clicking on its center and
	 * using the keyboard to clear the text.
	 */
	public void clear() {
		clickCenter();
		Keyboard.clear();
	}

	/**
	 * Writes the specified text to the {@code SikuliElement} by first clearing its
	 * content and then typing the text using SikuliX's screen type action.
	 *
	 * @param text The text to be written to the {@code SikuliElement}.
	 */
	public void write(String text) {
		clear();
		screen.type(text);
	}

	/**
	 * Performs a swipe-up action on the {@code SikuliElement} by first clicking on
	 * it and then using the mouse wheel to scroll up by the specified number of
	 * steps.
	 *
	 * @param steps The number of steps to scroll up.
	 */
	public void swipeUp(int steps) {
		click();
		Mouse.scrollUp(steps);
	}

	/**
	 * Performs a swipe-down action on the {@code SikuliElement} by first clicking
	 * on it and then using the mouse wheel to scroll down by the specified number
	 * of steps.
	 *
	 * @param steps The number of steps to scroll down.
	 */
	public void swipeDown(int steps) {
		click();
		Mouse.scrollDown(steps);
	}
}
