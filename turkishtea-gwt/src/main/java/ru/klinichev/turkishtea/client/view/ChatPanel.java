package ru.klinichev.turkishtea.client.view;

// import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.DevelopmentModeLogHandler;
import com.google.gwt.logging.client.SystemLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;
import ru.klinichev.turkishtea.client.MessageClient;
import ru.klinichev.turkishtea.shared.Message;

public class ChatPanel extends Composite {

	private static ChatPanel.ChatPanelUiBinder uiBinder = GWT.create(ChatPanel.ChatPanelUiBinder.class);

	interface ChatPanelUiBinder extends UiBinder<Widget, ChatPanel> {
	}
	
	private String thisUser;
	private String thatUser;
	private int thisId;
	private int thatId;

	private Date lastRefreshed;
	private boolean firstRefresh = true;

	@UiField
	TextArea sendArea;
	@UiField
	Button send;
	@UiField
	FlowPanel messages;
	@UiField
	ScrollPanel scrollPanel;

	private final WebSocket webSocket = WebSocket.newWebSocketIfSupported();
	
	private MessageClient client = GWT.create(MessageClient.class);
	
	private static final Logger simpleLogger = Logger.getLogger("ChatPanel");
	
	public ChatPanel(String thisUser, String thatUser, int thisId, int thatId) {
		this.thisUser = thisUser;
		this.thatUser = thatUser;
		this.thisId = thisId;
		this.thatId = thatId;
		
		simpleLogger.log(Level.INFO, "Starting ChatPanel");

		initWidget(uiBinder.createAndBindUi(this));

		sendArea.getElement().setAttribute("placeholder", "Type your text here " + thisUser);

		firstRefresh = true;
        refreshMessages();

        if (webSocket != null) {
        	webSocket.setListener(new WebSocketListenerAdapter() {
				@Override
				public void onMessage(WebSocket webSocket, String data) {
					simpleLogger.log(Level.INFO, "Received via websocket: " + data);
					refreshMessages();
				}
			});
		}
		else {
			simpleLogger.log(Level.WARNING, "Websocket is not available");
		}

		final String moduleBaseURL = GWT.getHostPageBaseURL();
		webSocket.connect(moduleBaseURL.replaceFirst( "http", "ws" ) + "add");
	}

	@UiHandler("send")
	void onClick(ClickEvent event) {
		final String content = sendArea.getValue().trim();
		client.addMessage(thisId, thatId, content, new SendMessageCallback());
		sendArea.setText(null);
	}

	@UiHandler("sendArea")
	void onKeyDown(KeyDownEvent event) {
		if(event.isShiftKeyDown()) {
			if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			}
		}
		else if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			final String content = sendArea.getValue().trim();
			client.addMessage(thisId, thatId, content, new SendMessageCallback());
			sendArea.setText(null);
			event.preventDefault(); // preventing the cursor from jumping onto a new line
		}
	}
	
	private void addNewMessage(Message message) {
		simpleLogger.log(Level.INFO, "Starting addNewMessage()");
		FlowPanel row = new FlowPanel();

		String author = null;
		if (message.getSender() == thisId) author = thisUser;
		else author = thatUser;
		Label user = new Label(author);
		user.addStyleName("author");
		row.add(user);

		Label content = new Label(message.getContent());
		content.addStyleName("content");
		row.add(content);

		Label dateText = new Label();
		dateText.addStyleName("date");
		DateTimeFormat dateFormatDay = DateTimeFormat.getFormat("d MMMM");
		DateTimeFormat dateFormatTime = DateTimeFormat.getFormat("HH:mm:ss");
		Date now = new Date();
		Date creation = new Date(message.getCreationDate());
		if (now.getTime() - message.getCreationDate() < 18*3600*1000) {
			dateText.setText(dateFormatTime.format(creation));
		}
		else {
			dateText.setText(dateFormatDay.format(creation));
		}
		row.add(dateText);

		messages.add(row);
		row.addStyleName("messageRow");
		
		if (lastRefreshed == null || creation.after(lastRefreshed)) {
            lastRefreshed = creation;
        }
	}
	
	public void refreshMessages() {
		long date = (lastRefreshed == null) ? 0 : lastRefreshed.getTime();
		client.listMessages(date, thisId, thatId, new RefreshMessagesCallback());
	}
	
	private class SendMessageCallback implements MethodCallback<Void> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			alert("Unable to send message. Try again later.");
			simpleLogger.log(Level.SEVERE, "Message sending failed: ", exception);
		}

		@Override
		public void onSuccess(Method method, Void response) {
			webSocket.send("Message");
		}
		
	}
	
	private class RefreshMessagesCallback implements MethodCallback<List<Message>> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			alert("Unable to update the message list. The reason: " + exception.toString());
			simpleLogger.log(Level.SEVERE, "Messages refreshing failed: ", exception);
		}

		@Override
		public void onSuccess(Method method, List<Message> response) {
			for (int i = 0; i < response.size(); i++) {
				addNewMessage(response.get(i));
			}
			if (!response.isEmpty()) {
				scrollPanel.scrollToBottom();
			}
			if (firstRefresh) {
				scrollPanel.scrollToBottom();
				lastRefreshed = new Date();
				firstRefresh = false;
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

}
