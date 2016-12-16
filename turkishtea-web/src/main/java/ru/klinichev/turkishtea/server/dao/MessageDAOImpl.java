package ru.klinichev.turkishtea.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
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

    String url = "jdbc:sqlite:C:\\sqlite\\db\\hottea.db";

    private static final Logger simpleLogger = Logger.getLogger("MessageDAOImpl");

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    /* @Override
    @Transactional(readOnly = false)
    public void addToDatabase(Message newMessage) {

        Connection c = null;
        PreparedStatement pst = null;
        simpleLogger.log(Level.INFO, "Starting addToDatabase");
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            pst = c.prepareStatement("insert into messages (senderid, receiverid, content, date) values (?, ?, ?, ?)");
            pst.setInt(1, newMessage.getSender());
            pst.setInt(2, newMessage.getReceiver());
            pst.setString(3, newMessage.getContent());
            pst.setLong(4, newMessage.getCreationDate());
            pst.execute();
        }
        catch (Exception e) {
            simpleLogger.log(Level.SEVERE, "Exception : ", e);
        }
        finally {
            try {
                if (c != null) {
                    c.close();
                }
            }
            catch (SQLException e) {
                simpleLogger.log(Level.SEVERE, "SQLException : ", e);
            }
        }

    }

    @Override
    public List<Message> loadDatabase(long since, int thisId, int thatId) {

        List<Message> selectedMessages = new ArrayList<Message>();
        Connection c = null;
        PreparedStatement pst = null;
        simpleLogger.log(Level.INFO, "Starting loadDatabase");
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            pst = c.prepareStatement("select senderid, receiverid, content, \"date\" from messages where \"date\" > ? and ((senderid = ? and receiverid = ?) or (senderid = ? and receiverid = ?))");
            pst.setLong(1, since);
            pst.setInt(2, thisId);
            pst.setInt(3, thatId);
            pst.setInt(4, thatId);
            pst.setInt(5, thisId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                selectedMessages.add(new Message(rs.getInt("senderid"), rs.getInt("receiverid"), rs.getString("content"), rs.getLong("date")));
            }
        }
        catch (Exception e) {
            simpleLogger.log(Level.SEVERE, "Exception : ", e);
        }
        finally {
            try {
                if (c != null) {
                    c.close();
                }
            }
            catch (SQLException e) {
                simpleLogger.log(Level.SEVERE, "SQLException : ", e);
            }
        }
        return selectedMessages;

    } */

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
        String hql = "FROM Message WHERE creationDate > since AND ((sender = thisId AND receiver = thatId) " +
                "OR (sender = thatId AND receiver = thisId))";
        Query query = session.createQuery(hql);
        List<Message> messages = query.list();
        return messages;
    }

}
