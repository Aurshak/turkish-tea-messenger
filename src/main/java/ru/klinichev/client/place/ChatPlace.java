package ru.klinichev.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ChatPlace extends Place {
	
private String chatName;
	
	public ChatPlace (String token) {
		this.chatName = token;
	}
	
	public String getChatName () {
		return chatName;
	}
	
	public static class Tokenizer implements PlaceTokenizer<ChatPlace> {

		@Override
		public ChatPlace getPlace(String token) {
			return new ChatPlace(token);
		}

		@Override
		public String getToken(ChatPlace place) {
			return place.getChatName();
		}
		
	}

}
