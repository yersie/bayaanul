package yersih.bayaanul.db;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanul.common.Attendance;
import yersih.bayaanul.common.Enrollment;
import yersih.bayaanul.common.GradeClass;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static yersih.bayaanul.db.Database.connect;

public class AttendanceData {

    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String COLUMN_ATTENDANCE_COURSE_ID = "attendance_course";
    private static final String COLUMN_ATTENDANCE_STUDENT_INDEX = "attendance_student";
    private static final String COLUMN_ATTENDANCE_LESSON = "attendance_lesson";
    private static final String COLUMN_ATTENDANCE_PRESENCE = "attendance_presence";
    private static final String COLUMN_ATTENDANCE_DATETIME = "attendance_datetime";

    private static AttendanceData instance = new AttendanceData();


    public static AttendanceData getInstance(){
        return instance;
    }

    private ListProperty<GradeClass> classes = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Attendance> attendances = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObservableList<GradeClass> getClasses() {
        return classes;
    }

    public ListProperty<GradeClass> classesProperty() {
        return classes;
    }

    public void setClasses(ObservableList<GradeClass> classesArray) {
        classes.set(classes);
    }

    public ObservableList<Attendance> getAttendances() {
        return attendances.get();
    }

    public ListProperty<Attendance> attendancesProperty() {
        return attendances;
    }

    public void setAttendances(ObservableList<Attendance> attendanceList) {
        attendances.set(attendanceList);
    }

    private AttendanceData() {

        try (Statement statement = connect().createStatement()) {

            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                            "%s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s TEXT NOT NULL, %s TIMESTAMP NOT NULL, " +
                            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE," +
                            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE)",
                    TABLE_ATTENDANCE, COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_STUDENT_INDEX, COLUMN_ATTENDANCE_LESSON,
                    COLUMN_ATTENDANCE_PRESENCE, COLUMN_ATTENDANCE_DATETIME,
                    COLUMN_ATTENDANCE_COURSE_ID, GradeData.TABLE_GRADE, GradeData.COLUMN_GRADE_ID,
                    COLUMN_ATTENDANCE_STUDENT_INDEX, StudentData.TABLE_STUDENTS, StudentData.COLUMN_STUDENT_INDEX));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addClassAttendance(List<Attendance> attendanceArray) throws SQLException {

        for (Attendance attendance : attendanceArray) {
            addClassAttendance(attendance);
        }

        if (classes.stream().noneMatch(c -> c.getCourseId().equals(attendanceArray.get(0).getCourseId()) &&
                c.getClassLesson().equals(attendanceArray.get(0).getClassLesson()) &&
                c.getClassDateTime().isEqual(attendanceArray.get(0).getClassDateTime()))) {
            classes.add(new GradeClass
                    (attendanceArray.get(0).getCourseId(), attendanceArray.get(0).getClassLesson(), attendanceArray.get(0).getClassDateTime()));
        }

    }

    private void addClassAttendance(Attendance attendance) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                TABLE_ATTENDANCE, COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_STUDENT_INDEX, COLUMN_ATTENDANCE_LESSON,
                COLUMN_ATTENDANCE_PRESENCE, COLUMN_ATTENDANCE_DATETIME);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {

            preparedStatement.setString(1, attendance.getCourseId());
            preparedStatement.setString(2, attendance.getStudentIndex());
            preparedStatement.setString(3, attendance.getClassLesson());
            preparedStatement.setBoolean(4, attendance.isPresent());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(attendance.getClassDateTime()));

            preparedStatement.executeUpdate();
            attendances.add(attendance);
        }
    }

    public void updateClassAttendance(GradeClass oldClass, List<Attendance> attendanceArray) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ? AND %s =? AND %s = ? AND %s = ?",
                //UPDATE VALUES
                TABLE_ATTENDANCE, COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_DATETIME, COLUMN_ATTENDANCE_LESSON,
                COLUMN_ATTENDANCE_PRESENCE,
                //UPDATE FIELDS
                COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_STUDENT_INDEX, COLUMN_ATTENDANCE_LESSON, COLUMN_ATTENDANCE_DATETIME);

        for (Attendance attendance : attendanceArray) {
            try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {

                preparedStatement.setString(1, attendance.getCourseId());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(attendance.getClassDateTime()));
                preparedStatement.setString(3, attendance.getClassLesson());
                preparedStatement.setBoolean(4, attendance.isPresent());

                preparedStatement.setString(5, oldClass.getCourseId());
                preparedStatement.setString(6, attendance.getStudentIndex());
                preparedStatement.setString(7, oldClass.getClassLesson());
                preparedStatement.setTimestamp(8, Timestamp.valueOf(oldClass.getClassDateTime()));

                preparedStatement.executeUpdate();
            }
        }
        oldClass.setCourseId(attendanceArray.get(0).getCourseId());
        oldClass.setClassLesson(attendanceArray.get(0).getClassLesson());
        oldClass.setClassDateTime(attendanceArray.get(0).getClassDateTime());
    }

    public void deleteAllClassAttendances(GradeClass gradeClass) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                TABLE_ATTENDANCE, COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_LESSON, COLUMN_ATTENDANCE_DATETIME);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
            preparedStatement.setString(1, gradeClass.getCourseId());
            preparedStatement.setString(2, gradeClass.getClassLesson());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(gradeClass.getClassDateTime()));
            preparedStatement.executeUpdate();

            classes.removeIf(c -> c.getCourseId().equals(gradeClass.getCourseId()) &&
                    c.getClassLesson().equals(gradeClass.getClassLesson()) &&
                    c.getClassDateTime().isEqual(gradeClass.getClassDateTime()));

            attendances.removeIf(a -> gradeClass.getCourseId().equals(a.getCourseId()) &&
                    gradeClass.getClassDateTime().isEqual(a.getClassDateTime()) &&
                    gradeClass.getClassLesson().equals(a.getClassLesson()));

        }
    }

    void deleteStudentAttendances(String studentIndex) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                TABLE_ATTENDANCE,COLUMN_ATTENDANCE_STUDENT_INDEX);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
            preparedStatement.setString(1, studentIndex);
            preparedStatement.executeUpdate();

            classes.removeIf(removeClassesIfNoAttendance());

            attendances.removeIf(a -> a.getStudentIndex().equals(studentIndex));

        }
    }

    void deleteCourseAttendances(String courseId) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",TABLE_ATTENDANCE,
                COLUMN_ATTENDANCE_COURSE_ID);

        try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
            preparedStatement.setString(1, courseId);
            preparedStatement.executeUpdate();

            classes.removeIf(removeClassesIfNoAttendance());

            attendances.removeIf(a -> a.getCourseId().equals(courseId));

        }
    }

    private Predicate<GradeClass> removeClassesIfNoAttendance() {
        return c -> attendances.stream().noneMatch(a -> c.getClassDateTime().equals(a.getClassDateTime()) &&
                c.getCourseId().equals(a.getCourseId()) && c.getClassLesson().equals(a.getClassLesson()));
    }



    public void loadAttendances() throws SQLException {
        String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s",
                COLUMN_ATTENDANCE_COURSE_ID, COLUMN_ATTENDANCE_STUDENT_INDEX, COLUMN_ATTENDANCE_LESSON,
                COLUMN_ATTENDANCE_PRESENCE, COLUMN_ATTENDANCE_DATETIME, TABLE_ATTENDANCE);

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String courseId = resultSet.getString(COLUMN_ATTENDANCE_COURSE_ID);
                String studentIndex = resultSet.getString(COLUMN_ATTENDANCE_STUDENT_INDEX);
                String lesson = resultSet.getString(COLUMN_ATTENDANCE_LESSON);
                boolean presence = resultSet.getBoolean(COLUMN_ATTENDANCE_PRESENCE);
                LocalDateTime dateTime = resultSet.getTimestamp(COLUMN_ATTENDANCE_DATETIME).toLocalDateTime();

                attendances.add(new Attendance(courseId, studentIndex, lesson, dateTime, presence));
                if (classes.stream().noneMatch(
                        courseClass -> courseClass.getClassDateTime().equals(dateTime) &&
                                courseClass.getCourseId().equals(courseId) && courseClass.getClassLesson().equals(lesson))) {

                    classes.add(new GradeClass(courseId, lesson, dateTime));
                }
            }
        }

    }

    void addAttendanceForNewEnrollment(Enrollment enrollment) throws SQLException {
        for (GradeClass gradeClass : classes) {
            if (gradeClass.getCourseId().equals(enrollment.getCourseId())) {
                if (attendances.stream().noneMatch(a -> a.getCourseId().equals(gradeClass.getCourseId()) &&
                        a.getClassDateTime().equals(gradeClass.getClassDateTime()) &&
                        a.getClassLesson().equals(gradeClass.getClassLesson()) &&
                        a.getStudentIndex().equals(enrollment.getStudentIndex()))) {
                    addClassAttendance(new Attendance(gradeClass.getCourseId(),
                            enrollment.getStudentIndex(), gradeClass.getClassLesson(),
                            gradeClass.getClassDateTime(), false));
                }
            }
        }
    }


}
