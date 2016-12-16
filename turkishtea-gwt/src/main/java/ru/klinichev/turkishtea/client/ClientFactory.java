package ru.klinichev.turkishtea.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import ru.klinichev.turkishtea.client.view.ChatView;
import ru.klinichev.turkishtea.client.view.HelloView;

public interface ClientFactory {
	EventBus getEventBus();
	PlaceController getPlaceController();
	HelloView getHelloView();
	ChatView getChatView();
}
