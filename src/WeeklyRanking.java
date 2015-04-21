
public  class WeeklyRanking implements Comparable {
	static Integer weightingS = 5;
	static Integer weighting1A = 4;
	static Integer weighting2A = 3;
	
	double secondSlope = 0;
	double secondAverage = 0;
	double firstSlope = 0;
	double firstAverage = 0;
	double slope;
	public CurrencyType ct; 
	public WeeklyRanking(double secondSlope, double secondAverage, double firstSlope, double firstAverage, double slope, CurrencyType ct) {
		this.slope = slope;
		this.secondSlope = secondSlope;
		this.secondAverage = secondAverage;
		this.firstSlope = firstSlope;
		this.firstAverage = firstAverage;
		this.ct = ct;
	}
	public CurrencyType getCT() {
		return this.ct;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		//This is subject to change.
		int positive = 0;
		int negative = 0; 
		if(((WeeklyRanking)o).secondAverage > secondAverage) {
			positive += weighting2A;
		} else {
			negative += weighting2A;
		}

		if(((WeeklyRanking)o).firstAverage > firstAverage) {
			positive += weighting1A;
		} else {
			negative += weighting1A;
		}
		if(((WeeklyRanking)o).slope > slope) {
			positive += weightingS;
		} else {
			negative += weightingS;

		}
		if(positive > negative) {
			return 1;
		} else {
			return -1;
		}
	}

}
