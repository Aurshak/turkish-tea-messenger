package ru.klinichev.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import ru.klinichev.client.view.ChatPanel;
import ru.klinichev.client.UserService;
import ru.klinichev.client.UserServiceAsync;
import ru.klinichev.client.place.HelloPlace;

public class ChatViewImpl extends Composite implements ChatView {
	
	private final UserServiceAsync userService = GWT.create(UserService.class);
	
	private Presenter listener;
	private String name;
	
	private ResizeLayoutPanel mainPanel = new ResizeLayoutPanel();
	private TabLayoutPanel contentPanel;
	private HorizontalPanel dockPanel = new HorizontalPanel();
	private HTMLPanel rootPanel = new HTMLPanel("");
	private Label topLabel = new Label();
	private Button exit = new Button("Exit");
	private Image image = new Image();
	
	public ChatViewImpl() {
		
		initWidget(rootPanel);
        
        rootPanel.setWidth("100%");
		rootPanel.setHeight("100%");
		
		setHeader(dockPanel);
        rootPanel.add(dockPanel);
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		userService.setSessionName(name, new SetNameCallback());
		topLabel.setText(name);
		contentPanel = new TabLayoutPanel(3, Unit.EM);
		manageLayoutPanel();
		mainPanel.setWidth("100%");
		mainPanel.setHeight("45em");
		mainPanel.add(contentPanel);
		rootPanel.add(mainPanel);
		// rootPanel.addStyleName("center");
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;		
	}
	
	private void manageLayoutPanel() {
		contentPanel.setHeight("100%");
	    contentPanel.setWidth("80%");
	    userService.getAllUsers(new GetAllUsersCallback());
	}
	
	private void setHeader(HorizontalPanel dockPanel) {
		image.setUrl("cay.jpg");
		image.setPixelSize(18, 27);
		
		topLabel.addStyleName("veryImportant");
		
		exit.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				userService.setSessionName("", new SetNameCallback());
				contentPanel.removeFromParent();
				listener.goTo(new HelloPlace());
			}
		});
		exit.addStyleName("button");
		
		dockPanel.add(image);
		dockPanel.add(topLabel);
		dockPanel.add(exit);
		
		dockPanel.setWidth("79%");
		dockPanel.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_LEFT);
		// dockPanel.setCellHorizontalAlignment(topLabel, HorizontalPanel.ALIGN_LEFT);
		dockPanel.setCellHorizontalAlignment(exit, HorizontalPanel.ALIGN_RIGHT);
		dockPanel.addStyleName("headerPanel");
	}
	
	private void buildTabs(List<String> users) {
		for (String u: users) {
			if (!u.equals(name)) {
				final ChatPanel chatPanel = new ChatPanel(name, u);
				contentPanel.add(chatPanel, u);
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
	
	private class SetNameCallback implements AsyncCallback<Void> {

		@Override
		public void onFailure(Throwable caught) {
			alert("Unable to start the session. The reason: " + caught.getMessage());			
		}

		@Override
		public void onSuccess(Void result) {
						
		}
		
	}
	
	private class GetAllUsersCallback implements AsyncCallback<List<String>> {

		@Override
		public void onFailure(Throwable caught) {
			alert("Unable to get the list of users. The reason: " + caught.getMessage());			
		}

		@Override
		public void onSuccess(List<String> result) {
		    buildTabs(result);
		}
		
	}

}
