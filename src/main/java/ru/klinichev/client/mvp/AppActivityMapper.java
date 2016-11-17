package ru.klinichev.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import ru.klinichev.client.ClientFactory;
import ru.klinichev.client.activity.ChatActivity;
import ru.klinichev.client.activity.HelloActivity;
import ru.klinichev.client.place.ChatPlace;
import ru.klinichev.client.place.HelloPlace;

public class AppActivityMapper implements ActivityMapper {
	private ClientFactory clientFactory;
	
	public AppActivityMapper (ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof HelloPlace)
			return new HelloActivity((HelloPlace) place, clientFactory);
		else if (place instanceof ChatPlace)
			return new ChatActivity((ChatPlace) place, clientFactory);
		return null;
	}
}
