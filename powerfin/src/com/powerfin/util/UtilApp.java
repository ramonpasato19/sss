package com.powerfin.util;

import java.math.*;
import java.text.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

import com.powerfin.exception.*;

public class UtilApp {

	/**
	 * Fecha usada para los campos FHASTA como un java.sql.Date.
	 */
	public static final Date DEFAULT_EXPIRY_DATE;

	public static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	public static SimpleDateFormat completeFormatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static DecimalFormat formatDecimal = new DecimalFormat("########.##");
	public static DecimalFormat formatInteger = new DecimalFormat("###");

	public static boolean isNullOrEmpty(String field)
	{
		if(field==null)
			return true;
		if (field.isEmpty())
			return true;
		return false;
	}
	
	public static boolean fieldIsEmpty(String value) {
		if (value == null || value.trim().isEmpty())
			return true;
		return false;
	}

	public static String dateToString(Date date) {
		if (date == null)
			return "null_date_to_covert";

		return formatDate.format(date);
	}

	public static String dateToString(Date date, String format) {
		if (date == null)
			return "null_date_to_covert";
		SimpleDateFormat formatDate = new SimpleDateFormat(format, Locale.getDefault());
		return formatDate.format(date);
	}
	
	public static Date stringToDate(String dateString) throws ParseException {
		if (dateString == null)
			throw new InternalException("string_to_convert_is_null");

		return formatDate.parse(dateString);
	}
	
	public static Date stringToDate(String dateString, String format) throws ParseException {
		if (dateString == null)
			throw new InternalException("string_to_convert_is_null");
		SimpleDateFormat formatDate = new SimpleDateFormat(format, Locale.getDefault());
		return formatDate.parse(dateString);
	}
	
	static {
		try {
			DEFAULT_EXPIRY_DATE = formatDate.parse("2999-12-31");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidDate(String date) {
		try {
			formatDate.setLenient(false);
			formatDate.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidDecimalNumber(String number) {
		try {
			new BigDecimal(number);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isValidIntegerNumber(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static BigDecimal valueToOfficialValue(BigDecimal value, BigDecimal exchangeRate) {
		return roundUP(value.multiply(exchangeRate),2);
	}
	
	public static BigDecimal roundUP(BigDecimal value, int scale)
	{
		return value.setScale(scale, RoundingMode.HALF_UP);
	}
	
	public static int getDaysCountBetweenDates(Date dateBefore, Date dateAfter) {
		LocalDate localDateBefore = LocalDate.parse(dateToString(dateBefore));
		LocalDate localDateAfter = LocalDate.parse(dateToString(dateAfter));
	    return new Long(ChronoUnit.DAYS.between(localDateBefore, localDateAfter)).intValue();
	}
	
	public static int getSecondsCountBetweenDates(Date dateBefore, Date dateAfter) {
		return (int) ((dateAfter.getTime()-dateBefore.getTime())/1000);
	}
	
	public static boolean isGreaterThanZero(BigDecimal value) throws Exception
	{
		if (value == null)
			return false;
		return value.compareTo(BigDecimal.ZERO)>0;
	}
	
}
