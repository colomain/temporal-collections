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
package org.hibernate.collection;

import java.util.Date;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.kowboy.temporal.TemporalData;
import org.kowboy.temporal.TemporalDataFactory;
import org.kowboy.temporal.TimeLine;
import org.kowboy.temporal.TimeLineFactory;
import org.kowboy.temporal.TimePeriod;


/**
 * Base class for persistent TimeLine implementations.
 */
public class PersistentTimeLine extends PersistentSet implements TimeLine {
    private static final long serialVersionUID = 241072427457869264L;
    TimeLineFactory factory;
    
    public PersistentTimeLine() {
        super();
    }
    
    public PersistentTimeLine(SessionImplementor session, TimeLineFactory factory) {
        super(session);
        this.factory = factory;
    }
    
    public PersistentTimeLine(SessionImplementor session, TimeLine timeLine, TimeLineFactory factory) {
        super(session, timeLine);
        this.factory = factory;
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.set = factory.createTimeLine();
    }

	public void clear(TimePeriod period) {
		write();
		((TimeLine) set).clear(period);
	}

	public TemporalData getAsOf(Date asOf) {
		read();
		return ((TimeLine) set).getAsOf(asOf);
	}

	public TimeLine getEffectiveSubset(TimePeriod timePeriod) {
		read();
		return ((TimeLine) set).getEffectiveSubset(timePeriod);
	}

	public Date getLatestEffectiveDate() {
		read();
		return ((TimeLine) set).getLatestEffectiveDate();
	}

	public Object getProperty(String prop, Date asOf) {
		read();
		return ((TimeLine) set).getProperty(prop, asOf);
	}

	public TimeLine getSubset(TimePeriod timePeriod) {
		read();
		return ((TimeLine) set).getSubset(timePeriod);
	}

	public void setProperty(String prop, TimePeriod period, Object value,
			TemporalDataFactory factory) {
		write();
		((TimeLine) set).setProperty(prop, period, value, factory);
	}
}
