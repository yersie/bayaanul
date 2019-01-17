package yersih.bayaanulquran.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import yersih.bayaanulquran.db.EnrollmentData;

import java.sql.SQLException;

public class Enrollment implements Model {
    private final StringProperty courseId;
    private final StringProperty studentIndex;

    public Enrollment(String course, String student) {
        this.courseId = new SimpleStringProperty(course);
        this.studentIndex = new SimpleStringProperty(student);
    }

    public String getStudentIndex() {
        return studentIndex.get();
    }

    public StringProperty studentProperty() {
        return studentIndex;
    }

    public void setStudentIndex(String student) {
        this.studentIndex.set(student);
    }

    public String getCourseId() {
        return courseId.get();
    }

    public StringProperty courseProperty() {
        return courseId;
    }

    public void setCourseId(String course) {
        this.courseId.set(course);
    }

    @Override
    public void add() throws SQLException {
        EnrollmentData.getInstance().addEnrollment(this);
    }

    @Override
    public void set() {

    }

    @Override
    public void delete() throws SQLException {
        EnrollmentData.getInstance().deleteEnrollment(this);

    }
}