package ru.klinichev.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ru.klinichev.client.MessageService;
import ru.klinichev.shared.Message;

@SuppressWarnings("serial")
public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {
	
	private final ArrayList<Message> messages = new ArrayList<Message>();
	String url = "jdbc:sqlite:C:\\sqlite\\db\\hottea.db";

	@Override
	public void addMessage(String sender, String receiver, String content) throws RuntimeException {
		
		sender = escapeHtml(sender);
		receiver = escapeHtml(receiver);
		content = escapeHtml(content);
		
		Message newMessage = new Message(sender, receiver, content, new Date());
		messages.add(newMessage);
		addToDatabase(newMessage);
		
	}

	@Override
	public List<Message> listMessages(Date since, String thisUser, String thatUser) throws RuntimeException {		
		List<Message> selectedMessages = new ArrayList<Message>();
		if (since == null) {
			selectedMessages = loadDatabase(thisUser, thatUser);
		}
		else {
			for (Message m: messages) {
				if (m.creationDate.after(since) && ((m.sender.equals(thisUser) && m.receiver.equals(thatUser)) 
						|| (m.sender.equals(thatUser) && m.receiver.equals(thisUser)))) {
					selectedMessages.add(m);
				}
			}
		}
		return selectedMessages;
	}
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	private void addToDatabase(Message newMessage) {
		
		Connection c = null;
		Statement st1 = null;
		Statement st2 = null;
		PreparedStatement pst = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			st1 = c.createStatement();
			st1.setQueryTimeout(30);
			ResultSet rs1 = st1.executeQuery("select userid from users where name = '" + newMessage.sender + "'");
			int senderId = rs1.getInt("userid");
			st2 = c.createStatement();
			st2.setQueryTimeout(30);
			ResultSet rs2 = st2.executeQuery("select userid from users where name = '" + newMessage.receiver + "'");
			int receiverId = rs2.getInt("userid");
			pst = c.prepareStatement("insert into messages (senderid, receiverid, content, date) values (" + 
			senderId + ", " + receiverId +", ?, ?)");
			pst.setString(1, newMessage.content);
			pst.setTimestamp(2, new Timestamp(newMessage.creationDate.getTime()));
			pst.execute();
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			try {
				if (c != null) {
					c.close();
				}
			}
			catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
	}
	
	private List<Message> loadDatabase(String thisUser, String thatUser) {
		List<Message> selectedMessages = new ArrayList<Message>();
		Connection c = null;
		Statement st1 = null;
		Statement st2 = null;
		Statement st3 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			st1 = c.createStatement();
			st1.setQueryTimeout(30);
			ResultSet rs1 = st1.executeQuery("select userid from users where name = '" + thisUser + "'");
			int thisUserId = rs1.getInt("userid");
			st2 = c.createStatement();
			st2.setQueryTimeout(30);
			ResultSet rs2 = st2.executeQuery("select userid from users where name = '" + thatUser + "'");
			int thatUserId = rs2.getInt("userid");
			st3 = c.createStatement();
			st3.setQueryTimeout(30);
			ResultSet rs = st3.executeQuery("select " + 
			"(select name from users where userid = messages.senderid) as sendername, " +
			"(select name from users where userid = messages.receiverid) as receivername, " +
			"content, date from messages where " + 
			"(senderid = " + thisUserId +" and receiverid = " + thatUserId + ") or " + 
			"(senderid = " + thatUserId +" and receiverid = " + thisUserId + ")");
			while (rs.next()) {
				selectedMessages.add(new Message(rs.getString("sendername"), rs.getString("receivername"),  
						rs.getString("content"), new Date(rs.getTimestamp("date").getTime())));
			}
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			try {
				if (c != null) {
					c.close();
				}
			}
			catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		return selectedMessages;
	}

}
