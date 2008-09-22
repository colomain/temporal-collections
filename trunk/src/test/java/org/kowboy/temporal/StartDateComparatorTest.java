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

import java.util.Comparator;
import junit.framework.TestCase;

public class StartDateComparatorTest extends TestCase {

    /**
     * Default constructor
     */
    public StartDateComparatorTest() {
        super("Start Date Comparator Test");
    }

    /**
     * @param name The name of the test.
     */
    public StartDateComparatorTest(String name) {
        super(name);
    }

    /**
     * Tests the comparator.
     */
    @SuppressWarnings("unchecked")
	public void testComparator() {
        Comparator c = new StartDateComparator();
        TemporalData d1 = new SimpleTemporalData();
        TemporalData d2 = new SimpleTemporalData();
        
        d1.setTimePeriod(new TimePeriod());
        d2.setTimePeriod((TimePeriod) d1.getTimePeriod().clone());
        assertEquals(0, c.compare(d1, d2));

        d2.getTimePeriod().setStartDate(Utils.newDate(2099, 4, 5));
        assertEquals(-1, c.compare(d1, d2));
        
        d2.getTimePeriod().setStartDate(Utils.newDate(1990, 4, 5));
        assertEquals(1, c.compare(d1, d2));
        
        d2.setTimePeriod(null);
        try {
            c.compare(d1, d2);
            fail("Exception should be thrown when trying to test a "
                    + "null value");
        } catch (IllegalArgumentException ex) {
            assertTrue("Caught expected exception", true);
        }
        
        d2 = null;
        try {
            c.compare(d1, d2);
            fail("Exception should be thrown when trying to test a "
                    + "null value");
        } catch (IllegalArgumentException ex) {
            assertTrue("Caught expected exception", true);
        }
    }
    
    /**
     * Mock object for testing.
     */
    @SuppressWarnings("serial")
	class SimpleTemporalData extends NormalizedTemporalData {
        public boolean equalsIgnorePeriod(TemporalData d) {
            return false;
        }

        public Object clone() {
            return super.clone();
        }

        public Object cloneData() {
            // There is no ID field in this object, so just clone it.
            return this.clone();
        }

        public Object getIdentity() {
            return null;
        }

        public void setIdentity(Object identity) {
            
        }
    }
}
