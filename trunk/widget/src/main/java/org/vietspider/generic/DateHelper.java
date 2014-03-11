/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.vietspider.generic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {

private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
	
	public static long daysBetween(Calendar start, Calendar end) {		
		// create copies
		GregorianCalendar startDate = new GregorianCalendar(Locale.getDefault());
		GregorianCalendar endDate = new GregorianCalendar(Locale.getDefault());

		// switch calendars to pure Julian mode for correct day-between calculation, from the Java API:
		// - To obtain a pure Julian calendar, set the change date to Date(Long.MAX_VALUE).
		startDate.setGregorianChange(new Date(Long.MAX_VALUE));
		endDate.setGregorianChange(new Date(Long.MAX_VALUE));

		// set them
		startDate.setTime(start.getTime());
		endDate.setTime(end.getTime());

		// force times to be exactly the same
		startDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		startDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);
	
		// now we should be able to do a "safe" millisecond/day caluclation to get the number of days
		long endMilli = endDate.getTimeInMillis();
		long startMilli = startDate.getTimeInMillis();

		// calculate # of days, finally
		long diff = (endMilli - startMilli) / MILLISECONDS_IN_DAY;
		
		return diff;
	}

	public static long daysBetween(Date start, Date end) {
		Calendar dEnd = Calendar.getInstance(Locale.getDefault());
		Calendar dStart = Calendar.getInstance(Locale.getDefault());
		dEnd.setTime(end);
		dStart.setTime(start);
		return daysBetween(dStart, dEnd);
	}	

    public static boolean isToday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return isToday(cal);
    }

    public static boolean isToday(Calendar cal) {
        Calendar today = Calendar.getInstance();

        if (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts a date to Julian date. Used for date differentials.
     * See http://scienceworld.wolfram.com/astronomy/JulianDate.html for super-mess.
     *
     * @param date Date to convert
     * @return Julian date in double
     */
    public static double toJulian(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);

        gc.set(Calendar.HOUR, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);

        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        month += 1; // julian is not zero based
        int day = gc.get(Calendar.DATE);

        return toJulian(year, month, day, 0, 0, 0);
    }

    // from http://www.koders.com/java/fidD951168ECB41F635425E554A6771D4DBC4C3E29B.aspx
    // only one I found that actually calculates it correctly
    private static double toJulian(int year, int month, int day, int hour, int min, int sec) {
        int y = year;
        int m = month;
        double timeofday;
        int ijulian;
        int IGREG = 15 + 31 * (10 + 12 * 1582); // 10/15/1582

        int adj;

        if (y < 0) y = y + 1;
        if (m > 2) {
            m = m + 1;
        } else {
            y = y - 1;
            m = m + 13;
        }

        ijulian = (int) (365.25 * y) + (int) (30.6001 * m) + day + 1720995;

        if (day + 31 * (m + 12 * y) >= IGREG) { // change for Gregorian calendar
            adj = y / 100;
            ijulian = ijulian + 2 - adj + adj / 4;
        }

        timeofday = hour / 24.0 + min / 1440.0 + sec / 86400.0;
        return ijulian + timeofday;
    }

    public static String getDate(Calendar cal, String dateFormat) {
        Calendar toUse = (Calendar) cal.clone();
        toUse.add(Calendar.MONTH, -1);

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setLenient(true);
        return df.format(cal.getTime());
    }
    
    public static boolean sameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        return sameDate(cal1, cal2);
    }

    public static boolean sameDate(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        }

        return false;
    }
    
    public static Date getDate(String str, String dateFormat) throws Exception
    {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setLenient(false);
        return df.parse(str);
    }

    public static String getFormattedDate(Date date, String dateFormat)
    {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setLenient(true);

        return df.format(date);
    }
}
