package ru.klinichev.turkishtea.client.view;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Timer;

import ru.klinichev.turkishtea.client.UserClient;
import ru.klinichev.turkishtea.client.place.HelloPlace;
import ru.klinichev.turkishtea.shared.User;

public class ChatViewImpl extends Composite implements ChatView {

	private static ChatViewImpl.ChatViewImplUiBinder uiBinder = GWT.create(ChatViewImpl.ChatViewImplUiBinder.class);

	interface ChatViewImplUiBinder extends UiBinder<Widget, ChatViewImpl> {
	}
	
	private UserClient client = GWT.create(UserClient.class);
	
	private Presenter listener;
	private String name;
	
	@UiField
	ResizeLayoutPanel mainPanel;
	@UiField
	Label topLabel;
	@UiField
	Button exit;

	private TabLayoutPanel contentPanel;
	
	private static final Logger simpleLogger = Logger.getLogger("ChatViewImpl");
	
	public ChatViewImpl() {
		
		simpleLogger.log(Level.INFO, "Starting ChatViewImpl()");

		initWidget(uiBinder.createAndBindUi(this));
		
	}

	@UiHandler("exit")
	void onClick(ClickEvent event) {
		// userService.setSessionName("", new SetNameCallback());
		contentPanel.removeFromParent();
		listener.goTo(new HelloPlace());
	}

	@Override
	public void setName(String name) {
		simpleLogger.log(Level.INFO, "Starting setName()");
		this.name = name;
		// userService.setSessionName(name, new SetNameCallback());
		topLabel.setText(name);
		contentPanel = new TabLayoutPanel(3, Unit.EM);
		manageLayoutPanel();
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;		
	}
	
	private void manageLayoutPanel() {
	    client.getAllUsers(new GetAllUsersCallback());
	    mainPanel.add(contentPanel);
	}
	
	private void buildTabs(List<User> users) {
		simpleLogger.log(Level.INFO, "Starting buildTabs()");
		int userId = 0;
		for (User u: users) {
			if (u.getName().equals(name)) {
				userId = u.getId();
				break;
			}
		}
		for (User u: users) {
			if (!u.getName().equals(name)) {
				final ChatPanel chatPanel = new ChatPanel(name, u.getName(), userId, u.getId());
				contentPanel.add(chatPanel, u.getName());
			}
		}
	}
	
	private void alert(String alertText) {
		final PopupPanel alertPopup = new PopupPanel();
		alertPopup.setWidget(new Label(alertText));
		alertPopup.setGlassEnabled(true);
		alertPopup.setAnimationEnabled(true);
		alertPopup.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
		alertPopup.show();
		Timer timer = new Timer() {
			@Override
			public void run() {
				alertPopup.hide();
			}
		};
		timer.schedule(2000);
	}
	
	/* private class SetNameCallback implements AsyncCallback<Void> {

		@Override
		public void onFailure(Throwable caught) {
			alert("Unable to start the session. The reason: " + caught.getMessage());			
		}

		@Override
		public void onSuccess(Void result) {
						
		}
		
	} */
	
	private class GetAllUsersCallback implements MethodCallback<List<User>> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			simpleLogger.log(Level.SEVERE, "Getting list of users failed: ", exception);
			alert("Unable to get the list of users. The reason: " + exception.toString());			
		}

		@Override
		public void onSuccess(Method method, List<User> response) {
			buildTabs(response);			
		}
		
	}

}
