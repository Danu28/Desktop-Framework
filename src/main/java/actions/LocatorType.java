package actions;

/**
 * An enumeration representing various types of locators used in test
 * automation. Each locator type corresponds to a specific way of identifying
 * elements in the application under test. The available locator types are as
 * follows:
 *
 * - NAME: Locate elements by their name attribute. - ID: Locate elements by
 * their id attribute. - TEXT: Locate elements by their visible text content. -
 * VALUE: Locate elements by their value attribute. - PARTIALNAME: Locate
 * elements by a partial match on their name attribute. - PARTIALID: Locate
 * elements by a partial match on their id attribute. - PARTIALTEXT: Locate
 * elements by a partial match on their visible text content. - PARTIALVALUE:
 * Locate elements by a partial match on their value attribute. - IMAGE: Locate
 * elements by matching an image pattern on the screen using Sikuli. - LOCATION:
 * Locate elements based on their on-screen position or coordinates. - OCR:
 * Locate elements by performing Optical Character Recognition (OCR) on the
 * screen content.
 */
public enum LocatorType {
	NAME, ID, TEXT, VALUE, PARTIALNAME, PARTIALID, PARTIALTEXT, PARTIALVALUE, IMAGE, LOCATION, OCR
}
