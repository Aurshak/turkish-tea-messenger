package ru.klinichev.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TurkishTeaMessengerViewImpl extends Composite implements TurkishTeaMessengerView {
	
	String helloName;
	Presenter presenter;

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String helloName) {
		this.helloName = helloName;
		// nameSpan.setInnerText(helloName);
		
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.presenter = listener;
	}

}
