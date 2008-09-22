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
package org.hibernate.usertype;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentTimeLine;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.kowboy.temporal.TimeLine;
import org.kowboy.temporal.TimeLineFactory;

public class PeriodOfExistenceType extends TimeLineType {

    @Override
    public Object instantiate() {
        return TimeLineFactory.POE_TL_FACTORY.createTimeLine();
    }

    public PersistentCollection instantiate(SessionImplementor sessionImpl, CollectionPersister persister) throws HibernateException {
        return new PersistentTimeLine(sessionImpl, TimeLineFactory.POE_TL_FACTORY);
    }

    public PersistentCollection wrap(SessionImplementor session, Object obj) {
        if (session.getEntityMode() == EntityMode.DOM4J) {
            throw new IllegalStateException("dom4j not supported");
        } else {
            return new PersistentTimeLine(session, (TimeLine) obj, TimeLineFactory.POE_TL_FACTORY);
        }
    }

	public Object instantiate(int anticipatedSize) {
		return instantiate();
	}
}
