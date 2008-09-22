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

import java.util.Iterator;

/**
 * Tests the <tt>PerpetualTimeLine</tt> class.
 */
public class PerpetualTest extends TimeLineTestCase {
    TimeLine line = null;
    
    protected void setUp() {
        line = new PerpetualTimeline();
    }
    
    /**
     * Tests the most basic operation of adding a temporal record to an empty
     * timeline.
     */
    @SuppressWarnings("unchecked")
	public void testSimpleAdd() {
        TemporalData d = new SimpleTemporalData(new TimePeriod(), "Foo");
        line.clear();
        assertEquals(0, line.size());
        assertTrue(line.add((TemporalData) d.clone()));
        assertEquals(1, line.size());
        Iterator<TemporalData> it = line.iterator();
        assertEquals(d, it.next());
    }
    
    /**
     * Tests what happens when a new record is added that has an earlier
     * start date than an existing record. This case only tests what happens
     * when there is one record in the timeline.
     */
    @SuppressWarnings("unchecked")
	public void testAddBefore() {
        SimpleTemporalData d1 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1998, 12, 5)),
                "Fred");
        SimpleTemporalData d2 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1994, 12, 3)),
                "Barney");
        line.clear();
        
        // d2 should replace d1.
        assertTrue("Could not insert temporal data", line.add(d1));
        assertTrue("Could not insert temporal data", line.add(d2));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        assertEquals(d2, line.iterator().next());
        
        // What if data is the same?
        SimpleTemporalData d1b = (SimpleTemporalData) d1.cloneData();
        d1b.setData(d2.getData());
        
        // d1 should be reused and it's time period adjusted.
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d1b));
        assertTrue("Could not insert temporal data", line.add(d2));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        assertTrue(line.contains(d2));
    }
    
    /**
     * Tests what happens when a new record is added to an existing timeline
     * that already has an effective record for that start date.
     */
    @SuppressWarnings("unchecked")
	public void testAddAfter() {
        SimpleTemporalData d1 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1998, 12, 5)),
                "Fred");
        SimpleTemporalData d2 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1994, 12, 3)),
                "Barney");
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d2));
        assertTrue("Could not insert temporal data", line.add(d1));
        assertEquals("Wrong number of elements in timeline", 2, line.size());
        Iterator<TemporalData> it = line.iterator();
        assertEquals(d2, it.next());
        assertEquals(d1, it.next());
        
        // What if the data is the same?
        d1.setData(d2.getData());
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d2));
        assertFalse("Could not insert temporal data", line.add(d1));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        
        // d2 should have been reused.
        SimpleTemporalData check = (SimpleTemporalData) line.iterator().next();
        assertTrue(d2 == check);
    }
    
    /**
     * Tests what happens when the new start date is equal to the start
     * date of an existing record.
     * 
     * Notice that if the data for the two records is the same and there are
     * additional records at the end of the timeline, then the return value  
     * for add(Object) is true eventhough the new element was not added.
     * This is because the operation removes the records at the end of the 
     * timeline and reuses the existing record for that start date. 
     */
    @SuppressWarnings("unchecked")
	public void testAddOverTop() {
        SimpleTemporalData d1 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1998, 12, 5)),
                "Fred");
        SimpleTemporalData d2 = (SimpleTemporalData) d1.cloneData();
        d2.setData("Wilma");
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d1));
        assertTrue("Could not insert temporal data", line.add(d2));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        assertEquals("Wrong element in timeline", d2, line.iterator().next());
        
        /*
         * What if the data is equal?
         * Since there are no other records at the end of the timeline, we aren't
         * really changing the timeline at all, so the second add(Object) returns 
         * false.
         */
        d2.setData(d1.getData());
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d1));
        assertFalse("This element should not have been added", line.add(d2));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        assertTrue("Wrong element in timeline", d1 == line.iterator().next());
        assertEquals(d1, d2);
        
        /*
         * What if there are records at the end?
         * Since the second add(Object) operation changes the timeline, the add
         * method should return true.
         */
        SimpleTemporalData d3 = new SimpleTemporalData(new TimePeriod(), "Bam Bam");
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d1));
        assertTrue("Could not insert temporal data", line.add(d3));
        assertTrue("Could not insert temporal data", line.add(d2));
        assertEquals("Wrong number of elements in timeline", 1, line.size());
        assertTrue("Wrong element in timeline", d2 == line.iterator().next());
    }
    
    /**
     * Tests what happens when there are multiple records removed by a new 
     * record.
     */
    @SuppressWarnings("unchecked")
	public void testAddBeforeMultiple() {
        SimpleTemporalData d1 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1992, 12, 5)),
                "Fred");
        SimpleTemporalData d2 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1993, 12, 3)),
                "Barney");
        SimpleTemporalData d3 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1994, 12, 3)),
                "Betty");
        SimpleTemporalData d4 = new SimpleTemporalData(
                new TimePeriod(Utils.newDate(1993, 5, 5)),
                "Betty"); // should remove d2 and d3.
        
        line.clear();
        assertTrue("Could not insert temporal data", line.add(d1));
        assertTrue("Could not insert temporal data", line.add(d2));
        assertTrue("Could not insert temporal data", line.add(d3));
        assertEquals("Wrong number of elements in timeline", 3, line.size());
        
        assertTrue("Could not insert temporal data", line.add(d4));
        assertEquals("Wrong number of elements in timeline", 2, line.size());
        Iterator<TemporalData> it = line.iterator();
        assertEquals("Wrong element order", d1, it.next());
        assertEquals("Wrong element order", d4, it.next());
    }

    @Override
    protected String getTestDataFileName() {
        return "/PerpetualTimeLineTestData.xml";
    }

    @Override
    protected TimeLineFactory getTimeLineFactory() {
        return new PerpetualTimeLineFactory();
    }   
}
