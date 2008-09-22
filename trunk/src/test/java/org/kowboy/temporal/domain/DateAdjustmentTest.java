package org.kowboy.temporal.domain;

import java.text.ParseException;
import java.util.Iterator;

import org.kowboy.temporal.TimePeriod;
import org.kowboy.temporal.Utils;

/**
 * This tests the insertion of a temporal data record with a time period 
 * that overlaps existing records. The existing records should be updated
 * with new start and/or end dates.
 */
public class DateAdjustmentTest extends PersistenceTest {
	private Person p;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		p = new Person();
		p.setFirstName("Bill");
		p.setLastName("Smith");
		session.save(p);
		
		// Add two phone records
		PhoneNumber ph = new PhoneNumber();
		ph.setAreaCode(502);
		ph.setNumberString("444-5555");
		ph.setTimePeriod(new TimePeriod("2008-01-01", "2008-05-10"));
		p.addPhoneHistory(ph);
		
		ph = new PhoneNumber();
		ph.setAreaCode(606);
		ph.setNumberString("555-6666");
		ph.setTimePeriod(new TimePeriod("2008-06-04", "2008-07-22"));
		p.addPhoneHistory(ph);
		session.update(p);
		session.getTransaction().commit();
		
		// Get a new session and attached person record.
		session = sessionFactory.openSession();
		session.beginTransaction();
		p = (Person) session.get(Person.class, p.getId());
	}
	
	@SuppressWarnings("unchecked")
	public void testOverlappingDates() throws ParseException {
		assertEquals(2, p.getPhoneHistory().size());
		
		PhoneNumber ph = new PhoneNumber();
		ph.setAreaCode(303);
		ph.setNumberString("222-3333");
		ph.setTimePeriod(new TimePeriod("2008-05-07", "2008-06-09"));
		p.addPhoneHistory(ph);
		session.update(p);
		commit();
		
		p = (Person) session.get(Person.class, p.getId());
		assertEquals(3, p.getPhoneHistory().size());
		
		Iterator<PhoneNumber> iter = (Iterator<PhoneNumber>) p.getPhoneHistory().iterator(); 
		
		// First record should end on 5/6.
		PhoneNumber ph1 = iter.next();
		assertEquals(Utils.newDate(2008, 1, 1), ph1.getTimePeriod().getStartDate());
		assertEquals(Utils.newDate(2008, 5, 6), ph1.getTimePeriod().getEndDate());
		
		PhoneNumber ph2 = iter.next();
		assertEquals(Utils.newDate(2008, 5, 7), ph2.getTimePeriod().getStartDate());
		assertEquals(Utils.newDate(2008, 6, 9), ph2.getTimePeriod().getEndDate());
		
		// Third record should start on 6/10.
		PhoneNumber ph3 = iter.next();
		assertEquals(Utils.newDate(2008, 6, 10), ph3.getTimePeriod().getStartDate());
		assertEquals(Utils.newDate(2008, 7, 22), ph3.getTimePeriod().getEndDate());
	}
	
	@SuppressWarnings("unchecked")
	public void testClear() {
		 assertEquals(2, p.getPhoneHistory().size());
		 TimePeriod clearPeriod = new TimePeriod(Utils.newDate(2008, 2, 4), TimePeriod.END_OF_TIME);
		 p.getPhoneHistory().clear(clearPeriod);
		 session.update(p);
		 commit();
		 
		 p = (Person) session.get(Person.class, p.getId());
		 assertEquals(1, p.getPhoneHistory().size());
		 Iterator<PhoneNumber> iter = (Iterator<PhoneNumber>) p.getPhoneHistory().iterator();
		 PhoneNumber ph1 = iter.next();
		 assertEquals(Utils.newDate(2008, 2, 3), ph1.getTimePeriod().getEndDate());
	}
	
	@SuppressWarnings("unchecked")
	public void testMerge() {
		assertEquals(2, p.getPhoneHistory().size());
		TimePeriod mergePeriod = new TimePeriod(Utils.newDate(2008, 2, 4), TimePeriod.END_OF_TIME);
		PhoneNumber ph = new PhoneNumber();
		ph.setAreaCode(502);
		ph.setNumberString("444-5555");
		ph.setTimePeriod(mergePeriod);
		p.addPhoneHistory(ph);
		session.update(p);
		commit();
		
		p = (Person) session.get(Person.class, p.getId());
		assertEquals(1, p.getPhoneHistory().size());
		Iterator<PhoneNumber> iter = (Iterator<PhoneNumber>) p.getPhoneHistory().iterator();
		PhoneNumber ph1 = iter.next();
		assertEquals(Utils.newDate(2008, 1, 1), ph1.getTimePeriod().getStartDate());
		assertEquals(TimePeriod.END_OF_TIME, ph1.getTimePeriod().getEndDate());
	}
}
