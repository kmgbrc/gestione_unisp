package it.unisp.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import it.unisp.model.Membri;
import it.unisp.model.Pagamenti;
import it.unisp.model.Prenotazioni;
import it.unisp.service.PrenotazioneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.itextpdf.layout.element.Image;

import java.io.*;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {

    String DIR_PATH_DOCS = "./documenti/";
    private static final Logger logger = LoggerFactory.getLogger(PrenotazioneService.class);

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

            // Carica il file PDF di sfondo (opzionale)
            try (InputStream bgInputStream = getClass().getResourceAsStream("/static/images/background.pdf")) {
                if (bgInputStream == null) {
                    throw new IOException("Background PDF not found in classpath");
                }

                try (PdfDocument backgroundPdf = new PdfDocument(new PdfReader(bgInputStream))) {
                    PdfPage backgroundPage = backgroundPdf.getPage(1);
                    PdfFormXObject pageCopy = backgroundPage.copyAsFormXObject(pdfDoc);

                    // Aggiungi la pagina di sfondo
                    PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                    canvas.addXObjectAt(pageCopy, 0, 0);
                }
            } catch (IOException e) {
                throw new Exception("Impossibile caricare il PDF di sfondo.", e);
            }

            try (Document document = new Document(pdfDoc)) {
                // Imposta i margini
                document.setMargins(50, 36, 36, 36);

                // Aggiungi intestazione
                PdfFont titleFont = PdfFontFactory.createFont("Helvetica-Bold");
                Paragraph title = new Paragraph("Ricevuta di Pagamento")
                        .setFont(titleFont)
                        .setFontSize(24)
                        .setMarginTop(100)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(title);

                // Aggiungi una tabella per i dettagli del pagamento
                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));

                table.addHeaderCell(new Paragraph("Campo").setFont(titleFont));
                table.addHeaderCell(new Paragraph("Valore").setFont(titleFont));

                table.addCell("Numero Transazione");
                table.addCell(pagamento.getTransazioneId());

                table.addCell("Data");
                table.addCell(DateUtils.formatDate(pagamento.getDataPagamento()));

                table.addCell("Importo");
                table.addCell("€" + pagamento.getImporto());

                table.addCell("Tipo");
                table.addCell(pagamento.getTipoPagamento().toString());

                document.add(table);

                // Chiudi il documento
            }

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


    public byte[] generaQrCodeFile(Prenotazioni prenotazione, byte[] qrCodeBytes) throws Exception {
        String filePath = DIR_PATH_DOCS + "qrcodes/" + "PMA" + prenotazione.getNumero() + prenotazione.getMembro().getId() + prenotazione.getAttivita().getId() + ".pdf";

        logger.info("Inizio generazione del file PDF per la prenotazione: {}", prenotazione.getNumero());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File dir = new File(DIR_PATH_DOCS + "qrcodes/");

        if (!dir.exists() && !dir.mkdirs()) {
            logger.error("Impossibile creare la directory: {}", dir.getAbsolutePath());
            throw new IOException("Impossibile creare la directory per il salvataggio del file.");
        }

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer)) {

            // Carica il file PDF di sfondo
            try (InputStream bgInputStream = getClass().getResourceAsStream("/static/images/background.pdf")) {
                if (bgInputStream == null) {
                    throw new IOException("Background PDF not found in classpath");
                }

                try (PdfDocument backgroundPdf = new PdfDocument(new PdfReader(bgInputStream))) {
                    PdfPage backgroundPage = backgroundPdf.getPage(1);
                    PdfFormXObject pageCopy = backgroundPage.copyAsFormXObject(pdfDoc);

                    // Aggiungi la pagina di sfondo
                    PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
                    canvas.addXObjectAt(pageCopy, 0, 0);
                }
            } catch (IOException e) {
                logger.error("Errore durante il caricamento del PDF di sfondo: {}", e.getMessage());
                throw new Exception("Impossibile caricare il PDF di sfondo.", e);
            }

            // Crea il documento per il contenuto
            try (Document document = new Document(pdfDoc)) {
                logger.info("Documento PDF aperto per aggiungere contenuto.");

                // Carica i font per il testo
                PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
                PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

                // Aggiungi un titolo con stile
                Paragraph title = new Paragraph("Dettagli Prenotazione")
                        .setFont(boldFont)
                        .setFontSize(24)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(100)
                        .setMarginBottom(20);
                document.add(title);

                // Aggiungi una tabella per i dettagli della prenotazione
                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));

                table.addHeaderCell(new Paragraph("Campo").setFont(boldFont));
                table.addHeaderCell(new Paragraph("Valore").setFont(boldFont));

                table.addCell("Numero Prenotazione");
                table.addCell(prenotazione.getNumero().toString());

                table.addCell("Membro");
                table.addCell(prenotazione.getMembro().getNome() + " " + prenotazione.getMembro().getCognome());

                table.addCell("Attività");
                table.addCell(prenotazione.getAttivita().getTitolo());

                if (prenotazione.getDelegato() != null) {
                    table.addCell("Delegato");
                    table.addCell(prenotazione.getDelegato().getNome() + " " + prenotazione.getDelegato().getCognome());
                }

                table.addCell("Data e Ora Prenotazione");
                table.addCell(DateUtils.formatDate(prenotazione.getOraPrenotazione()));

                document.add(table);
                logger.info("Tabella dettagli aggiunta al documento PDF.");

                // Aggiungi immagine QR Code
                ImageData imageData = ImageDataFactory.create(qrCodeBytes);
                Image qrCodeImage = new Image(imageData).setHorizontalAlignment(HorizontalAlignment.CENTER);

                qrCodeImage.setMarginTop(30);

                document.add(qrCodeImage);

                // Aggiungi legenda sotto il QR Code
                Paragraph qrLegend = new Paragraph(prenotazione.getQrCode())
                        .setFont(regularFont)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(0); // Margine sopra la legenda
                document.add(qrLegend);

                logger.info("Immagine QR Code e legenda aggiunti al documento PDF.");

                // Aggiungi un footer con il logo
                try (InputStream logoInputStream = getClass().getResourceAsStream("/static/images/logo.png")) {
                    if (logoInputStream == null) {
                        throw new IOException("Logo image not found in classpath");
                    }
                    ImageData logoImageData = ImageDataFactory.create(logoInputStream.readAllBytes());
                    Image logo = new Image(logoImageData).scaleToFit(100, 50).setHorizontalAlignment(HorizontalAlignment.RIGHT);
                    document.add(logo);
                } catch (IOException e) {
                    logger.error("Errore durante il caricamento del logo: {}", e.getMessage());
                    throw new Exception("Impossibile caricare il logo.", e);
                }

                logger.info("Logo aggiunto al documento PDF.");
            }
        } catch (IOException e) {
            logger.error("Errore durante la generazione del PDF: {}", e.getMessage());
            throw new Exception("Errore durante la generazione del PDF.", e);
        }

        // Salva il file PDF generato
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            outputStream.writeTo(fos);
            logger.info("File PDF salvato: {}", filePath);
        } catch (IOException e) {
            logger.error("Errore durante il salvataggio del file: {}", e.getMessage());
        }

        logger.info("File PDF generato: {}", filePath);
        return outputStream.toByteArray();
    }

    /*public byte[] generaCartaFidelita(Membri membro) {
        String filePath = DIR_PATH_DOCS + "carte_fidelta/" + "carta_fidelta_" + membro.getId() + ".pdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Creazione della cartella se non esiste
            File dir = new File(DIR_PATH_DOCS + "carte_fidelta/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Creazione del PDF
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Imposta le dimensioni della pagina
            pdfDoc.setDefaultPageSize(new PageSize(85.6f, 54f)); // Formato tipo carta d'identità
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            try (Document document = new Document(pdfDoc)) {
                // Imposta margini minimi
                document.setMargins(2, 2, 2, 2);

                // Aggiungere intestazione con logo (opzionale)
                try (InputStream logoInputStream = getClass().getResourceAsStream("/static/images/unisp_logo.jpg")) {
                    if (logoInputStream != null) {
                        ImageData logoImageData = ImageDataFactory.create(logoInputStream.readAllBytes());
                        Image logo = new Image(logoImageData).scaleToFit(20, 20).setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(logo);
                    }
                }

                // Aggiungi titolo
                Paragraph title = new Paragraph("Carta di Fedeltà")
                        .setFont(boldFont) // Usa il font in grassetto
                        .setFontSize(3)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(title);

                // Aggiungi dettagli del membro come semplici paragrafi
                document.add(new Paragraph("Nome: " + membro.getNome())
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0)); // Riduce lo spazio sotto il paragrafo

                document.add(new Paragraph("Cognome: " + membro.getCognome())
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0));

                document.add(new Paragraph("Codice Fiscale: " + membro.getCodiceFiscale())
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0));

                document.add(new Paragraph("Categoria: " + membro.getCategoria().name())
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0));

                document.add(new Paragraph("Data Creazione: " + membro.getDataCreazione().toString())
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0));

                document.add(new Paragraph("Scadenza Iscrizione: " +
                        (membro.getAnnoScadenzaIscrizione() != null ? membro.getAnnoScadenzaIscrizione().toString() : "N/A"))
                        .setFontSize(2)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(0));

                // Messaggio finale
                Paragraph footer = new Paragraph("Grazie per essere un nostro fedele membro!")
                        .setFontSize(1)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(footer);
            }

            // Salva il PDF nel file system
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                outputStream.writeTo(fos);
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio del file: " + e.getMessage());
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione della carta di fedeltà", e);
        }

        return outputStream.toByteArray();
    }*/
}
