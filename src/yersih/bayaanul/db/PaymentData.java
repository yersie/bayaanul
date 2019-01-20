package yersih.bayaanul.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yersih.bayaanul.common.Payment;

import java.sql.*;
import java.time.LocalDateTime;

import static yersih.bayaanul.db.Database.connect;

public class PaymentData {

    private static final String TABLE_PAYMENTS = "payments";
    private static final String COLUMN_PAYMENT_ID = "payment_id";
    private static final String COLUMN_PAYMENT_STUDENT_INDEX = "payment_student_index";
    private static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
    private static final String COLUMN_PAYMENT_DATE = "payment_date";

    private static final PaymentData instance = new PaymentData();

    public static PaymentData getInstance() {
        return instance;
    }

    private static final ObservableList<Payment> payments = FXCollections.observableArrayList();

    public ObservableList<Payment> getPayments() {
        return payments;
    }

    private PaymentData() {
        try (Statement statement = Database.connect().createStatement()) {
            statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s TIMESTAMP NOT NULL, %s REAL NOT NULL, %s TEXT NOT NULL, " +
                            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE)",
                    TABLE_PAYMENTS, COLUMN_PAYMENT_ID, COLUMN_PAYMENT_DATE, COLUMN_PAYMENT_AMOUNT,
                    COLUMN_PAYMENT_STUDENT_INDEX, COLUMN_PAYMENT_STUDENT_INDEX, StudentData.TABLE_STUDENTS, StudentData.COLUMN_STUDENT_INDEX));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addPayment(Payment payment) {
        String sql = String.format("INSERT INTO %s " +
                        "(%s, %s, %s) VALUES(?, ?, ?)",
                TABLE_PAYMENTS, COLUMN_PAYMENT_DATE, COLUMN_PAYMENT_AMOUNT, COLUMN_PAYMENT_STUDENT_INDEX);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(payment.getPaymentDateTime()));
            preparedStatement.setDouble(2, payment.getPaymentAmount());
            preparedStatement.setString(3, payment.getPaymentStudentIndex());
            preparedStatement.executeUpdate();
            payments.add(payment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayment(Payment payment) {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE_PAYMENTS, COLUMN_PAYMENT_DATE, COLUMN_PAYMENT_AMOUNT,
                COLUMN_PAYMENT_STUDENT_INDEX, COLUMN_PAYMENT_ID);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(payment.getPaymentDateTime()));
            preparedStatement.setDouble(2, payment.getPaymentAmount());
            preparedStatement.setString(3, payment.getPaymentStudentIndex());
            preparedStatement.setInt(4, payment.getPaymentId());
            preparedStatement.executeUpdate();

            for (int i = 0; i < payments.size(); i++) {
                if (payments.get(i).getPaymentId() == payment.getPaymentId()) {
                    payments.set(i, payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayment(Payment payment) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_PAYMENTS, COLUMN_PAYMENT_ID);

        try (PreparedStatement preparedStatement = Database.connect().prepareStatement(sql)) {
            preparedStatement.setInt(1, payment.getPaymentId());
            preparedStatement.executeUpdate();
            payments.remove(payment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPayments() {
        String sql = String.format("SELECT %s, %s, %s, %s FROM %s",
                COLUMN_PAYMENT_ID, COLUMN_PAYMENT_STUDENT_INDEX, COLUMN_PAYMENT_AMOUNT, COLUMN_PAYMENT_DATE, TABLE_PAYMENTS);

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int paymentId = resultSet.getInt(COLUMN_PAYMENT_ID);
                String paymentStudentIndex = resultSet.getString(COLUMN_PAYMENT_STUDENT_INDEX);
                double paymentAmount = resultSet.getDouble(COLUMN_PAYMENT_AMOUNT);
                LocalDateTime paymentDate = resultSet.getTimestamp(COLUMN_PAYMENT_DATE).toLocalDateTime();

                payments.add(new Payment(paymentId, paymentStudentIndex, paymentAmount, paymentDate));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
