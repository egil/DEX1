package dk.itu.spcl.dex.juidl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class UIGenerator {
	public static final double[] SUPPORTED_SCHEMA_VERSIONS = { 1.0 };
	private static Comparator<JSONObject> priorityComparator = new JSONWidgetPriorityComparator();
	private HashSet<String> idCol; 

	/**
	 * Use this method to generate the UI based on the schema.
	 * This method calls the validate method before generating.
	 * 
	 * @param schema
	 * @throws JUIDLException
	 */
	public void generate(JSONObject schema) throws JUIDLException {
		// validate schema
		validate(schema);
		
		// generate the main canvas
		generateCanvas(schema.optString("title"), 
					   schema.optString("description"),
					   schema.optDouble("version"));
		
		// generate the rest of the widgets
		JSONArray widgets = schema.optJSONArray("widgets");
		generateWidgets(widgets, null);
	}	

	/**
	 * Validate the schema base, based on version and rules.
	 * 
	 * Rules (version 1.0): 
	 * - title attribute required, is type string 
	 * - description attribute is optional, is type string 
	 * - version attribute is required, is type double 
	 * - widgets array is required, is type array
	 * 
	 * @param schema
	 * @throws JUIDLException
	 */
	public void validate(JSONObject schema) throws JUIDLException {
		// validate presence of title attribute and type is string
		if (!schema.has("title") || schema.isNull("title")) {
			throw new JUIDLException("The title attribute is missing or empty.");
		}

		// validate presence of version attribute and type is 1.0
		if (!schema.has("version")) {
			throw new JUIDLException("The version attribute is missing.");
		} else if (Arrays.binarySearch(SUPPORTED_SCHEMA_VERSIONS,
				schema.optDouble("version", 0.0)) < 0) {
			throw new JUIDLException("The schema version is not supported.");
		}

		// check if widgets array is present
		if (schema.optJSONArray("widgets") == null) {
			throw new JUIDLException(
					"The widgets attribute is missing or not a valid array.");
		}
		
		idCol = new HashSet<String>();
		JSONArray widgets = schema.optJSONArray("widgets");
		validateWidgets(widgets);
	}

	private void validateWidgets(JSONArray widgets) throws JUIDLException {
		int index = 0;
		JSONObject widget;				
		while (null != (widget = widgets.optJSONObject(index))) {
			validateWidget(widget);			
			index++;
		}		
	}

	private void validateWidget(JSONObject widget) throws JUIDLException {
		// check if widget has an ID and if it is unique
		if (!widget.has("id") || widget.optString("id") == null) {
			throw new JUIDLException(String.format("The widget {0} is missing a ID.", widget.toString()));
		} 
		String id = widget.optString("id");
		if (idCol.contains(id)) {
			throw new JUIDLException(String.format("The widget ID '{0}' is doublicated.", id));	
		}
		// add id to collection for duplication checking purposes
		idCol.add(id);
		
		// look for sub widgets and validate those if present
		JSONArray widgets = widget.optJSONArray("widgets");
		if(widgets != null && widgets.length() > 0) {
			validateWidgets(widgets);		
		}
	}
	
	private void generateWidgets(JSONArray widgets, String parentId) {			
		JSONObject[] sortedWidgets = new JSONObject[widgets.length()]; 
		int index = 0;
		JSONObject widget;	
		while (null != (widget = widgets.optJSONObject(index))) {			
			sortedWidgets[index] = widget;
			index++;
		}
		
		// sort array of widgets according to priority
		Arrays.sort(sortedWidgets, priorityComparator);
		
		// generate each widget
		for(index = 0; index < sortedWidgets.length; index++) {
			generateWidget(sortedWidgets[index], parentId);
		}
	}
	
	private void generateWidget(JSONObject widget, String parentId) {
		// create new widget	
		//Widget w = Widget.createFromJSONObject(widget);
//		String ct = widget.optString("content-type");
//		if (ct.equalsIgnoreCase("integer")) {
//			Widget<Integer> w = Widget.genericFillFromJSONObject(Integer.class, widget, parentId);
//			
//		} else if (ct.equalsIgnoreCase("boolean")) {
//			Widget<Boolean> w = new Widget<Boolean>();
//		} else if (ct.equalsIgnoreCase("integer[]")) {
//			Widget<List<Integer>> w = new Widget<List<Integer>>();
//		} else if (ct.equalsIgnoreCase("boolean[]")) {
//			Widget<List<Boolean>> w = new Widget<List<Boolean>>();
//		} else if (ct.equalsIgnoreCase("string[]")) {
//			Widget<List<String>> w = new Widget<List<String>>();
//		} else {
//			// default ("string")
//			Widget<String> w = new Widget<String>();
//		}
		
		// generate sub widgets
		JSONArray widgets = widget.optJSONArray("widgets");
		if(widgets != null && widgets.length() > 0) {
			generateWidgets(widgets, widget.optString("id"));		
		}
	}
	
	

	protected abstract void generateCanvas(String title, String description, double version);
//	
//	protected abstract void generateStringWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         String content);
//	
//	protected abstract void generateIntWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         int content);
//	
//	protected abstract void generateDoubleWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         double content);
//	
//	protected abstract void generateBooleanWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         boolean content);
//
//	protected abstract void generateStringListWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         List<String> content);
//	
//	protected abstract void generateIntListWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         List<Integer> content);
//	
//	protected abstract void generateDoubleListWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         List<Double> content);
//	
//	protected abstract void generateBooleanListWidget(String id, 
//	         String parentId, 
//	         String title, 
//	         String description, 
//	         WidgetType type,
//	         List<Boolean> content);
	/**
	 * Class used to 
	 */
	private static class JSONWidgetPriorityComparator implements Comparator<JSONObject>	{
		public int compare(JSONObject widget1, JSONObject widget2) {
			int p1 = widget1.optInt("priority", 0); 
			int p2 = widget2.optInt("priority", 0);
			return p1 - p2;
		}
	}
}
