package com.trade.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.trade.util.HttpCalls;
import com.trade.util.JsonFileReader;

public class LauncherFuture {
	static {
		System.setProperty("http.agent", "Chrome");
	}

	public static void main(String[] args) {
		LauncherFuture l = new LauncherFuture();
		JSONObject scripts, fno = null;
		JSONArray topGainer, topLoser, rsi, rsi2030, rsi3070, rsi7080  = null;
		JsonFileReader fileReader = new JsonFileReader();
		HttpCalls http = new HttpCalls();
		fno = fileReader.readJsonObjectFile("march-fno.json");
		scripts = fileReader.readJsonObjectFile("valid.json");
		
		topGainer = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/gainers?pageno=1&pagesize=30&sortby=percentchange&sortorder=desc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");
		topLoser = http.get("https://etmarketsapis.indiatimes.com/ET_Stats/losers?pageno=1&pagesize=30&sortby=percentchange&sortorder=asc&sort=intraday&exchange=nse&duration=1d&marketcap=largecap%2Cmidcap").getJSONArray("searchresult");
		rsi2030 = http.get("https://sas.indiatimes.com/TechnicalsClient/getRSI.htm?crossovertype=RSI_BETWEEN_20_AND_30&pagesize=100000&exchange=50&pageno=1&sortby=volume&sortorder=asc&indexid=2365&company=true&ctype=RSI&col_show=both").getJSONArray("searchResult");
		rsi3070 = http.get("https://sas.indiatimes.com/TechnicalsClient/getRSI.htm?crossovertype=RSI_BETWEEN_30_AND_70&pagesize=100000&exchange=50&pageno=1&sortby=volume&sortorder=asc&indexid=2365&company=true&ctype=RSI&col_show=both").getJSONArray("searchResult");
		rsi7080 = http.get("https://sas.indiatimes.com/TechnicalsClient/getRSI.htm?crossovertype=RSI_BETWEEN_70_AND_80&pagesize=100000&exchange=50&pageno=1&sortby=volume&sortorder=asc&indexid=2365&company=true&ctype=RSI&col_show=both").getJSONArray("searchResult");
		
		for(int i = 0; i < topLoser.length(); i++) {
			topGainer.put(topLoser.getJSONObject(i));
		}
		
		rsi = rsi2030;
		for(int i = 0; i < rsi3070.length(); i++) {
			rsi.put(rsi3070.getJSONObject(i));
		}
		for(int i = 0; i < rsi7080.length(); i++) {
			rsi.put(rsi7080.getJSONObject(i));
		}
		for(int i = 0; i < topGainer.length(); i++) {
			String symbol = topGainer.getJSONObject(i).get("nseScripCode").toString();
			float percentageChange = Float.parseFloat(topGainer.getJSONObject(i).get("percentChange").toString());
			float symbolRsi = l.getSymbolRsi(symbol, rsi);
			symbol = symbol.substring(0, symbol.length() - 2);
			String type ="";
			if(percentageChange > 0) {
				type = "GAINER";
			}else {
				type = "LOSER";
			}
			if(scripts.getJSONArray("Symbol").toString().contains(symbol) ) {
				symbol = symbol+"19MARFUT";
				if(!"NH19MARFUT".equalsIgnoreCase(symbol)) {
					float close = l.getCloseValue(fno.getJSONObject(symbol).get("instrument_token").toString());
					float cltp = l.getLtpValue(fno.getJSONObject(symbol).get("instrument_token").toString());
					if(cltp > 125) {
						int lotSize = (int) fno.getJSONObject(symbol).get("lot_size");
						System.out.println("TRG = "+type+" | "+ symbol + "("+lotSize+") | BUY ABOVE = "+ l.getBuyAbove(close) + " | LTP = "+ cltp + " | SELL BELOW = "+ l.getSellBelow(close) + " | % Change = "+ percentageChange + " | RSI = "+ symbolRsi);
						System.out.println();
					}
				}
			}
		}
	}
	
	public float getCloseValue(String zerodhaId) {
		HttpCalls http = new HttpCalls();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currDate = dateFormat.format(date);
		//currDate = "2019-03-20";
		String zerodhaUrl = "https://kitecharts-aws.zerodha.com/api/chart/"+zerodhaId+"/5minute?public_token=9ALGkNk3EMP2y4fFESMptluAkgn76990&user_id=YA6184&api_key=kitefront&access_token=&from="+currDate+"&to="+currDate+"&ciqrandom=1553015806671";
		return http.get(zerodhaUrl).getJSONObject("data").getJSONArray("candles").getJSONArray(0).getFloat(4);
	}
	
	public float getLtpValue(String zerodhaId) {
		HttpCalls http = new HttpCalls();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currDate = dateFormat.format(date);
		//currDate = "2019-03-20";
		String zerodhaUrl = "https://kitecharts-aws.zerodha.com/api/chart/"+zerodhaId+"/minute?public_token=9ALGkNk3EMP2y4fFESMptluAkgn76990&user_id=YA6184&api_key=kitefront&access_token=&from="+currDate+"&to="+currDate+"&ciqrandom=1553015806671";
		JSONArray arr = http.get(zerodhaUrl).getJSONObject("data").getJSONArray("candles");
		return arr.getJSONArray(arr.length() - 1).getFloat(4);
	}
	
	public float getBuyAbove(float close) {
		return (float) Math.pow(Math.sqrt(close) + 0.0625,2);
	}
	
	public float getSellBelow(float close) {
		return (float) Math.pow(Math.sqrt(close) - 0.0625,2);
	}
	
	public float getSymbolRsi(String symbol, JSONArray rsi) {
		for(int i = 0; i < rsi.length(); i++) {
			if(rsi.getJSONObject(i).get("scripCode").toString().equalsIgnoreCase(symbol)) {
				return rsi.getJSONObject(i).getFloat("currentRsi");
			}
		}
		return 0;
	}

}
