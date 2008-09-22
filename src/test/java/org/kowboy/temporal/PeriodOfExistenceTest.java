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
import java.util.Iterator;
import java.util.List;

import org.kowboy.temporal.domain.PhoneNumber;

public class PeriodOfExistenceTest extends TimeLineTestCase {
    private SimpleTemporalData d1, d2, d3, d4, d5 = null;
    private TimeLine line = null;
    private TemporalDataFactory phoneNumberFactory = new TemporalDataFactory() {
        public TemporalData newInstance() {
            return new PhoneNumber();
        }
    };
    
    /**
     * Default constructor.
     */
    public PeriodOfExistenceTest() {
        super("Period of Existence Time Line Test");
    }

    /**
     * @param name The test name.
     */
    public PeriodOfExistenceTest(String name) {
        super(name);
    }

    protected void setUp() {
        line = new PeriodOfExistenceTimeLine();
        
        // 1996/3/14 - N/A
        d1 = new SimpleTemporalData();
        d1.setTimePeriod(new TimePeriod(Utils.newDate(1996, 3, 14)));
        d1.setData("d1");
        
        // 1997/1/21 - 1997/2/4
        d2 = new SimpleTemporalData();
        d2.setTimePeriod(new TimePeriod(Utils.newDate(1997, 1, 21),
                Utils.newDate(1997, 2, 4)));
        d2.setData("d2");
        
        // 1998/7/2 - 1999/11/3
        d3 = new SimpleTemporalData();
        d3.setTimePeriod(new TimePeriod(Utils.newDate(1998, 7, 2),
                Utils.newDate(1999, 11, 3)));
        d3.setData("d3");
        
        // 1997/1/21 - N/A
        d4 = new SimpleTemporalData();
        d4.setTimePeriod(new TimePeriod(Utils.newDate(1997, 1, 21)));
        d4.setData("d4");
        
        // 2003/12/3 - 2004/2/4
        d5 = new SimpleTemporalData();
        d5.setTimePeriod(new TimePeriod(Utils.newDate(2003, 12, 3),
                Utils.newDate(2004, 2, 4)));
        d5.setData("d5");
    }
    
    /**
     * Tests the setting of one property for a specified time period.
     * For this test, I need TemporalData objects that have multiple
     * properties, so I'm using MembershipT.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public void testSetProperty() throws Exception {
        TimePeriod t1 = new TimePeriod(Utils.newDate(2000, 1, 14),
                Utils.newDate(2000, 2, 14));
        TimePeriod t2 = new TimePeriod(Utils.newDate(2000, 3, 27),
                Utils.newDate(2000, 5, 16));
        TimePeriod t3 = new TimePeriod(Utils.newDate(2000, 7, 3),
                Utils.newDate(2000, 8, 19));
        // Create initial timeline.
        line.setProperty("numberString", t1, "333-4444", phoneNumberFactory);
        line.setProperty("areaCode", t1, 502, phoneNumberFactory);
        
        line.setProperty("numberString", t2, "444-5555", phoneNumberFactory);
        line.setProperty("areaCode", t2, 202, phoneNumberFactory);
        
        line.setProperty("numberString", t3, "555-6666", phoneNumberFactory);
        line.setProperty("areaCode", t3, 606, phoneNumberFactory);
        
        // Verify initial timeline.
        assertEquals(3, line.size());
        Iterator<TemporalData> iter = line.iterator();
        PhoneNumber p1 = (PhoneNumber) iter.next();
        assertEquals(t1, p1.getTimePeriod());
        assertEquals("333-4444", p1.getNumberString());
        assertEquals(502, p1.getAreaCode().intValue());
        PhoneNumber p2 = (PhoneNumber) iter.next();
        assertEquals(t2, p2.getTimePeriod());
        assertEquals("444-5555", p2.getNumberString());
        assertEquals(202, p2.getAreaCode().intValue());
        PhoneNumber p3 = (PhoneNumber) iter.next();
        assertEquals(t3, p3.getTimePeriod());
        assertEquals("555-6666", p3.getNumberString());
        assertEquals(606, p3.getAreaCode().intValue());
        
        // Change a property and use a time period that spans mutliple
        // records as well as gaps in the initial timeline.
        TimePeriod testPeriod = new TimePeriod(Utils.newDate(2000, 1, 20), 
                Utils.newDate(2000, 6, 5));
        line.setProperty("numberString", testPeriod, "222-3333", phoneNumberFactory);
        
        assertEquals(6, line.size());
        iter = line.iterator();
        
        p1 = (PhoneNumber) iter.next(); // 1/14 - 1/19
        assertEquals(new TimePeriod(t1.getStartDate(), Utils.addDays(testPeriod.getStartDate(), -1)),
                p1.getTimePeriod());
        assertEquals("333-4444", p1.getNumberString());
        assertEquals(502, p1.getAreaCode().intValue());
        
        p2 = (PhoneNumber) iter.next(); // 1/20 - 2/14
        assertEquals(new TimePeriod(testPeriod.getStartDate(), t1.getEndDate()), p2.getTimePeriod());
        assertEquals("222-3333", p2.getNumberString());
        assertEquals(502, p2.getAreaCode().intValue());
        
        p3 = (PhoneNumber) iter.next(); // 2/15 - 3/26
        assertEquals(new TimePeriod(Utils.addDays(t1.getEndDate(), 1), Utils.addDays(t2.getStartDate(), -1)),
                p3.getTimePeriod());
        assertEquals("222-3333", p3.getNumberString());
        assertNull(p3.getAreaCode());
        
        PhoneNumber p4 = (PhoneNumber) iter.next(); // 3/27 - 5/16
        assertEquals(t2, p4.getTimePeriod());
        assertEquals("222-3333", p4.getNumberString());
        assertEquals(202, p4.getAreaCode().intValue());
        
        PhoneNumber p5 = (PhoneNumber) iter.next(); // 5/17 - 6/5
        assertEquals(new TimePeriod(Utils.addDays(t2.getEndDate(), 1), testPeriod.getEndDate()),
                p5.getTimePeriod());
        assertEquals("222-3333", p5.getNumberString());
        assertNull(p5.getAreaCode());
        
        PhoneNumber p6 = (PhoneNumber) iter.next(); // 7/3 - 8/19
        assertEquals(t3, p6.getTimePeriod());
        assertEquals("555-6666", p6.getNumberString());
        assertEquals(606, p6.getAreaCode().intValue());        
        
        // Force some records to merge.
        TimePeriod mergePeriod = new TimePeriod(Utils.newDate(2000, 1, 14),
                Utils.newDate(2000, 2, 14));
        line.setProperty("numberString", mergePeriod, "333-4444", phoneNumberFactory);
        line.setProperty("areaCode", mergePeriod, 502, phoneNumberFactory);
        assertEquals(5, line.size());
        p1 = (PhoneNumber) line.iterator().next();
        assertEquals(mergePeriod, p1.getTimePeriod());
        assertEquals("333-4444", p1.getNumberString());
        assertEquals(502, p1.getAreaCode().intValue());
    }
    
    /**
     * Simple add test. Only adds one period to an empty timeline.
     */
    @SuppressWarnings("unchecked")
    public void testSimpleAdd() {
        assertTrue(line.add(d1));
        assertEquals(1, line.size());
        
        Iterator i = line.iterator();
        TemporalData tl = (TemporalData) i.next();
        assertEquals(d1, tl);
        assertTrue(d1 == tl);
    }
    
    /**
     * Partial overlap, no merge. 
     * 
     * Tests what happens when a period is inserted into a timeline
     * that already has records partially overlapping the new period.
     * This means we're assuming one record overlaps the new start
     * date and/or one record overlaps the new end date. This test
     * also assumes different temporal data (no merging).
     */
    @SuppressWarnings("unchecked")
    public void testPartialOverlap() {
        // Create three records, with the first and third partially
        // overlapping the second.
        // d1 overlaps d2's startDate.
        // d3 overlaps d2's endDate.
        d1.getTimePeriod().setStartDate(Utils.newDate(1995, 2, 2));
        d1.getTimePeriod().setEndDate(Utils.newDate(1995, 4, 2));
        d2.getTimePeriod().setStartDate(Utils.newDate(1995, 3, 2));
        d2.getTimePeriod().setEndDate(Utils.newDate(1995, 6, 2));
        d3.getTimePeriod().setStartDate(Utils.newDate(1995, 5, 2));
        d3.getTimePeriod().setEndDate(Utils.newDate(1995, 7, 2));

        // First, overlap the beginning of the new record.
        TemporalData d1b = (TemporalData) d1.clone();
        TemporalData d2b = (TemporalData) d2.clone();
        assertTrue(line.add(d1b));
        assertTrue(line.add(d2b));
        assertEquals(2, line.size());

        // test the order.
        Iterator iter = line.iterator();
        assertEquals("Wrong order", d1b, iter.next());
        assertEquals("Wrong order", d2b, iter.next());
        
        // d1b's end date should be the day before d2's start.
        assertTrue(Utils.isOneDayBefore(d1b.getTimePeriod().getEndDate(),
                d2.getTimePeriod().getStartDate()));
        // make sure d2b's dates weren't tampered with.
        assertEquals("d2b's dates were changed", d2.getTimePeriod(), 
                d2b.getTimePeriod());
        
        // Second, overlap the end of the record.
        line.clear();
        TemporalData d3b = (TemporalData) d3.clone();
        d2b = (TemporalData) d2.clone();
        assertTrue(line.add(d3b)); // Insert d3 first.
        assertTrue(line.add(d2b)); // end date will overlap start of d3
        assertEquals(2, line.size());
        
        // test the order
        iter = line.iterator();
        assertEquals("Wrong order", d2b, iter.next());
        assertEquals("Wrong order", d3b, iter.next());
        
        // d3b's start date should be the day after d2's end date.
        assertTrue(Utils.isOneDayBefore(d2.getTimePeriod().getEndDate(),
                d3b.getTimePeriod().getStartDate()));
        // make sure d2b's dates weren't tampered with.
        assertEquals("d2b's dates were changed", d2.getTimePeriod(), 
                d2b.getTimePeriod());
        
        // Third, try the case where both ends overlap.
        line.clear();
        d1b = (TemporalData) d1.clone();
        d2b = (TemporalData) d2.clone();
        d3b = (TemporalData) d3.clone();
        assertTrue(line.add(d1b));
        assertTrue(line.add(d3b));
        assertTrue(line.add(d2b)); // add d2 last - both ends overlap
        assertEquals(3, line.size());
        iter = line.iterator();
        assertEquals("Wrong order", d1b, iter.next());
        assertEquals("Wrong order", d2b, iter.next());
        assertEquals("Wrong order", d3b, iter.next());
        // d1's end date should be the day before d2's start.
        assertTrue(Utils.isOneDayBefore(d1b.getTimePeriod().getEndDate(),
                d2.getTimePeriod().getStartDate()));
        // d3's start date should be the day after d2's end date.
        assertTrue(Utils.isOneDayBefore(d2.getTimePeriod().getEndDate(),
                d3b.getTimePeriod().getStartDate()));
        // make sure d2b's dates weren't tampered with.
        assertEquals("d2b's dates were changed", d2.getTimePeriod(), 
                d2b.getTimePeriod());
    }
    
    /**
     * No overlap, no merge, no adjacent time periods.
     * 
     * Tests what happens when a new period is inserted into a timeline
     * with no overlapping timeperiods. Also checks that adjacent records
     * are not merged when the data is different.
     */
    @SuppressWarnings("unchecked")
    public void testAddNoOverlap() {
        Iterator it = null;
        
        // First, in chronological order.
        assertTrue(line.add(d2)); // 1997/1/21 - 1997/2/4
        assertTrue(line.add(d3)); // 1998/7/2 - 1999-11-3
        assertEquals(2, line.size());
        
        // ensure proper order.
        it = line.iterator();
        assertEquals("Time Line is out of order", d2, it.next());
        assertEquals("Time Line is out of order", d3, it.next());
        
        line.clear();
        assertEquals(0, line.size());
        
        // Reverse chronological order.
        assertTrue(line.add(d5)); // 2003/12/3 - 2004/2/4
        assertTrue(line.add(d2)); // 1997/1/21 - 1997/2/4
        assertEquals(2, line.size());
        
        // ensure proper order.
        it = line.iterator();
        assertEquals("Time Line is out of order", d2, it.next());
        assertEquals("Time Line is out of order", d5, it.next());
        
        // Insert a period between the two.
        assertTrue(line.add(d3)); // 1998/7/2 - 1999/11/3
        assertEquals(3, line.size());
        
        // ensure proper order.
        it = line.iterator();
        assertEquals("Time Line is out of order", d2, it.next());
        assertEquals("Time Line is out of order", d3, it.next());
        assertEquals("Time Line is out of order", d5, it.next());
        
        // Test adjacent records.
        TemporalData d1b = (TemporalData) d1.clone();
        TemporalData d2b = (TemporalData) d2.clone();
        TemporalData d3b = (TemporalData) d3.clone();
        
        // set d1b's end date to the day before d2b's start.
        d1b.getTimePeriod().setEndDate(
                Utils.addDays(d2b.getTimePeriod().getStartDate(), -1));
        // set d3b's start date to the day after d2b's end.
        d3b.getTimePeriod().setStartDate(
                Utils.addDays(d2b.getTimePeriod().getEndDate(), 1));
        line.clear();
        assertTrue(line.add(d1b));
        assertTrue(line.add(d2b));
        assertTrue(line.add(d3b));
        assertEquals(3, line.size());        
        it = line.iterator();
        TemporalData check = (TemporalData) it.next();
        assertEquals(d1b, check);
        assertTrue(Utils.isOneDayBefore(d1b.getTimePeriod().getEndDate(),
                d2b.getTimePeriod().getStartDate()));
        assertEquals(d1.getTimePeriod().getStartDate(), 
                d1b.getTimePeriod().getStartDate());
        check = (TemporalData) it.next();
        assertEquals(d2b, check);
        assertEquals("d2b should not have changed", d2, d2b);
        check = (TemporalData) it.next();
        assertEquals(d3b, check);
        assertTrue(Utils.isOneDayBefore(d2b.getTimePeriod().getEndDate(),
                d3b.getTimePeriod().getStartDate()));
        assertEquals(d3.getTimePeriod().getEndDate(), 
                d3b.getTimePeriod().getEndDate());
    }
    
    /**
     * Subset overlap, no merge.
     * 
     * This method tests what happens when a new time period completely
     * contains one or more time periods in the existing time line. Existing
     * records within the new time period should be removed.
     */
    @SuppressWarnings("unchecked")
    public void testSubsetOverlap() {
        assertTrue(line.add(d2)); // 1997/1/21 - 1997/2/4
        assertTrue(line.add(d3)); // 1998/7/2 - 1999/11/3
        
        // d1 contains both of the previous time periods.
        assertTrue(line.add(d1)); // 1996/3/14 - 9999/12/31
        
        assertEquals(1, line.size());
        Iterator iter = line.iterator();
        assertEquals(d1, iter.next());
    }
    
    /**
     * Superset overlap, no merge. 
     * 
     * This method tests what happens when the new time period is 
     * completely contained within an existing period in the time line.
     * The result should be that the existing record is split and
     * it then becomes a case of partial overlap on both ends of the new
     * record (See testPartialOverlap()).
     */
    @SuppressWarnings("unchecked")
    public void testSupersetOverlap() {
        TemporalData d1b = (TemporalData) d1.clone();
        TemporalData d2b = (TemporalData) d2.clone();
        assertTrue(line.add(d1b));  // 1996/3/14 - 9999/12/31
        assertTrue(line.add(d2b));  // 1997/1/21 - 1997/2/4
        
        assertEquals(3, line.size());
        assertTrue("d1b should have been split in two and reused", line.contains(d1b));
        Iterator iter = line.iterator();
        TemporalData d1Split1 = (TemporalData) iter.next();
        assertTrue("Temporal data should be the same",
                d1b.equalsIgnorePeriod(d1Split1));
        // Check that end date is one day before d2's start.
        assertTrue(Utils.isOneDayBefore(
                d1Split1.getTimePeriod().getEndDate(),
                d2.getTimePeriod().getStartDate()));
        
        // Make sure d2 was not tampered with.
        assertEquals("d2 should not have changed.", d2, iter.next());
        
        TemporalData d1Split2 = (TemporalData) iter.next();
        assertTrue("Temporal data should be the same",
                d1b.equalsIgnorePeriod(d1Split2));
        // Check that start date is one day after d2's end.
        assertTrue(Utils.isOneDayBefore(
                d2.getTimePeriod().getEndDate(),
                d1Split2.getTimePeriod().getStartDate()));
        
        /*
         * What if the start dates are equal?
         */
        d1b = (TemporalData) d1.clone();
        d1b.getTimePeriod().setStartDate(d2b.getTimePeriod().getStartDate());
        d2b = (TemporalData) d2.clone();
        line.clear();
        assertTrue(line.add(d1b));  // 1997/1/21 - 9999/12/31
        assertTrue(line.add(d2b));  // 1997/1/21 - 1997/2/4
        assertEquals(2, line.size());
        iter = line.iterator();
        assertEquals("d2b should not have changed", d2, iter.next());
        d1Split1 = (TemporalData) iter.next();
        assertTrue(d1b.equalsIgnorePeriod(d1Split1));
        assertTrue(Utils.isOneDayBefore(d2.getTimePeriod().getEndDate(),
                d1Split1.getTimePeriod().getStartDate()));
        assertEquals(TimePeriod.END_OF_TIME, 
                d1Split1.getTimePeriod().getEndDate());
        
        /*
         * What if the end dates are equal?
         */
        d1b = (TemporalData) d1.clone();
        d1b.getTimePeriod().setEndDate(d2b.getTimePeriod().getEndDate());
        d2b = (TemporalData) d2.clone();
        line.clear();
        assertTrue(line.add(d1b));  // 1996/3/14 - 1997/2/4
        assertTrue(line.add(d2b));  // 1997/1/21 - 1997/2/4
        assertEquals(2, line.size());
        iter = line.iterator();
        d1Split1 = (TemporalData) iter.next();
        assertTrue(d1b.equalsIgnorePeriod(d1Split1));
        assertTrue(Utils.isOneDayBefore(
                d1Split1.getTimePeriod().getEndDate(),
                d2.getTimePeriod().getStartDate()));
        assertEquals(d1.getTimePeriod().getStartDate(), 
                d1Split1.getTimePeriod().getStartDate());        

        assertEquals("d2b should not have changed", d2, iter.next());
    }
    
    /**
     * Adjacent, merge.
     * 
     * This method tests what happens when the inserted record is adjacent
     * to an existing record and the data is the same.
     */
    @SuppressWarnings("unchecked")
    public void testAdjacentMerge() {
        // need to adjust the dates.
        Date ref = d1.getTimePeriod().getStartDate();
        d1.getTimePeriod().setEndDate(Utils.addDays(ref, 10));      // 10 day period.
        d2.getTimePeriod().setStartDate(Utils.addDays(ref, 11));    // adjacent to d1
        d2.getTimePeriod().setEndDate(Utils.addDays(ref, 16));      // 5 day period.
        d3.getTimePeriod().setStartDate(Utils.addDays(ref, 17));    // adjacent to d2
        d3.getTimePeriod().setEndDate(Utils.addDays(ref, 25));      // 8 day period.
        
        // Make sure temporal data properties are equal.
        d1.setData("green");
        d2.setData(d1.getData());
        d3.setData(d1.getData());
        
        // clone so we can reuse the dates we just set up.
        TemporalData d1b, d2b, d3b, check = null;
        Iterator it = null;
        d1b = (TemporalData) d1.clone();
        d2b = (TemporalData) d2.clone();
        d3b = (TemporalData) d3.clone();
        
        line.clear();
        assertTrue(line.add(d1b));
        assertTrue(line.add(d2b));
        assertTrue(line.add(d3b));
        assertEquals(1, line.size());
        
        // entry (d3b) should be the one that was modified.
        it = line.iterator();
        check = (TemporalData) it.next();
        assertEquals(d1b, check);
        assertEquals("green", ((SimpleTemporalData) check).getData());
        
        // check the period.
        assertEquals(new TimePeriod(ref, d3.getTimePeriod().getEndDate()),
                check.getTimePeriod());
        
        // Try a different insertion order (non-chronological).
        d1b = (TemporalData) d1.clone();
        d2b = (TemporalData) d2.clone();
        d3b = (TemporalData) d3.clone();
        
        line.clear();
        assertTrue(line.add(d2b)); // The d2b record will be modified.
        assertTrue(line.add(d1b));
        assertTrue(line.add(d3b));
        assertEquals(1, line.size());
        
        // previous entry (d1b) should be the one that was modified.
        it = line.iterator();
        check = (TemporalData) it.next();
        assertEquals(d1b, check);
        assertEquals("green", ((SimpleTemporalData) check).getData());
        
        // check the period.
        assertEquals(new TimePeriod(ref, d3.getTimePeriod().getEndDate()),
                check.getTimePeriod());
    }

    /**
     * This method tests what happens when a new record overlaps existing 
     * records and the data is the same.
     */
    @SuppressWarnings("unchecked")
    public void testOverlapMerge() {
        d1.getTimePeriod().setEndDate(
                Utils.addDays(d2.getTimePeriod().getStartDate(), 2));
        d3.getTimePeriod().setStartDate(
                Utils.addDays(d2.getTimePeriod().getEndDate(), -2));
        
        // same data.
        d1.setData(d2.getData());
        d3.setData(d2.getData());
        
        // The new time period will be from d1.start to d3.end
        TimePeriod newPeriod = new TimePeriod(
                d1.getTimePeriod().getStartDate(),
                d3.getTimePeriod().getEndDate());
        
        line.clear();
        assertTrue(line.add(d1));
        assertTrue(line.add(d3));
        assertTrue(line.add(d2)); // overlap on both ends.
        
        // Result should be one record.
        assertEquals(1, line.size());
        Iterator it = line.iterator();
        TemporalData check = (TemporalData) it.next();
        
        /*
         * d1 should be the object that was modified.
         * This is a database optimization where an existing record is
         * updated, rather than delete + insert. The record that is used is
         * the last merged record in the timeline.
         */ 
        assertEquals(d1, check);
        assertEquals(newPeriod, check.getTimePeriod());
    }
    
    /**
     * This method tests what happens when a new record is inserted into
     * a timeline that already contains the same data for the entire 
     * time period of the new record (the existing period is a superset
     * of the new period and the data is the same). In this case, nothing
     * should happen.
     */
    @SuppressWarnings("unchecked")
    public void testSupersetOverlapMerge() {
        d2.setData(d1.getData());
        line.clear();
        assertTrue(line.add(d1));
        assertFalse(line.add(d2));
        assertEquals(1, line.size());
        Iterator it = line.iterator();
        assertTrue("d1 should not have been replaced.", d1 == it.next());
        
        // What if we try to insert the same record twice. Should have same result.
        line.clear();
        assertTrue(line.add((TemporalData) d3.clone()));
        assertFalse(line.add((TemporalData) d3.clone()));
        assertEquals(1, line.size());        
    }

    /**
     * Tests the clear(TimePeriod) method.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public void testClear() throws Exception {
        line.clear();
        line.add(d1);
        line.add(d2);
        line.add(d3);
        line.add(d4);
        line.add(d5);
        
        // 1. clear from a start date to EoT.
        TimePeriod clearPeriod = new TimePeriod(Utils.newDate(2004, 1, 5)); 
        line.clear(clearPeriod);
        assertEquals(Utils.newDate(2004, 1, 4), d5.getTimePeriod().getEndDate());
        assertNull(line.getAsOf(Utils.newDate(2004, 1, 5)));
        assertEquals(0, line.getEffectiveSubset(clearPeriod).size());
        
        // 2. clear a period that is within an existing record (split).
        line.clear();
        line.add(d4); // 1997/1/21 - N/A
        // Clear out the year 2000.
        clearPeriod = new TimePeriod(Utils.newDate(2000, 1, 1), Utils.newDate(2000, 12, 31));
        line.clear(clearPeriod);
        assertEquals(2, line.size()); // split a single record.
        assertEquals(0, line.getEffectiveSubset(clearPeriod).size());
    }
    
    @SuppressWarnings("unchecked")
    public void testGaps() throws Exception {
        assertTrue(line.add(d2)); // 1997/1/21 - 1997/2/4
        assertTrue(line.add(d3)); // 1998/7/2 - 1999-11-3
        
        TimePeriod testPeriod = new TimePeriod(Utils.newDate(1997, 1, 13), 
                Utils.newDate(2000, 5, 14));
        List gaps = AbstractTimeLine.getGaps(line, testPeriod);
        assertEquals(3, gaps.size());
        assertEquals(new TimePeriod(testPeriod.getStartDate(), 
                Utils.addDays(d2.getTimePeriod().getStartDate(), -1)),
                gaps.get(0));
        assertEquals(new TimePeriod(Utils.addDays(d2.getTimePeriod().getEndDate(), 1),
                Utils.addDays(d3.getTimePeriod().getStartDate(), -1)),
                gaps.get(1));
        assertEquals(new TimePeriod(Utils.addDays(d3.getTimePeriod().getEndDate(), 1), 
                testPeriod.getEndDate()),
                gaps.get(2));
        
        assertTrue(line.add(d5)); // 2003/12/3 - 2004/2/4
        List gaps2 = AbstractTimeLine.getGaps(line, testPeriod);
        assertEquals(gaps, gaps2);
    }

    @Override
    protected String getTestDataFileName() {
        return "/PoETimeLineTestData.xml";
    }

    @Override
    protected TimeLineFactory getTimeLineFactory() {
        return new PeriodOfExistenceTimeLineFactory();
    }
}
