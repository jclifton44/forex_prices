import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SimuTest {
	//static String[] tradeList = {
		//"INR","MXN","MYR","RON","IDR","PHP","INR","ILS","IDR","IDR","INR","IDR","INR","PHP","BRL","INR","CNY","INR","MXN","HRK","IDR","INR","MXN","CNY","BRL","RUB","INR","INR","MXN","CNY","CNY","INR","RUB","INR","BRL","HRK","BRL","INR","IDR","PHP","THB","INR","CNY","BRL","PHP","TRY","JPY","MXN","IDR","RUB","THB","INR"
		//}
	static JsonParser jp = new JsonParser();
	static JsonObject jo;
	static JsonObject rates;
	public static void main(String[] args) throws IOException{
	    System.out.println("Testing...");
	    TreeMap<Integer, CurrencyType> tm = new TreeMap<Integer, CurrencyType>();
	    CurrencyType ct = CurrencyType.IDR;
	    for( int  i = 1 ; i < 50; i++) {
	    	if(i % 2 == 1) {
	    		tm.put(i, ct);
	    	} else {
	    		tm.put(i, CurrencyType.BGN);

	    	}
	    }
	    simuExchange(2011, tm);
	}
	public static void simuExchange(Integer year, TreeMap<Integer, CurrencyType> tm) throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String url = "http://api.fixer.io/";
		String suffix_url = "?base=USD";
		String response;
		double factor = -1.0d;
		double nextFactor = -1.0d;
		double returnFactor = 1.0d;
		CurrencyType lastCurrency = CurrencyType.USD;
		cal.add(Calendar.DAY_OF_MONTH, 3);

		for(Map.Entry<Integer, CurrencyType> entry: tm.entrySet()) {
			
			response = ValueTransformerTest.doRequest(url + simpleDateFormat.format(cal.getTime()) + suffix_url);
			cal.add(Calendar.DAY_OF_MONTH, 7);
			jo = (JsonObject) jp.parse(response);
			
			rates = (JsonObject) jp.parse(jo.get("rates").toString());

			//rates.get(entry.getValue().name()).toString();

			if(factor != -1) {
				if(rates.get(lastCurrency.name()) != null){
					
					nextFactor = Double.parseDouble(rates.get(lastCurrency.name()).toString());
					double gain = (double)((double)factor / (double)nextFactor);
					returnFactor *= gain;
				} 
			}
			if(rates.get(entry.getValue().name()) != null){
				factor = Double.parseDouble(rates.get(entry.getValue().name()).toString());
				lastCurrency = entry.getValue();
			} else {
				factor = 1.0d;
				lastCurrency = CurrencyType.USD;
			}

		}
		System.out.println(returnFactor);
		
	}
}
