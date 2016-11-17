package ru.klinichev.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import ru.klinichev.client.ClientFactory;
import ru.klinichev.client.place.HelloPlace;
import ru.klinichev.client.view.HelloView;

public class HelloActivity extends AbstractActivity implements HelloView.Presenter {
	
	private ClientFactory clientFactory;
	// private String name;
	
	public HelloActivity(HelloPlace place, ClientFactory clientFactory) {
		// this.name = place.getHelloName();
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		HelloView helloView = clientFactory.getHelloView();
		// helloView.setName(name);
		helloView.setPresenter(this);
		panel.setWidget(helloView.asWidget());		
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);		
	}

}
