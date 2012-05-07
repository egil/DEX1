package dk.itu.spcl.dex.juidl.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONObject;

import dk.itu.spcl.dex.juidl.UIGenerator;
import dk.itu.spcl.dex.juidl.Widget;

public class MockUIGenerator extends UIGenerator {
	public String canvasTitle;
	public String canvasDesription;
	public double canvasVersion;
	
	public Widget widget;
	public String stringContent;
	public String[] stringContentArray;
	public boolean boolContent;
	public boolean[] boolContentArray;
	public int intContent;
	public int[] intContentArray;

	@Override
	protected void generateCanvas(String title, String description,
			double version) {
		canvasTitle = title;
		canvasDesription = description;
		canvasVersion = version;
	}

	@Override
	protected void generateWidget(Widget w, String content) {
		widget = w;
		stringContent = content;
	}

	@Override
	protected void generateWidget(Widget w, String[] contentArray) {
		widget = w;
		this.stringContentArray = contentArray;
	}

	@Override
	protected void generateWidget(Widget w, boolean content) {
		widget = w;
		this.boolContent = content;
	}

	@Override
	protected void generateWidget(Widget w, boolean[] contentArray) {
		widget = w;
		this.boolContentArray = contentArray;
	}

	@Override
	protected void generateWidget(Widget w, int content) {
		widget = w;
		this.intContent = content;		
	}

	@Override
	protected void generateWidget(Widget w, int[] contentArray) {
		widget = w;
		this.intContentArray = contentArray;
	}

}
