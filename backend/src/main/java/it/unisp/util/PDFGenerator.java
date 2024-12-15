package it.unisp.util;

import it.unisp.model.Pagamenti;
import org.springframework.stereotype.Component;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class PDFGenerator {

    public byte[] generaRicevutaPagamento(Pagamenti pagamento) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();
            
            // Aggiungi intestazione
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Ricevuta di Pagamento", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Dettagli pagamento
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Numero Transazione: " + pagamento.getTransazioneId()));
            document.add(new Paragraph("Data: " + pagamento.getDataPagamento()));
            document.add(new Paragraph("Importo: €" + pagamento.getImporto()));
            document.add(new Paragraph("Tipo: " + pagamento.getTipoPagamento()));
            
            document.close();
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione del PDF", e);
        }
    }

    public void salvaDocumentoInCartella(String codice, byte[] ricevutaPdf, String path) {
        String directoryPath = "./documenti/" + path;
        File directory = new File(directoryPath);

        // Crea la cartella se non esiste
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Crea il file PDF
        File pdfFile = new File(directory, path + "-" + codice + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(ricevutaPdf);
            fos.flush();
            System.out.println("Documento salvato in: " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
