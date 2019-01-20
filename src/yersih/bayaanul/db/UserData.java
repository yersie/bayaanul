package yersih.bayaanul.db;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanul.common.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static yersih.bayaanul.db.Database.connect;

public class UserData {

    private static final String TABLE_USER = "users";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "name";
    private static final String COLUMN_HASH = "hash";
    private static final String COLUMN_POST = "post";

    private static UserData instance = new UserData();

    public static UserData getInstance() {
        return instance;
    }

    private final ListProperty<User> users = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ObjectProperty<User> loggedUser = new SimpleObjectProperty<>(null);

    public ObservableList<User> getUsers() {
        return users.get();
    }

    public ListProperty<User> usersProperty() {
        return users;
    }

    private UserData() {
        try (Statement statement = connect().createStatement()) {

            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s INT PRIMARY KEY," +
                            " %s TEXT NOT NULL UNIQUE, %s TEXT NOT NULL, %s TEXT NOT NULL)",
                    TABLE_USER, COLUMN_ID, COLUMN_USERNAME, COLUMN_HASH, COLUMN_POST));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User u) {
        String sql = String.format("INSERT INTO %s " +
                        "(%s,  %s, %s)" +
                        " VALUES(?, ?, ?)",
                TABLE_USER, COLUMN_USERNAME, COLUMN_HASH,
                COLUMN_POST);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
            preparedStatement.setString(1, u.getUsername());
            preparedStatement.setString(2, u.getPassword());
            preparedStatement.setString(3, u.getPost());
            preparedStatement.executeUpdate();
            users.add(getUserId(u));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private User getUserId(User u) {
        String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
                COLUMN_ID, TABLE_USER, COLUMN_USERNAME);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
            preparedStatement.setString(1, u.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            u.setUserId(resultSet.getInt(COLUMN_ID));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public void updateUser(User u) {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE_USER, COLUMN_USERNAME, COLUMN_HASH, COLUMN_POST,
                COLUMN_ID);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {

            preparedStatement.setString(1, u.getUsername());
            preparedStatement.setString(2, u.getPassword());
            preparedStatement.setString(3, u.getPost());
            preparedStatement.setInt(4, u.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User u) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_USER, COLUMN_ID);

        try (PreparedStatement studentPreparedStatement = connect().prepareStatement(sql)) {
            studentPreparedStatement.setInt(1, u.getUserId());
            studentPreparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        users.remove(u);
    }

    public User getLoggedUser() {
        return loggedUser.get();
    }

    public ObjectProperty<User> loggedUserProperty() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser.set(loggedUser);
    }

    public void loadUsers() {

        String sql = String.format("SELECT %s, %s, %s, %s FROM %s",
                COLUMN_ID, COLUMN_USERNAME, COLUMN_HASH, COLUMN_POST,
                TABLE_USER);

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int userId = resultSet.getInt(COLUMN_ID);
                String name = resultSet.getString(COLUMN_USERNAME);
                String hash = resultSet.getString(COLUMN_HASH);
                String post = resultSet.getString(COLUMN_POST);

                users.add(new User(userId, name,
                        hash, post));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
