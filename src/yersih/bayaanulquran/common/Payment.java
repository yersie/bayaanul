package yersih.bayaanulquran.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import yersih.bayaanulquran.db.PaymentData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment implements Model {

    private final IntegerProperty paymentId = new SimpleIntegerProperty();
    private final StringProperty paymentStudentIndex = new SimpleStringProperty();
    private final DoubleProperty paymentAmount = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDateTime> paymentDateTime = new SimpleObjectProperty<>();

    private final StringProperty paymentDateFormatted = new SimpleStringProperty();
    private final StringProperty paymentTimeFormatted = new SimpleStringProperty();

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public Payment(String studentIndex, double amount, LocalDateTime datetime) {
        this.paymentStudentIndex.set(studentIndex);
        this.paymentAmount.set(amount);
        this.paymentDateTime.set(datetime);

        this.paymentDateFormatted.bind(Bindings.createStringBinding(() -> paymentDateTime.get().format
                (dateFormatter), paymentDateTime));
        this.paymentTimeFormatted.bind(Bindings.createStringBinding(() -> paymentDateTime.get().format
                (timeFormatter), paymentDateTime));

    }

    public Payment(int paymentId, String studentIndex, double amount, LocalDateTime date) {
        this(studentIndex, amount, date);
        this.paymentId.set(paymentId);
    }


    public int getPaymentId() {
        return paymentId.get();
    }

    public IntegerProperty paymentIdProperty() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId.set(paymentId);
    }

    public String getPaymentStudentIndex() {
        return paymentStudentIndex.get();
    }

    public StringProperty paymentStudentIndexProperty() {
        return paymentStudentIndex;
    }

    public void setPaymentStudentIndex(String paymentStudentIndex) {
        this.paymentStudentIndex.set(paymentStudentIndex);
    }

    public double getPaymentAmount() {
        return paymentAmount.get();
    }

    public DoubleProperty paymentAmountProperty() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount.set(paymentAmount);
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime.get();
    }

    public ObjectProperty<LocalDateTime> paymentDateTimeProperty() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(LocalDateTime paymentDate) {
        this.paymentDateTime.set(paymentDate);
    }

    public String getPaymentDateFormatted() {
        return paymentDateFormatted.get();
    }

    public StringProperty paymentDateFormattedProperty() {
        return paymentDateFormatted;
    }

    public void setPaymentDateFormatted() {
        this.paymentDateFormatted.set(paymentDateTime.get().format(dateFormatter));
    }

    public String getPaymentTimeFormatted() {
        return paymentTimeFormatted.get();
    }

    public StringProperty paymentTimeFormattedProperty() {
        return paymentTimeFormatted;
    }

    public void setPaymentTimeFormatted() {
        this.paymentTimeFormatted.set(paymentDateTime.get().format(timeFormatter));
    }

    @Override
    public void add() {
        PaymentData.getInstance().addPayment(this);
    }

    @Override
    public void set() {
        PaymentData.getInstance().updatePayment(this);
    }

    @Override
    public void delete() {
        PaymentData.getInstance().deletePayment(this);
    }
}
