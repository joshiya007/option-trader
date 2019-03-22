package com.trade.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.trade.util.HttpCalls;
import com.trade.util.JsonFileReader;
import com.trade.util.Util;

public class LauncherOption {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSONObject scripts, fno = null;
		JSONArray topGainer, topLoser  = null;
		JsonFileReader fileReader = new JsonFileReader();
		HttpCalls http = new HttpCalls();
		Util util = new Util();
		
		scripts = fileReader.readJsonObjectFile("valid.json");
		fno = fileReader.readJsonObjectFile("march-fno.json");
		
		topGainer = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/gainers?pageno=1&pagesize=30&sortby=percentchange&sortorder=desc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");
		topLoser = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/losers?pageno=1&pagesize=30&sortby=percentchange&sortorder=asc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");
		
		ArrayList<JSONObject> validGainer = new ArrayList<>();
		
		for(int i = 0; i < topGainer.length(); i++) {
			String symbol = topGainer.getJSONObject(i).get("nseScripCode").toString();
			symbol = symbol.substring(0, symbol.length() - 2);
			if(scripts.getJSONArray("Symbol").toString().contains(symbol) ) {
				validGainer.add(topGainer.getJSONObject(i));
			}
		}
		
		for(int j = 0; j < validGainer.size(); j++) {
			String symbol = validGainer.get(j).get("nseScripCode").toString();
			symbol = symbol.substring(0, symbol.length() - 2)+"19MAR";
			int ltp = (int) Float.parseFloat(topGainer.getJSONObject(j).get("current").toString());
			float diff = 0;
			Iterator<String> keysItr = fno.keys();
			ArrayList<Float> tempArray = new ArrayList<>();
			while(keysItr.hasNext()) {
				String key = keysItr.next();
				if(key.startsWith(symbol)) {
					if(key.contains("CE") && (!key.contains("FUT"))) {
						float tempStrikePrice = Float.parseFloat(key.substring(symbol.length(), key.length()-2));
						tempArray.add(tempStrikePrice);
					}
				}
			}
			if(tempArray.size() > 0) {
				Collections.sort(tempArray);
				float first = tempArray.get(0);
				float second = tempArray.get(1);
				diff = Math.abs(first - second);
			}
//			System.out.println(ltp+diff);
			System.out.println(ltp-diff);
		}		
		
		
	}
	
	

}
