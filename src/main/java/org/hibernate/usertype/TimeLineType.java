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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;

/**
 * Hibernate custom collection type for TimeLine.
 * 
 * @version $Rev$ $Date$
 * @author $Author$
 */
public abstract class TimeLineType implements UserCollectionType {
    public abstract Object instantiate();   

    @SuppressWarnings("unchecked")
	public Iterator getElementsIterator(Object obj) {
        return ((Collection) obj).iterator();
    }

    @SuppressWarnings("unchecked")
	public boolean contains(Object collection, Object obj) {
        return ((Collection) collection).contains(obj);
    }

    public Object indexOf(Object collection, Object obj) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public Object replaceElements(Object original, Object target,
            CollectionPersister persister, Object owner, Map copyCache,
            SessionImplementor sessionImpl) throws HibernateException {
        Collection result = (Collection) target;
        result.clear();
        result.addAll((Collection) original);
        return result;
    }
}
