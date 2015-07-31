package com.fortitudetec.example.todo.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

public abstract class AbstractHibernateDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SessionFactory _sessionFactory;

    protected Session getCurrentSession() {
        return _sessionFactory.getCurrentSession();
    }

    protected void flushSession() {
        getCurrentSession().flush();
    }

    protected void clearSession() {
        getCurrentSession().clear();
    }

    protected void flushAndClearSession() {
        flushSession();
        clearSession();
    }
}
