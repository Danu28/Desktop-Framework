package actions;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.sikuli.script.Screen;

import utils.ProjectConfiguration;

/**
 * The Keyboard class provides utility methods to simulate keyboard actions
 * using the Robot class. It supports key presses for letters, numbers, control
 * keys, function keys, and special keys. Additionally, it allows typing and
 * pasting text using the Sikuli Screen class.
 */
public class Keyboard {

	private static Robot robot;
	private static final Map<String, Integer> KEY_CODES = new LinkedHashMap<>();
	private static final Screen screen = new Screen();

	/**
	 * Initializes the Keyboard class by mapping key names to their corresponding
	 * KeyEvent.VK_ constants.
	 */
	private static void key(String c, int i) {
		KEY_CODES.put(c, i);
	}

	static {
		// Mapping of key names to their corresponding KeyEvent.VK_ constants
		key("a", KeyEvent.VK_A);
		key("b", KeyEvent.VK_B);
		key("c", KeyEvent.VK_C);
		key("d", KeyEvent.VK_D);
		key("e", KeyEvent.VK_E);
		key("f", KeyEvent.VK_F);
		key("g", KeyEvent.VK_G);
		key("h", KeyEvent.VK_H);
		key("i", KeyEvent.VK_I);
		key("j", KeyEvent.VK_J);
		key("k", KeyEvent.VK_K);
		key("l", KeyEvent.VK_L);
		key("m", KeyEvent.VK_M);
		key("n", KeyEvent.VK_N);
		key("o", KeyEvent.VK_O);
		key("p", KeyEvent.VK_P);
		key("q", KeyEvent.VK_Q);
		key("r", KeyEvent.VK_R);
		key("s", KeyEvent.VK_S);
		key("t", KeyEvent.VK_T);
		key("u", KeyEvent.VK_U);
		key("v", KeyEvent.VK_V);
		key("w", KeyEvent.VK_W);
		key("x", KeyEvent.VK_X);
		key("y", KeyEvent.VK_Y);
		key("z", KeyEvent.VK_Z);
		// ===================================
		key("control", KeyEvent.VK_CONTROL);
		key("alt", KeyEvent.VK_ALT);
		key("shift", KeyEvent.VK_SHIFT);
		key("tab", KeyEvent.VK_TAB);
		key("enter", KeyEvent.VK_ENTER);
		key("space", KeyEvent.VK_SPACE);
		key("backspace", KeyEvent.VK_BACK_SPACE);
		key("up", KeyEvent.VK_UP);
		key("right", KeyEvent.VK_RIGHT);
		key("down", KeyEvent.VK_DOWN);
		key("left", KeyEvent.VK_LEFT);
		key("pageup", KeyEvent.VK_PAGE_UP);
		key("pagedown", KeyEvent.VK_PAGE_DOWN);
		key("end", KeyEvent.VK_END);
		key("home", KeyEvent.VK_HOME);
		key("delete", KeyEvent.VK_DELETE);
		key("escape", KeyEvent.VK_ESCAPE);
		// ====================================
		key("fn", KeyEvent.VK_F);
		key("f1", KeyEvent.VK_F1);
		key("f2", KeyEvent.VK_F2);
		key("f3", KeyEvent.VK_F3);
		key("f4", KeyEvent.VK_F4);
		key("f5", KeyEvent.VK_F5);
		key("f6", KeyEvent.VK_F6);
		key("f7", KeyEvent.VK_F7);
		key("f8", KeyEvent.VK_F8);
		key("f9", KeyEvent.VK_F9);
		key("f10", KeyEvent.VK_F10);
		key("f11", KeyEvent.VK_F11);
		key("f12", KeyEvent.VK_F12);
	}

	/**
	 * Initializes the Robot instance if not already created.
	 */
	private static void initializeRobot() {
		if (robot == null) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				throw new RuntimeException("Failed to create Robot instance.", e);
			}
		}
	}

	/**
	 * Releases all currently pressed keys. This method should be called to ensure
	 * that no keys are stuck. It is recommended to use this method at the end of
	 * keyboard actions to release all keys.
	 */
	public static void releaseAllKeys() {
		initializeRobot();
		Set<String> keys = KEY_CODES.keySet();
		for (String key : keys) {
			int keyCode = KEY_CODES.get(key);
			robot.keyRelease(keyCode);
		}
	}

	/**
	 * Simulates key press and release actions using the Robot class for the given
	 * key.
	 *
	 * @param key The key to be pressed and released. The key should be a string
	 *            representation of the key name. For example, "a" for the 'A' key,
	 *            "enter" for the Enter key, "f12" for the F12 key, etc.
	 */
	private static void keyPress(String key) {
		initializeRobot();
		robot.delay(300);
		Integer keyCode = KEY_CODES.get(key.toLowerCase());
		if (keyCode != null) {
			robot.keyPress(keyCode);
		} else {
			System.out.println("Unsupported key: " + key);
		}
	}

	/**
	 * Simulates key release action using the Robot class for the given key.
	 *
	 * @param key The key to be released. The key should be a string representation
	 *            of the key name. For example, "a" for the 'A' key, "enter" for the
	 *            Enter key, "f12" for the F12 key, etc.
	 */
	private static void keyRelease(String key) {
		initializeRobot();
		robot.delay(300);
		Integer keyCode = KEY_CODES.get(key.toLowerCase());
		if (keyCode != null) {
			robot.keyRelease(keyCode);
		} else {
			System.out.println("Unsupported key: " + key);
		}
	}

	/**
	 * Simulates a special key press by pressing and releasing a sequence of keys.
	 *
	 * @param keys The keys to be pressed and released. The keys should be string
	 *             representations of the key names. For example, "ctrl", "alt",
	 *             "shift", "a", "b", etc.
	 */
	public static void specialKeyPress(String... keys) {
		initializeRobot();
		robot.delay(300);
		for (String key : keys) {
			keyPress(key);
		}
		for (int i = keys.length - 1; i >= 0; i--) {
			keyRelease(keys[i]);
		}
	}

	/**
	 * Clears the text input by pressing Ctrl+A (select all) and then pressing
	 * Delete. Note: This method is specific to text input fields in certain
	 * contexts.
	 */
	public static void clear() {
		keyPress("control");
		keyPress("a");
		keyRelease("a");
		keyRelease("control");
		keyPress("delete");
		keyRelease("delete");
	}

	/**
	 * Types the given text using the Sikuli Screen class.
	 *
	 * @param text The text to be typed. If the text ends with "Base-Files", it will
	 *             be replaced with the corresponding path defined in
	 *             ProjectConfiguration.baseFilesPath.
	 */
	public static void type(String text) {
		initializeRobot();
		robot.delay(100);
		if (text.endsWith("Base-Files"))
			text = ProjectConfiguration.baseFilesPath;
		screen.type(text);
	}

	/**
	 * Pastes the given text using the Sikuli Screen class. Note: This method is
	 * specific to text input fields in certain contexts.
	 *
	 * @param text The text to be pasted. If the text ends with "Base-Files", it
	 *             will be replaced with the corresponding path defined in
	 *             ProjectConfiguration.baseFilesPath.
	 */
	public static void paste(String text) {
		initializeRobot();
		clear();
		robot.delay(100);
		if (text.endsWith("Base-Files"))
			text = ProjectConfiguration.baseFilesPath;
		screen.paste(text);
	}

}