package yersih.bayaanulquran.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import yersih.bayaanulquran.db.AttendanceData;
import yersih.bayaanulquran.db.EnrollmentData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GradeClass {

    private final StringProperty courseId = new SimpleStringProperty();
    private final StringProperty classLesson = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> classDateTime = new SimpleObjectProperty<>();

    private final IntegerProperty numPresent = new SimpleIntegerProperty();
    private final IntegerProperty numAbsent = new SimpleIntegerProperty();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private final StringProperty classDateFormatted = new SimpleStringProperty();
    private final StringProperty classTimeFormatted = new SimpleStringProperty();


    public GradeClass() {
    }

    public GradeClass(String course, String lesson, LocalDateTime datetime) {
        courseId.set(course);
        classLesson.set(lesson);
        classDateTime.set(datetime);
        setClassDateFormatted();
        setClassTimeFormatted();

        classDateFormatted.bind(Bindings.createStringBinding(
                () -> classDateTime.get().format(dateFormatter),classDateTime));

        classTimeFormatted.bind(Bindings.createStringBinding(
                () -> classDateTime.get().format(timeFormatter),classDateTime));

        setNumPresent();
        setNumAbsent();
    }

    public void setCourseClass(String course, String lesson, LocalDateTime dateTime) {
        courseId.set(course);
        classLesson.set(lesson);
        classDateTime.set(dateTime);

        classDateFormatted.bind(Bindings.createStringBinding(
                () -> classDateTime.get().format(dateFormatter),classDateTime));

        classTimeFormatted.bind(Bindings.createStringBinding(
                () -> classDateTime.get().format(timeFormatter),classDateTime));

        setNumPresent();
        setNumAbsent();
    }

    public String getClassLesson() {
        return classLesson.get();
    }

    public StringProperty classLessonProperty() {
        return classLesson;
    }

    public void setClassLesson(String classLesson) {
        this.classLesson.set(classLesson);
    }

    public LocalDateTime getClassDateTime() {
        return classDateTime.get();
    }

    public ObjectProperty<LocalDateTime> classDateTimeProperty() {
        return classDateTime;
    }

    public void setClassDateTime(LocalDateTime classDate) {
        this.classDateTime.set(classDate);
    }

    public String getClassDateFormatted() {
        return classDateFormatted.get();
    }

    public String getCourseId() {
        return courseId.get();
    }

    public StringProperty courseIdProperty() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId.set(courseId);
    }

    public StringProperty classDateFormattedProperty() {
        return classDateFormatted;
    }

    public void setClassDateFormatted() {
        this.classDateFormatted.set(getClassDateTime().format(dateFormatter));
    }

    public String getClassTimeFormatted() {
        return classTimeFormatted.get();
    }

    public StringProperty classTimeFormattedProperty() {
        return classTimeFormatted;
    }

    public void setClassTimeFormatted() {
        this.classTimeFormatted.set(getClassDateTime().format(timeFormatter));
    }

    public int getNumPresent() {
        return numPresent.get();
    }

    public IntegerProperty numPresentProperty() {
        return numPresent;
    }

    public void setNumPresent() {
        numPresent.bind(Bindings.createIntegerBinding(() -> AttendanceData.getInstance().getAttendances().filtered
                        (a -> a.isEqual(this) &&
                                a.isPresent()).size(),
                AttendanceData.getInstance().attendancesProperty()));
    }

    public int getNumAbsent() {
        return numAbsent.get();
    }

    public IntegerProperty numAbsentProperty() {
        return numAbsent;
    }

    public void setNumAbsent() {
        numAbsent.bind(Bindings.createIntegerBinding(() -> EnrollmentData.getInstance().getEnrollments().filtered
                (e -> e.getCourseId().equals(this.getCourseId())).size(), AttendanceData.getInstance().attendancesProperty()).subtract(numPresent));
    }


    public boolean isEqual(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GradeClass) {
            GradeClass aGradeClass = (GradeClass) obj;
            return aGradeClass.getCourseId().equals(this.getCourseId()) && aGradeClass.getClassLesson().equals
                    (this.getClassLesson()) && aGradeClass.getClassDateTime().isEqual(this.getClassDateTime());
        }
        return false;
    }
}
