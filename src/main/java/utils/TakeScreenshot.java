package utils;

import org.sikuli.script.Screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TakeScreenshot {

	/**
	 * This method will take a screenshot and save it in the screenshots folder with
	 * the given test case name.
	 *
	 * @param screenshotName The name to be used for the screenshot file.
	 * @return The absolute path of the saved screenshot file, or null if an error
	 *         occurs.
	 */
	public static String captureScreenshot(String screenshotName) {
		try {
			BufferedImage img = new Screen().capture().getImage();
			String path = "screenshots\\" + screenshotName + ".png";
			File outputfile = new File(path);
			ImageIO.write(img, "png", outputfile);
			return outputfile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null; // Return null in case of any error.
		}
	}
}