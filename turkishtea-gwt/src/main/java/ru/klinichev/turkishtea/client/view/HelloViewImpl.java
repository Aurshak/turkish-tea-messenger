package ru.klinichev.turkishtea.client.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		// userService.getSessionName(new GetNameCallback());
	}
	
	@UiHandler("signUpButton")
	void onClick1(ClickEvent e) {
		signUpButton.setEnabled(false);
		logInButton.setEnabled(false);
		DialogBox signUpDialog = setDialog("signUp");
		signUpDialog.setText("Signing up");
		signUpDialog.setGlassEnabled(true);
		signUpDialog.setPopupPosition(Window.getClientWidth()/2 - 100, Window.getClientHeight()/2 - 75);
		signUpDialog.show(); // it flashes from the right
		loginBox.setFocus(true);
	}
	
	@UiHandler("logInButton")
	void onClick(ClickEvent e) {
		signUpButton.setEnabled(false);
		logInButton.setEnabled(false);
		DialogBox logInDialog = setDialog("logIn");
		logInDialog.setText("Logging in");
		logInDialog.setGlassEnabled(true);
		logInDialog.setPopupPosition(Window.getClientWidth()/2 - 100, Window.getClientHeight()/2 - 75);
		logInDialog.show(); // it flashes from the right
		loginBox.setFocus(true);
	}
	
	/* @Override
	public void setName(String name) {
		this.name = name;
	} */

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;		
	}
	
	private DialogBox setDialog (String type) {
		simpleLogger.log(Level.INFO, "Starting setDialog()");
		final DialogBox dialogBox = new DialogBox();
		// dialogBox.setAnimationEnabled(true);
		Button closeButton = new Button("Close");
		Button dialogSignUpButton = new Button("Sign up");
		if (type == "logIn") dialogSignUpButton = new Button("Log in");
		closeButton.getElement().setId("closeButton");
		dialogSignUpButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		HorizontalPanel loginPanel = new HorizontalPanel();
		loginPanel.add(loginBox);
		loginBox.getElement().setAttribute("placeholder", "Login");
		
		HorizontalPanel passwordPanel = new HorizontalPanel();
		final PasswordTextBox passwordBox = new PasswordTextBox();
		passwordPanel.add(passwordBox);
		passwordBox.getElement().setAttribute("placeholder", "Password");
		
		HorizontalPanel choicePanel = new HorizontalPanel();
		choicePanel.add(closeButton);
		choicePanel.add(dialogSignUpButton);
		
		dialogVPanel.add(loginPanel);
		dialogVPanel.add(passwordPanel);
		dialogVPanel.add(choicePanel);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(event -> {
            dialogBox.hide();
            signUpButton.setEnabled(true);
            logInButton.setEnabled(true);
        });
		
		if (type == "signUp") {
			dialogSignUpButton.addClickHandler(event -> {
                dialogBox.hide();
                signUpButton.setEnabled(true);
                logInButton.setEnabled(true);
                addToDatabase(loginBox.getValue(), passwordBox.getValue());
            });
			
			passwordBox.addKeyUpHandler(event -> {
                if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    dialogBox.hide();
                    signUpButton.setEnabled(true);
                    logInButton.setEnabled(true);
                    addToDatabase(loginBox.getValue(), passwordBox.getValue());
                }
            });
		}
		else if (type == "logIn") {
			dialogSignUpButton.addClickHandler(event -> {
                dialogBox.hide();
                signUpButton.setEnabled(true);
                logInButton.setEnabled(true);
                findInDatabase(loginBox.getValue(), passwordBox.getValue());
            });
			
			passwordBox.addKeyUpHandler(event -> {
                if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    dialogBox.hide();
                    signUpButton.setEnabled(true);
                    logInButton.setEnabled(true);
                    findInDatabase(loginBox.getValue(), passwordBox.getValue());
                }
            });
		}
		
		return dialogBox;
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
	
	/* private class GetNameCallback implements AsyncCallback<String> {

		@Override
		public void onFailure(Throwable caught) {
			alert("Unable to get session name. The reason: " + caught.getMessage());
		}

		@Override
		public void onSuccess(String result) {
			if (!result.equals("")) {
				listener.goTo(new ChatPlace(result));
			}
		}
		
	} */

}
