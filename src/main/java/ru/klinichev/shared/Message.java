package ru.klinichev.shared;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String sender;
	public String receiver;
	public String content;
	public Date creationDate;
	
	public Message() {
		this.sender = "";
		this.receiver = "";
		this.content = "";
		this.creationDate = new Date();
	}
	
	public Message (String sender, String receiver, String content) {
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.creationDate = new Date();
	}
	
	public Message (String sender, String receiver, String content, Date creationDate) {
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.creationDate = creationDate;
	}

}
