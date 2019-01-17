package yersih.bayaanulquran.ui.utils;

import javafx.scene.control.TableCell;

public class AttendanceCell<S,T> extends TableCell<S,T> {

    private final static String redText = "-fx-text-fill: ff3333;";
    private final static String modenaText = "-fx-text-fill: -fx-text-background-color;";


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? null : (String) item);
        setStyle(!empty && item.equals("Absent") ? redText : modenaText);
    }

}
