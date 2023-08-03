package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class XML_Library {

	private static final Logger log = LogManager.getLogger(XML_Library.class);
	private final ProjectConfiguration config;

	public XML_Library() {
		config = new ProjectConfiguration();
	}

	/**
	 * This method will format the capture GPS to the required format for
	 * comparison.
	 * 
	 * @param fileName The name of the file to be transformed.
	 * @param filePath The path of the file to be transformed.
	 * @return The path of the transformed GPS file, or null if there was an error.
	 */
	private String transformGPS(String fileName, String filePath) {
		log.info("transformGPS started");

		// Create a directory for storing transformed GPS files
		String transformedGPSPath = createTransformedGPSDirectory();
		if (transformedGPSPath == null) {
			log.error("Failed to create directory: " + ProjectConfiguration.transformedGPS_path);
			return null;
		}

		// Convert input file path to Path object
		Path inputPath = Paths.get(filePath);

		// Convert XSLT file path to Path object
		Path xsltPath = Paths.get(Settings.XLS_PATH);

		// Create output file path for transformed GPS
		Path outputPath = Paths.get(transformedGPSPath, fileName + ".xml");

		try {
			// Transform the input file using XSLT
			transformFile(inputPath, xsltPath, outputPath);
			log.info("transform-GPS completed");
			return outputPath.toString();
		} catch (IOException e) {
			log.error("IO error occurred during transform-GPS: " + e.getMessage());
		} catch (TransformerException e) {
			log.error("Transform-GPS TransformerException: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Creates a directory for storing transformed GPS files if it does not exist.
	 * 
	 * @return The absolute path of the transformed GPS directory, or null if there
	 *         was an error.
	 */
	private String createTransformedGPSDirectory() {
		File transformedGPS = new File(ProjectConfiguration.transformedGPS_path);
		if (!transformedGPS.exists()) {
			if (!transformedGPS.mkdir()) {
				return null;
			}
		}
		return transformedGPS.getAbsolutePath();
	}

	/**
	 * Transforms the given input file using the provided XSLT file and saves the
	 * result to the output file.
	 * 
	 * @param inputPath  The path of the input XML file to be transformed.
	 * @param xsltPath   The path of the XSLT file used for the transformation.
	 * @param outputPath The path of the output file where the transformed XML will
	 *                   be saved.
	 * @throws IOException          If an I/O error occurs while reading the input
	 *                              or writing the output.
	 * @throws TransformerException If an error occurs during the transformation
	 *                              process.
	 */
	private void transformFile(Path inputPath, Path xsltPath, Path outputPath)
			throws IOException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltPath.toFile()));
		transformer.transform(new StreamSource(inputPath.toFile()), new StreamResult(outputPath.toFile()));
	}

	/**
	 * Deletes the file from the given path.
	 * 
	 * @param docPath The path of the file to be deleted.
	 * @return The full path of the deleted file if it exists and is successfully
	 *         deleted, otherwise, returns the input docPath.
	 */
	public String deleteFile(String docPath) {
		String fullPath = parseDocPath(docPath);

		System.out.println(fullPath);

		File file = new File(fullPath);
		if (file.exists()) {
			if (file.delete()) {
				return fullPath;
			} else {
				Timer.waitTime(2000); // Add a delay before returning in case file deletion fails
			}
		}
		return fullPath;
	}

	/**
	 * Checks if the given text is present in the specified file.
	 * 
	 * @param docPath      The path of the file to be checked.
	 * @param verifyString The text to be verified in the file.
	 * @return true if the text is found in the file, otherwise false.
	 */
	@SuppressWarnings("resource")
	public boolean isTextInFile(String docPath, String verifyString) {
		String fullPath = parseDocPath(docPath);

		System.out.println(fullPath);

		boolean status = false;
		try {
			String str = convertDocumentToString(new File(fullPath));
			status = str.contains(verifyString);
		} catch (Exception e) {
			// Handle any other exception (e.g., log the error or display a message)
		}
		return status;
	}

	/**
	 * Converts the content of a Document file to a String.
	 *
	 * @param doc The File object representing the Document file.
	 * @return The content of the Document file as a String, or null if an IO error
	 *         occurs.
	 */
	private String convertDocumentToString(File doc) {
		StringBuilder stringBuilder = new StringBuilder();

		try (BufferedReader bufferReader = new BufferedReader(new FileReader(doc))) {
			String line;
			while ((line = bufferReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return stringBuilder.toString();
	}

	/**
	 * Checks if the given text (`verifyString`) is present in the specified XML
	 * file.
	 *
	 * @param docPath      The path of the XML file to be checked.
	 * @param verifyString The text to be verified in the XML file.
	 * @return true if the text is found in the XML file, otherwise false.
	 */
	@SuppressWarnings("resource")
	public boolean isInXML(String docPath, String verifyString) {
		boolean status = false;
		String xmlContent = convertDocumentToString(new File(docPath));

		if (xmlContent != null && xmlContent.contains(verifyString)) {
			status = true;
		}
		return status;
	}

	/**
	 * Checks if a given attribute value (`expectedValue`) matches the attribute of
	 * a specified tag (`tagName`) in the XML file.
	 *
	 * @param docPath       The path of the XML file to be checked.
	 * @param tagName       The name of the tag in the XML file.
	 * @param attribute     The name of the attribute to be checked. If set to
	 *                      "TEXT", it will check the text content of the tag.
	 * @param expectedValue The expected value of the attribute to match.
	 * @return true if the attribute value matches the expected value for any
	 *         occurrence of the specified tag in the XML file, otherwise false.
	 */
	public boolean getAttribute(String docPath, String tagName, String attribute, String expectedValue) {
		boolean actual = false;
		try {
			String xmlFilePath = parseDocPath(docPath);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFilePath);
			doc.normalize();

			NodeList searchNodes = doc.getElementsByTagName(tagName);

			for (int i = 0; i < searchNodes.getLength(); i++) {
				Element element = (Element) searchNodes.item(i);
				String attributeValue;
				if (attribute.equalsIgnoreCase("TEXT")) {
					attributeValue = element.getTextContent();
				} else {
					attributeValue = element.getAttribute(attribute);
				}
				if (attributeValue.equals(expectedValue)) {
					actual = true;
					break;
				}
			}
		} catch (Exception e) {
			// Log the exception or handle it appropriately
			e.printStackTrace();
		}
		System.err.println(actual);
		return actual;
	}

	/**
	 * Helper method to parse the document path and get the actual file path based
	 * on the provided prefix.
	 *
	 * @param docPath The document path to be parsed.
	 * @return The actual file path obtained after parsing the document path.
	 */
	private String parseDocPath(String docPath) {
		String[] path = docPath.split("_");
		int dash = docPath.indexOf('_');
		path[1] = docPath.substring(dash + 1);

		if (path[0].toUpperCase().startsWith("PROGRAMDATA")) {
			return Settings.PROGRAM_DATA_PATH + "\\" + path[1];
		} else if (path[0].toUpperCase().startsWith("REPOPATH")) {
			return Settings.REPO_PATH + "\\" + path[1];
		}

		return docPath; // Return the original docPath if no prefix match
	}

	static boolean updateScreenVd = true;

	/**
	 * Update the value of the specified node or attribute in an XML file.
	 *
	 * @param filePath  The file path of the XML file to be updated.
	 * @param node      The name of the node to be updated.
	 * @param attribute The name of the attribute to be updated (can be null if
	 *                  updating the node value).
	 * @param value     The new value to be set for the node or attribute.
	 */
	private void update(String filePath, String node, String attribute, String value) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(filePath);

			Node search = document.getElementsByTagName(node).item(0);
			if (search != null) {
				if (attribute != null && !attribute.isEmpty()) {
					// Update attribute value
					NamedNodeMap attr = search.getAttributes();
					Node nodeAttr = attr.getNamedItem(attribute);
					if (nodeAttr != null) {
						nodeAttr.setTextContent(value);
					} else {
						// Attribute not found, you may want to handle this case accordingly
						System.err.println("Attribute not found: " + attribute);
					}
				} else {
					// Update node value
					search.setTextContent(value);
				}

				// Save file
				saveXMLContent(document, filePath);
			} else {
				// Node not found, you may want to handle this case accordingly
				System.err.println("Node not found: " + node);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the value of the specified node in an XML file.
	 *
	 * @param filePath The file path of the XML file to be updated.
	 * @param node     The name of the node to be updated.
	 * @param value    The new value to be set for the node.
	 */
	private void update(String filePath, String node, String value) {
		update(filePath, node, null, value); // Delegate to the existing 4-argument update method
	}

	/**
	 * Save the XML content to an XML file.
	 *
	 * @param document The Document object representing the XML content to be saved.
	 * @param xmlFile  The file path of the XML file to which the content will be
	 *                 saved.
	 */
	private void saveXMLContent(Document document, String xmlFile) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			// Set output properties to enable indentation
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource domSource = new DOMSource(document);

			// Create a FileOutputStream to write to the XML file
			FileOutputStream outputStream = new FileOutputStream(xmlFile);

			StreamResult streamResult = new StreamResult(outputStream);

			transformer.transform(domSource, streamResult);

			// Close the resources
			outputStream.close();
		} catch (TransformerException | IOException ex) {
			// Log the exception instead of printing
			log.error("Error saving XML content to file: " + xmlFile, ex);
		}
	}

	/**
	 * Update the capture window mode in the settings to the specified mode.
	 *
	 * @param mode The capture window mode to be set. Supported modes are: -
	 *             "DESKTOP": Capture the entire desktop. - "ACTIVE WINDOW": Capture
	 *             the active window. - "FULL SCREEN": Capture the full screen. -
	 *             "DOCUMENT AREA": Capture the document area. - "CUSTOM SIZE":
	 *             Capture using a custom size.
	 *
	 * @throws IllegalArgumentException If the provided mode input is invalid.
	 */
	public void selectWindow(String mode) {
		// Convert mode to uppercase for case-insensitivity
		mode = mode.toUpperCase();

		// Map input mode to corresponding mode value using a HashMap
		HashMap<String, String> modeMappings = new HashMap<>();
		modeMappings.put("DESKTOP", "3");
		modeMappings.put("ACTIVE WINDOW", "0");
		modeMappings.put("FULL SCREEN", "2");
		modeMappings.put("DOCUMENT AREA", "5");
		modeMappings.put("CUSTOM SIZE", "4");

		// Check if the mode input is valid
		if (!modeMappings.containsKey(mode)) {
			throw new IllegalArgumentException("Invalid mode input: " + mode);
		}

		// Path to the XML file that contains the capture preferences
		String newCapturePref = Settings.PROGRAM_DATA_PATH + "\\Epiplex500\\Settings\\Common\\NewCapturePreference.xml";

		// Update the XML file with the new mode value
		update(newCapturePref, "ScreenImage", "Mode", modeMappings.get(mode));
	}

	/**
	 * This method will update settings for capturing video and audio preferences.
	 *
	 * @param screenVideoAllowed Whether to allow screen video (1 for true, 0 for
	 *                           false).
	 * @param audioVideoAllowed  Whether to allow audio and video (1 for true, 0 for
	 *                           false).
	 * @param capturePreference  Capture preference for audio and video (0 for
	 *                           audio-video, 1 for audio-only).
	 */
	private void updateCaptureSettings(String screenVideoAllowed, String audioVideoAllowed, String capturePreference) {
		update(NEW_CAPTURE_PREF_PATH, "ScreenCapture", "Allow", screenVideoAllowed);
		update(NEW_CAPTURE_PREF_PATH, "AudioVideo", "Allow", audioVideoAllowed);
		update(NEW_CAPTURE_PREF_PATH, "AudioVideo", "CapturePreference", capturePreference);
	}

	/**
	 * This method will update settings to enable screen video.
	 */
	private void enableScreenVideo() {
		String ALLOW_SCREEN_VIDEO = "1";
		String ALLOW_AUDIO_VIDEO = "0";
		String CAPTURE_PREFERENCE_AUDIO_VIDEO = "0";
		updateCaptureSettings(ALLOW_SCREEN_VIDEO, ALLOW_AUDIO_VIDEO, CAPTURE_PREFERENCE_AUDIO_VIDEO);
	}

	/**
	 * This method will update settings to enable capture audio.
	 */
	private void enableCaptureAudio() {
		String ALLOW_SCREEN_VIDEO = "0";
		String ALLOW_AUDIO_VIDEO = "1";
		String CAPTURE_PREFERENCE_AUDIO = "1";
		updateCaptureSettings(ALLOW_SCREEN_VIDEO, ALLOW_AUDIO_VIDEO, CAPTURE_PREFERENCE_AUDIO);
	}

	/**
	 * This method will update settings to enable capture audio and video.
	 */
	private void enableCaptureAudioVideo() {
		String ALLOW_SCREEN_VIDEO = "1";
		String ALLOW_AUDIO_VIDEO = "0";
		String CAPTURE_PREFERENCE_AUDIO_VIDEO = "0";
		updateCaptureSettings(ALLOW_SCREEN_VIDEO, ALLOW_AUDIO_VIDEO, CAPTURE_PREFERENCE_AUDIO_VIDEO);
	}

	/**
	 * This method will update setting according to requested i.e. SCREEN VIDEO,
	 * CAPTURE AUDIO, or AUDIO VIDEO.
	 *
	 * @param setting The setting to be updated (SCREEN VIDEO, CAPTURE AUDIO, or
	 *                AUDIO VIDEO).
	 */
	public void updateCaptureSetting(String setting) {
		switch (setting.toUpperCase()) {
		case "SCREEN VIDEO":
			enableScreenVideo();
			break;
		case "CAPTURE AUDIO":
			enableCaptureAudio();
			break;
		case "AUDIO VIDEO":
			enableCaptureAudioVideo();
			break;
		default:
			break;
		}
	}

	/**
	 * This method will update capture settings as per capture automation need
	 * 
	 * @param updateFlag
	 */

	private final String CAPTURE_SETTINGS_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\Settings\\Capture\\CaptureSettings.xml";
	private final String NEW_CAPTURE_PREF_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\Settings\\Common\\NewCapturePreference.xml";
	private final String BUILD_SETTINGS_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\OEM\\Settings\\BuildSettings.xml";
	private final String STT_SETTING_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\Settings\\SpeechToText\\STTSettings.xml";
	private final String TTT_SETTING_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\Settings\\SpeechToText\\TTTSettings.xml";
	private final String CLAW_ADOPTER_PATH = Settings.PROGRAM_DATA_PATH
			+ "\\Epiplex500\\Settings\\Capture\\Claw_Adaptor.xml";

	/**
	 * This method will update settings based on the updateFlag.
	 *
	 * @param updateFlag Whether to update settings (true or false).
	 */
	public void updateCaptureSettings(boolean updateFlag) {
		try {
			log.info("Automation settings updation started");

			if (updateFlag) {
				update(CAPTURE_SETTINGS_PATH, "Settings", "GPSEncryption", "false");
				update(CAPTURE_SETTINGS_PATH, "Settings", "GpsCabEnabled", "false");
				update(CAPTURE_SETTINGS_PATH, "Settings", "ImageEncryptionEnabled", "false");
			} else {
				update(CAPTURE_SETTINGS_PATH, "Settings", "GPSEncryption", "true");
				update(CAPTURE_SETTINGS_PATH, "Settings", "GpsCabEnabled", "true");
				update(CAPTURE_SETTINGS_PATH, "Settings", "ImageEncryptionEnabled", "true");
			}

			update(CAPTURE_SETTINGS_PATH, "LowLevelCapture", "Enable", "0");

			update(NEW_CAPTURE_PREF_PATH, "Sentence", "UseCtrlImage", "0");
			update(NEW_CAPTURE_PREF_PATH, "UIACapture", "Enabled", "1");
			update(BUILD_SETTINGS_PATH, "Epiplex", "GDPR", "1");
			update(STT_SETTING_PATH, "CredentialsFile", ProjectConfiguration.baseFilesPath + "\\License.json");
			update(STT_SETTING_PATH, "LicenseKey", "6159d0f608ca4ebc95e2c2890b8b422e");
			update(STT_SETTING_PATH, "Region", "centralindia");

			update(TTT_SETTING_PATH, "SubscriptionKey", "b5b54cb063e3451aa0664b82824b1401");
			update(TTT_SETTING_PATH, "EndPoint", "https://api.cognitive.microsofttranslator.com/");
			update(TTT_SETTING_PATH, "Location", "centralindia");
			update(TTT_SETTING_PATH, "LicenseFilePath", ProjectConfiguration.baseFilesPath + "\\License.json");

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document clawAdopterDoc = docBuilder.parse(CLAW_ADOPTER_PATH);
			Document newCapturePrefDoc = docBuilder.parse(NEW_CAPTURE_PREF_PATH);

			updateClawAdopter(clawAdopterDoc, updateFlag);
			updateNewCapturePref(newCapturePrefDoc, updateFlag);

			/**
			 * write it back to the xml
			 */
			saveXMLContent(clawAdopterDoc, CLAW_ADOPTER_PATH);
			saveXMLContent(newCapturePrefDoc, NEW_CAPTURE_PREF_PATH);

		} catch (Exception e) {
			log.error("Exception in updateCaptureSettings: " + e.getMessage());
		}
		log.info("Automation settings updation completed");
	}

	/**
	 * Update the Claw Adopter information in the XML document if not already
	 * present.
	 *
	 * @param doc        The XML document to be updated.
	 * @param updateFlag A boolean flag indicating whether to perform the update. If
	 *                   true, the method will check if the Claw Adopter information
	 *                   is already present in the XML. If false, the method will
	 *                   skip the update.
	 */
	private void updateClawAdopter(Document doc, boolean updateFlag) {
		try {
			// Check if updateFlag is true and if Claw Adopter information is not already
			// present in the XML
			if (updateFlag && !isInXML(CLAW_ADOPTER_PATH, "<Info AppName=\"EPIPLEX500ERP.EXE\"")) {
				Node search = doc.getElementsByTagName("AppBased").item(0);
				Element info = doc.createElement("Info");
				info.setAttribute("AppName", "EPIPLEX500ERP.EXE");
				info.setAttribute("ClassName", "*");
				info.setAttribute("Logic", "Both");
				info.setAttribute("Sequence", "10|~|1");
				info.setAttribute("Sync", "1");
				info.setAttribute("Sync_Time", "500");
				search.appendChild(info);
			}
		} catch (Exception e) {
			log.error("Exception in updateClawAdopter: " + e.getMessage());
		}
	}

	/**
	 * Update the NewCapturePreference in the XML document if the updateFlag is
	 * false and if the specified content is already present in the XML.
	 *
	 * @param doc        The XML document to be updated.
	 * @param updateFlag A boolean flag indicating whether to perform the update. If
	 *                   false, the method will check if the specified content is
	 *                   already present in the XML. If true, the method will skip
	 *                   the update.
	 */
	private void updateNewCapturePref(Document doc, boolean updateFlag) {
		try {
			// Check if updateFlag is false and if the specified content is already present
			// in the XML
			if (!updateFlag
					&& isInXML(NEW_CAPTURE_PREF_PATH, "<Applications>explorer.exe|notepad.exe</Applications>")) {
				Element search = (Element) doc.getElementsByTagName("Applications").item(0);
				search.setTextContent("explorer.exe");
				search = (Element) doc.getElementsByTagName("SelectedApps").item(0);
				search.setTextContent("explorer.exe");
			}
		} catch (Exception e) {
			log.error("Exception in updateNewCapturePref: " + e.getMessage());
		}
	}

	/**
	 * Compare the attributes of two nodes (baseNode and testNode) and store the
	 * comparison results in a table.
	 *
	 * @param baseNode The base node containing attributes for comparison.
	 * @param testNode The test node containing attributes for comparison.
	 * @return true if the attributes of the two nodes match; false otherwise.
	 */
	private boolean compareAttributes(Node baseNode, Node testNode) {
		boolean status = true;
		NamedNodeMap baseMap = baseNode.getAttributes();
		NamedNodeMap testMap = testNode.getAttributes();

		if (baseMap.getLength() == testMap.getLength()) {
			for (int i = 0; i < baseMap.getLength(); i++) {
				Node base = baseMap.item(i);
				Node test = testMap.item(i);
				String baseName = base.getNodeName();
				String testName = test.getNodeName();
				if (baseName.equals(testName)) {
					String baseValue = base.getNodeValue();
					String testValue = test.getNodeValue();
					if (baseValue.equals(testValue)) {
						// If attribute values match, store the comparison results in the table
						table[row][0] = "" + row;
						table[row][1] = "<b>" + baseName;
						table[row][2] = "<b style=\"color:LimeGreen;\">" + baseValue;
						table[row][3] = "<b style=\"color:LimeGreen;\">" + testValue;
					} else {
						// If attribute values do not match, store the comparison results in the table
						// and set status to false
						table[row][0] = "<b>" + row;
						table[row][1] = "<b>" + baseName;
						table[row][2] = "<b style=\"color:red;\">" + baseValue;
						table[row][3] = "<b style=\"color:red;\">" + testValue;
						status = false;
					}
				}
				row++;
			}
		}

		return status;
	}

	private static String[][] table = null;
	private static int row = 1;

	/**
	 * Compare the attributes of corresponding "Step" nodes in two XML documents
	 * (doc1 and doc2). Create an ExtentTest reportLogger for each Step and log the
	 * comparison results.
	 *
	 * @param doc1         The first XML document to compare.
	 * @param doc2         The second XML document to compare.
	 * @param reportLogger The ExtentTest logger to create and log the comparison
	 *                     results.
	 */
	private void check(Document doc1, Document doc2, ExtentTest reportLogger) {
		NodeList doc1Nodes = doc1.getElementsByTagName("Step");
		NodeList doc2Nodes = doc2.getElementsByTagName("Step");

		if (doc1Nodes.getLength() == doc2Nodes.getLength()) {
			for (int i = 0; i < doc1Nodes.getLength(); i++) {
				row = 1;
				Node node = doc1Nodes.item(i);
				String value = node.getAttributes().getNamedItem("StepSentence").getNodeValue();
				reportLogger = ProjectConfiguration.extentReporter.createTest(value);

				NodeList doc1Gen = doc1.getElementsByTagName("GeneralDetails");
				NodeList doc2Gen = doc2.getElementsByTagName("GeneralDetails");

				NodeList doc1Con = doc1.getElementsByTagName("ControlDetails");
				NodeList doc2Con = doc2.getElementsByTagName("ControlDetails");

				NodeList doc1Aut = doc1.getElementsByTagName("AutomationDetails");
				NodeList doc2Aut = doc2.getElementsByTagName("AutomationDetails");

				int total = doc1Gen.item(0).getAttributes().getLength() + doc1Con.item(0).getAttributes().getLength()
						+ doc1Aut.item(0).getAttributes().getLength() + 1;

				table = new String[total][4];
				table[0][0] = "<b>" + "SI NO";
				table[0][1] = "<b>" + "Property";
				table[0][2] = "<b>" + "Expected";
				table[0][3] = "<b>" + "Actual";

				boolean status1 = compareAttributes(doc1Gen.item(i), doc2Gen.item(i));
				boolean status2 = compareAttributes(doc1Con.item(i), doc2Con.item(i));
				boolean status3 = compareAttributes(doc1Aut.item(i), doc2Aut.item(i));
				if (status1 && status2 && status3)
					reportLogger.log(Status.PASS, MarkupHelper.createTable(table));
				else
					reportLogger.log(Status.FAIL, MarkupHelper.createTable(table));
			}
		}
	}

	/**
	 * Compare two GPS files based on their transformed XML representation. The
	 * comparison results are logged using the given ExtentTest reportLogger.
	 *
	 * @param baseFileName    The name of the base GPS file (without extension).
	 * @param currentFileName The name of the current GPS file (without extension).
	 * @param reportLogger    The ExtentTest logger to log the comparison results.
	 */
	public void compareGPSFiles(String baseFileName, String currentFileName, ExtentTest reportLogger) {
		String baseFilePath = ProjectConfiguration.baseFilesPath + "\\GPS Files\\" + baseFileName + ".gps";
		String currentFilePath = Settings.REPO_PATH + "\\Capture\\" + currentFileName + ".gps";
		String baseXML = transformGPS(baseFileName, baseFilePath);
		String currentXML = transformGPS(currentFileName, currentFilePath);

		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builderFactory.setNamespaceAware(true);
			builderFactory.setIgnoringElementContentWhitespace(true);
			builderFactory.setIgnoringComments(true);

			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document baseDocument = null;
			Document currentDocument = null;

			baseDocument = builder.parse(baseXML);
			currentDocument = builder.parse(currentXML);

			baseDocument.normalizeDocument();
			currentDocument.normalizeDocument();

			int baseDocumentLength = baseDocument.getElementsByTagName("Step").getLength();
			int currentDocumentLength = currentDocument.getElementsByTagName("Step").getLength();

			if (baseDocumentLength != currentDocumentLength) {
				reportLogger.fail(
						config.getFailMarkUp(baseFileName + " and " + currentFileName + " Step count not matching"));
				return;
			}

			Diff diff = new Diff(baseDocument, currentDocument);
			DetailedDiff detailedDiff = new DetailedDiff(diff);
			@SuppressWarnings("unchecked")
			List<Difference> differences = detailedDiff.getAllDifferences();

			if (differences.isEmpty()) {
				reportLogger.pass(
						config.getPassMarkUp(baseFileName + " and " + currentFileName + " Both files are matching."));
				check(baseDocument, currentDocument, reportLogger);
			} else {
				check(baseDocument, currentDocument, reportLogger);
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Exception in compareGPSFiles: " + e.getMessage());
			log.error("baseXML content:\n" + baseXML);
			log.error("currentXML content:\n" + currentXML);
		}
	}
}
