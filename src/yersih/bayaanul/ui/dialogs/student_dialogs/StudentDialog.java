package yersih.bayaanul.ui.dialogs.student_dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import yersih.bayaanul.common.Student;
import yersih.bayaanul.db.StudentData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class StudentDialog extends Dialog<ButtonType> {

    private StudentDialogController studentDialogController;
    private Button okButton, closeButton;

    public StudentDialog(Scene root) {

        initOwner(root.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("student_dialog.fxml"));

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);



        okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);

        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        studentDialogController = fxmlLoader.getController();
    }


    public void addStudent() throws SQLException {
        setTitle("Add Student Details");
        okButton.disableProperty().bind(studentDialogController.requiredFieldEmpty.or(studentDialogController.indexUnique.not()));
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get().getText().equals("OK"))) {
            studentDialogController.onDialogResult();
        }
    }


    public void updateStudent(Student student) throws SQLException {
        setTitle("Edit Student Details");
        studentDialogController.setUpdateStudent(student);
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            studentDialogController.onDialogResult();
        }
    }

    public static void deleteStudentAlert(Student student) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Delete Student: " + student.getStudentIndex() + " " + student.getStudentFullName());
        alert.setContentText("Are you sure you want to delete this student?");
        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        yesButton.setDefaultButton(false);
        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        noButton.setDefaultButton(true);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            StudentData.getInstance().deleteStudent(student);
        }
    }

}
