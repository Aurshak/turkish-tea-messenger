package ru.klinichev.turkishtea.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.TextCallback;
import ru.klinichev.turkishtea.shared.User;

@Path("/api/users")
public interface UserClient extends RestService {
	
	@POST
	void addUser(@QueryParam("login") String login, @QueryParam("password") String password, 
			MethodCallback<Void> callback) throws RuntimeException;
	
	@GET
	@Path("/{login}")
	void checkUser(@PathParam("login") String login, @QueryParam("password") String password, 
			MethodCallback<Boolean> callback) throws RuntimeException;
	
	@GET
	void getAllUsers(MethodCallback<List<User>> callback);
	
	@POST
	@Path("/session/{name}")
	void setSessionName(@PathParam("name") String name, MethodCallback<Void> callback);
	
	@GET
	@Path("/session")
	void getSession(TextCallback callback);

}
