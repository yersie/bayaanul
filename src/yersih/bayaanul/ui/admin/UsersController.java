package yersih.bayaanul.ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import yersih.bayaanul.common.User;
import yersih.bayaanul.db.UserData;

public class UsersController {
    @FXML
    Button addUserButton, editUserButton, deleteUserButton;
    @FXML
    private TableView<User> usersTable;

    @FXML
    private void initialize() {
        usersTable.itemsProperty().bind(UserData.getInstance().usersProperty());

    }


    public void addUser() {

    }

    public void updateUser() {

    }

    public void deleteUser() {

    }

}
