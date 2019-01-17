package yersih.bayaanulquran.ui.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {


    @FXML
    private Button btnLogin;

    @FXML
    private TextField tfLoginName;
    @FXML
    private PasswordField pfLoginPassword;
    @FXML
    private Label infoLabel;

    private Stage primaryStage;

    @FXML
    public void initialize() {
        btnLogin.setDisable(true);
    }

    @FXML
    private void onActionLogin() {
        String user = tfLoginName.getText();
        String pass = pfLoginPassword.getText();

        if (user.equals("shuaib") && pass.equals("shuaib9807013")) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("../navigation.fxml"));
                Scene scene = new Scene(parent);
                primaryStage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            infoLabel.setText("Incorrect Username or Password");
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        String name = tfLoginName.getText();
        String password = pfLoginPassword.getText();
        boolean disableButton = name.trim().isEmpty() || password.trim().isEmpty();
        btnLogin.setDisable(disableButton);
        if (keyEvent.getSource().equals(pfLoginPassword) && keyEvent.getCode().equals(KeyCode.ENTER)) {
            onActionLogin();
        }


    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
}
