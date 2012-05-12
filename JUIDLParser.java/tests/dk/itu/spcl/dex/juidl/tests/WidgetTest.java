package dk.itu.spcl.dex.juidl.tests;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import dk.itu.spcl.dex.juidl.Widget;
import dk.itu.spcl.dex.juidl.WidgetType;

public class WidgetTest {

	@Test
	public void testGenericFillFromJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		String expId = "1234";
		json.put("id", expId);
		String expTitle = "The Title";
		json.put("title", expTitle);
		String expDesc = "The Description";
		json.put("description", expDesc);
		WidgetType expType = WidgetType.EDITABLE;
		json.put("type", "editable");
		int[] expectedContent = new int[]{3,4,5};
		json.put("content", expectedContent);
		String expParentId = "42";
		
		Widget widget = Widget.fromJSONObject(json, expParentId);

		assertEquals(expId, widget.getId());
		assertEquals(expTitle, widget.getTitle());
		assertEquals(expDesc, widget.getDescription());
		assertEquals(expType, widget.getType());
		assertEquals(expParentId, widget.getParentId());
	}

}
