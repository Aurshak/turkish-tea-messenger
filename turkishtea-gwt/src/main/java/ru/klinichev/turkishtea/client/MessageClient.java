package ru.klinichev.turkishtea.client;

import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import ru.klinichev.turkishtea.shared.Message;

@Path("/api/messages")
public interface MessageClient extends RestService {
	
	@POST
	void addMessage(@QueryParam("sender") int sender, @QueryParam("receiver") int receiver,
			@QueryParam("content") String content, MethodCallback<Void> callback) 
			throws RuntimeException;
	
	@GET
    void listMessages(@QueryParam("since") long since, @QueryParam("thisId") int thisId,
    		@QueryParam("thatId") int thatId,
    		MethodCallback<List<Message>> callback) throws RuntimeException;
}
