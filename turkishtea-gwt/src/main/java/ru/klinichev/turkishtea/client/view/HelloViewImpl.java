package ru.klinichev.turkishtea.client.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.KeyDownEvent;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import ru.klinichev.turkishtea.client.UserClient;
import ru.klinichev.turkishtea.client.place.ChatPlace;
import ru.klinichev.turkishtea.shared.Session;

public class HelloViewImpl extends Composite implements HelloView {

	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, HelloViewImpl> {
	}

	@UiField
	Button signUpButton;
	@UiField
	Button logInButton;
	
	private TextBox loginBox = new TextBox();
	
	private Presenter listener;
	
	private UserClient client = GWT.create(UserClient.class);
	
	private static final Logger simpleLogger = Logger.getLogger("HelloViewImpl");

	public HelloViewImpl() {
		simpleLogger.log(Level.INFO, "Starting HelloViewImpl()");
		initWidget(uiBinder.createAndBindUi(this));
		client.getSession(new GetSessionCallback());
	}
	
	@UiHandler("signUpButton")
	void onClick1(ClickEvent e) {
		signUpButton.setEnabled(false);
		logInButton.setEnabled(false);
		Dialog signUpDialog = new SignUpDialog(loginBox);
		signUpDialog.dialogBox.setGlassEnabled(true);
		signUpDialog.dialogBox.setPopupPosition(Window.getClientWidth()/2 - 100, Window.getClientHeight()/2 - 75);
		signUpDialog.dialogBox.show(); // it flashes from the right
		loginBox.setFocus(true);
	}
	
	@UiHandler("logInButton")
	void onClick(ClickEvent e) {
		signUpButton.setEnabled(false);
		logInButton.setEnabled(false);
		Dialog logInDialog = new LogInDialog(loginBox);
		logInDialog.dialogBox.setGlassEnabled(true);
		logInDialog.dialogBox.setPopupPosition(Window.getClientWidth()/2 - 100, Window.getClientHeight()/2 - 75);
		logInDialog.dialogBox.show(); // it flashes from the right
		loginBox.setFocus(true);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;		
	}
	
	private void addToDatabase(String login, String password) {
		simpleLogger.log(Level.INFO, "Starting addToDatabase()");
		client.addUser(login, password, new AddUserCallback());		
	}
	
	private void findInDatabase(String login, String password) {
		simpleLogger.log(Level.INFO, "Starting findInDatabase()");
		client.checkUser(login, password, new CheckUserCallback());		
	}
	
	private void alert(String alertText) {
		final PopupPanel alertPopup = new PopupPanel();
		alertPopup.setWidget(new Label(alertText));
		alertPopup.setGlassEnabled(true);
		alertPopup.setAnimationEnabled(true);
		alertPopup.center();
		Timer timer = new Timer() {
			@Override
			public void run() {
				alertPopup.hide();
			}
		};
		timer.schedule(2000);
	}
	
	private class AddUserCallback implements MethodCallback<Void> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			simpleLogger.log(Level.SEVERE, "Failed to add the user: ", exception);
			alert("This operation can't be done now. Please try again later.");				
		}

		@Override
		public void onSuccess(Method method, Void response) {
			alert("Registration succeeded! Now you can log in.");			
		}
		
	}
	
	private class CheckUserCallback implements MethodCallback<Boolean> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			simpleLogger.log(Level.SEVERE, "Failed to validate the user: ", exception);
			alert("This operation can't be done now. Please try again later. " + exception.toString());			
		}

		@Override
		public void onSuccess(Method method, Boolean response) {
			if (response) {
				listener.goTo(new ChatPlace(loginBox.getText()));
			}
			else {
				alert("Wrong login or password!");
			}			
		}
		
	}
	
	private class GetSessionCallback implements MethodCallback<Session> {

		@Override
		public void onFailure(Method method, Throwable exception) {
			simpleLogger.log(Level.SEVERE, "Unable to get session name: ", exception);
			alert("Unable to get session name. " + exception.toString());
		}

		@Override
		public void onSuccess(Method method, Session response) {
			simpleLogger.log(Level.INFO, "Session name: " + response);
			if (!response.getUsername().equals("DefaultSessionName")) {
				listener.goTo(new ChatPlace(response.getUsername()));
			}
		}
		
	}

	private class LogInDialog extends Dialog {

		public LogInDialog(TextBox newLoginBox) {
			super(newLoginBox);
			setConfirmButtonName("Log in");
			dialogBox.setText("Logging in");
		}

		@Override
		public void onClickClose(ClickEvent e) {
			super.onClickClose(e);
			signUpButton.setEnabled(true);
			logInButton.setEnabled(true);
		}

		@Override
		public void onClickConfirm(ClickEvent e) {
			super.onClickConfirm(e);
			signUpButton.setEnabled(true);
			logInButton.setEnabled(true);
			findInDatabase(loginBox.getValue(), passwordBox.getValue());
		}

		@Override
		public void onKeyDown(KeyDownEvent event) {
			super.onKeyDown(event);
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				signUpButton.setEnabled(true);
				logInButton.setEnabled(true);
				findInDatabase(loginBox.getValue(), passwordBox.getValue());
			}
		}

	}

	private class SignUpDialog extends Dialog {

		public SignUpDialog(TextBox newLoginBox) {
			super(newLoginBox);
			setConfirmButtonName("Sign up");
			dialogBox.setText("Signing up");
		}

		@Override
		public void onClickClose(ClickEvent e) {
			super.onClickClose(e);
			signUpButton.setEnabled(true);
			logInButton.setEnabled(true);
		}

		@Override
		public void onClickConfirm(ClickEvent e) {
			super.onClickConfirm(e);
			signUpButton.setEnabled(true);
			logInButton.setEnabled(true);
			addToDatabase(loginBox.getValue(), passwordBox.getValue());
		}

		@Override
		public void onKeyDown(KeyDownEvent event) {
			super.onKeyDown(event);
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				signUpButton.setEnabled(true);
				logInButton.setEnabled(true);
				addToDatabase(loginBox.getValue(), passwordBox.getValue());
			}
		}

	}

}
