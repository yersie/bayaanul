package yersih.bayaanulquran.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Attendance extends GradeClass {

    private final StringProperty studentIndex = new SimpleStringProperty();
    private final BooleanProperty presence = new SimpleBooleanProperty();
    private final StringProperty presenceText = new SimpleStringProperty();
    private final StringProperty studentName = new SimpleStringProperty();

    public Attendance(String index, boolean wasPresent) {
        setStudentIndex(index);
        setPresence(wasPresent);

    }

    public Attendance(String course, String student,
                      String lessonDescription, LocalDateTime datetime, boolean presence) {
        super(course,lessonDescription,datetime);
        setStudentIndex(student);
        setPresence(presence);
    }

    public String getStudentIndex() {
        return studentIndex.get();
    }

    public StringProperty studentIndexProperty() {
        return studentIndex;
    }

    public void setStudentIndex(String studentIndex) {
        this.studentIndex.set(studentIndex);
    }

    public boolean isPresent() {
        return presence.get();
    }

    public BooleanProperty presenceProperty() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence.set(presence);
        setPresenceText();
    }

    public String getPresenceText() {
        return presenceText.get();
    }

    public StringProperty presenceTextProperty() {
        return presenceText;
    }

    private void setPresenceText() {
        presenceText.bind(Bindings.createStringBinding(() -> {
            if (presence.get()) {
                return "Present";
            } else {
                return "Absent";
            }
        },presence));

    }

    public String getStudentName() {
        return studentName.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

}
