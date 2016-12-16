package ru.klinichev.turkishtea.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Path("users")
public class UserResource {
	
	String url = "jdbc:sqlite:C:\\sqlite\\db\\hottea.db";
	private static final Logger simpleLogger = Logger.getLogger("UserResource");
	
	@PostMapping
	@POST
	public void addUser(@QueryParam("login") String login, @QueryParam("password") String password) 
			throws RuntimeException {
		
		simpleLogger.log(Level.INFO, "Starting addUser method");
		
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
			simpleLogger.log(Level.SEVERE, "addUser(): Exception in try-block: ", e);
		}
		finally {
			try {
				if (c != null) {
					c.close();
				}
			}
			catch (SQLException e) {
				simpleLogger.log(Level.SEVERE, "addUser(): Exception in finally-try-block", e);
			}
		}
		
	}
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@GetMapping(path="/{login}", produces="application/json")
	@GET
	@Produces("application/json")
	@Path("/{login}")
	public boolean checkUser(@PathParam("login") @PathVariable String login, @QueryParam("password") String password) 
			throws RuntimeException {
		
		simpleLogger.log(Level.INFO, "Starting checkUser method");
		
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
			simpleLogger.log(Level.SEVERE, "checkUser(): Exception in try-block: ", e);
		}
		finally {
			try {
				if (c != null) {
					c.close();
				}
			}
			catch (SQLException e) {
				simpleLogger.log(Level.SEVERE, "checkUser(): Exception in finally-try-block", e);
			}
		}
		return result;
	}

	/* @POST
	public void setSessionName(@QueryParam("name") String name) {
		HttpSession httpSession = getThreadLocalRequest().getSession(true);
	    httpSession.setAttribute("username", name);		
	}

	@GET
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
	} */

	@GetMapping(produces="application/json")
	@GET
	@Produces("application/json")
	public Map<Integer, String> getAllUsers() {
		simpleLogger.log(Level.INFO, "Starting getAllUsers method");
		Map<Integer, String> users = new HashMap<>();
		Connection c = null;
		Statement st = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(url);
			st = c.createStatement();
			st.setQueryTimeout(30);
			ResultSet rs = st.executeQuery("select userid, name from users");
			while (rs.next()) {
				users.put(rs.getInt("userid"), rs.getString("name"));
			}
		}
		catch (Exception e) {
			simpleLogger.log(Level.SEVERE, "getAllUsers(): Exception in try-block: ", e);
		}
		finally {
			try {
				if (c != null) {
					c.close();
				}
			}
			catch (SQLException e) {
				simpleLogger.log(Level.SEVERE, "getAllUsers(): Exception in finally-try-block", e);
			}
		}
		return users;
	}

}
