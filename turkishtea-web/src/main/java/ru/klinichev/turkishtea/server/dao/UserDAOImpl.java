package ru.klinichev.turkishtea.server.dao;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.klinichev.turkishtea.shared.User;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Repository
public class UserDAOImpl implements UserDAO {

    private static final Logger simpleLogger = Logger.getLogger("UserDAOImpl");

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addUser(User newUser) {
        simpleLogger.log(Level.INFO, "Starting addUser");
        Session session = sessionFactory.openSession();
        session.save(newUser);
    }

    @Override
    public User getUserByName(String username) {
        simpleLogger.log(Level.INFO, "Starting getUserByName with username " + username);
        Session session = sessionFactory.openSession();
        String hql = "FROM User WHERE name = :username";
        Query query = session.createQuery(hql);
        query.setParameter("username", username);
        if (query.list().isEmpty()) {
            return null;
        }
        else {
            User user = (User) query.list().get(0);
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        simpleLogger.log(Level.INFO, "Starting getAllUsers");
        Session session = sessionFactory.openSession();
        String hql = "FROM User";
        Query query = session.createQuery(hql);
        List<User> users = query.list();
        return users;
    }
}
