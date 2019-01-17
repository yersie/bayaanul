package yersih.bayaanulquran.ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import yersih.bayaanulquran.common.User;
import yersih.bayaanulquran.db.UserData;

public class UsersController {

    @FXML
    private TableView<User> userTableView;
    @FXML
    private void initialize() {
        userTableView.itemsProperty().bind(UserData.getInstance().usersProperty());
    }


    public void addUser() {

    }

    public void updateUser() {

    }

    public void deleteUser() {

    }

}
