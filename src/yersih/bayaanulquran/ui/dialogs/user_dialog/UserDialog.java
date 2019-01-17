package yersih.bayaanulquran.ui.dialogs.user_dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import yersih.bayaanulquran.common.User;

import java.io.IOException;
import java.util.Optional;

public class UserDialog extends Dialog<ButtonType> {

    private UserDialogController userDialogController;

    private final Button okButton;

    public UserDialog(Scene root) {

        initOwner(root.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("user_dialog.fxml"));

        getDialogPane().getButtonTypes().add(ButtonType.OK);

        okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);

        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

       userDialogController = fxmlLoader.getController();
    }

    public void registerUser() {
        setTitle("Register User");
        setHeaderText("Register  User");
        okButton.setText("Register");
        okButton.disableProperty().bind(userDialogController.okToRegister.not());
        userDialogController.setRegisterDialog();

        showDialog();
    }

    public void updateUser(User u) {
        setTitle("Edit User");
        setHeaderText("Edit User Details");
        okButton.setText("Update");
        userDialogController.setUpdateDialog(u);
        showDialog();
    }

    public void LoginUser() {
        setTitle("Login");
        setHeaderText("User Login");
        okButton.setText("Login");
        okButton.disableProperty().bind(userDialogController.okToLogin.not());

        userDialogController.setLoginDialog();
        showDialog();
    }

    private void showDialog() {
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            userDialogController.onDialogResult(okButton);
        }
    }




}
