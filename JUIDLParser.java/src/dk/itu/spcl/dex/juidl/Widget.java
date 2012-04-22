package dk.itu.spcl.dex.juidl;

import java.util.List;

import org.json.JSONObject;

public class Widget<T> {
	private String _id;
	private String _parentId;
	private String _title;
	private String _description;
	private WidgetType _type;
	private T _content;
	private List<Widget> _widgets;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getParentId() {
		return _parentId;
	}

	public void setParentId(String _parentId) {
		this._parentId = _parentId;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String _description) {
		this._description = _description;
	}

	public WidgetType getType() {
		return _type;
	}

	public void setType(WidgetType _type) {
		this._type = _type;
	}

	public T getContent() {
		return _content;
	}

	public void setContent(T _content) {
		this._content = _content;
	}

	public List<Widget> getWidgets() {
		return _widgets;
	}

	public void setWidgets(List<Widget> _widgets) {
		this._widgets = _widgets;
	}

	public static <T> Widget<T> genericFillFromJSONObject(JSONObject json, String parentId) {
		Widget<T> w = new Widget<T>();
		w.setId(json.optString("id"));
		w.setDescription(json.optString("description"));
		w.setTitle(json.optString("title"));
		w.setType(Enum.valueOf(WidgetType.class, json.optString("type", "read-only").replace('-', '_').toUpperCase()));
		w.setParentId(parentId);
		
		
		return w;
	}
}