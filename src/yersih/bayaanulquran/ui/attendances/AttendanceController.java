package yersih.bayaanulquran.ui.attendances;

import javafx.beans.InvalidationListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import yersih.bayaanulquran.common.Attendance;
import yersih.bayaanulquran.common.Grade;
import yersih.bayaanulquran.common.GradeClass;
import yersih.bayaanulquran.db.AttendanceData;
import yersih.bayaanulquran.db.GradeData;
import yersih.bayaanulquran.db.StudentData;
import yersih.bayaanulquran.ui.utils.AttendanceCell;
import yersih.bayaanulquran.ui.utils.AbsentNumCell;
import yersih.bayaanulquran.ui.utils.NumCell;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Predicate;

public class AttendanceController {
    @FXML
    private ComboBox<Grade> courseComboBox;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private TableView<Attendance> attendanceTableView;
    @FXML
    private TableView<GradeClass> classTable;
    @FXML
    private TableColumn<GradeClass, Integer> absentNumColumn;
    @FXML
    private TableColumn<GradeClass, Void> classNumColumn;
    @FXML
    private TableColumn<Attendance, Void> studentNumColumn;
    @FXML
    private TableColumn<Attendance, String> studentNameColumn;
    @FXML
    private TableColumn<Attendance, String> studentAttendanceColumn;


    private final FilteredList<GradeClass> gradeClassFilteredList = new FilteredList<>(
            AttendanceData.getInstance().classesProperty());

    private final FilteredList<Attendance> attendanceFilteredList = new FilteredList<>(
            AttendanceData.getInstance().attendancesProperty(), attendance -> false);


    @FXML
    private void initialize() {

        //Set Grade ComboBox Items From GradeData Grade ObservableList ListProperty
        //Set Classes TableView Items to FilteredList From GradeData Classes ObservableList ListProperty
        //Set Attendance Table Items to FilteredList From GradeData Attendance ObservableList List Property

        courseComboBox.itemsProperty().bind(GradeData.getInstance().gradesProperty());
        classTable.setItems(gradeClassFilteredList.sorted(Comparator.comparing(GradeClass::getClassDateTime)));
        attendanceTableView.setItems(attendanceFilteredList.sorted(Comparator.comparing(Attendance::getStudentIndex)));


//        ChangeListener listener = (obs,o,n) ->
//                classTable.itemsProperty().set(AttendanceData.classesProperty().filtered(getCourseComboPredicate().and
//                        (getStartDatePredicate()).and
//                        (getEndDatePredicate())));


        final InvalidationListener invalidationListener = obs ->
                gradeClassFilteredList.setPredicate(getCourseComboPredicate().and(getStartDatePredicate()).and(getEndDatePredicate()));

        courseComboBox.valueProperty().addListener(invalidationListener);
        startDatePicker.valueProperty().addListener(invalidationListener);
        endDatePicker.valueProperty().addListener(invalidationListener);


        classTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) ->
                attendanceFilteredList.setPredicate(getAttendancePredicate()));

        courseComboBox.getSelectionModel().selectFirst();
        startDatePicker.setValue(LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1));
        endDatePicker.setValue(LocalDate.now().plusDays(1));

        classNumColumn.setCellFactory(c -> new NumCell<>());
        studentNumColumn.setCellFactory(c -> new NumCell<>());

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

        studentAttendanceColumn.setCellFactory(c -> new AttendanceCell<>());

        absentNumColumn.setCellFactory(c -> new AbsentNumCell());

    }


    private Predicate<GradeClass> getCourseComboPredicate() {
        return c -> {
            if (courseComboBox.valueProperty().isNotNull().get()) {
                return c.getCourseId().equals(courseComboBox.getValue().getGradeId());
            } else {
                return true;
            }
        };
    }

    private Predicate<GradeClass> getStartDatePredicate() {
        return c -> {
            if (startDatePicker.valueProperty().isNotNull().get()) {
                return c.getClassDateTime().toLocalDate().isAfter(startDatePicker.getValue()) ||
                        c.getClassDateTime().toLocalDate().isEqual(startDatePicker.getValue());
            } else {
                return true;
            }
        };
    }


    private Predicate<GradeClass> getEndDatePredicate() {
        return c -> {
            if (endDatePicker.valueProperty().isNotNull().get()) {
                return c.getClassDateTime().toLocalDate().isBefore(endDatePicker.getValue()) ||
                        c.getClassDateTime().toLocalDate().isEqual(endDatePicker.getValue());
            } else {
                return true;
            }
        };
    }

    private Predicate<Attendance> getAttendancePredicate() {
        return a -> {
            if (classTable.getSelectionModel().selectedItemProperty().isNotNull().get()) {
                return a.getCourseId().equals(classTable.getSelectionModel().getSelectedItem().getCourseId()) &&
                        a.getClassDateTime().isEqual(classTable.getSelectionModel().getSelectedItem().getClassDateTime());
            } else {
                return false;
            }
        };
    }

}
