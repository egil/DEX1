package dk.itu.spcl.dex.juidl;

import org.json.JSONObject;

public class Widget {
	private String _id;
	private String _parentId;
	private String _title;
	private String _description;
	private String _contentType;
	private int _priority;
	private WidgetType _type;
	
	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}

	public String getParentId() {
		return _parentId;
	}

	public void setParentId(String parentId) {
		this._parentId = parentId;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public WidgetType getType() {
		return _type;
	}

	public void setType(WidgetType type) {
		this._type = type;
	}
	
	public String getContentType() {
		return _contentType;
	}
	private void setContentType(String contentType) {
		this._contentType = contentType;
	}

	public static Widget fromJSONObject(JSONObject json, String parentId) {
		Widget w = new Widget();
		w.setId(json.optString("id"));
		w.setDescription(json.optString("description"));
		w.setTitle(json.optString("title"));
		w.setType(Enum.valueOf(WidgetType.class, json.optString("type", "read-only").replace('-', '_').toUpperCase()));
		w.setParentId(parentId);
		w.setContentType(json.optString("content-type", "string").toLowerCase());
		
		return w;
	}

	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		this._priority = priority;
	}
	
}