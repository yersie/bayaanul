package yersih.bayaanul.ui.dialogs.student_dialogs;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import yersih.bayaanul.common.Enrollment;
import yersih.bayaanul.common.Grade;
import yersih.bayaanul.common.Student;
import yersih.bayaanul.db.EnrollmentData;
import yersih.bayaanul.db.GradeData;
import yersih.bayaanul.db.StudentData;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDialogController {


    @FXML
    public TextField tfStudentIndex, tfStudentFullName, tfStudentAddress, tfStudentNationalId, tfStudentPhone;
    @FXML
    private DatePicker dpStudentJoinDate;
    @FXML
    private TableView<Grade> courseTableView, studentCourseTableView;

    BooleanProperty requiredFieldEmpty = new SimpleBooleanProperty();
    BooleanProperty indexUnique = new SimpleBooleanProperty();

    private Student student = null;


    @FXML
    private void initialize() {

//        isUpdate = false;

        requiredFieldEmpty.bind(tfStudentIndex.textProperty().isEmpty().or(tfStudentFullName.textProperty().isEmpty()));

        indexUnique.bind(Bindings.createBooleanBinding(() ->
                StudentData.getInstance().studentsProperty().stream().noneMatch
                        (s -> s.studentIndexProperty().isEqualTo(tfStudentIndex.textProperty()).get())
                ,tfStudentIndex.textProperty()));

        dpStudentJoinDate.setValue(LocalDate.now());

        courseTableView.setItems(GradeData.getInstance().getGrades());
        courseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        courseTableView.setEditable(true);


    }


    public void onDialogResult() throws SQLException {
        String studentIndex = tfStudentIndex.getText().trim();
        String studentFullName = tfStudentFullName.getText().trim();
        String studentNationalId = tfStudentNationalId.getText().trim();
        String studentAddress = tfStudentAddress.getText().trim();
        String studentPhone = tfStudentPhone.getText().trim();
        LocalDate studentJoinDate = dpStudentJoinDate.getValue();

        if (student == null) {
            student = new Student(studentIndex, studentFullName, studentNationalId, studentAddress, studentPhone, studentJoinDate);
            StudentData.getInstance().addStudent(student);

            for (Grade c : studentCourseTableView.getItems()) {
                EnrollmentData.getInstance().addEnrollment(new Enrollment(c.getGradeId(), student.getStudentIndex()));
            }

        } else {
            student.setStudentIndex(studentIndex);
            student.setStudentFullName(studentFullName);
            student.setStudentNationalId(studentNationalId);
            student.setStudentAddress(studentAddress);
            student.setStudentPhoneNumber(studentPhone);
            student.setStudentJoinDate(dpStudentJoinDate.getValue());

            StudentData.getInstance().updateStudent(student);


            //ADD NEW ENROLLMENTS FROM DIALOG
            for (Grade c : studentCourseTableView.getItems()) {

                if (EnrollmentData.getInstance().getEnrollments().stream().noneMatch
                        (e -> e.getCourseId().equals(c.getGradeId()) && e.getStudentIndex().equals(student.getStudentIndex()))) {
                    EnrollmentData.getInstance().addEnrollment(new Enrollment(c.getGradeId(), student.getStudentIndex()));
                }


            }

            //REMOVE OLD REMOVED ENROLLMENTS FROM DATABASE
            FilteredList<Enrollment> filteredRemoveList = new FilteredList<>(EnrollmentData.getInstance().getEnrollments());

            filteredRemoveList.setPredicate(e -> studentCourseTableView.getItems().stream().noneMatch
                    (c -> c.getGradeId().equals(e.getCourseId()))
             && e.getStudentIndex().equals(student.getStudentIndex()));

            List<Enrollment> removeList = new ArrayList<>(filteredRemoveList);

            for (Enrollment e: removeList) {
                EnrollmentData.getInstance().deleteEnrollment(e);
            }

        }
    }

    @FXML
    public void addGrade() {
        for (Grade grade : courseTableView.getSelectionModel().getSelectedItems()) {
            if (!studentCourseTableView.getItems().contains(grade))
                studentCourseTableView.getItems().add(grade);
        }
    }

    @FXML
    public void removeGrade() {
        studentCourseTableView.getItems().removeAll
                (studentCourseTableView.getSelectionModel().getSelectedItems());
    }

    void setUpdateStudent(Student s) {
//        isUpdate = true;

        this.student = s;

        tfStudentIndex.setText(student.getStudentIndex());
        tfStudentFullName.setText(student.getStudentFullName());
        tfStudentNationalId.setText(student.getStudentNationalId());
        tfStudentAddress.setText(student.getStudentAddress());
        tfStudentPhone.setText(student.getStudentPhoneNumber());
        dpStudentJoinDate.setValue(student.getStudentJoinDate());

//        ObservableList<Grade> studentCourses = GradeData.getInstance().getGrades().stream().filter(s -> s.getGradeId())

        studentCourseTableView.getItems().setAll(GradeData.getInstance().getGrades().filtered
                (c -> s.getEnrolledCourseIdList().stream().anyMatch(e -> e.equals(c.getGradeId()))));
    }



}
