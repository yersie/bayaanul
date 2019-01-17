package yersih.bayaanulquran.ui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yersih.bayaanulquran.db.*;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent home = FXMLLoader.load(getClass().getResource("navigation.fxml"));

        Scene sceneHome = new Scene(home,800,600);

        primaryStage.setScene(sceneHome);
        primaryStage.setTitle("Bayaanul Quran");

        primaryStage.show();


    }

    @Override
    public void init() throws SQLException, IOException {
        GradeData.getInstance().loadGrades();
        EnrollmentData.getInstance().loadEnrollments();
        AttendanceData.getInstance().loadAttendances();
        StudentData.getInstance().loadStudents();
        PaymentData.getInstance().loadPayments();
        UserData.getInstance().loadUsers();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
