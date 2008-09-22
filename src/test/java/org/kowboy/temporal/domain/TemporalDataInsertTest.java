package org.kowboy.temporal.domain;

import java.text.ParseException;

import org.kowboy.temporal.TimePeriod;
import org.kowboy.temporal.Utils;

/**
 * This will test the insertion of temporal data record(s).  
 */
public class TemporalDataInsertTest extends PersistenceTest {
	
	private Person p;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		p = new Person();
		p.setFirstName("Bill");
		p.setLastName("Smith");
		session.save(p);
	}
	
	public void testInsert() throws ParseException {

		// Insert a phone number.
		PhoneNumber ph = new PhoneNumber();
		ph.setAreaCode(502);
		ph.setNumberString("444-5555");
		ph.setTimePeriod(new TimePeriod("2008-01-01", "2008-05-10"));
		p.addPhoneHistory(ph);
		session.update(p);
		commit();
		
		Person p2 = (Person) session.get(Person.class, p.getId());
		assertEquals(p.getFirstName(), p2.getFirstName());
		assertEquals(p.getLastName(), p2.getLastName());
		assertEquals(1, p2.getPhoneHistory().size());
		PhoneNumber ph2 = (PhoneNumber) p2.getPhoneHistory().getAsOf(Utils.newDate(2008, 2, 1));
		assertEquals("444-5555", ph2.getNumberString());		
	}
}
