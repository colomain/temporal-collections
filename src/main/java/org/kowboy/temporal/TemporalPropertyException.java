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

public class TemporalPropertyException extends RuntimeException {
    private static final long serialVersionUID = -8335191579630309550L;

    public TemporalPropertyException() {
        super();
    }

    public TemporalPropertyException(String msg, Throwable root) {
        super(msg, root);
    }

    public TemporalPropertyException(String msg) {
        super(msg);
    }

    public TemporalPropertyException(Throwable root) {
        super(root);
    }
}
