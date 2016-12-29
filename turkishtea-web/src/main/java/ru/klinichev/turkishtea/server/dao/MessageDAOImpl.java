package ru.klinichev.turkishtea.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.klinichev.turkishtea.shared.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class MessageDAOImpl implements MessageDAO {

    private static final Logger simpleLogger = Logger.getLogger("MessageDAOImpl");

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addToDatabase(Message newMessage) {
        simpleLogger.log(Level.INFO, "Starting addToDatabase");
        Session session = sessionFactory.openSession();
        session.save(newMessage);
    }

    @Override
    public List<Message> loadDatabase(long since, int thisId, int thatId) {
        simpleLogger.log(Level.INFO, "Starting loadDatabase");
        Session session = sessionFactory.openSession();
        String hql = "FROM Message WHERE creationDate > :since AND ((sender = :thisId AND receiver = :thatId) " +
                "OR (sender = :thatId AND receiver = :thisId))";
        Query query = session.createQuery(hql);
        query.setParameter("since", since);
        query.setParameter("thisId", thisId);
        query.setParameter("thatId", thatId);
        List<Message> messages = query.list();
        return messages;
    }

}
