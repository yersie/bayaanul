package yersih.bayaanulquran.ui.dialogs.attendance_dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import yersih.bayaanulquran.common.Attendance;
import yersih.bayaanulquran.common.Grade;
import yersih.bayaanulquran.common.GradeClass;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.AttendanceData;
import yersih.bayaanulquran.db.GradeData;
import yersih.bayaanulquran.db.EnrollmentData;
import yersih.bayaanulquran.db.StudentData;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDialogController {

    @FXML
    private TableView<Attendance> attendanceItemTableView;
    @FXML
    private TableColumn<Student, Boolean> presenceTableColumn;
    @FXML
    private DatePicker classDatePicker;
    @FXML
    private TextField tfLesson;
    @FXML
    private Spinner<Integer> hourSpinner, minuteSpinner;
    @FXML
    private Spinner<String> amPmSpinner;
    @FXML
    public ComboBox<Grade> courseComboBox;
    @FXML
    private TableColumn<Attendance,String> studentNameColumn;

    private GradeClass oldGradeClass;

    private ObservableList<Attendance> attendances;

    @FXML
    public void initialize() {

        courseComboBox.setItems(GradeData.getInstance().getGrades());

        courseComboBox.getSelectionModel().selectedItemProperty().addListener(obs ->
                setCourse(courseComboBox.getSelectionModel().selectedItemProperty().isNotNull().get() ?
                        courseComboBox.getSelectionModel().getSelectedItem() : null));

        presenceTableColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        ObservableList<String> amPm = FXCollections.observableArrayList();
        amPm.add("AM");
        amPm.add("PM");

        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 7));
        amPmSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(amPm));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60));

        classDatePicker.setValue(LocalDate.now());

        studentNameColumn.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(
                            StudentData.getInstance().getStudents().stream().filter
                                    ( s -> s.getStudentIndex().equals(item)).findFirst().get().getStudentFullName());
                }
            }
        });

    }

    public void onDialogResult() throws SQLException {
        LocalDate classDate = classDatePicker.getValue();
        String lesson = tfLesson.getText();
        int hour = hourSpinner.getValue();
        String ampm = amPmSpinner.getValue();
        int minute = minuteSpinner.getValue();

        if (ampm.equals("PM") && hour != 12) {
            hour = hour + 12;
        } else if (ampm.equals("AM") && hour == 12) {
            hour = 0;
        }


        LocalDateTime dateTime = classDate.atTime(hour, minute);

        for (Attendance attendance : attendanceItemTableView.getItems()) {
            attendance.setCourseClass(courseComboBox.getValue().getGradeId(), lesson, dateTime);
        }


        if (oldGradeClass == null) {
            AttendanceData.getInstance().addClassAttendance(attendances);
        } else {
            AttendanceData.getInstance().updateClassAttendance(oldGradeClass, attendances);
        }
    }

    public void setCourse(Grade grade) {

        courseComboBox.getSelectionModel().select(grade);

        attendances = FXCollections.observableArrayList();

        List<Student> courseStudents = new ArrayList<>(EnrollmentData.getInstance().getCourseEnrolledStudents(grade));

        for (Student student : courseStudents) {
            attendances.add(new Attendance(student.getStudentIndex(), true));
        }

        attendanceItemTableView.setItems(attendances);
    }


    public void onUpdate(GradeClass gradeClass) {

        oldGradeClass = gradeClass;
        Grade grade = courseComboBox.getItems().filtered
                (c -> c.getGradeId().equals(gradeClass.getCourseId())).get(0);

        courseComboBox.getSelectionModel().select(grade);

        attendances = AttendanceData.getInstance().getAttendances().filtered
                (a -> a.getCourseId().equals(gradeClass.getCourseId()) &&
                        a.getClassDateTime().equals(gradeClass.getClassDateTime()) &&
                        a.getClassLesson().equals(gradeClass.getClassLesson()));

        attendanceItemTableView.setItems(attendances);

        classDatePicker.setValue(gradeClass.getClassDateTime().toLocalDate());
        hourSpinner.getValueFactory().setValue(gradeClass.getClassDateTime().getHour());
        minuteSpinner.getValueFactory().setValue(gradeClass.getClassDateTime().getMinute());
        tfLesson.setText(gradeClass.getClassLesson());

    }


}
