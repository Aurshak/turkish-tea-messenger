package ru.klinichev.client.view;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface ChatView extends IsWidget {
	void setName (String name);
	void setPresenter (Presenter listener);
	
	public interface Presenter {
		void goTo (Place place);
	}

}
