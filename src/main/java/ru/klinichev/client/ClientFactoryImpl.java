package ru.klinichev.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

import ru.klinichev.client.view.ChatView;
import ru.klinichev.client.view.ChatViewImpl;
import ru.klinichev.client.view.HelloView;
import ru.klinichev.client.view.HelloViewImpl;

public class ClientFactoryImpl implements ClientFactory {
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final HelloView helloView = new HelloViewImpl();
	private static final ChatView chatView = new ChatViewImpl();
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}
	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}
	@Override
	public HelloView getHelloView() {
		return helloView;
	}
	@Override
	public ChatView getChatView() {
		return chatView;
	}
}
