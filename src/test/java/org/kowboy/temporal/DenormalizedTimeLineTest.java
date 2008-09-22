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
import junit.framework.TestCase;

public class DenormalizedTimeLineTest extends TestCase {
    private DenormalizedTimeLine timeLine = null;

    @SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
        timeLine = new DenormalizedTimeLineImpl(new MockTimeLineFactory());
        timeLine.add(new MockTemporalData("key1", "data1"));
        timeLine.add(new MockTemporalData("key2", "data2"));
        timeLine.add(new MockTemporalData("key3", "data3"));
    }

    /**
     * Test the <tt>size()</tt> method.
     * @see DenormalizedTimeLineImpl#size()
     */
    public void testSize() {
        DenormalizedTimeLine empty = new DenormalizedTimeLineImpl(new MockTimeLineFactory());
        assertEquals(0, empty.size());
        assertTrue(empty.isEmpty());
        
        assertEquals(3, timeLine.size());
    }

    /**
     * Test the <tt>add(Object)</tt> method.
     */
    @SuppressWarnings("unchecked")
	public void testAddObject() {
        int size = timeLine.size();
        MockTemporalData md = new MockTemporalData("key1", "data2");
        md.getTimePeriod().setStartDate(Utils.newDate(1999, 3, 12));
        assertTrue(timeLine.add(md));
        assertEquals(size + 1, timeLine.size());
    }
    
    /**
     * Test the <tt>getAsOf(Object, Date)</tt> method.
     */
    public void testGetAsOf() {
        MockTemporalData md1 = (MockTemporalData) timeLine.getAsOf("key1", new Date());
        assertNotNull(md1);
        MockTemporalData md2 = (MockTemporalData) timeLine.getAsOf("key2", new Date());
        assertNotNull(md2);
        MockTemporalData md3 = (MockTemporalData) timeLine.getAsOf("key3", new Date());
        assertNotNull(md3);
        assertTrue(!md1.equals(md2));
        assertTrue(!md2.equals(md3));
    }

    /**
     * Test the <tt>iterator()</tt> method.
     */
    @SuppressWarnings("unchecked")
	public void testIterator() {
        int count = 0;
        Iterator<TemporalData> it = timeLine.iterator();
        while (it.hasNext()) {
            assertTrue(it.next() instanceof MockTemporalData);
            count++;
        }
        assertEquals(timeLine.size(), count);
    }
    
    class MockTimeLineFactory implements TimeLineFactory {
        private static final long serialVersionUID = 1L;
        public TimeLine createTimeLine() {
            return new MockTimeLine();
        }
    }
    
    /**
     * Mock TimeLine
     */
    @SuppressWarnings("unchecked")
	class MockTimeLine extends AbstractTimeLine {
        private static final long serialVersionUID = -3461374790348426435L;

        MockTimeLine() {
            super();
        }
        
        public TimeLine newInstance() {
            return new MockTimeLine();
        }
        
        public boolean add(Object obj) {
            return set.add((TemporalData) obj);
        }

        public void clear(TimePeriod period) {
            return;
        }
    }
    
    /**
     * MockTemporalData 
     * 
     * @version $Rev$ $Date$
     * @author $Author$
     */
    class MockTemporalData extends AbstractTemporalData {    
        private static final long serialVersionUID = 1L;
        private Object key;
        private String data;
        
        MockTemporalData(Object key, String data) {
            this.key = key;
            this.data = data;
            setTimePeriod(new TimePeriod());
        }
        
        public Object getTimeLineKey() {
            return key;
        }
        
        public String getData() {
            return data;
        }
        
        public boolean equalsIgnorePeriod(TemporalData d) {
            MockTemporalData m = (MockTemporalData) d;
            return key.equals(d.getTimeLineKey()) && getData().equals(m.getData());
        }
        
        public boolean equals(Object obj) {
            MockTemporalData d = (MockTemporalData) obj;
            return getTimeLineKey().equals(d.getTimeLineKey())
                && getTimePeriod().getStartDate().equals(d.getTimePeriod().getStartDate()); 
        }
        
        public int hashCode() {
            return getTimeLineKey().hashCode();
        }
        
        public Object cloneData() {
            return super.clone();
        }

        @Override
        public void setTimeLineKey(Object key) {
            this.key = key;
        }

        public Object getIdentity() {
            return null;
        }

        public void setIdentity(Object identity) {
            return;
        }
    }
}
