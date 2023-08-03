package core;

import java.util.HashMap;
import java.util.Map;
import mmarquee.automation.ControlType;

/**
 * The Controls class is a utility class that provides a mapping between control
 * types and their corresponding values in the mmarquee.automation.ControlType
 * enumeration. It is used to convert a control type string to its corresponding
 * ControlType value.
 */
public class Controls {

	// A mapping between control type strings and their corresponding ControlType
	// values
	private static final Map<String, ControlType> controlMap = new HashMap<>();

	// Initialize the controlMap with control type strings and their corresponding
	// ControlType values
	static {
		controlMap.put("BUTTON", ControlType.Button);
		controlMap.put("NONE", ControlType.None);
		controlMap.put("CALENDAR", ControlType.Calendar);
		controlMap.put("CHECKBOX", ControlType.CheckBox);
		controlMap.put("COMBOBOX", ControlType.ComboBox);
		controlMap.put("EDIT", ControlType.Edit);
		controlMap.put("HYPERLINK", ControlType.Hyperlink);
		controlMap.put("IMAGE", ControlType.Image);
		controlMap.put("LISTITEM", ControlType.ListItem);
		controlMap.put("LIST", ControlType.List);
		controlMap.put("MENU", ControlType.Menu);
		controlMap.put("MENUBAR", ControlType.MenuBar);
		controlMap.put("MENUITEM", ControlType.MenuItem);
		controlMap.put("PROGRESSBAR", ControlType.ProgressBar);
		controlMap.put("RADIOBUTTON", ControlType.RadioButton);
		controlMap.put("SCROLLBAR", ControlType.ScrollBar);
		controlMap.put("SLIDER", ControlType.Slider);
		controlMap.put("SPINNER", ControlType.Spinner);
		controlMap.put("STATUSBAR", ControlType.StatusBar);
		controlMap.put("TAB", ControlType.Tab);
		controlMap.put("TABITEM", ControlType.TabItem);
		controlMap.put("TEXT", ControlType.Text);
		controlMap.put("TOOLBAR", ControlType.ToolBar);
		controlMap.put("TOOLTIP", ControlType.ToolTip);
		controlMap.put("TREE", ControlType.Tree);
		controlMap.put("TREEITEM", ControlType.TreeItem);
		controlMap.put("CUSTOM", ControlType.Custom);
		controlMap.put("GROUP", ControlType.Group);
		controlMap.put("THUMB", ControlType.Thumb);
		controlMap.put("DATAGRID", ControlType.DataGrid);
		controlMap.put("DATAITEM", ControlType.DataItem);
		controlMap.put("DOCUMENT", ControlType.Document);
		controlMap.put("SPLITBUTTON", ControlType.SplitButton);
		controlMap.put("WINDOW", ControlType.Window);
		controlMap.put("PANE", ControlType.Pane);
		controlMap.put("HEADER", ControlType.Header);
		controlMap.put("HEADERITEM", ControlType.HeaderItem);
		controlMap.put("TABLE", ControlType.Table);
		controlMap.put("TITLEBAR", ControlType.TitleBar);
		controlMap.put("SEPARATOR", ControlType.Separator);
		controlMap.put("SEMANTICZOOM", ControlType.SemanticZoom);
		controlMap.put("APPBAR", ControlType.AppBar);
	}

	/**
	 * Get the ControlType value for the given control type string.
	 * 
	 * @param controlType The control type string to look up (case-insensitive).
	 * @return The ControlType value corresponding to the control type string, or
	 *         null if the control type is not found in the mapping.
	 */
	public static ControlType getControl(String controlType) {
		ControlType result = controlMap.get(controlType.toUpperCase());
		if (result == null) {
			System.out.println("Unsupported Control Type :- " + controlType);
		}
		return result;
	}
}