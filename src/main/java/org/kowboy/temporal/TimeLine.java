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

/**
 * Interface for TimeLines.
 */
@SuppressWarnings("unchecked")
public interface TimeLine extends Set {

    /**
     * Gets the object from this collection that was effective
     * as of the Date specified.
     * @param asOf Date for which TimePeriod is returned
     * @return TimePeriod
     */
    TemporalData getAsOf(Date asOf);
    
    /**
     * Get a TimeLine of records that are effective during the
     * specified time period. This includes records that overlap
     * the start and end dates of the provided time period.
     * 
     * @param timePeriod The range of dates to search for effective records.
     * @return A TimeLine of the effective records for the specified date.
     */
    TimeLine getEffectiveSubset(TimePeriod timePeriod);
    
    /**
     * Get a TimeLine of records that are completely contained within
     * the specified time period.
     * 
     * @param timePeriod The maximum date range of the new TimeLine.
     * @return A new TimeLine.
     */
    TimeLine getSubset(TimePeriod timePeriod);
    
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
    void setProperty(String prop, TimePeriod period, Object value, TemporalDataFactory factory);
    
    /**
     * Gets the value of a temporal property for a specified effective date.
     *  
     * @param prop the name of the property to get.
     * @param asOf the effective date.
     * @return the property value at the specified effective date.
     */
    Object getProperty(String prop, Date asOf);

    /**
     * Creates a "gap" in the timeline(s). This can be used to terminate records
     * from a particular start date to the end of time.
     * 
     * @param period The timeperiod to clear from these timelines.
     */
    void clear(TimePeriod period);
    
    /**
     * Gets the latest effective date (start date) in this TimeLine.
     * 
     * @return The start date from the last period in the timeline.
     */
    Date getLatestEffectiveDate();
}
