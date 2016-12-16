package ru.klinichev.turkishtea.shared;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;

@Entity
@Table(name = "MESSAGES")
public class Message implements Serializable {

	@Id
	@Column(name = "MESSAGEID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "SENDERID")
	private Integer sender;

	@Column(name = "RECEIVERID")
	private Integer receiver;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "DATE")
	private Long creationDate;
	
	/* public Message (int sender, int receiver, String content) {
		this(sender, receiver, content, new Date().getTime());
	} */
	
	@JsonCreator
	public Message (@JsonProperty("sender") int sender, @JsonProperty("receiver") int receiver,
			@JsonProperty("content") String content, @JsonProperty("creationDate") long creationDate) {
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.creationDate = creationDate;
	}

	public Message() {
		this(1, 2, "", new Date().getTime());
	}

	public Integer getSender() {
		return sender;
	}

	public Integer getReceiver() {
		return receiver;
	}

	public String getContent() {
		return content;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setSender(Integer sender) {
		this.sender = sender;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}
}
