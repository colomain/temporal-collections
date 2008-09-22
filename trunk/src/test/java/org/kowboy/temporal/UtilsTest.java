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

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

    public void testForLeapYear() {
        assertFalse(Utils.isLeapYear(1800));
        assertFalse(Utils.isLeapYear(1900));
        assertFalse(Utils.isLeapYear(2100));
        assertFalse(Utils.isLeapYear(2200));
        assertFalse(Utils.isLeapYear(2300));
        assertFalse(Utils.isLeapYear(2500));
        assertTrue(Utils.isLeapYear(2000));
        assertTrue(Utils.isLeapYear(2004));
    }

    public void testForDaysInMonth() {
        int y = 1800;
        int m = 1;
        int d = 1;

        // Not a Leap Year && Month is Feb
        m = 2;
        assertEquals(28, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        // Month is March
        m = 3;
        assertEquals(31, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        // Month is Jan
        m = 1;
        assertEquals(31, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        // Month is April
        m = 4;
        assertEquals(30, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        // Leap Year && Month is Feb
        y = 2000;
        m = 2;
        assertEquals(29, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        // Month is August
        m = 8;
        assertEquals(31, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

        y = 2005;
        m = 2;
        assertEquals(28, Utils.getNoOfDaysInMonth(Utils.newDate(y, m, d)));

    }

    public void testForIsOneDayBefore() {
        //1. Same Year, same Month, Day Difference 1 , toDate> from Date
        // :Expected Behaviour is true
        //2. Same Year, same Month, Day Difference 2 toDate> from Date
        // :Expected Behaviour is false
        //3. Same Year, same Month, Day Difference 1 toDate < fromDate
        // :Expected Behaviour is false
        //4. Same Year, same Month, Day Difference 2 toDate < fromDate
        // :Expected Behaviour is false
        //5. Same Year, Different Month, toDate "date = First Day of the
        // Month", fromDate "date= Last day of the Month"
        //                                 Expected Behaviour is false
        //6. Same Year, Different Month, toDate "date = First Day of the
        // Month", fromDate "date= Not Last day of the Month"
        //                                 Expected Behaviour is false

        // ALL Leap Year Test condition.
        //7. Same Year, same Month, Day Difference 1 , toDate> from Date
        // :Expected Behaviour is true
        //8. Same Year, same Month, Day Difference 2 toDate> from Date
        // :Expected Behaviour is false
        //9. Same Year, Different Month, toDate "date = First Day of the
        // Month", fromDate "date= Last day of the Month"
        //                                 Expected Behaviour is false
        //10. Same Year, Different Month, toDate "date = First Day of the
        // Month", fromDate "date= Not Last day of the Month"
        //                                 Expected Behaviour is false

        java.util.Date fromDate;
        java.util.Date toDate;

        //1.
        toDate = Utils.newDate(2005, 2, 2);
        fromDate = Utils.newDate(2005, 2, 3);
        assertTrue(Utils.isOneDayBefore(toDate, fromDate));

        //2.
        toDate = Utils.newDate(2005, 2, 2);
        fromDate = Utils.newDate(2005, 2, 4);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        //3.
        toDate = Utils.newDate(2005, 2, 4);
        fromDate = Utils.newDate(2005, 2, 2);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        //4.
        toDate = Utils.newDate(2005, 2, 4);
        fromDate = Utils.newDate(2005, 2, 2);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        //5.
        toDate = Utils.newDate(2005, 2, 28);
        fromDate = Utils.newDate(2005, 3, 1);
        assertTrue(Utils.isOneDayBefore(toDate, fromDate));

        //6.
        toDate = Utils.newDate(2005, 2, 28);
        fromDate = Utils.newDate(2005, 3, 2);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        // All Leap Year..

        //7.
        toDate = Utils.newDate(2000, 2, 2);
        fromDate = Utils.newDate(2000, 2, 3);
        assertTrue(Utils.isOneDayBefore(toDate, fromDate));

        //8.
        toDate = Utils.newDate(2000, 2, 2);
        fromDate = Utils.newDate(2000, 2, 4);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        //9.
        toDate = Utils.newDate(2000, 2, 29);
        fromDate = Utils.newDate(2000, 3, 1);
        assertTrue(Utils.isOneDayBefore(toDate, fromDate));

        //10.
        toDate = Utils.newDate(2000, 2, 28);
        fromDate = Utils.newDate(2000, 3, 1);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));

        //11.
        toDate = Utils.newDate(2001, 2, 28);
        fromDate = Utils.newDate(2002, 3, 10);
        assertFalse(Utils.isOneDayBefore(toDate, fromDate));
    }

    /**
     * Tests the addDays() method.
     */
    public void testAddDays() {
        java.util.Date source = null;
        java.util.Date result = null;

        long oneDayInMillis = 24 * 60 * 60 * 1000;

        source = Utils.newDate(2005, 6, 30);
        result = Utils.addDays(source, 5);
        assertEquals(5 * oneDayInMillis, result.getTime() - source.getTime());
        assertEquals(result, Utils.newDate(2005, 7, 5));

        source = result;
        result = Utils.addDays(source, -6);
        assertEquals(6 * oneDayInMillis, source.getTime() - result.getTime());
        assertEquals(result, Utils.newDate(2005, 6, 29));
    }

    /**
     * Test the todaysDate() method.
     */
    public void testTodaysDate() {
        java.util.Date today = Utils.todaysDate();
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(today);
        assertEquals(0, c.get(Calendar.HOUR));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));

        Calendar now = Calendar.getInstance();
        assertEquals(now.get(Calendar.YEAR), c.get(Calendar.YEAR));
        assertEquals(now.get(Calendar.MONTH), c.get(Calendar.MONTH));
        assertEquals(now.get(Calendar.DAY_OF_MONTH), c.get(Calendar.DAY_OF_MONTH));
    }
}
