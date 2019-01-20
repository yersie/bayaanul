package yersih.bayaanul.db;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import yersih.bayaanul.common.Enrollment;
import yersih.bayaanul.common.Grade;
import yersih.bayaanul.common.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static yersih.bayaanul.db.Database.connect;

public class EnrollmentData {

    private static final String TABLE_ENROLLMENT = "course_enrollment";
    private static final String COLUMN_ENROLLMENT_COURSE_ID = "enrollment_course";
    private static final String COLUMN_ENROLLMENT_STUDENT_INDEX = "enrollment_student";

    private static final EnrollmentData instance = new EnrollmentData();

    private static final ListProperty<Enrollment> enrollments = new SimpleListProperty<>(FXCollections.observableArrayList());

    public static EnrollmentData getInstance() {
        return instance;
    }

    public ObservableList<Enrollment> getEnrollments() {
        return enrollments.get();
    }

    public ListProperty<Enrollment> enrollmentsProperty() {
        return enrollments;
    }

    private EnrollmentData() {
        try (Statement statement = Database.connect().createStatement()) {
            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT NOT NULL, %s TEXT NOT NULL, " +
                            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE, " +
                            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE)",
                    TABLE_ENROLLMENT, COLUMN_ENROLLMENT_COURSE_ID, COLUMN_ENROLLMENT_STUDENT_INDEX,
                    COLUMN_ENROLLMENT_COURSE_ID, GradeData.TABLE_GRADE, GradeData.COLUMN_GRADE_NAME,
                    COLUMN_ENROLLMENT_STUDENT_INDEX, StudentData.TABLE_STUDENTS, StudentData.COLUMN_STUDENT_INDEX));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addEnrollment(Enrollment enrollment) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s,%s) VALUES (?,?)",
                TABLE_ENROLLMENT, COLUMN_ENROLLMENT_COURSE_ID, COLUMN_ENROLLMENT_STUDENT_INDEX);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, enrollment.getCourseId());
            preparedStatement.setString(2, enrollment.getStudentIndex());
            preparedStatement.executeUpdate();
            enrollments.add(enrollment);
            AttendanceData.getInstance().addAttendanceForNewEnrollment(enrollment);
        }
    }

    public void deleteEnrollment(Enrollment enrollment) throws SQLException {

        String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                TABLE_ENROLLMENT, COLUMN_ENROLLMENT_COURSE_ID, COLUMN_ENROLLMENT_STUDENT_INDEX);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, enrollment.getCourseId());
            preparedStatement.setString(2, enrollment.getStudentIndex());
            preparedStatement.executeUpdate();
            enrollments.removeIf(e -> enrollment.getCourseId().equals(e.getCourseId()) &&
                    enrollment.getStudentIndex().equals(e.getStudentIndex()));
        }
    }

    void deleteCourseEnrollments(String courseId) throws SQLException {

        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                TABLE_ENROLLMENT,COLUMN_ENROLLMENT_COURSE_ID);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, courseId);
            preparedStatement.executeUpdate();
            enrollments.removeIf(e -> e.getCourseId().equals(courseId));
        }
    }

    void deleteStudentEnrollments(String studentIndex) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                TABLE_ENROLLMENT,COLUMN_ENROLLMENT_STUDENT_INDEX);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, studentIndex);
            preparedStatement.executeUpdate();
            enrollments.removeIf(e -> e.getCourseId().equals(studentIndex));
        }
    }




    public FilteredList<Student> getCourseEnrolledStudents(Grade grade) {
        return new FilteredList<>(StudentData.getInstance().getStudents(),
                student -> getEnrollments().stream().anyMatch(
                        enrollment -> enrollment.getStudentIndex().equals(student.getStudentIndex()) &&
                                enrollment.getCourseId().equals(grade.getGradeId())));
    }

    public void loadEnrollments() {
        String sql = String.format("SELECT %s, %s FROM %s",
                COLUMN_ENROLLMENT_STUDENT_INDEX, COLUMN_ENROLLMENT_COURSE_ID, TABLE_ENROLLMENT);

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String courseId = resultSet.getString(COLUMN_ENROLLMENT_COURSE_ID);
                String studentIndex = resultSet.getString(COLUMN_ENROLLMENT_STUDENT_INDEX);

                enrollments.add(new Enrollment(courseId, studentIndex));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
