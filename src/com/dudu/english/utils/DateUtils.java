package com.dudu.english.utils;

import java.util.Calendar;

public class DateUtils {

	public static Calendar stringToCal(String dateStr) {
		// 2023-03-18T16:07:03.547Z
		String yearStr = dateStr.substring(0, 4);
		String monthStr = dateStr.substring(5, 7);
		String dayStr = dateStr.substring(8, 10);
		String hourStr = dateStr.substring(11, 13);
		String minutesStr = dateStr.substring(14, 16);
		
		int year = Integer.parseInt(yearStr);
		int month = Integer.parseInt(monthStr) - 1;
		int day = Integer.parseInt(dayStr);
		int hour = Integer.parseInt(hourStr);
		int minutes = Integer.parseInt(minutesStr);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 0);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minutes);
		return cal;
	}
	
}
