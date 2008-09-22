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
import java.util.Collection;
import java.util.Iterator;

/**
 * This class implements a <tt>TimeLine</tt> that has periods with starting
 * dates but no end date. In other words, a data record is effective until
 * the next point in the timeline. Thus there are no gaps in the effective
 * timeline from the start date of the first record through the end of time.<p>
 *
 * <b>Rules</b><ol><li>
 *
 * If a new record is added with a start date that is earlier than the first
 * record in the timeline, then all existing records are removed and the new
 * one is inserted.<li>
 *
 * If a new record is added and there is already an existing record effective
 * for the new start date, then all records with start dates on or after the
 * new start date are removed. This is consistent with the previous rule.<p>
 *
 * If there is an existing record with the same start date as the new record
 * and the temporal data is the same, then the existing record is left as is,
 * all records after it are removed, and the new record is not inserted. This
 * is an exception to the previous rule. However, in this scenario, the
 * <tt>add(Object)</tt> method will return <tt>true</tt> since the timeline
 * was modified.<p>
 *
 * </ol>
 *
 * @see org.kowboy.temporal.TimeLine
 * @see org.kowboy.temporal.AbstractTimeLine
 * @see java.util.Collection#add(Object)
 * @see org.kowboy.temporal.TemporalData
 * @see org.kowboy.temporal.AbstractTemporalData
 * @see org.kowboy.temporal.TimePeriod
 */
public class PerpetualTimeline extends PeriodOfExistenceTimeLine implements Serializable {

    private static final long serialVersionUID = 1161477454037425415L;

    /**
     * Default constructor.
     */
    public PerpetualTimeline() {
        super();
    }

    /**
     * Creates a new perpetual timeline and initializes it with
     * temporal data records from the specified collection.
     *
     * @param c A collection of temporal data records to initialize
     *      this timeline with.
     */
    public PerpetualTimeline(Collection<TemporalData> c) {
        super(c);
    }

    /**
     * Adds the specified element to this TimeLine. This will modify the
     * timeline according to the rules for Perpetual Timeline pattern.
     *
     * @param newData element to be added to this timeline.
     * @return <tt>true</tt> if the <tt>TimeLine</tt> was modified.
     */
    @Override
    public boolean add(Object obj) {
        if (null == obj) {
            return false;
        }
        boolean changed = false;
        TemporalData newData = (TemporalData) obj;
        if (isEmpty()) {
            changed = addData(newData);
        } else {
            changed = super.add(newData);
            if (!changed) {
                return false;
            }
        }
        
        // Fill gaps in the timeline.
        Iterator<TemporalData> it = iterator();
        TemporalData previous = it.next();
        TemporalData next = previous;
        while (it.hasNext()) {
            next = it.next();
            if (!previous.getTimePeriod().isAdjacentTo(next.getTimePeriod())) {
                previous.getTimePeriod().setEndDate(
                        Utils.addDays(next.getTimePeriod().getStartDate(), -1));
            }
            previous = next;
        }
        next.getTimePeriod().setEndDate(TimePeriod.END_OF_TIME);
        mergeAdjacent();

        return changed;
    }

    /**
     * Create a new TimeLine isntance.
     *
     * @return The new TimeLine.
     */
    @Override
    protected TimeLine newInstance() {
        return new PerpetualTimeline();
    }

    /**
     * Not supported for this timeline type.
     * 
     * @param period The time period.
     */
    @Override
    public void clear(TimePeriod period) {
        throw new UnsupportedOperationException("Perpetual timelines can not be terminated.");
    }
}
