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
import java.util.Date;
import java.util.Iterator;

/**
 * This class implements a <tt>TimeLine</tt> that has mutually exlusive periods 
 * of existence.<p>
 * 
 * <b>Rules</b><p>
 * 
 * <ol><li>
 * 
 * This timeline can have adjacent, but not overlapping time periods. Adjacency
 * is defined as being one day before or after another period. Example: if A's
 * start date is one day after B's end date, A is <i>adjacent</i> to B (and
 * vice versa).<li>
 * 
 * If exising records partially overlap the new record's period, then the old 
 * record(s) will be truncated to be adjacent to the new period.<li>
 * 
 * If the new records period completely contains one or more existing records,
 * those records are removed from the timeline and replaced by the new one. The
 * existing periods are <i>subsets</i> of the new one.<li>
 * 
 * If an existing record's period completely contains the new record's period
 * (the existing period is a <i>superset</i> of the new one), then one of two
 * things can happen: <ol><li>
 * 
 *      If the data is the same for both new and existing 
 *      records, the timeline is not modified and the add method returns 
 *      <tt>false</tt> to indicate that the new record was not added.<li>
 * 
 *      Otherwise, split the existing record and treat them as partially
 *      overlapping records (see rule 2), resulting in three adjacent records.
 *  </ol><li>
 * 
 * If there are no overlapping periods, the new record is inserted normally,
 * without any modifications to other records.<li>
 * 
 * If, after applying the above rules, there are adjacent records with the
 * same temporal data, those records will be merged into one.
 * 
 * </ol><p>
 * 
 * <b>Performance</b><p>
 * 
 * Adjacent record merging may happen while certain rules are being processed
 * in order to improve database performance. By reusing exising detached
 * objects that have database identity (primary key field), we can decrease
 * the number of database operations by doing updates when possible, rather 
 * than delete and insert.
 * 
 * @see org.kowboy.temporal.TimeLine
 * @see org.kowboy.temporal.AbstractTimeLine
 * @see java.util.Collection#add(Object)
 * @see org.kowboy.temporal.TemporalData
 * @see org.kowboy.temporal.AbstractTemporalData
 * @see org.kowboy.temporal.TimePeriod
 */
@SuppressWarnings("unchecked")
public class PeriodOfExistenceTimeLine extends AbstractTimeLine
implements Cloneable, Serializable {
    private static final long serialVersionUID = -4417638177142865738L;

    /**
     * Constructs a new, empty TimeLine.
     */
    public PeriodOfExistenceTimeLine() {
        super();
    }
    
    /**
     * Constructs a new TimeLine containing the TimePeriod elements from 
     * Collection c.
     * @param c The TimePeriod elements that will comprise the new TimeLine.
     */
    public PeriodOfExistenceTimeLine(Collection<TemporalData> c) {
        super(c);
    }

    /**
     * Adds the specified record to this TimeLine based on the rules for the
     * Period of Existence pattern. This method may cause existing records in the collection to
     * be modified or even removed. Adjacent records with equal data (as
     * tested through the equalsIgnorePeriod method) will be merged.
     *
     * @param newData element to be added to this set.
     * @return <tt>true</tt> if the new record was inserted into the
     *      timeline, false otherwise.
     */
    @Override
    public boolean add(Object obj) {
        boolean timeLineChanged = false;
        if (obj == null || contains(obj)) {
            return false;
        }
        TemporalData newData = (TemporalData) obj;
        
        // If there are no records in this TimeLine, insert and we're done.
        if (isEmpty()) {
            return addData(newData);
        }
                
        TimePeriod newTP = newData.getTimePeriod();
        
        Date cutoff = Utils.addDays(newTP.getEndDate(), 1);

        Iterator it = iterator();
        while (it.hasNext()) {
            TemporalData old = (TemporalData) it.next();
            TimePeriod oldTP = old.getTimePeriod();
            if (cutoff.before(oldTP.getStartDate())) {
                // The old record is beyond the new period. The rest
                // of the records in the iterator will be too.
                break;
            } else if (!newTP.intersects(oldTP)) {
                continue;
            } else if (newTP.contains(oldTP)) {
                // old is a subset of new.
                it.remove();
            } else if (oldTP.contains(newTP)) {
                /*
                 * Old is a superset of new.
                 * If the data is the same, do nothing and return immediately.
                 */
                if (old.equalsIgnorePeriod(newData)) {
                    return false; // we didn't add the new record.
                }
                
                // Split the old record in two.
                splitRecord(newTP, old);
                
                // No reason to continue iterating.
                timeLineChanged = true;
                break;
            } else if (oldTP.contains(newTP.getStartDate())) {
                // Partial overlap at the beginning of the new record.
                oldTP.setEndDate(Utils.addDays(newTP.getStartDate(), -1));
                if (!oldTP.isValid()) {
                    it.remove(); // old record ends before it starts - not valid.
                }
            } else {
                /* 
                 * Partial overlap at the end of the new time period. 
                 */
                oldTP.setStartDate(Utils.addDays(newTP.getEndDate(), 1));
                // If the time period of the old record is not valid, remove it.
                if (!oldTP.isValid()) {
                    it.remove();
                }
            }
        }

        timeLineChanged = addData(newData) || timeLineChanged;
        mergeAdjacent();
        return timeLineChanged;
    }

    /**
     * Split an existing record. This method may add a record to the 
     * TimeLine, so be careful if there are any active iterators. They
     * may not include the new element.
     * 
     * @param newTP The new time period.
     * @param old The old record that completely contains the new time period.
     */
    private void splitRecord(TimePeriod newTP, TemporalData old) {
        // Create three adjacent records.
        TimePeriod split1 = new TimePeriod(old.getTimePeriod());
        TimePeriod split2 = new TimePeriod(old.getTimePeriod());
        split1.setEndDate(Utils.addDays(newTP.getStartDate(), -1));
        split2.setStartDate(Utils.addDays(newTP.getEndDate(), 1));
                        
        // check for valid date ranges.
        if (split1.isValid()) {
            // reuse old record for split 1.
            old.setTimePeriod(split1);
            if (split2.isValid()) {
                TemporalData split2data = (TemporalData) old.cloneData(); // no ID
                split2data.setTimePeriod(split2);
                addData(split2data);
            }
        } else if (split2.isValid()) {
            // reuse new record for split 2.
            old.setTimePeriod(split2);
        }
    }

    /**
     * Get a new TimeLine instance.
     */
    @Override
    protected TimeLine newInstance() {
        return new PeriodOfExistenceTimeLine();
    }
    
    /**
     * Creates a "gap" in the timeline(s). This can be used to terminate records
     * from a particular start date to the end of time.
     * 
     * @param clearPeriod The timeperiod to clear from these timelines.
     */
    public void clear(TimePeriod clearPeriod) {
        if (clearPeriod == null) {
            return;
        }
        
        Iterator it = iterator();
        while (it.hasNext()) {
            TemporalData data = (TemporalData) it.next();
            if (clearPeriod.contains(data.getTimePeriod())) {
                it.remove();
                continue;
            } else if (data.getTimePeriod().contains(clearPeriod)) {
                // split record.
                TemporalData split = (TemporalData) data.cloneData();
                data.getTimePeriod().setEndDate(Utils.addDays(clearPeriod.getStartDate(), -1));
                if (!data.getTimePeriod().isValid()) {
                    it.remove();
                }
                split.getTimePeriod().setStartDate(Utils.addDays(clearPeriod.getEndDate(), 1));
                if (split.getTimePeriod().isValid()) {
                    add(split);
                    return;
                }
            } else if (clearPeriod.contains(data.getTimePeriod().getStartDate())) {
                data.getTimePeriod().setStartDate(Utils.addDays(clearPeriod.getEndDate(), 1));
            } else if (clearPeriod.contains(data.getTimePeriod().getEndDate())) {
                data.getTimePeriod().setEndDate(Utils.addDays(clearPeriod.getStartDate(), -1));
            }
            
            if (!data.getTimePeriod().isValid()) {
                it.remove();
            }
        }
    }
}
