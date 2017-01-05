package info_2_ex_8;

public class JulianDate implements JulianDateInterface {
	protected long days;
	int gregDay;
	int gregMonth;
	int gregYear;

	public JulianDate(int day, int month, int year) {
		this.days=gregToJulian(day,month,year);
		this.gregDay=day;
		this.gregMonth=month;
		this.gregYear=year;
	}
	public JulianDate(long julianDays) {
		this.days=julianDays;		

		//from https://en.wikipedia.org/wiki/Julian_day
		long f = days + 1401 + (((4 * days + 274277) / 146097) * 3) / 4 + (-38);
		
		long h = 5 * (((4 * f + 3) % 1461) / 4) + 2;
		gregDay = (int) (((h % 153)) / 5 + 1);
		gregMonth = (int) (((h / 153 + 2) % 12) + 1);
		gregYear = (int) (((4 * f + 3) / 1461) - 4716 + (12 + 2 - gregMonth) / 12);
	}
	
	private long gregToJulian (int day, int month, int year) {
		long julianDate;
		
		long a = ((14-month)/12);
		long y = year+4800-a;
		long m = month+12*a-3;
		
		julianDate = day+(((153*m+2)/5))+365*y+(y/4)-(y/100)+(y/400)-32045;
		
		return julianDate;
	}


	/* (non-Javadoc)
	 * @see JulianDateInterfaces#getDays()
	 */
	/* (non-Javadoc)
	 * @see JulianDateInterface#getDays()
	 */
	@Override
	public long getDays() {
		return this.days;
	}
	
	/* (non-Javadoc)
	 * @see JulianDateInterface#getGregDay()
	 */
	@Override
	public int getGregDay() {
		return this.gregDay;
	}
	
	/* (non-Javadoc)
	 * @see JulianDateInterface#getGregMonth()
	 */
	@Override
	public int getGregMonth() {
		return this.gregMonth;
	}
	
	/* (non-Javadoc)
	 * @see JulianDateInterface#getGregYear()
	 */
	@Override
	public int getGregYear() {
		return this.gregYear;
	}
	
	
	/* (non-Javadoc)
	 * @see JulianDateInterface#getGregYear()
	 */
	@Override
	public String getGregDate() {
		String fullDate = getGregDay() + "." + getGregMonth() + "." + getGregYear();
		
		
		return fullDate;
	}
	
	public long daysSince(JulianDateInterface julianDate) {

		return this.days-julianDate.getDays();

	}
	
	
	/* (non-Javadoc)
	 * @see JulianDateInterfaces#getWeekday()
	 */
	/* (non-Javadoc)
	 * @see JulianDateInterface#getWeekday()
	 */
	@Override
	public String getWeekday() {
		int weekday = (int)this.days%7;
		switch(weekday) {
			case 0: return "Monday";
			case 1: return "Tuesday";
			case 2: return "Wednesday";
			case 3: return "Thursday";
			case 4: return "Friday";
			case 5: return "Saturday";
			case 6: return "Sunday";
			default: return "Error";
		}
	}
	
	
	@Override
	public String toString() {
		return "jd_" + days;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (days ^ (days >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JulianDate other = (JulianDate) obj;
		if (days != other.days)
			return false;
		return true;
	}

	
	
	public static void main(String[] args) {
		JulianDate date = new JulianDate(50);
		System.out.println(date.getGregYear());
	}
	
	
}
