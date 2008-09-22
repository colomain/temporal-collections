package org.kowboy.temporal.domain;

import java.util.Date;

import org.kowboy.temporal.PeriodOfExistenceTimeLine;
import org.kowboy.temporal.TemporalData;
import org.kowboy.temporal.TemporalDataFactory;
import org.kowboy.temporal.TimeLine;
import org.kowboy.temporal.TimePeriod;

public class Person {
	private Integer id;
	private String firstName;
	private String lastName;
	private TimeLine phoneHistory = new PeriodOfExistenceTimeLine();
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setPhoneHistory(TimeLine phoneHistory) {
		this.phoneHistory = phoneHistory;
	}
	public TimeLine getPhoneHistory() {
		return phoneHistory;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public void addPhoneHistory(PhoneNumber ph) {
		phoneHistory.add(ph);
	}
	
	public String getNumberString(Date asOf) {
		return (String) phoneHistory.getProperty("numberString", asOf);
	}
	
	public void setNumberString(String numberString, TimePeriod period) {
		phoneHistory.setProperty("numberString", period, numberString, new PhoneNumberFactory());
	}
	
	public Integer getAreaCode(Date asOf) {
		return (Integer) phoneHistory.getProperty("areaCode", asOf);
	}
	
	public void setAreaCode(Integer areaCode, TimePeriod period) {
		phoneHistory.setProperty("areaCode", period, areaCode, new PhoneNumberFactory());
	}
	
	class PhoneNumberFactory implements TemporalDataFactory {
		public TemporalData newInstance() {
			return new PhoneNumber();
		}
	}
}
