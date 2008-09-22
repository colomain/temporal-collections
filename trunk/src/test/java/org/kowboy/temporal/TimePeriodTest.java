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

import java.util.Date;
import junit.framework.TestCase;

public class TimePeriodTest extends TestCase {
    
    /**
     * Default constructor.
     */
    public TimePeriodTest() {
        this("Time Period Test");
    }

    /**
     * @param name The name of the test case.
     */
    public TimePeriodTest(String name) {
        super(name);
    }

    /**
     * Test the contains() method.
     */
    public void testContains() {
        TimePeriod tp1 = new TimePeriod();
        tp1.setStartDate(Utils.newDate(1994, 12, 3));
        tp1.setEndDate(Utils.newDate(1995, 5, 2));
        
        assertTrue(tp1.contains(Utils.newDate(1995, 1, 1)));
        assertTrue(tp1.contains(Utils.newDate(1994, 12, 3)));
        assertTrue(tp1.contains(Utils.newDate(1995, 5, 2)));
        assertFalse(tp1.contains(Utils.newDate(1994, 12, 2)));
        assertFalse(tp1.contains(Utils.newDate(1995, 5, 3)));
        assertFalse(tp1.contains(TimePeriod.END_OF_TIME));
        
        // check null end date behavior.
        tp1.setEndDate(TimePeriod.END_OF_TIME);
        assertTrue(tp1.contains(Utils.newDate(2007, 12, 31)));
        assertTrue(tp1.contains(TimePeriod.END_OF_TIME));
        assertFalse(tp1.contains(Utils.newDate(1979, 4, 5)));
    }
    
    /**
     * Test the equals() method.
     */
    public void testEquals() {
        TimePeriod p1 = new TimePeriod();
        assertFalse(p1.equals(null));
        assertTrue(p1.equals(p1));
        
        TimePeriod p2 = new TimePeriod();
        p2.setStartDate(TimePeriod.END_OF_TIME);
        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
        p2.setStartDate(p1.getStartDate());
        p2.setEndDate(p1.getEndDate());
        assertTrue(p2.equals(p1));
        assertTrue(p1.equals(p2));
    }
    
    /**
     * Tests the setting of start and end date to NULL.
     * Should result in the date 9999-12-31.
     */
    public void testNull() {
        Date nulldate = null;
        TimePeriod p1 = new TimePeriod(nulldate, nulldate);
        assertEquals(TimePeriod.END_OF_TIME, p1.getStartDate());
        assertEquals(TimePeriod.END_OF_TIME, p1.getEndDate());
        
        p1.setStartDate(nulldate);
        assertEquals(TimePeriod.END_OF_TIME, p1.getStartDate());
        
        p1.setEndDate(nulldate);
        assertEquals(TimePeriod.END_OF_TIME, p1.getEndDate());
    }
    
    /**
     * Tests to make sure invalid date ranges are correctly identified. 
     */
    public void testIsValid() {
        TimePeriod tp1 = new TimePeriod();
        assertTrue(tp1.isValid());
        tp1.setStartDate(TimePeriod.END_OF_TIME);
        tp1.setEndDate(new Date());
        assertFalse(tp1.isValid());
        tp1.setStartDate(tp1.getEndDate());
        assertTrue(tp1.isValid());
    }
    
    /**
     * Tests the contains(TimePeriod) method.
     */
    public void testContainsTimePeriod() {
        TimePeriod p1 = new TimePeriod(Utils.newDate(1993, 4, 5),
                Utils.newDate(1993, 4, 10));        
        TimePeriod p2 = new TimePeriod(Utils.newDate(1993, 4, 6),
                Utils.newDate(1993, 4, 8));
        assertTrue(p1.contains(p2));
        p2.setStartDate(p1.getStartDate());
        assertTrue(p1.contains(p2));
        p2.setEndDate(p1.getEndDate());
        assertTrue(p1.contains(p2));
        
        p2 = new TimePeriod();
        assertFalse(p1.contains(p2));
    }
    
    /**
     * Test if two time periods intersect.
     */
    public void testIntersect() {
        TimePeriod tp1 = new TimePeriod(
                Utils.newDate(1995, 3, 5), 
                Utils.newDate(1995, 4, 5));
        TimePeriod tp2 = new TimePeriod(
                Utils.newDate(1994, 5, 4),
                Utils.newDate(1995, 3, 20));
        assertTrue(tp1.intersects(tp2));
        assertTrue(tp2.intersects(tp1));
        assertTrue(tp1.intersects(tp1)); // test the edges (start and end).
        
        TimePeriod tp3 = new TimePeriod(
                Utils.newDate(1992, 1, 1),
                Utils.newDate(1999, 1, 1));
        assertTrue(tp1.intersects(tp3));
        assertTrue(tp3.intersects(tp1));
        
        TimePeriod tp4 = new TimePeriod(
                Utils.newDate(1995, 4, 6),
                Utils.newDate(1998, 1, 1));
        assertFalse(tp1.intersects(tp4));
        assertFalse(tp4.intersects(tp1));
    }
    
    /**
     * Tests the isAdjacentTo() method. 
     */
    public void testIsAdjacentTo() {
        TimePeriod tp1 = new TimePeriod(
                Utils.newDate(1995, 3, 5), 
                Utils.newDate(1995, 4, 5));
        TimePeriod tp2 = new TimePeriod(
                Utils.newDate(1995, 4, 6),
                Utils.newDate(1995, 6, 20));
        assertTrue(tp1.isAdjacentTo(tp2));
        assertTrue(tp2.isAdjacentTo(tp1));
        
        tp2.setStartDate(Utils.newDate(1995, 4, 7));
        assertFalse(tp1.isAdjacentTo(tp2));
    }
    
    /**
     * Test the merge() method.
     */
    public void testMerge() {
        // Test with intersecting periods.
        TimePeriod tp1 = new TimePeriod(
                Utils.newDate(1995, 3, 5), 
                Utils.newDate(1995, 4, 5));
        TimePeriod tp2 = new TimePeriod(
                Utils.newDate(1995, 4, 3),
                Utils.newDate(1995, 6, 20));
        TimePeriod check = tp1.merge(tp2);
        assertEquals(tp1.getStartDate(), check.getStartDate());
        assertEquals(tp2.getEndDate(), check.getEndDate());
        
        // Test with adjacent periods.
        tp2.setStartDate(Utils.newDate(1995, 4, 6));
        check = tp1.merge(tp2);
        assertEquals(tp1.getStartDate(), check.getStartDate());
        assertEquals(tp2.getEndDate(), check.getEndDate());
        
        // Test with a gap.
        tp2.setStartDate(Utils.newDate(1995, 4, 8));
        check = tp1.merge(tp2);
        assertEquals(tp1.getStartDate(), check.getStartDate());
        assertEquals(tp2.getEndDate(), check.getEndDate());
        
        // Test with total containment.
        tp2.setStartDate(Utils.newDate(1995, 3, 10));
        tp2.setEndDate(Utils.newDate(1995, 4, 1));
        check = tp1.merge(tp2);
        assertEquals(tp1.getStartDate(), check.getStartDate());
        assertEquals(tp1.getEndDate(), check.getEndDate());
    }
    
    public void testStringParams() throws Exception {
    	TimePeriod tp1 = new TimePeriod("2004-04-10", "2005-01-13");
    	TimePeriod tp2 = new TimePeriod(Utils.newDate(2004, 4, 10),
    			Utils.newDate(2005, 1, 13));
        assertEquals(tp2, tp1);
    }
}
