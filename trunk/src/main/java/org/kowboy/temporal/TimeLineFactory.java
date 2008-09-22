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

/**
 * Interface for TimeLine factories. Primary use is in DenormalizedTimeLine.
 */
public interface TimeLineFactory extends Serializable {

    public static final TimeLineFactory PERPETUAL_TL_FACTORY = 
        new PerpetualTimeLineFactory();
    public static final TimeLineFactory POE_TL_FACTORY = 
        new PeriodOfExistenceTimeLineFactory();
    
    /**
     * Creates a new TimeLine instance.
     * 
     * @return The new TimeLine.
     */
    public TimeLine createTimeLine();
}
