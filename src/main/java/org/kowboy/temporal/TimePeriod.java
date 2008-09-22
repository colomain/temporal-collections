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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This interface represents a span of time. An object that implements
 * this interface is considered "temporal".<p>
 * 
 * Implementing classes should override <tt>equals()</tt> and maybe 
 * <tt>hashCode()</tt> from <tt>java.lang.Object</tt>.
 * 
 * @see Object#equals(Object)
 * @see Object#hashCode()
 */
public class TimePeriod implements Cloneable, Serializable {
    private static final long serialVersionUID = 4308047380462786611L;
    private Date startDate;
    private Date endDate;
    
    /**
     * The Date 9999-12-31. This will be used in place of a null date value.
     * In otherwords, periods that have no end date will use this value,
     * rather than null. 
     */
    public static final Date END_OF_TIME = Utils.newDate(9999, 12, 31);

    /**
     * The string format for parseable dates.
     */
	public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Creates a TimePeriod with a start date of today and no end
     * date (9999/12/31).
     */
    public TimePeriod() {
        this(Utils.todaysDate(), END_OF_TIME);
    }
    
    /**
     * Creates a TimePeriod with the specified start date and no end date 
     * (9999/12/31).
     * 
     * @param start The start of the time period.
     */
    public TimePeriod(Date start) {
        this(start, null);
    }
    
    /**
     * Creates a TimePeriod with the specified start and end dates.
     * 
     * @param start The start of the time period.
     * @param end The end of the time period.
     */
    public TimePeriod(Date start, Date end) {
        setStartDate(start);
        setEndDate(end);
    }
    
    /**
     * Creates a TimePeriod with the specified start and end dates.
     * The string parameters are parsed and expected to be in the 
     * format yyyy-MM-dd.
     * 
     * @param start The start of the time period.
     * @param end The end of the time period.
     * @throws ParseException 
     */
    public TimePeriod(String start, String end) throws ParseException {
    	setStartDate(start);
    	setEndDate(end);
    }
    
    /**
     * Copy constructor.
     * 
     * @param period The timeperiod to copy from.
     */
    public TimePeriod(TimePeriod period) {
    	this.startDate = period.getStartDate();
    	this.endDate = period.getEndDate();
    }

	/**
     * @return Returns the endDate.
     */
    public Date getEndDate() {
        return endDate;
    }
    
    /**
     * Sets the end date. Null values are treated as
     * the end of time.
     * 
     * @param endDate The endDate to set.
     */
    public void setEndDate(Date endDate) {
        if (endDate == null) {
            this.endDate = END_OF_TIME;
        } else {
            //HACK To avoid TimeStamp comparison issue.
            this.endDate = new Date(endDate.getTime());
        }
    }

    /**
     * Sets the end date. The parameter must be parseable by the 
     * format "yyyy-MM-dd"
     * 
     * @param endDate The endDate to set.
     * @throws ParseException If the parameter is not parseable.
     */
    public void setEndDate(String endDate) throws ParseException {
        if (endDate.equalsIgnoreCase("undefined")) {
            this.endDate = END_OF_TIME;
        } else {
            this.endDate = new SimpleDateFormat(DATE_FORMAT).parse(endDate);
        }
    }
    
    /**
     * @return Returns the startDate.
     */
    public Date getStartDate() {
        return startDate;
    }
    
    /**
     * Sets the start date. Null values are treated as the
     * end of time.
     * 
     * @param startDate The startDate to set.
     */
    public void setStartDate(Date startDate) {
        if (startDate == null) {
            this.startDate = END_OF_TIME;
        } else {
            // HACK To avoid TimeStamp comparison issue. 
            this.startDate = new Date(startDate.getTime());
        }
    }
    
    /**
     * Sets the start date. The parameter must be parseable by the 
     * format "yyyy-MM-dd".
     * 
     * @param startDate The startDate to set.
     * @throws ParseException 
     */
    public void setStartDate(String startDate) throws ParseException {
        if (startDate.equalsIgnoreCase("undefined")) {
            this.startDate = END_OF_TIME;
        } else {
            this.startDate = new SimpleDateFormat(DATE_FORMAT).parse(startDate);
        }
    }
    
    /**
     * Determines if the specified date is within this time period
     * (inclusive).
     * 
     * @param d The date to test.
     * @return <tt>true</tt> if d is within this time period (inclusive),
     *      <tt>false</tt> otherwise.
     */
    public boolean contains(final Date d) {
        if (d == null) {
            // null date should be considered end of time.
            return endDate.equals(END_OF_TIME);
        }
        
        return (startDate.getTime() <= d.getTime() 
                && endDate.getTime() >= d.getTime());
    }
    
    /**
     * Determines if the specified TimePeriod is completely within this
     * time period (inclusive).
     * 
     * @param p The time period to test.
     * @return <tt>true</tt> if p is totally contained in this time period,
     *      <tt>false</tt> otherwise.
     */
    public boolean contains(final TimePeriod p) {
        return (this.contains(p.getStartDate()) && this.contains(p.getEndDate()));
    }
    
    /**
     * Checks to see if this TimePeriod is equal to the one specified.
     * 
     * @param o The TimePeriod to compare against.
     * @return <tt>true</tt> if the time periods are equal, false otherwise.
     */
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        } else if (super.equals(o)) {
            return true;
        }
        
        TimePeriod tp = (TimePeriod) o;
        return (this.startDate == tp.startDate  // handles null values
                || this.startDate.equals(tp.startDate))
                && (this.endDate == tp.endDate  // handles null values
                        || this.endDate.equals(tp.endDate));
    }
    
    /**
     * Computes the hashCode value for this TimePeriod.
     * @return the hashCode value.
     */
    public int hashCode() {
        return startDate.hashCode() * endDate.hashCode() % Integer.MAX_VALUE;
    }
    
    
    /**
     * Is this date range valid (ie, is the start date <= end date.
     * 
     * @return True if startDate <= endDate, false otherwise.
     */
    public boolean isValid() {
        return startDate.compareTo(endDate) <= 0;
    }
    
    /**
     * Create a clone of this time period.
     * 
     * @return The cloned object.
     */
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            // should never happen.
            e.printStackTrace();
        }
        return clone;
    }
    
    /**
     * Get a string representation of the time period.
     * 
     * @return A String representation of this time period.
     */
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        return df.format(startDate) + " TO " + df.format(endDate);
    }

    /**
     * Tests whether the specified time period overlaps with this one.
     * 
     * @param timePeriod The time period to test.
     * @return <tt>true</tt> if the specified time period overlaps, even
     *      for one day, false otherwise.
     */
    public boolean intersects(final TimePeriod timePeriod) {
        return contains(timePeriod.startDate) 
            || contains(timePeriod.endDate) 
            || timePeriod.contains(startDate)
            || timePeriod.contains(endDate);
    }

    /**
     * Tests whether the specified time period is adjacent to this time period.
     * This is simply a wrapper around Utils.isOneDayBefore(), but for TimePeriods
     * instead of individual dates.
     * 
     * @param tp The time period to compare to.
     * @return <tt>true</tt> if the specified time period is adjacent to this one, 
     *      <tt>false</tt> otherwise.
     */
    public boolean isAdjacentTo(final TimePeriod tp) {
        return Utils.isOneDayBefore(endDate, tp.getStartDate())
            || Utils.isOneDayBefore(tp.getEndDate(), startDate);
    }
    
    /**
     * Merge two time periods by using the earliest start date and the latest
     * end date. This implementation does not care if there is a gap between
     * the time periods. If this is not the desired behavior, the caller
     * should first check for intersection, adjacency, or whatever makes sense.
     * 
     * @param tp The TimePeriod to merge with.
     * @return The merged TimePeriod.
     */
    public TimePeriod merge(final TimePeriod tp) {
        TimePeriod mergedPeriod = (TimePeriod) tp.clone();
        if (startDate.before(mergedPeriod.getStartDate())) {
            mergedPeriod.setStartDate(startDate);
        }
        if (endDate.after(mergedPeriod.getEndDate())) {
            mergedPeriod.setEndDate(endDate);
        }
        return mergedPeriod;
    }
}
