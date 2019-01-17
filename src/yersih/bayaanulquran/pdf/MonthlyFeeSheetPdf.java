package yersih.bayaanulquran.pdf;


import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import yersih.bayaanulquran.common.Student;
import yersih.bayaanulquran.db.StudentData;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class MonthlyFeeSheetPdf {


    private static final String FONT = "fonts/Faruma.ttf";

    public void createPdf(List<Student> sL, String d, String m) throws IOException {

        List<Student> students = sL;
        String dest = d;
        String month = m;

        PdfWriter writer = new PdfWriter(dest);

        PdfDocument pdfDocument = new PdfDocument(writer);

        Document document = new Document(pdfDocument, PageSize.A4.rotate());
        document.setMargins(10, 10, 10, 10);

        PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

        //Add paragraph to the document
        Table table = new Table(new float[]{5, 5, 25, 10, 25, 10, 20});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFontSize(12);

        String[] columns = {"No.", "Index", "Name", "Phone", "Grades", "Total (RF)", month};
//                "January", "February", "March", "April", "May", "June",
//                "July", "August", "September", "October", "November", "December"};

        document.add(new Paragraph("Bayaanul Quran - Monthly Payments").setFontSize(18).setFont(bold));


        document.add(new Paragraph(month + " " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")))
                .setFontSize(12).setFont(bold));

        Arrays.stream(columns).forEach(c -> {
            Cell cell = new Cell().add(new Paragraph(c)).setFont(bold).setWidth(c.length() * 2).setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        });

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);

        students.forEach(student -> addStudentRow(table, student, font));

        Cell noBorderCell = new Cell().setBorder(Border.NO_BORDER);
        table.addCell(noBorderCell).addCell(noBorderCell).addCell(noBorderCell).addCell(noBorderCell).addCell(noBorderCell);

        double sTotal = students.stream().mapToDouble(Student::getStudentMonthly).sum();
        DecimalFormat df = new DecimalFormat("#.00");
        Cell totalMonthlyCell = new Cell().add(new Paragraph(df.format(sTotal)).setFont(bold)).setTextAlignment(TextAlignment.CENTER);
        table.addCell(totalMonthlyCell);
        table.addCell(new Cell().add(new Paragraph()));

        document.add(table);

        document.close();
    }

    public static void addStudentRow(Table table, Student student, PdfFont font) {

        Cell num = new Cell().add(
                new Paragraph(Integer.toString(StudentData.getInstance().getStudents().indexOf(student) + 1)).setFont(font))
                .setTextAlignment(TextAlignment.CENTER);

        Cell index = new Cell().add(
                new Paragraph(student.getStudentIndex()).setFont(font)).setTextAlignment(TextAlignment.CENTER);

        /*******************************************************************************************************
         * Name of Student // Dhivehi Support Added
         */

        String nameString = ReverseDhivehi.reverse(student.getStudentFullName());

        Cell name;
        name = new Cell().add(
                new Paragraph(nameString).setFont(font)).setTextAlignment(TextAlignment.CENTER);

        /*******************************************************************************************/


        Cell phone = new Cell().add(
                new Paragraph(student.getStudentPhoneNumber()).setFont(font)).setTextAlignment(TextAlignment.CENTER);
        Cell grades = new Cell().add(
                new Paragraph(student.getGradesString()).setFont(font)).setTextAlignment(TextAlignment.CENTER);

        DecimalFormat df = new DecimalFormat("#.00");
        Cell monthly = new Cell().add(
                new Paragraph(df.format(student.getStudentMonthly())).
                        setFont(font)).setTextAlignment(TextAlignment.CENTER);

        Cell blank = new Cell();

        table.addCell(num);
        table.addCell(index);
        table.addCell(name);
        table.addCell(phone);
        table.addCell(grades);
        table.addCell(monthly);
        table.addCell(blank);
    }

}

