package ru.klinichev.turkishtea.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.klinichev.turkishtea.server.dao.MessageDAOImpl;
import ru.klinichev.turkishtea.server.service.MessageService;
import ru.klinichev.turkishtea.shared.Message;

@RestController
@RequestMapping("/messages")
@Path("messages")
public class MessageResource {

	@Autowired
	@Qualifier(value = "messageService")
	private MessageService messageService;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	private static final Logger simpleLogger = Logger.getLogger("MessageResource");

	@PostMapping
	@POST
	public void addMessage(@QueryParam("sender") int sender, @QueryParam("receiver") int receiver,
			@QueryParam("content") String content) throws RuntimeException {
		
		simpleLogger.log(Level.INFO, "Starting addMessage method");

		content = escapeHtml(content);
		
		Message newMessage = new Message(sender, receiver, content, new Date().getTime());
		messageService.addToDatabase(newMessage);
		
	}

	@GetMapping(produces="application/json")
	@GET
	@Produces("application/json")
	public List<Message> listMessages(@QueryParam("since") long since, @QueryParam("thisId") int thisId,
			@QueryParam("thatId") int thatId) throws RuntimeException {
		simpleLogger.log(Level.INFO, "Starting listMessages on " + since);
		List<Message> selectedMessages = new ArrayList<Message>();
		selectedMessages = messageService.loadDatabase(since, thisId, thatId);
		simpleLogger.log(Level.INFO, "Ending listMessages");
		return selectedMessages;
	}
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

}
