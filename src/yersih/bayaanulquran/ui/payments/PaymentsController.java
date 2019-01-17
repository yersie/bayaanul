package yersih.bayaanulquran.ui.payments;


import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import yersih.bayaanulquran.common.Payment;
import yersih.bayaanulquran.db.PaymentData;
import yersih.bayaanulquran.db.StudentData;
import yersih.bayaanulquran.pdf.FeeCardsPdf;
import yersih.bayaanulquran.ui.dialogs.exception_dialog.ExceptionDialog;
import yersih.bayaanulquran.ui.dialogs.payment_dialogs.PaymentDialog;

import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Predicate;

public class PaymentsController {

    @FXML
    public TableView<Payment> paymentsTable;
    @FXML
    public TableColumn<Payment, String> paymentStudentNameColumn;
    @FXML
    private TextField searchPaymentField;
    @FXML
    private Button feeCardsButton;

    private FilteredList<Payment> paymentsFilteredList = new
            FilteredList<>(PaymentData.getInstance().getPayments(), p -> true);
    private SortedList<Payment> paymentSortedList = new
            SortedList<>(paymentsFilteredList, Comparator.comparing(Payment::getPaymentDateTime).reversed());

    private ListProperty<Payment> payments = new SimpleListProperty<>(paymentSortedList);


    @FXML
    private void initialize() {

        paymentsTable.itemsProperty().bind(payments);

        searchPaymentField.textProperty().addListener(obs -> paymentsFilteredList.setPredicate(searchPaymentPredicate()));

        paymentStudentNameColumn.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(StudentData.getInstance().getStudents().stream().filter
                            (s -> s.getStudentIndex().equals(item)).findFirst().get().getStudentFullName());
                }
            }
        });

        paymentStudentNameColumn.setCellValueFactory(c -> c.getValue().paymentStudentIndexProperty());

        feeCardsButton.disableProperty().bind(StudentData.getInstance().studentsProperty().emptyProperty());

    }


    @FXML
    public void addPayment() {
        try {
            new PaymentDialog(paymentsTable.getScene()).addPayment();
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }

    }

    @FXML
    public void EditPayment() {
        try {
            new PaymentDialog(paymentsTable.getScene()).updatePayment(paymentsTable.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }

    }

    @FXML
    public void deletePaymentAlert() {
        try {
            PaymentDialog.deletePaymentAlert(paymentsTable.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog().showExceptionDialog(e);
        }
    }

    @FXML
    public void generateFeeCards() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sheet to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fileChooser.setInitialFileName("Fee_Cards.pdf");
        File result = fileChooser.showSaveDialog(paymentsTable.getScene().getWindow());

        if (result != null) {
            try {
                new FeeCardsPdf(String.valueOf(LocalDate.now().getYear())).generatePdf(result);
            } catch (Exception e) {
                e.printStackTrace();
                new ExceptionDialog().showExceptionDialog(e);
            }
        }
    }

    public Predicate<Payment> searchPaymentPredicate() {
        return p -> searchPaymentField.getText().trim().isEmpty() ||
                p.getPaymentStudentIndex().contains(searchPaymentField.getText().toLowerCase()) ||
                StudentData.getInstance().getStudents().stream().filter
                        (s -> s.getStudentIndex().equals
                                (p.getPaymentStudentIndex())).findFirst().get().getStudentFullName().contains
                        (searchPaymentField.getText().toLowerCase());
    }

}
