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
package org.kowboy.temporal.domain;

import org.kowboy.temporal.NormalizedTemporalData;
import org.kowboy.temporal.TemporalData;
import org.kowboy.temporal.Utils;

/**
 * This is a sample object for testing.
 */
public class PhoneNumber extends NormalizedTemporalData {
	private static final long serialVersionUID = 5751115458896868623L;

	private Integer id;
	private String numberString;
	private Integer areaCode;
	
	public Object cloneData() {
		PhoneNumber pn = new PhoneNumber();
		pn.setNumberString(numberString);
		pn.setAreaCode(areaCode);
		return pn;
	}

	public boolean equalsIgnorePeriod(TemporalData d) {
		if (d == null) return false;
		if (!d.getClass().equals(PhoneNumber.class)) return false;
		PhoneNumber pn = (PhoneNumber) d;
		if (!Utils.nullSafeEquals(numberString, pn.numberString) || 
				!Utils.nullSafeEquals(areaCode, pn.areaCode)) return false;		
		return true;
	}

	public Object getIdentity() {
		return id;
	}

	public void setIdentity(Object identity) {
		this.id = (Integer) identity;
	}

	public void setNumberString(String numberString) {
		this.numberString = numberString;
	}

	public String getNumberString() {
		return numberString;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getAreaCode() {
		return areaCode;
	}
}
