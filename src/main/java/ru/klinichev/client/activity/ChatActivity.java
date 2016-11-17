package ru.klinichev.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import ru.klinichev.client.ClientFactory;
import ru.klinichev.client.place.ChatPlace;
import ru.klinichev.client.view.ChatView;

public class ChatActivity extends AbstractActivity implements ChatView.Presenter {
	
	private ClientFactory clientFactory;
	private String name;
	
	public ChatActivity(ChatPlace place, ClientFactory clientFactory) {
		this.name = place.getChatName();
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		ChatView chatView = clientFactory.getChatView();
		chatView.setName(name);
		chatView.setPresenter(this);
		panel.setWidget(chatView.asWidget());		
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);		
	}

}
