package yersih.bayaanulquran.ui.fees;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import yersih.bayaanulquran.common.Grade;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.GradeData;
import yersih.bayaanulquran.db.StudentData;
import yersih.bayaanulquran.pdf.MonthlyFeeSheetPdf;
import yersih.bayaanulquran.ui.dialogs.exception_dialog.ExceptionDialog;
import yersih.bayaanulquran.ui.utils.NumCell;

import java.io.File;


public class FeesController {

    @FXML
    private TableView<Student> feeSheetTable;
    @FXML
    private TableColumn<Student, String> studentCoursesColumn;
    @FXML
    private TableColumn<Student, Integer> studentNumberColumn;
    @FXML
    private TableColumn<Student, String> studentNameColumn;
    @FXML
    private TableColumn<Student, ObservableList> monthlyTotalColumn;
    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private void initialize() {

        monthComboBox.setItems(FXCollections.observableArrayList("January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"));

        TableColumn<Student,String> monthColumn = new TableColumn<>();

        monthComboBox.valueProperty().addListener(obs -> monthColumn.setText(monthComboBox.getValue()));

        monthComboBox.getSelectionModel().selectFirst();


        feeSheetTable.getColumns().add(monthColumn);

        feeSheetTable.itemsProperty().bind(StudentData.getInstance().studentsProperty());

        studentNumberColumn.setCellFactory(c -> new NumCell<>());

        monthlyTotalColumn.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(ObservableList item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(Double.toString(GradeData.getInstance().getGrades().filtered
                            (c -> item.contains(c.getGradeId())).stream().mapToDouble(Grade::getGradeFee).sum()));
                }
            }
        });

        studentNameColumn.setCellFactory(c -> new TableCell<>(){
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

        studentCoursesColumn.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(feeSheetTable.getItems().get(getIndex()).getGradesString());
                }
            }
        });


    }

    @FXML
    private void printSheet() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sheet to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File result = fileChooser.showSaveDialog(feeSheetTable.getScene().getWindow());

        if (result != null) {
            try {
                new MonthlyFeeSheetPdf()
                        .createPdf(StudentData.getInstance()
                                .getStudents(),result.getAbsolutePath(),monthComboBox.getValue());
            } catch (Exception e) {
                e.printStackTrace();
                new ExceptionDialog().showExceptionDialog(e);
            }
        }

    }


}
