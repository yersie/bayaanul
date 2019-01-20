package yersih.bayaanul.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Grade {


    private final StringProperty gradeId = new SimpleStringProperty();
    private final StringProperty gradeName = new SimpleStringProperty();
    private final DoubleProperty gradeFee = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> gradeStartDate = new SimpleObjectProperty<>();
    private final SimpleStringProperty classTime = new SimpleStringProperty();

    private final StringProperty startDateFormatted = new SimpleStringProperty();
    private final StringProperty classTimeFormatted = new SimpleStringProperty();



    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public Grade(String gName, double gFee, LocalDate gStartDate,String classTime) {
        this.gradeName.set(gName);
        this.gradeFee.set(gFee);
        this.gradeStartDate.set(gStartDate);
        this.classTime.set(classTime);

        startDateFormatted.bind(Bindings.createStringBinding(() -> getGradeStartDate().format(dateFormatter),
                gradeStartDate));
    }

    public Grade(String gId, String gName, double gFee, LocalDate gStartDate,String classTime) {
        this(gName,gFee,gStartDate, classTime);
        this.gradeId.set(gId);
    }

    public String getGradeId() {
        return gradeId.get();
    }

    public StringProperty gradeIdProperty() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId.set(gradeId);
    }

    public String getGradeName() {
        return gradeName.get();
    }

    public StringProperty gradeNameProperty() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName.set(gradeName);
    }

    public double getGradeFee() {
        return gradeFee.get();
    }

    public DoubleProperty gradeFeeProperty() {
        return gradeFee;
    }

    public void setGradeFee(double gradeFee) {
        this.gradeFee.set(gradeFee);
    }

    public LocalDate getGradeStartDate() {
        return gradeStartDate.get();
    }

    public String getClassTime() {
        return classTime.get();
    }

    public StringProperty classTimeProperty() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime.set(classTime);
    }

    public ObjectProperty<LocalDate> gradeStartDateProperty() {
        return gradeStartDate;
    }

    public void setGradeStartDate(LocalDate gradeStartDate) {
        this.gradeStartDate.set(gradeStartDate);
    }

    public String getStartDateFormatted() {
        return startDateFormatted.get();
    }

    public StringProperty startDateFormattedProperty() {
        return startDateFormatted;
    }

    @Override
    public String toString() {
        return getGradeName();
    }
}