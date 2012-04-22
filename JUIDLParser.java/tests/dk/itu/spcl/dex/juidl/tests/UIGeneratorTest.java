package dk.itu.spcl.dex.juidl.tests;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import dk.itu.spcl.dex.juidl.JUIDLException;
import dk.itu.spcl.dex.juidl.UIGenerator;

public class UIGeneratorTest {

	private MockUIGenerator p;

	@Before
	public void Setup() {
		p = new MockUIGenerator();
	}

	@Test
	public void canvasGenTest() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title', " +
							  "'description':'The Description', " +
							  "'version':1.0, 'widgets':[]}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.generate(schema_json);
		assertEquals("The Title", p.canvasTitle);		
		assertEquals("The Description", p.canvasDesription);
		assertEquals(1.0, p.canvasVersion, 0);
	}
	
	// @Test
	// public void sortingTest() {
	// String test_schema = "{'title':'The Title', 'version':1.0, " +
	// "'widgets':[{'id':'2'}," +
	// "{'id':'3'}," +
	// "]}";
	// JSONObject schema_json = new JSONObject(test_schema);
	// p.validate(schema_json);
	// }
}
