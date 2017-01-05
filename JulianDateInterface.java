package info_2_ex_8;


public interface JulianDateInterface {

	/* (non-Javadoc)
	 * @see JulianDateInterfaces#getDays()
	 */
	long getDays();

	int getGregDay();

	int getGregMonth();

	int getGregYear();

	/* (non-Javadoc)
	 * @see JulianDateInterfaces#getWeekday()
	 */
	String getWeekday();

	String getGregDate();

}