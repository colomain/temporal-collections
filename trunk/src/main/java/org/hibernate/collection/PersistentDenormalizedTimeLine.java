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
import org.kowboy.temporal.DenormalizedTimeLine;
import org.kowboy.temporal.DenormalizedTimeLineImpl;
import org.kowboy.temporal.TemporalData;
import org.kowboy.temporal.TemporalDataFactory;
import org.kowboy.temporal.TimeLineFactory;
import org.kowboy.temporal.TimePeriod;

public class PersistentDenormalizedTimeLine extends PersistentSet implements DenormalizedTimeLine {
    private static final long serialVersionUID = 2091601654356319502L;
    TimeLineFactory factory;
    
    public PersistentDenormalizedTimeLine(SessionImplementor session, TimeLineFactory factory) {
        super(session);
        this.factory = factory;
    }

    public PersistentDenormalizedTimeLine() {
        super();
    }

    public PersistentDenormalizedTimeLine(SessionImplementor session, DenormalizedTimeLine timeLine,
            TimeLineFactory factory) {
        super(session, timeLine);
        this.factory = factory;
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.set = new DenormalizedTimeLineImpl(factory);
    }

	public void clear(TimePeriod period) {
		write();
		((DenormalizedTimeLine) set).clear(period);
	}

	public TemporalData getAsOf(Object key, Date asOf) {
		read();
		return ((DenormalizedTimeLine) set).getAsOf(key, asOf);
	}

	public Object getProperty(Object key, String prop, Date asOf) {
		read();
		return ((DenormalizedTimeLine) set).getProperty(key, prop, asOf);
	}

	public void setProperty(Object key, String prop, TimePeriod period,
			Object value, TemporalDataFactory factory) {
		write();
		((DenormalizedTimeLine) set).setProperty(key, prop, period, value, factory);
	}
}
