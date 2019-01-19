package yersih.bayaanulquran.ui;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import yersih.bayaanulquran.db.DropToBox;
import yersih.bayaanulquran.db.UserData;
import yersih.bayaanulquran.ui.dialogs.exception_dialog.ExceptionDialog;
import yersih.bayaanulquran.ui.dialogs.user_dialog.UserDialog;

import java.io.IOException;

public class NavController {

    @FXML
    private AnchorPane navigationPane, opacityPane, drawerPane, mainWindow;

    @FXML
    private Label userLabel;

    @FXML
    private Button logoutButton,saveDataButton;

    private boolean isDrawerOpen;



    @FXML
    private void initialize() {

        StringBinding loggedUserText = Bindings.createStringBinding(
                () -> UserData.getInstance().loggedUserProperty().isNull().get() ? "": UserData.getInstance().getLoggedUser().getUsername(),
                UserData.getInstance().loggedUserProperty());

        userLabel.textProperty().bind(loggedUserText);

        logoutButton.disableProperty().bind(UserData.getInstance().loggedUserProperty().isNull());

        Platform.runLater(this::logoutUser);

        Platform.runLater(this::saveData);
    }



    @FXML
    public void navHamburgerToggle() {
        if (!isDrawerOpen) {
            drawerPane.setTranslateX(0);
            opacityPane.setVisible(true);
            isDrawerOpen = true;
        } else {
            drawerPane.setTranslateX(-250);
            opacityPane.setVisible(false);
            isDrawerOpen = false;
        }
    }

    @FXML
    public void viewStudents() {
        mainWindow.getChildren().clear();
        try {
            mainWindow.getChildren().add(FXMLLoader.load(getClass().getResource("students/students.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
        navHamburgerToggle();
    }

    @FXML
    public void viewCourses() {
        mainWindow.getChildren().clear();
        try {
            mainWindow.getChildren().add(FXMLLoader.load(getClass().getResource("grades/grades.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
        navHamburgerToggle();
    }

    @FXML
    public void viewPayments() {
        mainWindow.getChildren().clear();
        try {
            mainWindow.getChildren().add(FXMLLoader.load(getClass().getResource("payments/payments.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
        navHamburgerToggle();
    }

    @FXML
    public void viewAttendance() {
        mainWindow.getChildren().clear();
        try {
            mainWindow.getChildren().add(FXMLLoader.load(getClass().getResource("attendances/attendance.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
        navHamburgerToggle();
    }

    @FXML
    public void viewFees() {
        mainWindow.getChildren().clear();
        try {
            mainWindow.getChildren().add(FXMLLoader.load(getClass().getResource("fees/fees.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
        navHamburgerToggle();
    }

    @FXML
    public void saveData() {
        try {
            new DropToBox().uploadDatabase(saveDataButton);
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

    @FXML
    public void logoutUser() {
        UserData.getInstance().setLoggedUser(null);
        while (UserData.getInstance().getLoggedUser() == null) {
            if (UserData.getInstance().getUsers().size() > 0) {
                UserDialog userDialog = new UserDialog(mainWindow.getScene());
                userDialog.LoginUser();
            } else {
                UserDialog userDialog = new UserDialog(mainWindow.getScene());
                userDialog.registerUser();
            }

        }
    }

}
