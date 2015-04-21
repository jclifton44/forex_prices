import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class Transform extends HttpServlet{
	static{
		ObjectifyService.register(PointStatistic.class);
	}
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	System.out.println("Doing Get");
    	Integer year = 2000;
    	Integer week = 1;
		SortedSet<WeeklyRanking> ranking = new TreeSet<WeeklyRanking>();
	/*	for(int rating1 = -10; rating1 < 11; rating1++) {
			for(int rating2 = -10; rating2 < 11; rating2++) {
				for(int rating3 = -10; rating3 < 11; rating3++) {
					for(int rating4 = -10; rating4 < 11; rating4++) {
						WeeklyRanking.weightingS = rating1;
						WeeklyRanking.weighting1S = rating2;
						WeeklyRanking.weighting1A = rating3;
						WeeklyRanking.weighting2A = rating4;*/
						ValueTransformerTest.initWeekWeighting();
						for(year = 2000; year < 2015; year++) {
							for(week = 0; week < 52; week++) {
						    	Query<PointStatistic> keys = ofy().load().type(PointStatistic.class).filter("year", year).filter("week", week).filter("tp", TimePeriod.WEEK);
						    	for(PointStatistic p: keys) {
									ranking.add(new WeeklyRanking(p.double_slope, p.double_average, p.first_slope, p.first_average, p.slope, p.to));
					
						    	}
								ValueTransformerTest.factorWeek(week, ranking);
								ranking.clear();
	
							}
						}
						//System.out.print(rating1 + "," + rating2 + "," + rating3 + "," + rating4 + ", ");
						SimuTest.simuExchange(Integer.parseInt(req.getParameter("year")),ValueTransformerTest.export(ValueTransformerTest.ww));
			
					/*}
				}
			}
		}*/

    	System.out.println("Finishing");
    }
}
