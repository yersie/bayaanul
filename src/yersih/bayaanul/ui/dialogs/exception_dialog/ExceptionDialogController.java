package yersih.bayaanul.ui.dialogs.exception_dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialogController {

    @FXML
    private TextArea exceptionTextArea;

    void setExceptionText(Exception ex) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        exceptionTextArea.setText(exceptionText);
    }


}
