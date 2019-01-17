package yersih.bayaanulquran.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanulquran.db.GradeData;
import yersih.bayaanulquran.db.EnrollmentData;
import yersih.bayaanulquran.db.StudentData;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Student implements Model {

    private final StringProperty studentIndex;
    private final StringProperty studentFullName;
    private final StringProperty studentAddress;
    private final StringProperty studentNationalId;
    private final StringProperty studentPhoneNumber;
    private final ObjectProperty<LocalDate> studentJoinDate;

    private final StringProperty studentJoinDateFormatted = new SimpleStringProperty();

    private ListProperty<String> enrolledCourseIdList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");


    public Student(String sIndex, String fullName,
                   String nationalId, String address, String phone, LocalDate joinDate) {

        this.studentIndex = new SimpleStringProperty(sIndex);
        this.studentFullName = new SimpleStringProperty(fullName);
        this.studentAddress = new SimpleStringProperty(address);
        this.studentJoinDate = new SimpleObjectProperty<>(joinDate);
        this.studentNationalId = new SimpleStringProperty(nationalId);
        this.studentPhoneNumber = new SimpleStringProperty(phone);

        setStudentJoinDateFormatted();
        this.studentJoinDate.addListener(observable -> setStudentJoinDateFormatted());

        enrolledCourseIdList.bind(Bindings.createObjectBinding(() -> FXCollections.observableArrayList(
                EnrollmentData.getInstance().getEnrollments().filtered(
                            (e -> e.getStudentIndex().equals(getStudentIndex()))).stream().map(Enrollment::getCourseId).collect(Collectors.toList())),
                EnrollmentData.getInstance().enrollmentsProperty()));


//                new ListBinding<String>(){
//            @Override
//            protected ObservableList<String> computeValue() {
//                List<String> idList =  EnrollmentData.getInstance().getEnrollments().stream().filter(e -> e.getStudentIndex().equals(getStudentIndex())).map
//                        (Enrollment::getGradeId).collect(Collectors.toList());
//                return FXCollections.observableArrayList(idList); }
//        });

    }


    // Getter, Setter and Bean Property for Student Index

    public String getStudentIndex() {
        return studentIndex.get();
    }

    public StringProperty studentIndexProperty() {
        return studentIndex;
    }

    public void setStudentIndex(String studentIndex) {
        this.studentIndex.set(studentIndex);
    }

    // Getter, Setter and Bean Property for Student Full Name

    public String getStudentFullName() {
        return studentFullName.get();
    }

    public StringProperty studentFullNameProperty() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName.set(studentFullName);
    }

    // Getter, Setter and Bean Property for Student National ID

    public String getStudentNationalId() {
        return studentNationalId.get();
    }

    public StringProperty studentNationalIdProperty() {
        return studentNationalId;
    }

    public void setStudentNationalId(String studentNationalId) {
        this.studentNationalId.set(studentNationalId);
    }

    // Getter, Setter and Bean Property for Student Address

    public String getStudentAddress() {
        return studentAddress.get();
    }

    public StringProperty studentAddressProperty() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress.set(studentAddress);
    }

    // Getter, Setter and Bean Property for Student Joined Date

    public LocalDate getStudentJoinDate() {
        return studentJoinDate.get();
    }

    public ObjectProperty<LocalDate> studentJoinDateProperty() {
        return studentJoinDate;
    }

    public void setStudentJoinDate(LocalDate studentJoinDate) {
        this.studentJoinDate.set(studentJoinDate);
    }

    // Getter, Setter and Bean Property for Student Phone Number

    public String getStudentPhoneNumber() {
        return studentPhoneNumber.get();
    }

    public StringProperty studentPhoneNumberProperty() {
        return studentPhoneNumber;
    }

    public void setStudentPhoneNumber(String studentPhoneNumber) {
        this.studentPhoneNumber.set(studentPhoneNumber);
    }

    // Getter, Setter and Bean Property for Student Join Date Formatted ("MMMM dd, yyyy")

    public void setStudentJoinDateFormatted() {
        this.studentJoinDateFormatted.set(getStudentJoinDate().format(dateTimeFormatter));
    }

    public String getStudentJoinDateFormatted() {
        return studentJoinDateFormatted.get();
    }

    public StringProperty studentJoinDateFormattedProperty() {
        return studentJoinDateFormatted;
    }

    public ObservableList<String> getEnrolledCourseIdList() {
        return enrolledCourseIdList.get();
    }

    public ListProperty<String> enrolledCourseIdListProperty() {
        return enrolledCourseIdList;
    }

    public String getGradesString() {
        List<String> courses = GradeData.getInstance().getGrades().stream().filter
                (c -> getEnrolledCourseIdList().contains(c.getGradeId())).map(Grade::getGradeName).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < courses.size(); i++) {
            sb.append(courses.get(i));
            if (i < (courses.size() - 1)) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public double getStudentMonthly() {
        return GradeData.getInstance().getGrades().stream().filter
                (c -> getEnrolledCourseIdList().contains(c.getGradeId())).mapToDouble(Grade::getGradeFee).sum();
    }

    @Override
    public String toString() {
        return String.format("%s : %s", studentIndex.get(), studentFullName.get());
    }

    @Override
    public void add() throws SQLException, IOException {
        StudentData.getInstance().addStudent(this);
    }

    @Override
    public void set() throws SQLException, IOException {
        StudentData.getInstance().updateStudent(this);
    }

    @Override
    public void delete() throws SQLException, IOException {
        StudentData.getInstance().deleteStudent(this);
    }
}
