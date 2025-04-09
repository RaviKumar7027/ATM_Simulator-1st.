import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.util.List;

public class PDFExportManager {

    public static void generateTransactionPDF(List<Transaction> transactions, String fileName) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Transaction History"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Date");
            table.addCell("Type");
            table.addCell("Amount");

            for (Transaction tx : transactions) {
                table.addCell(tx.getDate().toString());
                table.addCell(tx.getType());
                table.addCell(String.valueOf(tx.getAmount()));
            }

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
