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

/**
 * Interface that temporal objects should implement. This adds start and end
 * date properties to the implementing class, as well as methods to test equality
 * of the data only (without the time period) and clone the object without
 * persistent identity (primary key field).
 */
public interface TemporalData extends Cloneable {
    /**
     * Get the effective time period for this temporal data.
     *
     * @return The TimePeriod.
     */
    TimePeriod getTimePeriod();

    /**
     * Get the natural key for TimeLine that holds this temporal data (this
     * does not include effective date). If the implementing object only has
     * one timeline (no natural key for separating timelines), then it should
     * return some constant value, preferably a <tt>static final Integer</tt>.
     * Key classes <b>must</b> implement <tt>hashCode()</tt> and <tt>equals(Object)</tt>
     *
     * @return The natural key for a distinct timeline.
     */
    Object getTimeLineKey();

    /**
     * Set the effective time period for this temporal data.
     *
     * @param p The new TimePeriod.
     */
    void setTimePeriod(TimePeriod p);

    /**
     * Test the equality of the temporal data, not including
     * the effective dates.
     *
     * @param d The object to test for similar data.
     * @return <tt>true</tt> if the temporal data is equal,
     *      <tt>false</tt> otherwise.
     */
    boolean equalsIgnorePeriod(TemporalData d);

    /**
     * Copy all data fields (including TimePeriod), except for the database
     * primary key field.
     *
     * @return A cloned object with no database identity.
     */
    Object cloneData();

    /**
     * Clones the TimePeriod.
     *
     * @return The cloned object
     */
    Object clone();
    
    /**
     * Sets the TimeLine Key used to differentiate between denormalized
     * TimeLines.
     * 
     * @return The TimeLine key.
     */
    void setTimeLineKey(Object key);
    
    /**
     * @return The surrogate identifier for this temporal record.
     */
    Object getIdentity();
    
    /**
     * Sets the surrogate itentifier.
     * 
     * @param identity The surrogate identifier.
     */
    void setIdentity(Object identity);
    
    /**
     * Gets the logical unique key, which should consist of
     * the timeLineKey and the effective date.
     *  
     * @return The logical unique key.
     */
    Object getLogicalKey();
}
