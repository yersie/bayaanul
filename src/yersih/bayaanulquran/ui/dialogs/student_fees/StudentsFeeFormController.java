package yersih.bayaanulquran.ui.dialogs.student_fees;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import yersih.bayaanulquran.common.Student;

import java.time.LocalDate;

public class StudentsFeeFormController {

    @FXML
    private TableView<StudentRow> feeTable;




    public class StudentRow extends Student {


        public StudentRow(String sIndex, String fullName, String nationalId, String address, String phone, LocalDate joinDate) {
            super(sIndex, fullName, nationalId, address, phone, joinDate);
        }
    }
}
