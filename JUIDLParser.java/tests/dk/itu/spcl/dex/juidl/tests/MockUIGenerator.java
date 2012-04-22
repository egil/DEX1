package dk.itu.spcl.dex.juidl.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONObject;

import dk.itu.spcl.dex.juidl.UIGenerator;

public class MockUIGenerator extends UIGenerator {
	public String canvasTitle;
	public String canvasDesription;
	public double canvasVersion;
	public ArrayList<JSONObject> canvasItems = new ArrayList<JSONObject>();

	@Override
	protected void generateCanvas(String title, String description,
			double version) {
		canvasTitle = title;
		canvasDesription = description;
		canvasVersion = version;
	}
}
