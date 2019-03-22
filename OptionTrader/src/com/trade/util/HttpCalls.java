package com.trade.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONObject;

public class HttpCalls {

	public JSONObject get(String input) {
		JSONObject resultJson = null;
		URL url;
		try {
			url = new URL(input);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			resultJson = new JSONObject(convertStreamToString(is));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultJson;
	}
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

}
