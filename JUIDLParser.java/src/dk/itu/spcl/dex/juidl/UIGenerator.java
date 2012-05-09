package dk.itu.spcl.dex.juidl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class UIGenerator {
	public static final double[] SUPPORTED_SCHEMA_VERSIONS = { 1.0 };
	private static Comparator<JSONObject> priorityComparator = new JSONWidgetPriorityComparator();
	private HashSet<String> idCol;

	/**
	 * Use this method to generate the UI based on the schema. This method calls
	 * the validate method before generating.
	 * 
	 * @param schema
	 * @throws JUIDLException
	 */
	public void generate(JSONObject schema) throws JUIDLException {
		// validate schema
		validate(schema);

		// generate the main canvas
		generateCanvas(schema.optString("title"),
				schema.optString("description"), schema.optDouble("version"));

		// generate the rest of the widgets
		JSONArray widgets = schema.optJSONArray("widgets");
		generateWidgets(widgets, null);
	}

	/**
	 * Validate the schema base, based on version and rules.
	 * 
	 * Rules (version 1.0): - title attribute required, is type string -
	 * description attribute is optional, is type string - version attribute is
	 * required, is type double - widgets array is required, is type array
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
			throw new JUIDLException(String.format(
					"The widget {0} is missing a ID.", widget.toString()));
		}
		String id = widget.optString("id");
		if (idCol.contains(id)) {
			throw new JUIDLException(String.format("The widget ID '{0}' is duplicated.", id));	
		}
		// add id to collection for duplication checking purposes
		idCol.add(id);

		// look for sub widgets and validate those if present
		JSONArray widgets = widget.optJSONArray("widgets");
		if (widgets != null && widgets.length() > 0) {
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
		for (index = 0; index < sortedWidgets.length; index++) {
			generateWidget(sortedWidgets[index], parentId);
		}
	}

	private void generateWidget(JSONObject widget, String parentId) {
		// create new widget
		Widget w = Widget.fromJSONObject(widget, parentId);
		String ct = w.getContentType();

		if (w.getType() == WidgetType.MULTI_SELECT
				|| w.getType() == WidgetType.SELECT) {
			// content is an array
			JSONArray arr = widget.optJSONArray("content");

			if (ct.equalsIgnoreCase("string")) {
				String[] content = new String[arr.length()];
				for (int i = 0, len = arr.length(); i < len; i++) {
					content[i] = arr.optString(i);
				}
				generateWidget(w, content);
			} else if (ct.equalsIgnoreCase("integer")) {
				int[] content = new int[arr.length()];
				for (int i = 0, len = arr.length(); i < len; i++) {
					content[i] = arr.optInt(i);
				}
				generateWidget(w, content);
			} else if (ct.equalsIgnoreCase("boolean")) {
				boolean[] content = new boolean[arr.length()];
				for (int i = 0, len = arr.length(); i < len; i++) {
					content[i] = arr.optBoolean(i);
				}
				generateWidget(w, content);
			}
		} else {
			// In this case the content attribute is not an
			// array, just a single value
			if (ct.equalsIgnoreCase("string")) {
				generateWidget(w, widget.optString("content"));
			} else if (ct.equalsIgnoreCase("integer")) {
				generateWidget(w, widget.optInt("content"));
			} else if (ct.equalsIgnoreCase("boolean")) {
				generateWidget(w, widget.optBoolean("content"));
			}
		}

		// generate sub widgets
		JSONArray widgets = widget.optJSONArray("widgets");
		if (widgets != null && widgets.length() > 0) {
			generateWidgets(widgets, w.getId());
		}
	}

	protected abstract void generateCanvas(String title, String description,
			double version);

	protected abstract void generateWidget(Widget w, String content);

	protected abstract void generateWidget(Widget w, String[] contentArray);

	protected abstract void generateWidget(Widget w, boolean content);

	protected abstract void generateWidget(Widget w, boolean[] contentArray);

	protected abstract void generateWidget(Widget w, int content);

	protected abstract void generateWidget(Widget w, int[] contentArray);

	/**
	 * Class used to order widgets before generating
	 */
	private static class JSONWidgetPriorityComparator implements
			Comparator<JSONObject> {
		public int compare(JSONObject widget1, JSONObject widget2) {
			int p1 = widget1.optInt("priority", 0);
			int p2 = widget2.optInt("priority", 0);
			return p1 - p2;
		}
	}
}
