package yersih.bayaanul.db;


import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanul.common.Student;

import java.sql.*;
import java.time.LocalDate;

import static yersih.bayaanul.db.Database.connect;

public class StudentData {

    static final String TABLE_STUDENTS = "students";
    static final String COLUMN_STUDENT_INDEX = "student_index";
    private static final String COLUMN_STUDENT_FULL_NAME = "student_full_name";
    private static final String COLUMN_STUDENT_ADDRESS = "student_address";
    private static final String COLUMN_STUDENT_NATIONAL_ID = "student_national_id";
    private static final String COLUMN_STUDENT_JOIN_DATE = "student_date_of_join";
    private static final String COLUMN_STUDENT_PHONE = "student_phone";


    private static StudentData instance;

    private final ListProperty<Student> students = new SimpleListProperty<>(FXCollections.observableArrayList());

    public final ObservableList<Student> getStudents() {
        return students.get();
    }

    public final ListProperty<Student> studentsProperty() {
        return students;
    }

    public static StudentData getInstance() {
            try {
                if (instance == null) {
                    instance = new StudentData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return instance;
    }


    private StudentData() throws SQLException {
        try (Statement statement = connect().createStatement()) {
            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY," +
                            " %s TEXT NOT NULL, %s TEXT, %s TEXT UNIQUE, %s TEXT, %s TIMESTAMP NOT NULL)",
                    TABLE_STUDENTS, COLUMN_STUDENT_INDEX, COLUMN_STUDENT_FULL_NAME, COLUMN_STUDENT_ADDRESS,
                    COLUMN_STUDENT_NATIONAL_ID, COLUMN_STUDENT_PHONE, COLUMN_STUDENT_JOIN_DATE));
        }
    }

    public void addStudent(Student student) throws SQLException {

        if (isStudentValid(student)) {
            String sql = String.format("INSERT INTO %s " +
                            "(%s, %s, %s, %s, %s, %s)" +
                            " VALUES(?, ?, ?, ?, ?, ?)",
                    TABLE_STUDENTS, COLUMN_STUDENT_INDEX, COLUMN_STUDENT_FULL_NAME, COLUMN_STUDENT_ADDRESS,
                    COLUMN_STUDENT_NATIONAL_ID, COLUMN_STUDENT_PHONE, COLUMN_STUDENT_JOIN_DATE);

            try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {
                preparedStatement.setString(1, student.getStudentIndex());
                preparedStatement.setString(2, student.getStudentFullName());
                preparedStatement.setString(3, student.getStudentAddress());
                preparedStatement.setString(4, student.getStudentNationalId());
                preparedStatement.setString(5, student.getStudentPhoneNumber());
                preparedStatement.setDate(6, Date.valueOf(student.getStudentJoinDate()));
                preparedStatement.executeUpdate();
                students.add(student);
            }
        }
    }

    public void updateStudent(Student student) throws SQLException {
        if (isStudentValid(student)) {
            String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?,  %s = ? WHERE %s = ?",
                    TABLE_STUDENTS, COLUMN_STUDENT_INDEX, COLUMN_STUDENT_FULL_NAME, COLUMN_STUDENT_ADDRESS,
                    COLUMN_STUDENT_NATIONAL_ID, COLUMN_STUDENT_JOIN_DATE, COLUMN_STUDENT_INDEX);

            try (PreparedStatement preparedStatement = connect().prepareStatement(sql)) {

                preparedStatement.setString(1, student.getStudentIndex());
                preparedStatement.setString(2, student.getStudentFullName());
                preparedStatement.setString(3, student.getStudentAddress());
                preparedStatement.setString(4, student.getStudentNationalId());
                preparedStatement.setDate(5, Date.valueOf(student.getStudentJoinDate()));
                preparedStatement.setString(6, student.getStudentIndex());
                preparedStatement.executeUpdate();

            }
        }
    }

    public void deleteStudent(Student s) throws SQLException {
        if (isStudentValid(s)) {
            String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_STUDENTS, COLUMN_STUDENT_INDEX);

            try (PreparedStatement studentPreparedStatement = connect().prepareStatement(sql)) {
                studentPreparedStatement.setString(1, s.getStudentIndex());
                studentPreparedStatement.executeUpdate();


                PaymentData.getInstance().getPayments().removeIf
                        (payment -> payment.getPaymentStudentIndex().equals(s.getStudentIndex()));

                AttendanceData.getInstance().deleteStudentAttendances(s.getStudentIndex());
                EnrollmentData.getInstance().deleteStudentEnrollments(s.getStudentIndex());

                students.remove(s);
            }
        }
    }

    public void loadStudents() throws SQLException {
        String sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s",
                COLUMN_STUDENT_INDEX, COLUMN_STUDENT_FULL_NAME, COLUMN_STUDENT_NATIONAL_ID, COLUMN_STUDENT_ADDRESS,
                COLUMN_STUDENT_PHONE, COLUMN_STUDENT_JOIN_DATE, TABLE_STUDENTS);


        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {


            while (resultSet.next()) {
                String studentIndex = resultSet.getString(COLUMN_STUDENT_INDEX);
                String studentFullName = resultSet.getString(COLUMN_STUDENT_FULL_NAME);
                String studentNationalId = resultSet.getString(COLUMN_STUDENT_NATIONAL_ID);
                String studentAddress = resultSet.getString(COLUMN_STUDENT_ADDRESS);
                LocalDate studentJoinDate = resultSet.getDate(COLUMN_STUDENT_JOIN_DATE).toLocalDate();
                String studentPhone = resultSet.getString(COLUMN_STUDENT_PHONE);

                students.add(new Student(studentIndex, studentFullName,
                        studentNationalId, studentAddress, studentPhone, studentJoinDate));
            }
        }
    }

    private boolean isStudentValid(Student student) {
        return !(student.getStudentIndex().trim().isEmpty() ||
                student.getStudentFullName().trim().isEmpty() ||
                student.getStudentNationalId().trim().isEmpty());
    }

}


