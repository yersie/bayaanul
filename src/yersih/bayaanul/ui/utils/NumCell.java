package yersih.bayaanul.ui.utils;

import javafx.scene.control.TableCell;

public class NumCell<S,T> extends TableCell<S,T> {
    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);
        if (isEmpty() || i < 0) {
            setText(null);
        } else {
            setText(Integer.toString(i+1));
        }
    }
}
