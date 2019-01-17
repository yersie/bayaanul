package yersih.bayaanulquran.ui.students;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import yersih.bayaanulquran.common.Attendance;
import yersih.bayaanulquran.common.Grade;
import yersih.bayaanulquran.common.Payment;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.*;
import yersih.bayaanulquran.ui.dialogs.exception_dialog.ExceptionDialog;
import yersih.bayaanulquran.ui.dialogs.payment_dialogs.PaymentDialog;
import yersih.bayaanulquran.ui.dialogs.student_dialogs.StudentDialog;
import yersih.bayaanulquran.ui.utils.AttendanceCell;
import yersih.bayaanulquran.ui.utils.NumCell;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.function.Predicate;

public class StudentsController {

    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableView<Payment> paymentsTableView;
    @FXML
    private TableView<Grade> coursesTable;
    @FXML
    private TableView<Attendance> attendanceTable;
    @FXML
    private Label nameLabel, indexLabel, nidLabel, phoneLabel, addressLabel,
            joinDateLabel, feeTotalLabel, feePaidLabel, feeRemainingLabel;
    @FXML
    private Button addStudentButton, editStudentButton, deleteStudentButton,
            receivePaymentButton, editPaymentButton, deletePaymentButton;
    @FXML
    private TextField searchStudentField;
    @FXML
    private TableColumn<Grade, String> courseFeeColumn;
    @FXML
    private TableColumn<Attendance, String> attendanceColumn;
    @FXML
    private TableColumn<Student, Student> studentListIndexColumn;

    private final FilteredList<Student> studentFilteredList = new
            FilteredList<>(StudentData.getInstance().getStudents(), student -> true);
    private final FilteredList<Grade> gradeFilteredList = new
            FilteredList<>(GradeData.getInstance().getGrades(), course -> false);
    private final FilteredList<Payment> paymentFilteredList = new
            FilteredList<>(PaymentData.getInstance().getPayments(), payment -> false);
    private final FilteredList<Attendance> attendanceFilteredList = new
            FilteredList<>(AttendanceData.getInstance().getAttendances(), a -> false);

    private final SortedList<Student> studentSortedList = new SortedList<>(studentFilteredList,
            Comparator.comparing(Student::getStudentIndex).reversed());
    private final SortedList<Grade> gradeSortedList = new SortedList<>(gradeFilteredList,
            Comparator.comparing(Grade::getGradeId).reversed());
    private final SortedList<Payment> paymentSortedList = new SortedList<>(paymentFilteredList,
            Comparator.comparing(Payment::getPaymentDateTime));
    private final SortedList<Attendance> attendanceSortedList = new SortedList<>(attendanceFilteredList,
            Comparator.comparing(Attendance::getClassDateTime).reversed());


    private final ListProperty<Student> studentListProperty = new SimpleListProperty<>(studentSortedList);
    private final ListProperty<Grade> gradeListProperty = new SimpleListProperty<>(gradeSortedList);
    private final ListProperty<Payment> paymentListProperty = new SimpleListProperty<>(paymentSortedList);
    private final ListProperty<Attendance> attendanceListProperty = new SimpleListProperty<>(attendanceSortedList);

    private final DoubleProperty feePaid = new SimpleDoubleProperty(0),
            feeTotal = new SimpleDoubleProperty(0),
            feeRemaining = new SimpleDoubleProperty(0);

    @FXML
    public void initialize() {
        // Bind the (Sorted List) List Properties  to Table
        studentsTable.itemsProperty().bind(studentListProperty);
        coursesTable.itemsProperty().bind(gradeListProperty);
        paymentsTableView.itemsProperty().bind(paymentListProperty);
        attendanceTable.itemsProperty().bind(attendanceListProperty);

        // Search Field Listens to Input and Updates Student Filtered List Predicate
        searchStudentField.textProperty().addListener
                (obs -> studentFilteredList.setPredicate(searchStudentPredicate()));

        studentsTable.getSelectionModel().selectedItemProperty().addListener(studentsTableItemListener());
        studentsTable.getSelectionModel().selectedItemProperty().addListener(attendanceUpdateListener());
        studentsTable.getSelectionModel().selectedItemProperty().addListener(coursesUpdateListener());
        coursesTable.getSelectionModel().selectedItemProperty().addListener(attendanceUpdateListener());
        EnrollmentData.getInstance().enrollmentsProperty().addListener(coursesUpdateListener());

        attendanceColumn.setCellFactory(c -> new AttendanceCell<>());


        // Fee Total, Fee Paid and Fee Remaining Double Bindings and Label Bindings
        feeTotal.bind(Bindings.createDoubleBinding(() -> gradeListProperty.stream()
                .mapToDouble(Grade::getGradeFee).sum(), gradeListProperty));
        feePaid.bind(Bindings.createDoubleBinding(() -> paymentListProperty.stream()
                .mapToDouble(Payment::getPaymentAmount).sum(), paymentListProperty));
        feeRemaining.bind(feeTotal.subtract(feePaid));

        feeTotalLabel.textProperty().bind(feeTotal.asString().concat(" RF"));
        feePaidLabel.textProperty().bind(feePaid.asString().concat(" RF"));
        feeRemainingLabel.textProperty().bind(feeRemaining.asString().concat(" RF"));


        // Buttons Disable Property Bindings
        editStudentButton.disableProperty().bind(studentsTable.getSelectionModel().selectedItemProperty().isNull());
        deleteStudentButton.disableProperty().bind(editStudentButton.disabledProperty());
        receivePaymentButton.setDisable(false);
        editPaymentButton.disableProperty().bind(paymentsTableView.getSelectionModel().selectedItemProperty().isNull());
        deletePaymentButton.disableProperty().bind(editPaymentButton.disabledProperty());


        /**
         * student list (No.) column cellfactory
         */

        studentListIndexColumn.setCellFactory(list -> new NumCell<>());

    }


    @FXML
    private void addStudent() {
        try {
            new StudentDialog(studentsTable.getScene()).addStudent();
        } catch (SQLException e) {
            new ExceptionDialog().showExceptionDialog(e);
        }

    }

    @FXML
    private void updateStudent() {
        try {
            new StudentDialog(studentsTable.getScene())
                    .updateStudent(studentsTable.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            showException(e);
        }
    }

    @FXML
    private void deleteStudentAlert() {
        try {
            StudentDialog.deleteStudentAlert(studentsTable.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            showException(e);
        }
    }

    @FXML
    private void addPayment() {
        new PaymentDialog(studentsTable.getScene()).addPayment(studentsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void editPayment() {
        new PaymentDialog(studentsTable.getScene()).updatePayment(
                paymentsTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void deletePayment() {
        PaymentDialog.deletePaymentAlert(paymentsTableView.getSelectionModel().getSelectedItem());
    }

    private Predicate<Student> searchStudentPredicate() {
        return s -> searchStudentField.getText().trim().isEmpty() ||
                s.getStudentIndex().toLowerCase().contains(searchStudentField.getText().trim().toLowerCase()) ||
                s.getStudentFullName().toLowerCase().contains(searchStudentField.getText().trim().toLowerCase());
    }

    private Predicate<Attendance> attendancePredicate() {
        return a -> coursesTable.getSelectionModel().selectedItemProperty().isNotNull().get() &&
                a.getCourseId().equals(coursesTable.getSelectionModel().getSelectedItem().getGradeId()) &&
                a.getStudentIndex().equals(studentsTable.getSelectionModel().getSelectedItem().getStudentIndex());
    }

    private InvalidationListener attendanceUpdateListener() {
        return observable -> attendanceFilteredList.setPredicate(attendancePredicate());
    }

    private InvalidationListener studentsTableItemListener() {
        return observable -> {
            if (studentsTable.getSelectionModel().selectedItemProperty().isNotNull().get()) {

                nameLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentFullNameProperty());
                indexLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentIndexProperty());
                nidLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentNationalIdProperty());
                phoneLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentPhoneNumberProperty());
                addressLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentAddressProperty());
                joinDateLabel.textProperty().bind(studentsTable.getSelectionModel().getSelectedItem().studentJoinDateFormattedProperty());


//                EnrollmentData.getInstance().getEnrollments().addListener(
//                        (ListChangeListener<Enrollment>) c -> gradeFilteredList.setPredicate(course ->
//                                EnrollmentData.getInstance().getEnrollments().stream().anyMatch(
//                                        enrollment -> enrollment.getStudentIndex().equals(
//                                                studentsTable.getSelectionModel().getSelectedItem().getStudentIndex()) &&
//                                                enrollment.getGradeId().equals(course.getGradeId()))));

                paymentFilteredList.setPredicate(payment ->
                        studentsTable.getSelectionModel().getSelectedItem().getStudentIndex().equals(payment.getPaymentStudentIndex()));

            } else {
                nameLabel.textProperty().unbind();
                indexLabel.textProperty().unbind();
                nidLabel.textProperty().unbind();
                phoneLabel.textProperty().unbind();
                addressLabel.textProperty().unbind();
                joinDateLabel.textProperty().unbind();

                nameLabel.setText(null);
                indexLabel.setText(null);
                nidLabel.setText(null);
                addressLabel.setText(null);
                phoneLabel.setText(null);
                joinDateLabel.setText(null);

                gradeFilteredList.setPredicate(course -> false);
                paymentFilteredList.setPredicate(payment -> false);

            }
        };
    }

    private InvalidationListener coursesUpdateListener() {
        return obs -> {
            if (studentsTable.getSelectionModel().selectedItemProperty().isNotNull().get()) {
                gradeFilteredList.setPredicate(g -> EnrollmentData.getInstance().getEnrollments().stream().anyMatch(
                        enrollment -> enrollment.getStudentIndex().equals(
                                studentsTable.getSelectionModel().getSelectedItem().getStudentIndex()) &&
                                enrollment.getCourseId().equals(g.getGradeId())));
            } else {
                gradeFilteredList.setPredicate(g -> false);
            }
        };
    }

    private static void showException(Exception e) {
        e.printStackTrace();
        new ExceptionDialog().showExceptionDialog(e);
    }

}

