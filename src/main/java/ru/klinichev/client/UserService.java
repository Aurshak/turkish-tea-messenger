package ru.klinichev.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	void addUser(String login, String password) throws RuntimeException;
	boolean checkUser(String login, String password) throws RuntimeException;
	List<String> getAllUsers();
	void setSessionName(String name);
	String getSessionName();
}
