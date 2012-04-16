package dk.itu.spcl.dex.juidl.tests;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.itu.spcl.dex.juidl.JUIDLException;
import dk.itu.spcl.dex.juidl.UIGenerator;

public class UIGeneratorValidateTest {
	private UIGenerator p;	
	
	@Before
	public void Setup() {
		p = new MockUIGenerator();
	}
	
	@Test
	public void generate_test() throws JUIDLException, JSONException {
		String test_schema = "{'title':'Title','version':1.0,'widgets':[]}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.generate(schema_json);
	}

	@Test(expected=JUIDLException.class)
	public void required_title_test() throws JSONException, JUIDLException {
		String test_schema = "{}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}
	
	@Test(expected=JUIDLException.class)
	public void required_version_test() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title'}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}
	
	@Test(expected=JUIDLException.class)
	public void version_supported_test() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title', 'version':-1.0 }";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}
	
	@Test(expected=JUIDLException.class)
	public void required_group_array_test() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title', 'version':1.0, " +
			"'widgets':[{'type':'group'}]}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}
	
	@Test(expected=JUIDLException.class)
	public void required_widget_id_test() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title', 'version':1.0, " +
			"'widgets':[{}]}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}
	
	@Test(expected=JUIDLException.class)
	public void required_widget_doublicated_id_test() throws JSONException, JUIDLException {
		String test_schema = "{'title':'The Title', 'version':1.0, " +
			"'widgets':[{'id':'2'}, {'id':'3'}, {'id':'2'}]}";
		JSONObject schema_json = new JSONObject(test_schema);
		p.validate(schema_json);
	}	
}
