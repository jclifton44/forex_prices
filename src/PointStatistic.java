import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


@Entity
public class PointStatistic implements StatisticUnit {
	public double standard_deviation = 0;
	public double average = 0;
	public double slope = 0;
	public double first_standard_deviation = 0;
	public double first_average = 0;
	public double first_slope = 0;
	public double double_standard_deviation = 0;
	public double double_average = 0;
	public double double_slope = 0;
	@Index public int week = 0;
	@Index public int month = 0;
	@Index public int year = 0;
	@Index public TimePeriod tp;
	public CurrencyType from;
	@Index public CurrencyType to;
	public Double beginning_price;
	public Double closing_price;
    @Id Long id;
    @Index String license;
	public PointStatistic( double average, double sd, double slope, double caverage,double csd,  double cslope, double iaverage,double isd,  double islope, int week, int month, int year, TimePeriod tp, CurrencyType to, CurrencyType from,Double beginning_price, Double closing_price) {

		this.standard_deviation = sd;
		this.average = average;
		this.slope = slope;
		this.first_standard_deviation = csd;
		this.first_average = caverage;
		this.first_slope = cslope;
		this.double_standard_deviation = isd;
		this.double_average = iaverage;
		this.double_slope = islope;
		this.week = week;
		this.month = month;
		this.year = year;
		this.tp = tp;
		this.to = to;
		this.from = from;
		this.beginning_price = beginning_price;
		this.closing_price = closing_price;
	}
	public PointStatistic() {}
	public String toString() {
		return to + ": " + standard_deviation + " " + first_standard_deviation + " " + double_standard_deviation + " " + tp;
	}
}
