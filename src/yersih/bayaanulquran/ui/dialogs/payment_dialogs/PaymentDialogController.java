package yersih.bayaanulquran.ui.dialogs.payment_dialogs;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import yersih.bayaanulquran.common.Payment;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.PaymentData;
import yersih.bayaanulquran.db.StudentData;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDialogController extends Dialog<ButtonType> {


    @FXML private ComboBox<Student> comboStudents;
    @FXML private TextField tfPaymentAmount;
    @FXML private DatePicker dpPaymentDate;
    @FXML private Spinner<Integer> hourSpinner,minuteSpinner;
    @FXML private Spinner<String> amPmSpinner;
    @FXML private TextField searchStudentField;

    private FilteredList<Student> studentFilteredList = new FilteredList<>(StudentData.getInstance().getStudents());

    private Payment payment;


    @FXML
    private void initialize() {

        comboStudents.setItems(studentFilteredList);
        dpPaymentDate.setValue(LocalDate.now());

        LocalDateTime time = LocalDateTime.now();
        amPmSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>
                (FXCollections.observableArrayList("AM","PM")));
        if (time.getHour() >= 12 ) {
            amPmSpinner.increment();
            hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,12,time.getHour() - 12));
        } else {
            hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,12,time.getHour()));
        }

        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,time.getMinute()));

        comboStudents.valueProperty().addListener(obs -> {
            String text = searchStudentField.getText();
            if (text.trim().isEmpty()) {
                studentFilteredList.setPredicate(s -> true);
            } else {
                studentFilteredList.setPredicate(s -> s.getStudentIndex().toLowerCase().contains(text.toLowerCase()) ||
                        s.getStudentFullName().toLowerCase().contains(text.toLowerCase()));
                comboStudents.getSelectionModel().selectFirst();
            }
        });
    }


    public void onDialogResult() {

        Student student = comboStudents.getValue();
        double paymentAmount = Double.parseDouble(tfPaymentAmount.getText().trim());


        int hour = hourSpinner.getValue();
        if (amPmSpinner.getValue().equals("PM") && hourSpinner.getValue() != 12) {
            hour = hour +12;
        } else if (amPmSpinner.getValue().equals("AM") && hour == 12) {
            hour = 0;
        }
        LocalDateTime paymentDate = dpPaymentDate.getValue().atTime(hour,minuteSpinner.getValue());


        if (payment == null) {
            payment = new Payment(student.getStudentIndex(), paymentAmount, paymentDate);
            PaymentData.getInstance().addPayment(payment);

        } else {
            payment.setPaymentAmount(paymentAmount);
            payment.setPaymentDateTime(paymentDate);
            PaymentData.getInstance().updatePayment(payment);

        }
    }

    public void setUpdatePayment(Payment payment) {

        for (Student student : comboStudents.getItems()) {
            if (payment.getPaymentStudentIndex().equals(student.getStudentIndex())) {
                comboStudents.getSelectionModel().select(student);
            }
        }
        comboStudents.setDisable(true);

        this.payment = payment;
        tfPaymentAmount.setText(String.valueOf(payment.getPaymentAmount()));
        dpPaymentDate.setValue(payment.getPaymentDateTime().toLocalDate());
    }

    public void setStudent(Student student) {
        comboStudents.getSelectionModel().select(student);
    }


}
