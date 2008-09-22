package org.kowboy.temporal.domain;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public abstract class PersistenceTest extends TestCase {

	protected SessionFactory sessionFactory;
	protected Session session;
	
	protected void setUp() throws Exception {
		super.setUp();
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (session.isOpen()) session.close();
		if (!sessionFactory.isClosed()) sessionFactory.close();
	}	
	
	protected void commit() {
		session.getTransaction().commit();
		session = sessionFactory.openSession();
		session.beginTransaction();
	}
}
