package ru.klinichev.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import ru.klinichev.shared.Message;

@RemoteServiceRelativePath("message")
public interface MessageService extends RemoteService {
	void addMessage(String sender, String receiver, String content) throws RuntimeException;
    List<Message> listMessages(Date since, String thisUser, String thatUser) throws RuntimeException;
}
