package yersih.bayaanul.db;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DropToBox {


    private static final String ACCESS_TOKEN = "urFESRJqgHAAAAAAAAAAFbsFvhJGgn81-Bp5XGkyylhemj_Idu1noSc-mWJIGT7X";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("H_m_s");

    private DbxClientV2 clientV2;

    public DropToBox() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/bayaanul").build();
        clientV2 = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public void uploadDatabase(Button button) {

        button.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        Runnable upload = () -> {
            alert.setTitle("Save Database Alert");
            try (InputStream in = new FileInputStream("data/database.db")) {

                String path = "/data/" + System.getenv().get("USERNAME") +
                        "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("Y_M_D")) +
                        "/" + LocalDateTime.now().format(dateTimeFormatter) + "/database.db";

                clientV2.files().uploadBuilder(path).uploadAndFinish(in);
                alert.setContentText("Database successfully saved to DropBox.");
            } catch (IOException | DbxException e) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Could not saved database to DropBox.\n" +
                        "Please check your internet connection and try again.");
            }

            Platform.runLater(() -> {
                alert.show();
                button.setDisable(false);
            });
        };

        new Thread(upload).start();
    }


}
