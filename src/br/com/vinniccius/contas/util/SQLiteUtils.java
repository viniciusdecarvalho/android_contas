package br.com.vinniccius.contas.util;

import java.util.Date;

public class SQLiteUtils {

	public static class db {
		public static String dateString(Date date){
			return DateUtils.dateToString(date, DateUtils.DATE_DB);
		}
		
		public static String dateTimeString(Date date){
			return DateUtils.dateToString(date, DateUtils.DATETIME_DB);
		}
		
		public static Date dateString(String date) {
			return DateUtils.stringToDate(date, DateUtils.DATE_DB);
		}
		
		public static Date dateTimeString(String date) {
			return DateUtils.stringToDate(date, DateUtils.DATETIME_DB);
		}
	}
	
	public static class view {
		public static String dateString(Date date){
			return DateUtils.dateToString(date, DateUtils.DATE_VIEW);
		}
		
		public static String dateTimeString(Date date){
			return DateUtils.dateToString(date, DateUtils.DATETIME_VIEW);
		}
		
		public static Date dateString(String date) {
			return DateUtils.stringToDate(date, DateUtils.DATE_VIEW);
		}
		
		public static Date dateTimeString(String date) {
			return DateUtils.stringToDate(date, DateUtils.DATETIME_VIEW);
		}
	}
	
}
