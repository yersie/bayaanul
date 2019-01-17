package yersih.bayaanulquran.db;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

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
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/bayaanulquran").build();
        clientV2 = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public void uploadDatabase() throws IOException, DbxException {
        try (InputStream in = new FileInputStream("data/database.db")) {
            clientV2.files().uploadBuilder(
                    "/data/" + System.getenv().get("USERNAME") +
                    "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("Y_M_D")) +
                    "/" + LocalDateTime.now().format(dateTimeFormatter) + "/database.db"
            ).uploadAndFinish(in);
        }
    }


}
