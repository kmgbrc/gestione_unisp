package it.unisp.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import it.unisp.model.Pagamenti;
import it.unisp.model.Prenotazioni;
import org.springframework.stereotype.Component;
import com.itextpdf.layout.element.Image;

import java.io.*;

@Component
public class PDFGenerator {

    String DIR_PATH_DOCS = "./documenti/";

    public byte[] generaRicevutaPagamento(Pagamenti pagamento) {
        String filePath = DIR_PATH_DOCS + "ricevuta/" + "pagamento_" + pagamento.getTransazioneId() + ".pdf";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Creazione della cartella se non esiste
            File dir = new File(DIR_PATH_DOCS + "ricevuta/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Creazione del PDF
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Aggiungi intestazione
            PdfFont titleFont = PdfFontFactory.createFont("Helvetica-Bold");
            Paragraph title = new Paragraph("Ricevuta di Pagamento")
                    .setFont(titleFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Dettagli pagamento
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Numero Transazione: " + pagamento.getTransazioneId()));
            document.add(new Paragraph("Data: " + pagamento.getDataPagamento()));
            document.add(new Paragraph("Importo: €" + pagamento.getImporto()));
            document.add(new Paragraph("Tipo: " + pagamento.getTipoPagamento()));

            // Chiudi il documento
            document.close();

            // Salva il PDF nel file system
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                outputStream.writeTo(fos); // Scrivi i byte nel file
                System.out.println("File PDF salvato: " + filePath);
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio del file: " + e.getMessage());
            }

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione del PDF", e);
        }
    }

/*    public void salvaDocumentoInCartella(String codice, byte[] ricevutaPdf, String path) {
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
    }*/

    public byte[] generaQrCodeFile(Prenotazioni prenotazione, byte[] qrCodeBytes) throws Exception {
        String filePath = DIR_PATH_DOCS + "qrcodes/" + "PMA" + prenotazione.getNumero() + prenotazione.getMembro().getId() + prenotazione.getAttivita().getId() + ".pdf";

        // Utilizza un ByteArrayOutputStream per generare il PDF in memoria
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Creazione della cartella se non esiste
        File dir = new File(DIR_PATH_DOCS + "qrcodes/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Creazione del PDF
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Aggiungi dettagli della prenotazione
        document.add(new Paragraph("Dettagli Prenotazione"));
        document.add(new Paragraph("Numero Prenotazione: " + prenotazione.getNumero()));
        document.add(new Paragraph("Membro: " + prenotazione.getMembro().getNome() + " " + prenotazione.getMembro().getCognome()));
        document.add(new Paragraph("Attività: " + prenotazione.getAttivita().getTitolo()));
        if (prenotazione.getDelegato() != null) {
            document.add(new Paragraph("Delegato: " + prenotazione.getDelegato().getNome() + " " + prenotazione.getDelegato().getCognome()));
        }
        document.add(new Paragraph("Data e Ora Prenotazione: " + prenotazione.getOraPrenotazione()));

        // Aggiungi immagine QR Code
        ImageData imageData = ImageDataFactory.create(qrCodeBytes);
        Image qrCodeImage = new Image(imageData);
        document.add(qrCodeImage);

        // Chiudi il documento
        document.close();

        // Salva il PDF nel file system
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            outputStream.writeTo(fos); // Scrivi i byte nel file
            System.out.println("File PDF salvato: " + filePath);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio del file: " + e.getMessage());
        }


        System.out.println("File PDF generato: " + filePath);

        // Restituisci i byte del PDF generato
        return outputStream.toByteArray();
    }

}
