package yersih.bayaanul.pdf;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import yersih.bayaanul.common.Student;
import yersih.bayaanul.db.StudentData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeeCardsPdf {

    private static final int fullWidth = 2480;
    private static final int fullHeight = 3508;

    private static final int cardWidth = 1240;
    private static final int cardHeight = 1754;

    private String year;

    public FeeCardsPdf(String year) {
        this.year = year;
    }


    public void generatePdf(File dest) throws IOException, FontFormatException {

        int pages = StudentData.getInstance().getStudents().size() / 4;
        int extraCards = StudentData.getInstance().getStudents().size() % 4;


        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(0, 0, 0, 0);

        int startCard;
        for (startCard = 0; (startCard / 4) < pages; startCard += 4) {
            document.add(generatePageImage(startCard, 4));
        }

        if (extraCards > 0) {
            document.add(generatePageImage(StudentData.getInstance().getStudents().size() - extraCards, extraCards));
        }

        document.close();

    }

    private Image generatePageImage(int index, int cards) throws IOException, FontFormatException {
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < cards; i++) {
            students.add(StudentData.getInstance().getStudents().get(index + i));
        }

        BufferedImage image = new BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphic = image.createGraphics();
        graphic.setBackground(Color.WHITE);
        graphic.clearRect(0, 0, fullWidth, fullHeight);
        graphic.setColor(Color.BLACK);


        //Draw Empty Cards
        BufferedImage img = ImageIO.read(new File("resources/feecard.png"));


        graphic.drawImage(img, 0, 0, cardWidth, cardHeight, null);

        if (students.size() > 1) {
            graphic.drawImage(img, cardWidth, 0, cardWidth, cardHeight, null);
        }
        if (students.size() > 2) {
            graphic.drawImage(img, 0, cardHeight, cardWidth, cardHeight, null);
        }
        if (students.size() > 3) {
            graphic.drawImage(img, cardWidth, cardHeight, cardWidth, cardHeight, null);
        }


        // Draw Year String
        final int yearX = 420;
        final int yearY = 298;
        graphic.setFont(graphic.getFont().deriveFont(80f));


        graphic.drawString(year, yearX, yearY);

        if (students.size() > 1) {
            graphic.drawString(year, cardWidth + yearX, yearY);
        }

        if (students.size() > 2) {
            graphic.drawString(year, yearX, cardHeight + yearY);
        }

        if (students.size() > 3) {
            graphic.drawString(year, cardWidth + yearX, cardHeight + yearY);
        }


        // Draw Student Index and Name Strings on Cards
        Font dhivehi = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Faruma.ttf"))
                .deriveFont(72f);
        graphic.setFont(dhivehi);
        FontMetrics fontMetrics = graphic.getFontMetrics();
        final int nameX = 1160;
        final int nameY = 460;


        graphic.drawString(ReverseDhivehi.reverseUnicode(students.get(0).getStudentFullName()),
                nameX - fontMetrics.stringWidth(students.get(0).getStudentFullName()), nameY);

        if (students.size() > 1) {
            graphic.drawString(ReverseDhivehi.reverseUnicode(students.get(1).getStudentFullName()),
                    (cardWidth + nameX) - fontMetrics.stringWidth(students.get(1).getStudentFullName()), nameY);
        }

        if (students.size() > 2) {
            graphic.drawString(ReverseDhivehi.reverseUnicode(students.get(2).getStudentFullName()),
                    nameX - fontMetrics.stringWidth(students.get(2).getStudentFullName()), cardHeight + nameY);
        }

        if (students.size() > 3) {
            graphic.drawString(ReverseDhivehi.reverseUnicode(students.get(3).getStudentFullName()),
                    (cardWidth + nameX) - fontMetrics.stringWidth(students.get(3).getStudentFullName()), cardHeight + nameY);
        }


        final int indexX = 72;
        final int indexY = 460;
        graphic.setFont(dhivehi.deriveFont(72f));


        graphic.drawString(students.get(0).getStudentIndex(), indexX, indexY);

        if (students.size() > 1) {
            graphic.drawString(students.get(1).getStudentIndex(), cardWidth + indexX, indexY);
        }

        if (students.size() > 2) {
            graphic.drawString(students.get(2).getStudentIndex(), indexX, cardHeight + indexY);
        }

        if (students.size() > 3) {
            graphic.drawString(students.get(3).getStudentIndex(), cardWidth + indexX, cardHeight + indexY);
        }


        // Create an iTextImage from the Filled Fee Cards BufferedImage

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return new com.itextpdf.layout.element.Image(ImageDataFactory.create(baos.toByteArray()));

    }
}
