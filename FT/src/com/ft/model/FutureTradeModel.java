package com.ft.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ft.util.HttpCalls;
import com.ft.util.JsonFileReader;

public class FutureTradeModel {
	static {
		System.setProperty("http.agent", "Chrome");
	}

	public JSONArray getFutureTrades(){
		JSONArray result = new JSONArray();
		FutureTradeModel l = new FutureTradeModel();
		JSONObject scripts, fno = null;
		JSONArray topGainer, topLoser= null;
		JsonFileReader fileReader = new JsonFileReader();
		HttpCalls http = new HttpCalls();
		fno = fileReader.readJsonObjectFile("march-fno.json");
		scripts = fileReader.readJsonObjectFile("valid.json");
		
		topGainer = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/gainers?pageno=1&pagesize=150&sortby=percentchange&sortorder=desc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");
		topLoser = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/losers?pageno=1&pagesize=150&sortby=percentchange&sortorder=asc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");

		for(int i = 0; i < topLoser.length(); i++) {
			topGainer.put(topLoser.getJSONObject(i));
		}
		for(int i = 0; i < topGainer.length(); i++) {
			String symbol = topGainer.getJSONObject(i).get("nseScripCode").toString();
			float percentageChange = Float.parseFloat(topGainer.getJSONObject(i).get("percentChange").toString());
			float dayOpen = Float.parseFloat(topGainer.getJSONObject(i).get("open").toString());
			float dayHigh = Float.parseFloat(topGainer.getJSONObject(i).get("high").toString());
			float dayLow = Float.parseFloat(topGainer.getJSONObject(i).get("low").toString());
			symbol = symbol.substring(0, symbol.length() - 2);
			String type ="";
			if(percentageChange > 0) {
				type = "GAINER";
			}else {
				type = "LOSER";
			}
			if(scripts.getJSONArray("Symbol").toString().contains(symbol) ) {
				symbol = symbol+"19MARFUT";
				if( (!"NH19MARFUT".equalsIgnoreCase(symbol)) || (!"ITI19MARFUT".equalsIgnoreCase(symbol)) ) {
					float close = l.getCloseValue(fno.getJSONObject(symbol).get("instrument_token").toString());
					float cltp = l.getLtpValue(fno.getJSONObject(symbol).get("instrument_token").toString());
					if ( (cltp > 200) && (cltp < 1200) ){
						int lotSize =  Integer.parseInt(fno.getJSONObject(symbol).get("lot_size").toString());
						float bAbove = l.getBuyAbove(close);
						float sBelow = l.getSellBelow(close);
						JSONObject tempObject = new JSONObject();
						tempObject.put("symbol", symbol);
						tempObject.put("percentageChange", percentageChange);
						tempObject.put("dayOpen", dayOpen);
						tempObject.put("dayHigh", dayHigh);
						tempObject.put("dayLow", dayLow);
						tempObject.put("type", type);
						tempObject.put("lotSize", lotSize);
						tempObject.put("ltp", cltp);
						tempObject.put("buyAbove", bAbove);
						tempObject.put("sellBelow", sBelow);
						result.put(tempObject);
					}
				}
			}
		}
		return result;
	}
	
	private float getCloseValue(String zerodhaId) {
		HttpCalls http = new HttpCalls();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currDate = dateFormat.format(date);
		currDate = "2019-03-22";
		String zerodhaUrl = "https://kitecharts-aws.zerodha.com/api/chart/"+zerodhaId+"/5minute?public_token=9ALGkNk3EMP2y4fFESMptluAkgn76990&user_id=YA6184&api_key=kitefront&access_token=&from="+currDate+"&to="+currDate+"&ciqrandom=1553015806671";
		return http.get(zerodhaUrl).getJSONObject("data").getJSONArray("candles").getJSONArray(0).getFloat(4);
	}
	
	private float getLtpValue(String zerodhaId) {
		HttpCalls http = new HttpCalls();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currDate = dateFormat.format(date);
		currDate = "2019-03-22";
		String zerodhaUrl = "https://kitecharts-aws.zerodha.com/api/chart/"+zerodhaId+"/minute?public_token=9ALGkNk3EMP2y4fFESMptluAkgn76990&user_id=YA6184&api_key=kitefront&access_token=&from="+currDate+"&to="+currDate+"&ciqrandom=1553015806671";
		JSONArray arr = http.get(zerodhaUrl).getJSONObject("data").getJSONArray("candles");
		return arr.getJSONArray(arr.length() - 1).getFloat(4);
	}
	
	private float getBuyAbove(float close) {
		return (float) Math.pow(Math.sqrt(close) + 0.0625,2);
	}
	
	private float getSellBelow(float close) {
		return (float) Math.pow(Math.sqrt(close) - 0.0625,2);
	}

}
