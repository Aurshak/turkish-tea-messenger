package ru.klinichev.turkishtea.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.klinichev.turkishtea.server.service.UserService;
import ru.klinichev.turkishtea.shared.Session;
import ru.klinichev.turkishtea.shared.User;

@RestController
@RequestMapping("/users")
@Path("users")
public class UserResource implements BeanFactoryAware {

	@Autowired
	private UserService userService;

	private BeanFactory beanFactory;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private static final Logger simpleLogger = Logger.getLogger("UserResource");
	
	@PostMapping
	@POST
	public void addUser(@QueryParam("login") String login, @QueryParam("password") String password) 
			throws RuntimeException {
		
		simpleLogger.log(Level.INFO, "Starting addUser method");
		
		login = escapeHtml(login);
		password = escapeHtml(password);
		
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

		userService.addUser(new User(login, hashed));
		
	}
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@GetMapping(path="/{login}")
	@GET
	@Path("/{login}")
	public boolean checkUser(@PathParam("login") @PathVariable String login, @QueryParam("password") String password) 
			throws RuntimeException {

		/* try {
			FileHandler fh = new FileHandler("C:/sqlite/hottea.log");
			simpleLogger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			simpleLogger.log(Level.INFO, "Starting to log into file");
		} catch (IOException e) {
			e.printStackTrace();
		} */

		simpleLogger.log(Level.INFO, "Starting checkUser method");
		
		login = escapeHtml(login);
		password = escapeHtml(password);

		if (userService.getUserByName(login) == null) {
			return false;
		}
		else {
			String hashed = userService.getUserByName(login).getPassword();
			return BCrypt.checkpw(password, hashed);
		}

	}

	@PostMapping(path="/session/{name}")
	@POST
	@Path("/session/{name}")
	public void setSessionName(@PathParam("name") @PathVariable String name) {
		SessionManager sessionManager = beanFactory.getBean(SessionManager.class);
		sessionManager.getSession().setUsername(name);
	}

	@GetMapping(path="/session", produces="application/json")
	@GET
	@Produces("application/json")
	@Path("/session")
	public Session getSession() {
		simpleLogger.log(Level.INFO, "Starting getSession");
		SessionManager sessionManager = beanFactory.getBean(SessionManager.class);
		Session session = sessionManager.getSession();
		simpleLogger.log(Level.INFO, session.getUsername());
		return session;
	}

	@GetMapping(produces="application/json")
	@GET
	@Produces("application/json")
	public List<User> getAllUsers() {
		simpleLogger.log(Level.INFO, "Starting getAllUsers method");
		return userService.getAllUsers();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
