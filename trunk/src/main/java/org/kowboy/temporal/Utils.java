/*
 * Temporal Collections - Hibernate implementation of temporal data patterns.
 * Copyright (C) 2008  Craig McDaniel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kowboy.temporal;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Utils {

    /**
     * Convenience method for Date math.
     *
     * @param date The source date.
     * @param days The number of days to add to the source date
     *      (can be negative).
     * @return The new Date.
     */
    public static java.util.Date addDays(java.util.Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }
    
    public static long daysBetween(Date from, Date to) {
        long t1 = to.getTime() / 1000;
        long t2 = from.getTime() / 1000;
        long d = t1 - t2;
        long result = d / 86400;
        
        return result;
    }

    /**
     * This method return the maximum number of days in a months. It also checks for the leap
     * year inside it. Checks for the following
     * <li>Check for Month (1) Month is Feb, also Check For Leap Year) (2)Month
     * is Odd(Jan,Mar) Or Even Or August(31 Days)
     *
     * @param date
     *            for which max days are to be returned.
     * @return int no of max days in a months.
     */

    public static int getNoOfDaysInMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Check if the passed year is a leap Year or not. Year is a leap year if
     * evenly divisible by 4 , but not 100, unless it's divisible by 400.
     *
     * @param inYear
     *            value
     * @return boolean true if year is a leap year else false.
     */
    public static boolean isLeapYear(int inYear) {
        return ((inYear % 4 == 0)) && ((inYear % 100 != 0))
                || ((inYear % 400 == 0));
    }

    /**
     * This method checks for the difference between dates and returns true if
     * toDate is exactly 1 day ahead of fromDate.
     *
     * @param from
     *            date instance
     * @param to
     *            date instance
     * @return boolean value
     *
     */
    public static boolean isOneDayBefore(Date from, Date to) {
    	long d = daysBetween(from, to);
    	return d == 1;
    }

    public static String join(Iterable<String> collection, String separator) {
		StringBuffer result = new StringBuffer();
		for (Iterator<String> iter = collection.iterator(); iter.hasNext();) {
			result.append(iter.next());
			if (iter.hasNext())
				result.append(separator);
		}
		return result.toString();
	}

    /**
     * Convenient replacement for deprecated Date constructor.
     *
     * @param year the year as you desire it (2005 = 2005)
     * @param month the month as you desire it (2 = feb)
     * @param day the day of the month
     * @return a java.util.Date instance with your values
     */
    public static java.util.Date newDate(int year, int month, int day) {
        return newDate(year, month, day, 0, 0, 0);
    }

    /**
     * Crappy method to try to isolate Date deprecated calls AND to fix a ton of
     * Date bugs throughout our code.
     *
     * @param year the year as you desire it (2005 = 2005)
     * @param month the month as you desire it (2 = feb)
     * @param day the day of the month
     * @param hour the hour
     * @param min the minute
     * @param sec the second
     * @return a java.util.Date instance with your values
     */
    public static java.util.Date newDate(int year, int month, int day, int hour, int min, int sec) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month - 1, day, hour, min, sec);
        return c.getTime();
    }
    
	/**
     * Convenient equals() method that checks to see if the objects are
     * both null or both equal by the Object#equals(Object) method.
     * @param a Object 1
     * @param b Object 2
     * @return <tt>true<tt> if both objects are null or equal, <tt>false</tt>
     *      otherwise.
     */
    public static final boolean nullSafeEquals(Object a, Object b) {
        if (a == null ^ b == null) {
            return false;
        } else if (a != null) {
            return a.equals(b);
        }
        return true; // both are null.
    }

	/**
     * Gets today's date at midnight (00:00:00).
     *
     * @return Today's date at midnight.
     */
    public static java.util.Date todaysDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
	
	/**
	 * Appends the object to the buffer using the specified format. If the object is null,
	 * then it is treated as an empty string.
	 * 
	 * @param buf The StringBuffer to append to.
	 * @param format The String format to use.
	 * @param obj The object to format and append to the buffer.
	 */
	public static void nullSafeAppend(StringBuffer buf, String format, Object obj) {
		if (null == obj) {
			buf.append(String.format(format, ""));
		} else {
			buf.append(String.format(format, obj));
		}
	}
	
	public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
	
	public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }
}
