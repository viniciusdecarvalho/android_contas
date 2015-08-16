package br.com.vinniccius.contas.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.DatePicker;

public class DateUtils {
	
	static final Locale LOCALE = Locale.getDefault();
	public static final String TIME = "hh:mm:ss";
	public static final String DATE_VIEW = "dd/MM/yyyy";
	public static final String DATE_DB = "yyyy-MM-dd";
	public static final String DATETIME_VIEW = "dd/MM/yyyy " + TIME;
	public static final String DATETIME_DB = "yyyy-MM-dd " + TIME;
	
	public static Date stringToDate(String date, String pattern) {
		try {
			return new SimpleDateFormat(pattern, LOCALE).parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String dateToString(Date date, String pattern){
		if (date != null) {
			return new SimpleDateFormat(pattern, LOCALE).format(date);
		}
		return null;
	}

	public static boolean isDate(String dateString, String pattern) {
		return stringToDate(dateString, pattern) != null;
	}	
	
	public static Calendar getDatePickerDate(DatePicker picker) {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.YEAR, picker.getYear());
		instance.set(Calendar.MONTH, picker.getMonth());
		instance.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
		return instance;
	}

	public static final String DATE_MASK = "##/##/####";

}
