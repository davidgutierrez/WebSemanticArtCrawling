package org.uniandes.websemantic.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.uniandes.websemantic.object.Artist;

public class HibernateSession {

	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;

	private static HibernateSession instance = null;

	private static Session session;
	
	protected HibernateSession() {
		sessionFactory = createSessionFactory();
	}
	public static HibernateSession getInstance() {
		if(instance == null) {
			instance = new HibernateSession();
		}
		return instance;
	}

	private static SessionFactory createSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.configure();
		serviceRegistry = new ServiceRegistryBuilder().applySettings(
				configuration.getProperties()). buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}


	public void closeSession() {
		if(!session.isOpen()){
			session.getTransaction().commit();
			session.close();
		}
	}
	public void save(Artist pageArtist) {
		session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(pageArtist);
		session.getTransaction().commit();
		session.close();
	}
	public List<?> createCriteria(Class<Artist> class1) {
		session = sessionFactory.openSession();
		session.beginTransaction();
		List<?> list = session.createCriteria(class1).list();
		session.getTransaction().commit();
		session.close();
		return list;
	}

}
