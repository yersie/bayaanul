package yersih.bayaanulquran.ui.dialogs.exception_dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;

import java.io.IOException;

public class ExceptionDialog extends Alert {

    private ExceptionDialogController exceptionDialogController;

    public ExceptionDialog() {
        super(AlertType.ERROR);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("exception_dialog.fxml"));

        try {
            getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        exceptionDialogController = fxmlLoader.getController();
    }

    public void showExceptionDialog(Exception e) {
        setTitle("Error");
        exceptionDialogController.setExceptionText(e);
        showAndWait();
    }
}
