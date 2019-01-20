package yersih.bayaanul.ui.dialogs.attendance_dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import yersih.bayaanul.common.Grade;
import yersih.bayaanul.common.GradeClass;
import yersih.bayaanul.db.AttendanceData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AttendanceDialog extends Dialog<ButtonType> {

    private AttendanceDialogController attendanceController;

    public AttendanceDialog(Scene root) {

        initOwner(root.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("attendance_dialog.fxml"));

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        attendanceController = fxmlLoader.getController();
    }

    public void addClass() throws SQLException {
        setTitle("Add Class Attendance");
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            attendanceController.onDialogResult();
        }
    }

    public void addClass(Grade grade) throws SQLException {
        attendanceController.setCourse(grade);
        setTitle("Add Class Attendance");
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            attendanceController.onDialogResult();
        }
    }

    public void updateClass(GradeClass gradeClass) throws SQLException {
        attendanceController.onUpdate(gradeClass);
        setTitle("Edit Class Attendance");
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            attendanceController.onDialogResult();
        }
    }

    public static void deleteClassAlert(GradeClass gradeClass) throws SQLException {
        if (gradeClass != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Class");
            alert.setHeaderText("Delete Class and Attendance Details");
            alert.setContentText("Are you sure you want to delete this Class and Class Attendance Details?");
            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            yesButton.setDefaultButton(false);
            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            noButton.setDefaultButton(true);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                AttendanceData.getInstance().deleteAllClassAttendances(gradeClass);
            }
        }
    }

}
