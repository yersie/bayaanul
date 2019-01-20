package yersih.bayaanul.ui.dialogs.payment_dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import yersih.bayaanul.common.Payment;
import yersih.bayaanul.common.Student;
import yersih.bayaanul.db.PaymentData;

import java.io.IOException;
import java.util.Optional;

public class PaymentDialog extends Dialog<ButtonType> {

    private PaymentDialogController paymentDialogController;

    public PaymentDialog(Scene root) {

        initOwner(root.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("payment_dialog.fxml"));

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        paymentDialogController = fxmlLoader.getController();

    }

    public void addPayment() {
        setTitle("Add Payment Details");
        Optional<ButtonType> result = showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            paymentDialogController.onDialogResult();
        }
    }

    public void addPayment(Student student) {
        if (student != null) {
            paymentDialogController.setStudent(student);
        }
        addPayment();
    }


    public void updatePayment(Payment payment) {
        if (payment != null) {
            setTitle("Edit Payment Details");
            paymentDialogController.setUpdatePayment(payment);
            Optional<ButtonType> result = showAndWait();
            if (result.isPresent() && (result.get() == ButtonType.OK)) {
                paymentDialogController.onDialogResult();
            }
        }
    }

    public static void deletePaymentAlert(Payment p) {
        if (p != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Payment Record");
            alert.setHeaderText("Delete payment " + p.getPaymentAmount() + " RF");
            alert.setContentText("Are you sure you want to delete this payment?");
            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            yesButton.setDefaultButton(false);
            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            noButton.setDefaultButton(true);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PaymentData.getInstance().deletePayment(p);
            }
        }
    }

}
