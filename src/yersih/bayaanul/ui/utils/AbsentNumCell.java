package yersih.bayaanul.ui.utils;

import javafx.scene.control.TableCell;
import yersih.bayaanul.common.GradeClass;

public class AbsentNumCell extends TableCell<GradeClass,Integer> {

    private final static String redText = "-fx-text-fill: ff3333;";
    private final static String modenaText = "-fx-text-fill: -fx-text-background-color;";

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(Integer.toString(item));
            if (item != 0) {
                setStyle(redText);
            } else {
                setStyle(modenaText);
            }
        }
    }
}
