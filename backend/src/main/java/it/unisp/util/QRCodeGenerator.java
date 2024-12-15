package it.unisp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class QRCodeGenerator {
    private static final String QR_CODE_PATH = "qrcodes/";

    public byte[] generateQRCode(String data) {
        try {
            // Controlla l'esistenza della directory e creala se non esiste
            Path directory = Paths.get(QR_CODE_PATH);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

            // Usa un ByteArrayOutputStream per salvare l'immagine in memoria
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            return pngOutputStream.toByteArray(); // Restituisce i byte dell'immagine
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Errore nella generazione del QR Code", e);
        }
    }
}
