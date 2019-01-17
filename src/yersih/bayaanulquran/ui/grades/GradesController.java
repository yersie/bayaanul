package yersih.bayaanulquran.ui.grades;


import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import yersih.bayaanulquran.common.Grade;
import yersih.bayaanulquran.common.GradeClass;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.AttendanceData;
import yersih.bayaanulquran.db.GradeData;
import yersih.bayaanulquran.db.EnrollmentData;
import yersih.bayaanulquran.db.StudentData;
import yersih.bayaanulquran.ui.dialogs.attendance_dialogs.AttendanceDialog;
import yersih.bayaanulquran.ui.dialogs.grade_dialogs.GradeDialog;
import yersih.bayaanulquran.ui.dialogs.exception_dialog.ExceptionDialog;
import yersih.bayaanulquran.ui.utils.AbsentNumCell;
import yersih.bayaanulquran.ui.utils.NumCell;

import java.util.Comparator;

public class GradesController {
    @FXML
    private BorderPane coursesBorderPane;
    @FXML
    private TableView<Grade> coursesTableView;
    @FXML
    private TableView<Student> studentsTableView;
    @FXML
    private TableView<GradeClass> classHeldTableView;
    @FXML
    private TableColumn<Student, Void> studentNumColumn;
    @FXML
    private TableColumn<GradeClass,Integer> absentNumColumn;
    @FXML
    private Label nameLabel, feeLabel, startDateLabel, classTimeLabel;
    @FXML
    private Button editCourseButton, deleteCourseButton, addClassButton, editClassButton, deleteClassButton;
    @FXML
    private SplitPane mainSplitPane;

    private FilteredList<Student> studentsFilteredList = new
            FilteredList<>(StudentData.getInstance().getStudents(), student -> false);
    private FilteredList<GradeClass> gradeClassFilteredList = new
            FilteredList<>(AttendanceData.getInstance().getClasses(), courseClass -> false);

    private ListProperty<Student> studentListProperty = new SimpleListProperty<>(studentsFilteredList.sorted
            (Comparator.comparing(Student::getStudentIndex).reversed()));
    private ListProperty<GradeClass> classListProperty = new SimpleListProperty<>(gradeClassFilteredList.sorted
            (Comparator.comparing(GradeClass::getClassDateTime).reversed()));

    @FXML
    private void initialize() {

        editCourseButton.disableProperty().bind(coursesTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteCourseButton.disableProperty().bind(editCourseButton.disabledProperty());
        editClassButton.disableProperty().bind(classHeldTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteClassButton.disableProperty().bind(editClassButton.disableProperty());

        coursesTableView.itemsProperty().bind(GradeData.getInstance().gradesProperty());
        studentsTableView.itemsProperty().bind(studentListProperty);
        classHeldTableView.itemsProperty().bind(classListProperty);

        coursesTableView.getSelectionModel().selectedItemProperty().addListener((obs) -> {
            if (coursesTableView.getSelectionModel().selectedItemProperty().isNotNull().get()) {

                nameLabel.textProperty().bind(coursesTableView.getSelectionModel().getSelectedItem().gradeNameProperty());
                feeLabel.textProperty().bind(coursesTableView.getSelectionModel().getSelectedItem().gradeFeeProperty().asString());
                startDateLabel.textProperty().bind(coursesTableView.getSelectionModel().getSelectedItem().startDateFormattedProperty());
                classTimeLabel.textProperty().bind(coursesTableView.getSelectionModel().getSelectedItem().classTimeProperty());

                studentsFilteredList.setPredicate(
                        student -> EnrollmentData.getInstance().getEnrollments().stream().anyMatch(
                                enrollment -> enrollment.getStudentIndex().equals(student.getStudentIndex()) &&
                                        enrollment.getCourseId().equals
                                        (coursesTableView.getSelectionModel().getSelectedItem().getGradeId())));

                gradeClassFilteredList.setPredicate
                        (courseClass -> courseClass.getCourseId().equals
                                (coursesTableView.getSelectionModel().getSelectedItem().getGradeId()));

            } else {
                studentsFilteredList.setPredicate(student -> false);
            }
        });

        studentNumColumn.setCellFactory(col -> new NumCell<>());
        absentNumColumn.setCellFactory(c -> new AbsentNumCell());

    }


    @FXML
    public void addCourse() {
        try {
            new GradeDialog(coursesBorderPane.getScene()).addCourse();
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }

    }

    @FXML
    public void editCourse() {
        try {
            new GradeDialog(coursesBorderPane.getScene()).updateCourse(coursesTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }


    }

    @FXML
    public void deleteCourseAlert() {
        try {
            GradeDialog.deleteCourseAlert(coursesTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

    @FXML
    public void addClass() {
        try {
            if (coursesTableView.getSelectionModel().selectedItemProperty().isNotNull().get()) {
                new AttendanceDialog(coursesBorderPane.getScene()).addClass(coursesTableView.getSelectionModel().getSelectedItem());
            } else {
                new AttendanceDialog(coursesBorderPane.getScene()).addClass();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

    @FXML
    public void editClass() {
        try {
            new AttendanceDialog(coursesBorderPane.getScene()).updateClass
                    (classHeldTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

    @FXML
    public void deleteClass() {
        try {
            AttendanceDialog.deleteClassAlert(classHeldTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

}
