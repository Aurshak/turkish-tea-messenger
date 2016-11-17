package ru.klinichev.client.view;

// import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import ru.klinichev.client.MessageService;
import ru.klinichev.client.MessageServiceAsync;
import ru.klinichev.shared.Message;

public class ChatPanel extends VerticalPanel {
	
	private String thisUser;
	private String thatUser;
	
	private static final int REFRESH_INTERVAL = 2000;
	private Date lastRefreshed;
	private Timer refreshMessageTimer;
	private int retryMultiplier = 5;
	private boolean firstRefresh = true;
	
	private HorizontalPanel sendPanel = new HorizontalPanel();
	private TextArea sendArea = new TextArea();
	private Button send = new Button("Send");
	
	private ScrollPanel scrollPanel = new ScrollPanel();
	private FlexTable messages = new FlexTable();
	
	private final MessageServiceAsync messageService = GWT.create(MessageService.class);
	
	// public int unread;
	
	public ChatPanel(String thisUser, String thatUser) {
		this.thisUser = thisUser;
		this.thatUser = thatUser;
		startPanel();
		// unread = 0;
		firstRefresh = true;
		refreshMessageTimer = new Timer() {
            public void run() {
                refreshMessages();
            }
        };
        refreshMessages();
	}
	
	private void startPanel() {
		
		sendArea.getElement().setAttribute("placeholder", "Type your text here " + thisUser);
		
		messages.addStyleName("messageList");
		sendArea.setCharacterWidth(100);
		sendArea.setVisibleLines(7);
		
		scrollPanel.add(messages);
		sendPanel.add(sendArea);
		sendPanel.add(send);
		add(scrollPanel);
		add(sendPanel);
		
		setHeight("45em");
		setWidth("100%");
		scrollPanel.setHeight("30em");
		scrollPanel.setWidth("100%");
		sendPanel.setHeight("13em");
		sendPanel.setWidth("70em");
		messages.setWidth("100%");
		messages.getColumnFormatter().setWidth(0, "15%");
		messages.getColumnFormatter().setWidth(1, "70%");
		messages.getColumnFormatter().setWidth(2, "15%");
		setCellHorizontalAlignment(scrollPanel, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHorizontalAlignment(sendPanel, HasHorizontalAlignment.ALIGN_CENTER);
		setCellVerticalAlignment(sendPanel, HasVerticalAlignment.ALIGN_BOTTOM);
		// setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		send.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				final String content = sendArea.getValue().trim();
				messageService.addMessage(thisUser, thatUser, content, new SendMessageCallback());
				sendArea.setText(null);
			}
		});
		
		sendArea.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.isShiftKeyDown()) {
					if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					}
				}
				else if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					final String content = sendArea.getValue().trim();
					messageService.addMessage(thisUser, thatUser, content, new SendMessageCallback());
					sendArea.setText(null);
					event.preventDefault(); // preventing the cursor from jumping onto a new line
				}
			}
		});
		
	}
	
	private void addNewMessage(Message message) {
		int row = messages.getRowCount();
		Label user = new Label(message.sender);
		user.addStyleName("important");
		messages.setWidget(row, 0, user);
		messages.setText(row, 1, message.content);
		Label dateText = new Label();
		dateText.addStyleName("lightText");
		DateTimeFormat dateFormatDay = DateTimeFormat.getFormat("d MMMM");
		DateTimeFormat dateFormatTime = DateTimeFormat.getFormat("HH:mm:ss");
		Date now = new Date();
		if (now.getTime() - message.creationDate.getTime() < 18*3600*1000) {
			dateText.setText(dateFormatTime.format(message.creationDate));
		}
		else {
			dateText.setText(dateFormatDay.format(message.creationDate));
		}
		messages.setWidget(row, 2, dateText);
		
		if (lastRefreshed == null || message.creationDate.after(lastRefreshed)) {
            lastRefreshed = message.creationDate;
        }
		/* if (!firstRefresh) {
			unread++;
		} */
	}
	
	public void refreshMessages() {
		messageService.listMessages(lastRefreshed, thisUser, thatUser, new RefreshMessagesCallback());
	}
	
	private class SendMessageCallback implements AsyncCallback<Void> {

		@Override
		public void onFailure(Throwable caughtWhileSending) {	
			alert("Unable to send message. Try again later.");
		}

		@Override
		public void onSuccess(Void result) {
			
		}
		
	}
	
	private class RefreshMessagesCallback implements AsyncCallback<List<Message>> {

		@Override
		public void onFailure(Throwable caught) {
			alert("Unable to update the message list. The reason: " + caught.getMessage());
			refreshMessageTimer.schedule(REFRESH_INTERVAL * retryMultiplier);
			retryMultiplier++;
		}

		@Override
		public void onSuccess(List<Message> result) {
			for (int i = 0; i < result.size(); i++) {
				addNewMessage(result.get(i));
			}
			if (!result.isEmpty()) {
				scrollPanel.scrollToBottom();
			}
			if (firstRefresh) {
				scrollPanel.scrollToBottom();
				lastRefreshed = new Date();
				firstRefresh = false;
			}
			refreshMessageTimer.schedule(REFRESH_INTERVAL);			
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
