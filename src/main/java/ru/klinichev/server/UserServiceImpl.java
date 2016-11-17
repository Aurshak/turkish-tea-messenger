package ru.klinichev.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ru.klinichev.client.UserService;

@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService {
	
	String url = "jdbc:sqlite:C:\\sqlite\\db\\hottea.db";
			
	@Override
	public void addUser(String login, String password) throws RuntimeException {
		
		login = escapeHtml(login);
		password = escapeHtml(password);
		
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		
		Connection c = null;
		PreparedStatement pst = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			pst = c.prepareStatement("insert into users (name, password) values (?, ?)");
			pst.setString(1, login);
			pst.setString(2, hashed);
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
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@Override
	public boolean checkUser(String login, String password) throws RuntimeException {
		
		login = escapeHtml(login);
		password = escapeHtml(password);
		
		boolean result = false;
		
		Connection c = null;
		Statement st = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			st = c.createStatement();
			st.setQueryTimeout(30);
			ResultSet rs = st.executeQuery("select password from users where name = '" + login + "'");
			if (rs.isClosed()) return false;
			String hashed = rs.getString("password");
			if (BCrypt.checkpw(password, hashed)) {
				result = true;
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
		return result;
	}

	@Override
	public void setSessionName(String name) {
		HttpSession httpSession = getThreadLocalRequest().getSession(true);
	    httpSession.setAttribute("username", name);		
	}

	@Override
	public String getSessionName() {
		HttpSession session = getThreadLocalRequest().getSession(true);
	    if (session.getAttribute("username") != null)
	    {
	        return (String) session.getAttribute("username");
	    }
	    else 
	    {
	        return "";
	    }
	}

	@Override
	public List<String> getAllUsers() {
		List<String> users = new ArrayList<String>();
		Connection c = null;
		Statement st = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			st = c.createStatement();
			st.setQueryTimeout(30);
			ResultSet rs = st.executeQuery("select name from users");
			while (rs.next()) {
				users.add(rs.getString("name"));
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
		return users;
	}

}
