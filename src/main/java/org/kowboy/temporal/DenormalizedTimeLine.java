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
import java.util.Set;

@SuppressWarnings("unchecked")
public interface DenormalizedTimeLine extends Set {

    /**
     * Gets the TemporalData for the specified key and date.
     * 
     * @param key The TimeLine key used to determine which denormalized timeline
     *      to search.
     * @param asOf The date to search for an effective record.
     * @return The TemporalData if found, otherwise null.
     */
    public TemporalData getAsOf(Object key, Date asOf);

    /**
     * Sets a temporal data property. If there is no temporal data record for
     * the specified period, then a new one will be created using the specified
     * factory.
     * 
     * @param key The TimeLine key.
     * @param prop the name of the property to set.
     * @param period the TimePeriod for the property.
     * @param value the value of the property.
     * @param factory a factory for creating the appropriate TemporalData objects.
     */
    public void setProperty(final Object key, final String prop,
            TimePeriod period, final Object value,
            final TemporalDataFactory factory);

    /**
     * Gets the value of a temporal property for a specified effective date.
     *  
     * @param key The TimeLine key.
     * @param prop the name of the property to get.
     * @param asOf the effective date.
     * @return the property value at the specified effective date.
     */
    public Object getProperty(Object key, String prop, Date asOf);

    /**
     * Creates a "gap" in the timeline(s). This can be used to terminate records
     * from a particular start date to the end of time.
     * 
     * @param period The timeperiod to clear from these timelines.
     */
    public void clear(TimePeriod period);

}