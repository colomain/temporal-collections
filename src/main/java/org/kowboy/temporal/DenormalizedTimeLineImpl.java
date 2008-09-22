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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DenormalizedTimeLineImpl 
extends AbstractCollection
implements Serializable, DenormalizedTimeLine {

    private static final long serialVersionUID = -4200687252881671503L;

    Map<Object,TimeLine> timeLines;
    private TimeLineFactory factory;
    
    /**
     * Creates a new composite timeline. 
     */
    public DenormalizedTimeLineImpl() {
        this(new PerpetualTimeLineFactory());
    }
    
    public DenormalizedTimeLineImpl(TimeLineFactory factory) {
        this.timeLines = new Hashtable<Object,TimeLine>();
        this.factory = factory;
    }
    
    /**
     * Creates a new denormalized timeline initialized with
     * temporal data from the specified <tt>Collection</tt>.
     * @param c The collection whose elements should be added
     *      to the new denormalized timeline.
     */
    public DenormalizedTimeLineImpl(TimeLineFactory factory, Collection<TemporalData> c) {
        this(factory);
        addAll(c);
    }
    
    /**
     * Adds the <tt>TemporalData</tt> object to one of the contained timelines
     * if one already exists for the temporal data key. If a timeline does not
     * already exist for this key, it will be created using the 
     * <tt>newTimeLine()</tt> abstract method that subclasses must implement.
     * 
     * @param d The <tt>TemporalData</tt> object to add.
     * @return <tt>true</tt> if the contained timeline was modified as a result
     *      of this operation, <tt>false</tt> otherwise.
     */
    @Override
    public boolean add(Object obj) {
        TemporalData d = (TemporalData) obj;
        TimeLine line = (TimeLine) timeLines.get(d.getTimeLineKey());
        if (line == null) {
            line = factory.createTimeLine();
            timeLines.put(d.getTimeLineKey(), line);
        }
        return line.add(d);
    }
    
    /**
     * Provides an iterator over all the elements in this 
     * composite timeline. There is no guaranteed order.
     * 
     * @return The iterator.
     */
    @Override
    public Iterator<TemporalData> iterator() {
        return new CompositeIterator();
    }

    /**
     * Gets the size of this composite timeline, which is the simple sum of all
     * timelines in this collection.
     * 
     * @return The size of this composite.
     */
    @Override
    public int size() {
        int sum = 0;
        Iterator it = timeLines.values().iterator();
        while (it.hasNext()) {
            Collection c = (Collection) it.next();
            sum += c.size();
        }
        return sum;
    }
    
    /**
     * Iterator that simply wraps the iterators of the underlying
     * timelines. Therefore, each timeline will be in chronological
     * order, but there is no telling what order the timelines will
     * be in relative to one another. Do not count on a particular
     * order.
     */
    class CompositeIterator implements Iterator<TemporalData> {
        List<Iterator> iterators;
        Iterator<TemporalData> currentIterator;
        int currentIndex = 0;

        public CompositeIterator() {
            if (timeLines.size() == 0) {
                return;
            }
            iterators = new ArrayList<Iterator>(timeLines.size());
            Iterator<TimeLine> lines = timeLines.values().iterator();
            while (lines.hasNext()) {
                // add the iterator for the timeline.
                iterators.add(lines.next().iterator());
            }
            currentIndex = 0;
            currentIterator = iterators.get(currentIndex);
        }
        
        public boolean hasNext() {
            if (currentIterator == null) {
                return false;
            }
            
            if (currentIterator.hasNext()) {
                return true;
            }
            // search remaining iterators for an element.
            for (int i = currentIndex + 1; i < iterators.size(); i++) {
                Iterator it = (Iterator) iterators.get(i);
                if (it.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        public TemporalData next() {
            if (currentIterator == null) {
                return null;
            }
            if (currentIterator.hasNext()) {
                return currentIterator.next();
            } else if (currentIndex < iterators.size() - 1) {
                currentIterator = iterators.get(++currentIndex);
                return this.next();
            }
            return null;
        }

        public void remove() {
            currentIterator.remove();
        }
    }
    
    public TemporalData getAsOf(Object key, Date asOf) {
        TimeLine line = (TimeLine) timeLines.get(key);
        if (line == null) {
            return null;
        }
        return line.getAsOf(asOf);
    }

    @Override
    public void clear() {
        timeLines.clear();
    }

    public void setProperty(final Object key,
            final String prop, 
            TimePeriod period, 
            final Object value, 
            final TemporalDataFactory factory) {
        TimeLine line = null;
        if (timeLines.containsKey(key)) {
            line = timeLines.get(key);
        } else {
            line = this.factory.createTimeLine();
            timeLines.put(key, line);
        }
        line.setProperty(prop, period, value, factory);
    }

    public Object getProperty(Object key, String prop, Date asOf) {
        TimeLine line = timeLines.get(key);
        if (line == null) {
            return null;
        }
        return line.getProperty(prop, asOf);
    }

    public void clear(TimePeriod period) {
        Iterator it = this.timeLines.values().iterator();
        while (it.hasNext()) {
            ((TimeLine) it.next()).clear(period);
        }
    }
}
