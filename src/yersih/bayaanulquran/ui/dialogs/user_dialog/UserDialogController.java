package yersih.bayaanulquran.ui.dialogs.user_dialog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.css.Style;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import yersih.bayaanulquran.common.User;
import yersih.bayaanulquran.db.StudentData;
import yersih.bayaanulquran.db.UserData;

import java.util.Optional;
import java.util.concurrent.Callable;

public class UserDialogController {

    @FXML
    private DialogPane userDialogPane;
    @FXML
    private Label usernameLabel, passwordOneLabel, passwordTwoLabel, oldPasswordLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordOneField, passwordTwoField, oldPasswordField;
    @FXML
    private ChoiceBox<String> postsChoiceBox;

    public BooleanProperty passwordEqual = new SimpleBooleanProperty();
    public BooleanProperty usernameUnique = new SimpleBooleanProperty();
    public BooleanProperty oldPasswordRight = new SimpleBooleanProperty(false);
    public BooleanProperty okToRegister = new SimpleBooleanProperty(false);
    public BooleanProperty okToLogin = new SimpleBooleanProperty(false);

    private static final String redText = "-fx-text-fill: ff3333;";
    private static final String normalText = "-fx-text-fill: -fx-text-background-color;";

    private ObjectProperty<User> user = new SimpleObjectProperty<>();


    @FXML
    private void initialize() {

        usernameUnique.bind((Bindings.createBooleanBinding(() -> UserData.getInstance().usersProperty().stream().noneMatch
                                (u -> u.getUsername().equals(usernameField.getText())),
                        UserData.getInstance().usersProperty(), usernameField.textProperty())));


        oldPasswordLabel.visibleProperty().bind(user.isNotNull());
        oldPasswordField.visibleProperty().bind(user.isNotNull());

        postsChoiceBox.setItems(FXCollections.observableArrayList("Admin"));
        postsChoiceBox.getSelectionModel().selectFirst();




        oldPasswordLabel.visibleProperty().bind(user.isNotNull());
        oldPasswordField.visibleProperty().bind(user.isNotNull());


        Platform.runLater(() -> usernameField.requestFocus());
    }

    public void onDialogResult(Button button) {
        String username = usernameField.getText();
        String password1 = passwordOneField.getText();
        String post = postsChoiceBox.getValue();


        switch (button.getText()) {
            case "Register":
                UserData.getInstance().addUser(new User(username, password1, post));
                break;
            case "Update":
                UserData.getInstance().updateUser(user.get());
                break;
            case "Login":
                User user = UserData.getInstance().getUsers().filtered
                        (u -> u.getUsername().equals(username) && u.getPassword().equals(password1)).get(0);
                UserData.getInstance().setLoggedUser(user);
                break;
        }
    }

    public void setRegisterDialog() {
        userDialogPane.setHeaderText("Register New User");

        usernameLabel.styleProperty().bind(Bindings.createStringBinding
                (() -> usernameField.textProperty().isEmpty().or(usernameUnique).get() ? normalText : redText,
                        usernameField.textProperty(), usernameUnique));

        passwordEqual.bind(passwordOneField.textProperty().isEqualTo(passwordTwoField.textProperty()));

        passwordTwoLabel.styleProperty().bind(Bindings.createStringBinding
                (() -> passwordEqual.get() ? normalText: redText, passwordEqual));


        okToRegister.bind(usernameField.textProperty().isNotEmpty().and
                (passwordOneField.textProperty().isNotEmpty()).and(passwordTwoField.textProperty().isNotEmpty())
                .and(usernameUnique).and(passwordEqual));
    }






    public void setLoginDialog() {



        okToLogin.bind(Bindings.createBooleanBinding(
                () -> UserData.getInstance().usersProperty().get().stream().anyMatch
                        (u -> u.getUsername().equals(usernameField.getText()) && u.getPassword().equals(passwordOneField.getText())),
                UserData.getInstance().usersProperty(),usernameField.textProperty(),passwordOneField.textProperty()));


        usernameLabel.styleProperty().bind(Bindings.createStringBinding
                (() -> usernameUnique.not().get() ? normalText : redText, usernameUnique));

        passwordOneLabel.styleProperty().bind(Bindings.createStringBinding(
                () -> passwordOneField.textProperty().isEmpty().or(okToLogin).get() ? normalText: redText, usernameUnique,okToLogin));

        userDialogPane.getChildren().removeAll(passwordTwoLabel,passwordTwoField);

        passwordTwoLabel.setVisible(false);
        passwordTwoField.setVisible(false);


        userDialogPane.setHeaderText("User Login");
    }








    public void setUpdateDialog(User u) {
        userDialogPane.setHeaderText("Edit User Details");
        user = new SimpleObjectProperty<>(u);
        resetFields();
        oldPasswordField.setVisible(true);
        oldPasswordRight.bind(u.passwordProperty().isEqualTo(oldPasswordField.textProperty()));
        oldPasswordRight.addListener(obs -> usernameLabel.setStyle(usernameUnique.get() ? "-fx-text-fill: ff3333;" :
                "-fx-text-fill: -fx-text-background-color;"));
        oldPasswordLabel.styleProperty().bind(Bindings.createStringBinding(() -> oldPasswordRight.get() ? "-fx-text-fill: ff3333;" :
                "-fx-text-fill: -fx-text-background-color;", oldPasswordRight));


    }

    @FXML
    private void resetFields() {
        if (user.get() != null) {
            usernameField.setText(user.get().getUsername());
            postsChoiceBox.setValue(user.get().getPost());
            oldPasswordField.setVisible(true);
            oldPasswordField.setText(null);
            oldPasswordLabel.setVisible(true);
            oldPasswordField.setText(null);
        } else {
            usernameField.setText(null);
            passwordOneField.setText(null);
            passwordTwoField.setText(null);
            postsChoiceBox.getSelectionModel().selectFirst();
        }

    }


}
