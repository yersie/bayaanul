package yersih.bayaanul.ui.dialogs.grade_dialogs;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import yersih.bayaanul.common.Grade;
import yersih.bayaanul.db.GradeData;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class GradeDialogController {
    @FXML
    public TextField tfCourseName, tfCourseFee,tfClassTime;
    @FXML
    private DatePicker dpCourseStartDate;

    private BooleanProperty okToAdd;

    private Grade grade;


    @FXML
    private void initialize() {



    }


    public void onDialogResult(Button button) throws SQLException{

        String gradeName = tfCourseName.getText();
        double gradeFee = Double.parseDouble(tfCourseFee.getText());
        LocalDate gradeStartDate = dpCourseStartDate.getValue();
        String classTime = tfClassTime.getText();

        if (grade == null) {
            String id = Integer.toString(new Random().nextInt());
            grade = new Grade(id,gradeName, gradeFee, gradeStartDate,classTime);
            GradeData.getInstance().addGrade(grade);
        } else {
            grade.setGradeName(gradeName);
            grade.setGradeFee(gradeFee);
            grade.setGradeStartDate(gradeStartDate);
            grade.setClassTime(classTime);

            GradeData.getInstance().updateGrade(grade);
        }
    }

    public void setGrade(Grade grade) {
        this.grade = grade;

        tfCourseName.setText(grade.getGradeName());
        tfCourseFee.setText(String.valueOf(grade.getGradeFee()));
        dpCourseStartDate.setValue(grade.getGradeStartDate());
        tfClassTime.setText(grade.getClassTime());
    }

    public void injectOkButton() {

    }
}
