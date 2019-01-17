package yersih.bayaanulquran.db;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanulquran.common.Grade;

import java.sql.*;
import java.time.LocalDate;

import static yersih.bayaanulquran.db.Database.connect;

public class GradeData {

    public static final String TABLE_GRADE = "courses";
    public static final String COLUMN_GRADE_ID = "course_id";
    public static final String COLUMN_GRADE_NAME = "course_name";
    public static final String COLUMN_GRADE_FEE = "course_fee";
    public static final String COLUMN_GRADE_START_DATE = "course_start_date";
    public static final String COLUMN_GRADE_CLASSTIME = "course_end_date";

    private static final GradeData instance = new GradeData();

    public static GradeData getInstance() {
        return instance;
    }

    private final ListProperty<Grade> grades = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObservableList<Grade> getGrades() {
        return grades.get();
    }

    public ListProperty<Grade> gradesProperty() {
        return grades;
    }

    public void setGrades(ObservableList<Grade> gradeObservableList) {
        grades.set(grades);
    }



    private GradeData() {
        try (Statement statement = Database.connect().createStatement()) {
            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s TEXT UNIQUE, %s REAL NOT NULL, %s TIMESTAMP NOT NULL, %s TEXT NOT NULL)",
                    TABLE_GRADE, COLUMN_GRADE_ID, COLUMN_GRADE_NAME, COLUMN_GRADE_FEE,
                    COLUMN_GRADE_START_DATE, COLUMN_GRADE_CLASSTIME));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGrade(Grade grade) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?)",
                TABLE_GRADE, COLUMN_GRADE_ID, COLUMN_GRADE_NAME,
                COLUMN_GRADE_FEE, COLUMN_GRADE_START_DATE, COLUMN_GRADE_CLASSTIME);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, grade.getGradeId());
            preparedStatement.setString(2, grade.getGradeName());
            preparedStatement.setDouble(3, grade.getGradeFee());
            preparedStatement.setDate(4, Date.valueOf(grade.getGradeStartDate()));
            preparedStatement.setString(5, grade.getClassTime());
            preparedStatement.executeUpdate();
            grades.add(setGradeId(grade));
        }
    }

    private Grade setGradeId(Grade grade) throws SQLException {
        String sql = String.format("SELECT %s FROM %s WHERE %s = ?",COLUMN_GRADE_ID,TABLE_GRADE,COLUMN_GRADE_NAME);
        try(PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1,grade.getGradeName());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            grade.setGradeId(resultSet.getString(COLUMN_GRADE_ID));
            return grade;
        }
    }


    public void updateGrade(Grade grade) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE_GRADE, COLUMN_GRADE_NAME, COLUMN_GRADE_FEE, COLUMN_GRADE_START_DATE,
                COLUMN_GRADE_CLASSTIME, COLUMN_GRADE_ID);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setString(1, grade.getGradeName());
            preparedStatement.setDouble(2, grade.getGradeFee());
            preparedStatement.setDate(3, Date.valueOf(grade.getGradeStartDate()));
            preparedStatement.setString(4, grade.getClassTime());
            preparedStatement.setString(5, grade.getGradeId());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteGrade(Grade grade) throws SQLException {

        String studentSql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_GRADE, COLUMN_GRADE_ID);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(studentSql)) {
            preparedStatement.setString(1, grade.getGradeId());
            preparedStatement.executeUpdate();
            EnrollmentData.getInstance().deleteCourseEnrollments(grade.getGradeId());
            AttendanceData.getInstance().deleteCourseAttendances(grade.getGradeId());
            grades.removeIf(c -> c.getGradeId().equals(grade.getGradeId()));
            }
        }

    public void loadGrades() throws SQLException {
        String sql = String.format("SELECT %s, %s, %s, %s, %s  FROM %s",
                COLUMN_GRADE_ID, COLUMN_GRADE_NAME, COLUMN_GRADE_FEE, COLUMN_GRADE_START_DATE, COLUMN_GRADE_CLASSTIME, TABLE_GRADE);

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String courseId = resultSet.getString(COLUMN_GRADE_ID);
                String courseName = resultSet.getString(COLUMN_GRADE_NAME);
                double courseFee = resultSet.getDouble(COLUMN_GRADE_FEE);
                LocalDate courseStartDate = resultSet.getDate(COLUMN_GRADE_START_DATE).toLocalDate();
                String classTime = resultSet.getString(COLUMN_GRADE_CLASSTIME);

                grades.add(new Grade(courseId, courseName, courseFee, courseStartDate,classTime));
            }
        }
    }
}
