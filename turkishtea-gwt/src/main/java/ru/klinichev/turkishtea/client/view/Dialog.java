package ru.klinichev.turkishtea.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Dialog extends Composite {

    interface DialogUiBinder extends UiBinder<Widget, Dialog> {
    }

    @UiField
    DialogBox dialogBox;
    @UiField(provided = true)
    TextBox loginBox;
    @UiField
    PasswordTextBox passwordBox;
    @UiField
    Button closeButton;
    @UiField
    Button confirmButton;

    private static final Logger simpleLogger = Logger.getLogger("Dialog");

    private static DialogUiBinder ourUiBinder = GWT.create(DialogUiBinder.class);

    public Dialog(TextBox newLoginBox) {
        simpleLogger.log(Level.INFO, "Starting new dialog");
        this.loginBox = newLoginBox;
        initWidget(ourUiBinder.createAndBindUi(this));
        loginBox.getElement().setAttribute("placeholder", "Login");
        passwordBox.getElement().setAttribute("placeholder", "Password");
    }

    public void setConfirmButtonName (String name) {
        confirmButton.setText(name);
    }

    @UiHandler("closeButton")
    void onClickClose(ClickEvent e) {
        dialogBox.hide();
    }

    @UiHandler("confirmButton")
    void onClickConfirm(ClickEvent e) {
        dialogBox.hide();
    }

    @UiHandler("passwordBox")
    void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            dialogBox.hide();
        }
    }

}