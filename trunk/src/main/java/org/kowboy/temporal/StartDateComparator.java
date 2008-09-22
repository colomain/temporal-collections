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
import java.util.Comparator;

/**
 * Check the start dates of two TimePeriods to see which one
 * occurs first in time.
 */
public class StartDateComparator implements Comparator<TemporalData>, Serializable  {
    private static final long serialVersionUID = 3135545518046763585L;

    /**
     * Compares two Timeperiods
     * @param arg0 Object of TimePeriod
     * @param arg1 Object of TimePeriod
     * @return int based on the comparisions
     */
    public int compare(TemporalData d1, TemporalData d2) {
        if (d1 == null || d2 == null
                || d1.getTimePeriod() == null
                || d2.getTimePeriod() == null) {
            throw new IllegalArgumentException
                ("Can not compare null object references");
        }
        return d1.getTimePeriod().getStartDate()
            .compareTo(d2.getTimePeriod().getStartDate());
    }
}
