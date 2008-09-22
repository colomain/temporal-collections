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
import java.util.Date;

public abstract class AbstractTemporalData
implements TemporalData, Cloneable, Serializable {

	private static final long serialVersionUID = 5956612398236281241L;
	protected TimePeriod period;

    /**
     * Get the TimePeriod.
     *
     * @return The TimePeriod for this temporal data.
     */
    public TimePeriod getTimePeriod() {
        return period;
    }

    /**
     * Set the TimePeriod.
     *
     * @param p The TimePeriod to set.
     */
    public void setTimePeriod(TimePeriod p) {
        this.period = p;
    }

    /**
     * Create a clone of this temporal data.
     *
     * @return The cloned object.
     */
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
            ((TemporalData) clone).setTimePeriod(new TimePeriod(period));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    /**
     * Tests for natural key equality.
     *
     * @param obj The object to compare against
     * @return <tt>true</tt> if equal, <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        TemporalData d = (TemporalData) obj;
        return equalsIgnorePeriod(d) && getTimePeriod().equals(d.getTimePeriod());
    }

    /**
     * Computes the hash code based on the timeline key object and
     * the effective date.
     *
     * @return The hash code for this temporal data.
     */
    public int hashCode() {
        return getTimePeriod().getStartDate().hashCode()
            * getTimeLineKey().hashCode() % Integer.MAX_VALUE;
    }

    public abstract void setTimeLineKey(Object key);

    public abstract Object getTimeLineKey();

    public Object getLogicalKey() {
        return new LogicalKey(getTimeLineKey(), getTimePeriod().getStartDate());
    }
    
    final class LogicalKey {
        private Object key;
        private Date effective;
        public LogicalKey(Object timeLineKey, Date effectiveDate) {
            this.key = timeLineKey;
            this.effective = effectiveDate;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            LogicalKey lk = (LogicalKey) obj;
            return Utils.nullSafeEquals(key, lk.key) && Utils.nullSafeEquals(effective, lk.effective);
        }
        
        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return key+" "+effective;
        }
    }
}
