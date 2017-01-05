package info_2_ex_8;

import java.util.GregorianCalendar;

/**
 * The main part of the calculator doing the calculations.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2008.03.30
 */
public class CalcEngine {
	// The calculator's state is maintained in three fields:
	// buildingDisplayValue, haveLeftOperand, and lastOperator.

	// Are we already building a value in the display, or will the
	// next digit be the first of a new one?
	private boolean buildingDisplayValue;
	// Has a left operand already been entered (or calculated)?
	private boolean haveLeftOperand;
	// The most recent operator that was entered.
	private char lastOperator;

	// The current value (to be) shown in the display.
	private int displayValue;
	// The value of an existing left operand.
	private int leftOperand;

	private String theDay;
	private boolean dayOnDisplay;

	private String dateString;
	private boolean buildingDate;
	private boolean backToGreg;

	/**
	 * Create a CalcEngine.
	 */
	public CalcEngine() {
		clear();
	}

	/**
	 * @return The value that should currently be displayed on the calculator
	 *         display.
	 */
	public String getDisplayValue() {
		if (dateString != "")
			return dateString;
		else if (dayOnDisplay) {
			return theDay;
		} else {
			return "" + displayValue;
		}

	}

	public void getWeekDay() {
		if (!dayOnDisplay) {
			if (dateString != "") {
				parseDateIfNecessary();
				backToGreg = true;
			}
			theDay = new JulianDate(displayValue).getWeekday();
			dayOnDisplay = true;

		} else{
			if(backToGreg){ julianToGregorian();}
			dayOnDisplay = false;}

	}

	/**
	 * A number button was pressed. Either start a new operand, or incorporate
	 * this number as the least significant digit of an existing one.
	 * 
	 * @param number
	 *            The number pressed on the calculator.
	 */
	public void numberPressed(int number) {
		if (buildingDate) {
			dateString += number;
		} else if (buildingDisplayValue) {
			// Incorporate this digit.
			displayValue = displayValue * 10 + number;
		} else {
			// Start building a new number.
			displayValue = number;
			buildingDisplayValue = true;
		}
	}

	public void dot() {
		if (!buildingDisplayValue)
			keySequenceError();

		if (!buildingDate) {
			dateString += displayValue;
			dateString += ".";
			displayValue = 0;
			buildingDate = true;
		} else {

			dateString += ".";
		}
	}

	/**
	 * The 'plus' button was pressed.
	 */
	public void plus() {
		applyOperator('+');
	}

	/**
	 * The 'minus' button was pressed.
	 */
	public void minus() {
		applyOperator('-');
	}

	/**
	 * The '=' button was pressed.
	 */
	public void equals() {
		// This should completes the building of a second operand,
		// so ensure that we really have a left operand, an operator
		// and a right operand.
		if (haveLeftOperand && lastOperator != '?' && buildingDisplayValue) {
//			parseDateIfNeccessary();
			calculateResult();
			lastOperator = '?';
			buildingDisplayValue = false;
		} else {
			keySequenceError();
		}
	}

	// converts gregorian date to julian date
	public void parseDateIfNecessary() {
		if (buildingDate) {
			String[] date = dateString.split("\\.");
			
			if(date.length != 3)
			{
				dateError();
				return;
			}
			
			displayValue = (int) (new JulianDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]),
					Integer.parseInt(date[2]))).getDays();
			dateString = "";
			buildingDate = false;
		}
	}

	// converts julian date to gregorian date
	public void julianToGregorian() {
		JulianDate newGreg = new JulianDate(displayValue);
		dateString = newGreg.getGregDate();
		displayValue = 0;
		buildingDate = true;
	}

	/**
	 * The 'C' (clear) button was pressed. Reset everything to a starting state.
	 */
	public void clear() {
		lastOperator = '?';
		haveLeftOperand = false;
		buildingDisplayValue = false;
		displayValue = 0;
		theDay = "";
		dateString = "";
		buildingDate = false;
		dayOnDisplay = false;
	}

	/**
	 * @return The title of this calculation engine.
	 */
	public String getTitle() {
		return "Java Calculator";
	}

	/**
	 * @return The author of this engine.
	 */
	public String getAuthor() {
		return "David J. Barnes and Michael Kolling";
	}

	/**
	 * @return The version number of this engine.
	 */
	public String getVersion() {
		return "Version 1.0";
	}

	/**
	 * Combine leftOperand, lastOperator, and the current display value. The
	 * result becomes both the leftOperand and the new display value.
	 */
	private void calculateResult() {
		switch (lastOperator) {
		case '+':
			displayValue = leftOperand + displayValue;
			haveLeftOperand = true;
			leftOperand = displayValue;
			if(backToGreg){
				julianToGregorian();
				backToGreg = false;
			}
			
			break;
		case '-':
			if(buildingDate){
				calculateDaysBetween();
				
			} else{
			displayValue = leftOperand - displayValue;
			haveLeftOperand = true;
			leftOperand = displayValue;
			if(backToGreg){
				julianToGregorian();
				backToGreg = false;
			}
			}
			break;
		default:
			keySequenceError();
			break;
		}
	}
	
	
	private void calculateDaysBetween(){
		
		JulianDate fistDate = new JulianDate(leftOperand);
		parseDateIfNecessary();
		JulianDate secondDate = new JulianDate(displayValue);
		displayValue = Math.abs((int)fistDate.daysSince(secondDate));
		
		
		
	}

	/**
	 * Apply an operator.
	 * 
	 * @param operator
	 *            The operator to apply.
	 */
	private void applyOperator(char operator) {
		// If we are not in the process of building a new operand
		// then it is an error, unless we have just calculated a
		// result using '='.
		if (!buildingDisplayValue && !(haveLeftOperand && lastOperator == '?')) {
			keySequenceError();
			return;
		}

//		parseDateIfNeccessary();

		if (lastOperator != '?') {
			// First apply the previous operator.
			calculateResult();
		} else {
			// The displayValue now becomes the left operand of this
			// new operator.
			haveLeftOperand = true;
			if(dateString !=""){
				parseDateIfNecessary();
				backToGreg = true;
			}
			leftOperand = displayValue;
//			if(backToGreg) julianToGregorian();
		}
		lastOperator = operator;
		buildingDisplayValue = false;
	}

	/**
	 * Report an error in the sequence of keys that was pressed.
	 */
	private void keySequenceError() {
		System.out.println("A key sequence error has occurred.");
		// Reset everything.
		clear();
	}
	/**
	 * Report an error with the date format.
	 */
	private void dateError() {
		System.out.println("The entered date was not valid.");
		// Reset everything.
		clear();
	}
}