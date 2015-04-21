
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

import com.googlecode.objectify.ObjectifyService;

import javax.servlet.http.HttpServlet;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class ValueTransformerTest extends HttpServlet {
	static{
		ObjectifyService.register(PointStatistic.class);
	}
	private static final Logger log = Logger.getLogger("CLASS");

	private final static String USER_AGENT = "Mozilla/5.0";
	static JsonParser jp = new JsonParser();
	static JsonObject jo;
	static JsonObject rates;
	static HashMap<Integer, SortedSet<WeekWeighting>> ww = new HashMap<Integer, SortedSet<WeekWeighting>>();
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	System.out.println("Performing parse");
		initWeekWeighting();
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 3);
		Calendar endDate = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String url = "http://api.fixer.io/";
		String suffix_url = "?base=USD";
		String url_final = "";
		int baseDay = 0;
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);
		int currentMonth = cal.get(Calendar.MONTH);
		int currentYear = cal.get(Calendar.YEAR);
		int weekOfYear = 0;
		HashMap<String, ArrayList<Double>> weeklySum = new HashMap<String,ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> monthlySum = new HashMap<String,ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> yearlySum = new HashMap<String,ArrayList<Double>>();

		String response = "";
		weeklySum = emptyHash(weeklySum);
		monthlySum = emptyHash(monthlySum);
		yearlySum = emptyHash(yearlySum);
		ArrayList<Double> change;
		ArrayList<Double> inflection;

		for( ;checkDates(cal, endDate); cal = addDay(cal)) {

			response = doRequest(url + simpleDateFormat.format(cal.getTime()) + suffix_url);
			jo = (JsonObject) jp.parse(response);

			rates = (JsonObject) jp.parse(jo.get("rates").toString());
			for(CurrencyType ct : CurrencyType.values()) {


				if(rates.get(ct.name()) != null) {
					ArrayList tmp = weeklySum.get(ct.name());
					tmp.add(Double.parseDouble(rates.get(ct.name()).toString()));
					weeklySum.put(ct.name(),tmp);
					tmp = monthlySum.get(ct.name());
					tmp.add(Double.parseDouble(rates.get(ct.name()).toString()));
					monthlySum.put(ct.name(),tmp);
					tmp = yearlySum.get(ct.name());
					tmp.add(Double.parseDouble(rates.get(ct.name()).toString()));
					yearlySum.put(ct.name(),tmp);
				}
			}
			if(baseDay++ % 7 == 6 ) {
				//dp = new DataPoint()
				SortedSet<WeeklyRanking> ss = processDataset(TimePeriod.WEEK, weeklySum, cal);
				weeklySum = emptyHash(weeklySum);
				factorWeek(weekOfYear++ % 52, ss);
				
				
			}
			if(cal.get(Calendar.MONTH) != currentMonth) {
				processDataset(TimePeriod.MONTH, monthlySum, cal);
				currentMonth = cal.get(Calendar.MONTH);
				monthlySum = emptyHash(monthlySum);
			}
			if(cal.get(Calendar.YEAR) != currentYear) {
				processDataset(TimePeriod.YEAR, yearlySum, cal);
				currentYear = cal.get(Calendar.YEAR);
				yearlySum = emptyHash(yearlySum);
				baseDay = 0;
				weeklySum = emptyHash(weeklySum);
				cal.set(Calendar.DAY_OF_MONTH, 3);

				

			}
			if(baseDay++ % 7 == 6 ) {
				//dp = new DataPoint()
				SortedSet<WeeklyRanking> ss = processDataset(TimePeriod.WEEK, weeklySum, cal);
				weeklySum = emptyHash(weeklySum);
				factorWeek(weekOfYear++ % 52, ss);
				
				
			}
		}
		for( int i = 0; i < 5; i++ ) {
			SimuTest.simuExchange(2009+ i,export(ww));
		}

	}

    public static TreeMap<Integer, CurrencyType> export(HashMap<Integer, SortedSet<WeekWeighting>> ww) {
    	boolean testAndSet = false;
    	Integer week = 1;
    	TreeMap<Integer, CurrencyType> optimalList = new TreeMap<Integer, CurrencyType>();
    	for(int j = 0; j < 52; j++) {//Sorted or unsorted
			int l = ww.get(j).size();
			TreeSet<WeekWeighting> wwts = new TreeSet();
			for(WeekWeighting ws: ww.get(j)) {
				//System.out.print("("+ ws.ct + "," + (double)((double)ws.value / (double) ws.dataEntries) + ") | ");

				wwts.add(ws);
			}
			ww.put(j, wwts);
			testAndSet = true;
			for(WeekWeighting ws: wwts) {
				if(testAndSet) {
					optimalList.put(j+1, ws.ct);
				}
				testAndSet = false;
			//	System.out.print(ws.ct + " " + String.format("%04.2f",  (double)((double)ws.value / (double) ws.dataEntries)) + " | ");
			}	
		//	System.out.println("");
		}
    	return optimalList;
    }
	public static void initWeekWeighting() {
		// TODO Auto-generated method stub
		for(int j = 0; j < 52; j++ ) {
			TreeSet<WeekWeighting> ts = new TreeSet();

			for(CurrencyType ct: CurrencyType.values()) {
				ts.add(new WeekWeighting(ct));
			}
			ww.put(j, ts);

		}
	}

	public static void factorWeek(int i, SortedSet<WeeklyRanking> ss) {
		// TODO Auto-generated method stub
		int size = 0;
		for(WeeklyRanking st: ss) {
			size++;
			for(WeekWeighting ws: ww.get(i)) {
				if(st.ct == ws.ct ) {
					ws.addValue(size);
				}
			}
		}
	}

	public static SortedSet<WeeklyRanking> processDataset(TimePeriod tp, HashMap<String, ArrayList<Double>> hm, Calendar cal) {
		ArrayList<Double> change;
		ArrayList<Double> inflection;
		SortedSet<WeeklyRanking> ranking = new TreeSet<WeeklyRanking>();

		for(CurrencyType ct : CurrencyType.values()) {
			ArrayList<Double> dataset = hm.get(ct.name());
			if(dataset.size() != 0) {
				double mean = calcMean(dataset);
				double deviation = calcDeviation(dataset, mean);
				double slope = calcSlope(dataset);
				change = derive(dataset);
				double c_mean = calcMean(change);
				double c_deviation = calcDeviation(change, c_mean);
				double c_slope = calcSlope(change);
				inflection = derive(change);
				double i_mean = calcMean(inflection);
				double i_deviation = calcDeviation(inflection, i_mean);
				double i_slope = calcSlope(inflection);
				PointStatistic ps = new PointStatistic(
						mean,deviation,slope,
						c_mean,c_deviation,c_slope,
						i_mean,i_deviation,i_slope, 
						cal.get(Calendar.DAY_OF_MONTH),
						cal.get(Calendar.MONTH), 
						cal.get(Calendar.YEAR), 
						tp,
						ct,
						CurrencyType.USD, 
						dataset.get(0),
						dataset.get(dataset.size() - 1)
						);
				ranking.add(new WeeklyRanking(i_slope, i_mean, c_slope, c_mean, slope, ct));

				ofy().save().entity(ps).now(); 

			}


		}
		return ranking;
	}
	public static ArrayList<Double> derive(ArrayList<Double> arg) {
		ArrayList<Double> deriviation = new ArrayList<Double>();
		for(int i = 0; i < arg.size() - 1; i++ ){
			deriviation.add(arg.get(i + 1) - arg.get(i));
		}
		return deriviation;
	}
	private static double calcSlope(ArrayList<Double> dataset) {
		// TODO Auto-generated method stub
		if(dataset.size() > 0 ){
			return (dataset.get(dataset.size()-1) - dataset.get(0)) / dataset.size();
		}else {
			return -1;
		}
		
	}


	private static double calcDeviation(ArrayList<Double> dataset, double mean) {
		// TODO Auto-generated method stub
		double sum = 0;
		
		for(Double a: dataset) {
			sum += (a - mean) * (a - mean);
		}
		return Math.sqrt(sum / dataset.size());
		
	}


	private static double calcMean(ArrayList<Double> dataset) {
		// TODO Auto-generated method stub
		double size = 0;
		for(Double a: dataset) {
			size += a;
		}
		return size / dataset.size();
	}


	public static HashMap<String, ArrayList<Double>> emptyHash(HashMap hm) {
		for(CurrencyType ct : CurrencyType.values()) {
			hm.put(ct.name(),new ArrayList<Double>());
		}
		return hm;
	}
	
	public static String doRequest(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private static Calendar addDay(Calendar cal) {
		// TODO Auto-generated method stub
		cal.add(Calendar.DATE, 1);  // number of days to add
		return cal;
		
	}

	private static boolean checkDates(Calendar cal, Calendar endDate) {
		// TODO Auto-generated method stub
		if(cal.compareTo(endDate ) < 0) {
			return true;
		}
		return false;
	}

}
