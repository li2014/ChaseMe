package org.droidplanner.checklist.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Environment;

public abstract class ListXmlParser {

	public interface OnXmlParserError {
		public void onError(XmlPullParser parser);
	}

	protected OnXmlParserError errorListener;
	protected XmlPullParser _xpp;


	public void setOnXMLParserError(OnXmlParserError listener) {
		this.errorListener = listener;
	}

	public ListXmlParser() {
	}

	public ListXmlParser(Context context, int resourceId) {
		getListItemsFromResource(context, resourceId);
	}

	public ListXmlParser(String ioFile) throws FileNotFoundException, XmlPullParserException {
		getListItemsFromFile(ioFile);
	}

	public void getListItemsFromFile(String ioFile) throws FileNotFoundException, XmlPullParserException {
		ioFile = Environment.getExternalStorageDirectory()+"/DroidPlanner/Checklists/"+ioFile;
		File file = new File(ioFile);
		FileInputStream fis = new FileInputStream(file);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		_xpp = factory.newPullParser();
		_xpp.setInput(new InputStreamReader(fis));	
		do_parse(_xpp);
		}
	
	public void getListItemsFromResource(Context context, int resourceId) {
		XmlResourceParser is = context.getResources().getXml(resourceId);
		parse(is);
	}

	public void parse(String xmlStr) throws XmlPullParserException, IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		_xpp = factory.newPullParser();
		_xpp.setInput(new StringReader(xmlStr));
		do_parse(_xpp);
	}

	public void parse(XmlResourceParser xmlpp) {
		_xpp = xmlpp;
		do_parse(_xpp);
	}

	public void next() {
		if (_xpp != null)
			try {
				_xpp.next();
			} catch (XmlPullParserException e) {
				if (errorListener != null)
					errorListener.onError(_xpp);

				e.printStackTrace();
			} catch (IOException e) {
				if (errorListener != null)
					errorListener.onError(_xpp);

				e.printStackTrace();
			}
	}

	public int getDepth() {
		if (_xpp != null)
			return _xpp.getDepth();
		return -1;
	}

	private void do_parse(XmlPullParser _xpp) {

		int eventType = 0;
		try {
			eventType = _xpp.getEventType();
		} catch (XmlPullParserException e) {
			if (errorListener != null)
				errorListener.onError(_xpp);
			e.printStackTrace();
		}

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				process_StartDocument(_xpp);
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				process_EndDocument(_xpp);
			} else if (eventType == XmlPullParser.START_TAG) {
				process_StartTag(_xpp);
			} else if (eventType == XmlPullParser.END_TAG) {
				process_EndTag(_xpp);
			} else if (eventType == XmlPullParser.TEXT) {
				process_Text(_xpp);
			}
			try {
				eventType = _xpp.next();
			} catch (XmlPullParserException e) {
				if (errorListener != null)
					errorListener.onError(_xpp);
				e.printStackTrace();
			} catch (IOException e) {
				if (errorListener != null)
					errorListener.onError(_xpp);
				e.printStackTrace();
			}
		}
	}

	public abstract void process_StartDocument(XmlPullParser xpp);

	public abstract void process_EndDocument(XmlPullParser xpp);

	public abstract void process_StartTag(XmlPullParser xpp);

	public abstract void process_EndTag(XmlPullParser xpp);

	public abstract void process_Text(XmlPullParser xpp);
}