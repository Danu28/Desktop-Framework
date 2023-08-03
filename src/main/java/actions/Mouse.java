package actions;

import java.awt.Robot;

import utils.Timer;

/**
 * This class provides methods to perform mouse actions, such as scrolling up
 * and down.
 */
public class Mouse {

	private static final int MOUSE_SCROLL_WAIT_TIME = 500;

	/**
	 * Scrolls the mouse wheel down by the specified number of steps.
	 *
	 * @param steps The number of steps to scroll down.
	 */
	public static void scrollDown(int steps) {
		Robot robot = createRobotInstance();
		robot.mouseWheel(steps);
		Timer.waitTime(MOUSE_SCROLL_WAIT_TIME);
		robot.waitForIdle();
	}

	/**
	 * Scrolls the mouse wheel up by the specified number of steps.
	 *
	 * @param steps The number of steps to scroll up.
	 */
	public static void scrollUp(int steps) {
		Robot robot = createRobotInstance();
		robot.mouseWheel(-steps);
		Timer.waitTime(MOUSE_SCROLL_WAIT_TIME);
		robot.waitForIdle();
	}

	/**
	 * Creates an instance of the Robot class.
	 *
	 * @return The Robot instance.
	 * @throws RuntimeException if the Robot instance cannot be created.
	 */
	private static Robot createRobotInstance() {
		try {
			return new Robot();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create Robot instance.", e);
		}
	}
}