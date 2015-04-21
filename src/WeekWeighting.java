
public class WeekWeighting implements Comparable {
	public CurrencyType ct;
	int dataEntries;
	int index;
	public double value = 0;;
	public WeekWeighting(CurrencyType ct) {
		this.ct = ct;
		dataEntries = 1;
		
	}
	public void addValue(double value) {
		this.value += value;
		dataEntries++;
	}
	public CurrencyType getCT() {
		return this.ct;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(((double)((double)this.value / (double)this.dataEntries)) < ((double)((double)((WeekWeighting)o).value / (double)((WeekWeighting)o).dataEntries))) {
			return -1;
		} else if (((double)((double)this.value / (double)this.dataEntries)) == ((double)((double)((WeekWeighting)o).value / (double)((WeekWeighting)o).dataEntries))) {
			if(((WeekWeighting)o).ct == this.ct){
				return 0;
			}
			return 1;
		} else {
			return 1;
		}
		
	}
	
}
