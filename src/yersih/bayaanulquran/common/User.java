package yersih.bayaanulquran.common;

import javafx.beans.property.*;

public class User {

    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty post = new SimpleStringProperty();

    public User() {
    }

    public User(String user, String pass,String p) {
        setUsername(user);
        setPassword(pass);
        setPost(p);
    }

    public User(int id, String user, String pass, String post) {
        this(user,pass,post);
        setUserId(id);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getPost() {
        return post.get();
    }

    public StringProperty postProperty() {
        return post;
    }

    public void setPost(String post) {
        this.post.set(post);
    }
}
