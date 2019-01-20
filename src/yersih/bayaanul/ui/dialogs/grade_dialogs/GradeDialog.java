package yersih.bayaanul.ui.dialogs.grade_dialogs;

import javafx.event.EventDispatchChain;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import yersih.bayaanul.common.Grade;
import yersih.bayaanul.db.GradeData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class GradeDialog extends Dialog<ButtonType> {

    private GradeDialogController gradeDialogController;
    private Button okButton;

    public GradeDialog(Scene root) {

        initOwner(root.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("grade_dialog.fxml"));

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setUserData("OK");


        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        gradeDialogController = fxmlLoader.getController();

    }

    public void addCourse() throws SQLException{
//        setTitle("Add Grade Details");
        setHeaderText("Add Grade");
        showDialog();
    }

    public void updateCourse(Grade grade) throws SQLException {
        setTitle("Edit Grade Details");
        gradeDialogController.setGrade(grade);

        showDialog();
    }

    public static void deleteCourseAlert(Grade grade) throws SQLException {
        if (grade != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Grade");
            alert.setHeaderText("Delete Grade and Grade Attendances");
            alert.setContentText("Are you sure you want to delete this Grade and Grade Attendance Details?");
            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            yesButton.setDefaultButton(false);
            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            noButton.setDefaultButton(true);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                GradeData.getInstance().deleteGrade(grade);
            }
        }
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail);
    }

    private void showDialog() throws SQLException{
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            System.out.println(result.get().getButtonData().getTypeCode());
            gradeDialogController.onDialogResult(okButton);
        }
    }
}
