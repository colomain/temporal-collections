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


/**
 * <tt>TemporalData</tt> wrapper for generic <tt>Object</tt> instances.
 * Do NOT use this to wrap persistent classes (Hibernate-mapped objects).
 * Persistent objects should implement <tt>TemporalData</tt> themselves
 * so they can implement the <tt>cloneData()</tt> method so it doesn't
 * clone the database id field. This class is intended to wrap simple
 * <tt>Object</tt> types like <tt>String</tt>, <tt>Integer</tt>, 
 * <tt>Double</tt>, etc... Primarily for testing.
 * 
 * @see org.kowboy.temporal.TemporalData
 */
class SimpleTemporalData extends NormalizedTemporalData 
implements Cloneable {
    private static final long serialVersionUID = 4118561344763996802L;
    private Object data = null;
    
    /**
     * Creates an instance with a default period.
     */ 
    public SimpleTemporalData() {
        this(new TimePeriod());
    }
    
    /**
     * Creates an instance with the specified time period.
     * @param p The time period for which this data is effective.
     */
    public SimpleTemporalData(TimePeriod p) {
        setTimePeriod(p);
    }

    /**
     * Creates an instance with the specified time period.
     * 
     * @param start The start date of the period.
     * @param end The end date of the period.
     */
    public SimpleTemporalData(Date start, Date end) {
        this(new TimePeriod(start, end));
    }
    
    /**
     * Creates an instance with time period and data initialized.
     * 
     * @param p The time period for which this data is effective.
     * @param d The data.
     */
    public SimpleTemporalData(TimePeriod p, Object d) {
        setTimePeriod(p);
        this.data = d;
    }

    /**
     * Creates an instance with time period and data initialized.
     * 
     * @param start The start date of the period.
     * @param end The end date of the period.
     * @param d The data.
     */
    public SimpleTemporalData(Date start, Date end, Object d) {
        this(new TimePeriod(start, end), d);
    }

    /**
     * Gets the data for this temporal object.
     * 
     * @return Returns the data.
     */
    public Object getData() {
        return data;
    }
    
    /**
     * Sets the temporal data.
     * 
     * @param data The data to set.
     */
    public void setData(Object data) {
        this.data = data;
    }

    public boolean equalsIgnorePeriod(final TemporalData d) {
        if (d == null) {
            return false;
        }
        return data.equals(((SimpleTemporalData) d).data);
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        SimpleTemporalData d = (SimpleTemporalData) o;
        if (this.data == null || d.data == null) {
            return this.data == null 
                && d.data == null
                && this.getTimePeriod().equals(d.getTimePeriod());
        }
        return this.data.equals(d.data) 
            && this.getTimePeriod().equals(d.getTimePeriod());
    }
    
    public int hashCode() {
        return data.hashCode() * getTimePeriod().hashCode() % Integer.MAX_VALUE;
    }
    
    public Object clone() {
        SimpleTemporalData d = (SimpleTemporalData) super.clone();
        d.setTimePeriod((TimePeriod) getTimePeriod().clone());
        return d;
    }

    public Object cloneData() {
        // We assume that this object is not storing a persistent entity,
        // so we clone all fields.
        return this.clone();
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getTimeLineKey());
        buf.append(": ");
        buf.append(getTimePeriod());
        return buf.toString();
    }

    public Object getIdentity() {
        return data;
    }

    public void setIdentity(Object identity) {
        this.data = identity;
    }
}