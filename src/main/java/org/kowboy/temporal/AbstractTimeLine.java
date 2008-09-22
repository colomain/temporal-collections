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
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * <p>Abstract base class for <tt>TimeLine</tt> implementations. The easiest
 * way to create a new type of TimeLine is to extend this class and implement
 * the following methods:</p>
 *
 * <ul>
 *   <li><tt>add(Object)</tt> from <tt>Collection</tt>.</li>
 *   <li><tt>newInstance</tt> from this class.
 * </ul>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTimeLine
extends AbstractCollection
implements TimeLine, Serializable {

	private static final long serialVersionUID = 878619480037627257L;
	protected SortedSet<TemporalData> set;
    protected transient Map<Object,Object> reusableIds;

    /**
     * Default Constructor to call super default Constructor
     */
    public AbstractTimeLine() {
        this.set = new TreeSet<TemporalData>(new StartDateComparator());
        this.reusableIds = new HashMap<Object,Object>();
    }

    /**
     * @param c Collection
     */
    public AbstractTimeLine(Collection<TemporalData> c) {
        this();
        addAll(c);
    }

    /**
     * Gets the object from this collection that was effective
     * as of the Date specified.
     *
     * @param asOf The effective date to search for.
     * @return A TemporalData record for the effective date, or null if
     *      there is no record for this date.
     */
    public final TemporalData getAsOf(final Date asOf) {
        // Search for the first record with a startDate before asOf.
        Iterator iter = iterator();
        while (iter.hasNext()) {
            TemporalData data = (TemporalData) iter.next();
            TimePeriod tp = data.getTimePeriod();
            if (tp.contains(asOf)) {
                return data;
            }
        }
        return null;
    }

    /**
     * Returns an iterator over the elements in this set.  The elements
     * are returned in ascending order.
     *
     * @return an iterator over the elements in this set.
     */
    @Override
    public Iterator<TemporalData> iterator() {
        return new TimeLineIterator(set.iterator());
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality).
     */
    @Override
    public int size() {
        return set.size();
    }

    /**
     * Returns the comparator used to order this sorted set, or <tt>null</tt>
     * if this tree set uses its elements natural ordering.
     *
     * @return the comparator used to order this sorted set, which should be
     *  an instance of StartDateComparator.
     */
    public Comparator comparator() {
        return set.comparator();
    }

    /**
     * Get a TimeLine of records that are effective during the
     * specified time period. This includes records that overlap
     * the start and end dates of the provided time period.
     *
     * @param timePeriod The range of dates to search for effective records.
     * @return A TimeLine of the effective records for the specified date.
     */
    public final TimeLine getEffectiveSubset(final TimePeriod timePeriod) {
        TimeLine t = newInstance();
        Iterator it = this.iterator();
        while (it.hasNext()) {
            TemporalData d = (TemporalData) it.next();
            if (timePeriod.intersects(d.getTimePeriod())) {
                t.add(d);
            }
        }
        return t;
    }

    /**
     * Get a TimeLine of records that are completely contained within
     * the specified time period.
     *
     * @param timePeriod The maximum date range of the new TimeLine.
     * @return A new TimeLine.
     */
    public TimeLine getSubset(final TimePeriod timePeriod) {
        TimeLine t = newInstance();
        Iterator it = this.iterator();
        while (it.hasNext()) {
            TemporalData d = (TemporalData) it.next();
            if (timePeriod.contains(d.getTimePeriod())) {
                t.add(d);
            }
        }
        return t;
    }

    /**
     * Sets a temporal data property. If there is no temporal data record for
     * the specified period, then a new one will be created using the specified
     * factory.
     *
     * @param prop the name of the property to set.
     * @param period the TimePeriod for the property.
     * @param value the value of the property.
     * @param factory a factory for creating the appropriate TemporalData objects.
     * @return The TemporalData object that was affected, new or otherwise.
     */
    public void setProperty(final String prop,
            TimePeriod period,
            final Object value,
            final TemporalDataFactory factory) {
        
        // Handle splits at start and end of period.        
        TemporalData effective = getAsOf(period.getStartDate());
        if (effective != null && effective.getTimePeriod().getStartDate()
                .before(period.getStartDate())) {
            TemporalData newData = (TemporalData) effective.cloneData();
            newData.setTimePeriod(new TimePeriod(period.getStartDate(), 
                    effective.getTimePeriod().getEndDate()));
            if (setProperty(newData, prop, value)) {
                this.add(newData);
            }
        }
        effective = getAsOf(period.getEndDate());
        if (effective != null && effective.getTimePeriod().getEndDate()
                .after(period.getEndDate())) {
            TemporalData newData = (TemporalData) effective.cloneData();
            newData.setTimePeriod(new TimePeriod(Utils.addDays(period.getEndDate(), 1),
                    effective.getTimePeriod().getEndDate()));
            if (setProperty(effective, prop, value)) {
                this.add(newData);
            }
        }
        
        // Set properties on existing records within this period.
        Iterator<TemporalData> subLine = getSubset(period).iterator();
        while (subLine.hasNext()) {
            setProperty(subLine.next(), prop, value);
        }
        
        // Create new records for any gaps within the new period.
        Iterator<TimePeriod> gaps = getGaps(this, period).iterator();
        while (gaps.hasNext()) {
            TemporalData newData = factory.newInstance();
            setProperty(newData, prop, value);
            newData.setTimePeriod(gaps.next());
            this.add(newData);
        }
        
        // Finally, merge similar records.
        mergeAdjacent();
    }
        
    public static List getGaps(TimeLine line, TimePeriod period) {
        List gaps = new ArrayList();
        Date lastGapStart = period.getStartDate();
        Iterator<TemporalData> iter = line.iterator();
        while (iter.hasNext()) {
            TimePeriod p = iter.next().getTimePeriod();
            if (p.intersects(period)) {
                TimePeriod gap = new TimePeriod(lastGapStart, Utils.addDays(p.getStartDate(), -1));
                if (gap.isValid()) {
                    gaps.add(gap);
                }
            } else if (p.getStartDate().after(period.getEndDate())) {
                break;
            }
            if (lastGapStart.before(p.getEndDate())) {
                lastGapStart = Utils.addDays(p.getEndDate(), 1);
            }
        }
        // gap at the end?
        TimePeriod gap = new TimePeriod(lastGapStart, period.getEndDate());
        if (gap.isValid()) {
            gaps.add(gap);
        }
        return gaps;
    }

    private boolean setProperty(TemporalData data, String prop, Object value) 
        throws TemporalPropertyException {
        try {
            PropertyUtils.setProperty(data, prop, value);
        } catch (IllegalAccessException e) {
            throw new TemporalPropertyException("Can not access property " + prop, e);
        } catch (InvocationTargetException e) {
            throw new TemporalPropertyException("Can not invoke property setter for " + prop, e);
        } catch (NoSuchMethodException e) {
            throw new TemporalPropertyException("No setter method for property " + prop, e);
        }
        return true;
    }

    /**
     * Gets the value of a temporal property for a specified effective date.
     *
     * @param prop the name of the property to get.
     * @param asOf the effective date.
     * @return the property value at the specified effective date.
     */
    public Object getProperty(String prop, Date asOf) {
        TemporalData d = getAsOf(asOf);
        if (d == null) {
            return null;
        }

        try {
            return PropertyUtils.getProperty(d, prop);
        } catch (IllegalAccessException e) {
            throw new TemporalPropertyException("Can not access property " + prop, e);
        } catch (InvocationTargetException e) {
            throw new TemporalPropertyException("Can not invoke property setter for " + prop, e);
        } catch (NoSuchMethodException e) {
            throw new TemporalPropertyException("No setter method for property " + prop, e);
        }
    }
    
    /**
     * Gets the latest effective date (start date) in this TimeLine.
     * 
     * @return The start date from the last period in the timeline.
     */
    public Date getLatestEffectiveDate() {
        return ((TreeSet<TemporalData>) set).last().getTimePeriod().getStartDate();
    }

    /**
     * Implementing classes will need to provide new TimeLine instances
     * for creating timeline subsets.
     *
     * @return A new TimeLine object.
     */
    protected abstract TimeLine newInstance();
    
    protected boolean addData(TemporalData data) {
        boolean added = set.add(data);
        // Should we try to reuse a database id?
        if (added && data.getIdentity() == null) {
            assignId(data);
        }
        return added;
    }
    
    protected void assignId(TemporalData data) {
        Object key = data.getLogicalKey();
        if (reusableIds.containsKey(key)) {
            data.setIdentity(reusableIds.get(key));
            reusableIds.remove(key);
        }
    }
    
    /**
     * Merges adjacent periods with equal data.
     */
    protected void mergeAdjacent() {
        if (size() == 0) {
            return;
        }
        
        Iterator<TemporalData> it = iterator();
        TemporalData last = it.next();
        while (it.hasNext()) {
            TemporalData current = (TemporalData) it.next();
            if (last.getTimePeriod().isAdjacentTo(current.getTimePeriod())
                    && last.equalsIgnorePeriod(current)) {
                // Remove current and merge time periods & database id.
                it.remove();
                last.getTimePeriod().setEndDate(current.getTimePeriod().getEndDate());
                
                if (last.getIdentity() == null) {
                    assignId(last);
                }
            } else {
                last = current;
            }
        }
    }

    final class TimeLineIterator implements Iterator<TemporalData> {
        Iterator<TemporalData> wrapped = null;
        TemporalData current = null;
        
        public TimeLineIterator(Iterator<TemporalData> wrapped) {
            this.wrapped = wrapped;
        }
        
        public boolean hasNext() {
            return wrapped.hasNext();
        }

        public TemporalData next() {
            current = wrapped.next();
            return current;
        }

        public void remove() {
            wrapped.remove();
            if (current != null && current.getIdentity() != null) {
                // Save the surrogate row id for possible reuse.
                reusableIds.put(current.getLogicalKey(), current.getIdentity());
            }
        }
        
        public TemporalData current() {
            return current;
        }
    }
}
