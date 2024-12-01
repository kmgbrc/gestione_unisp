package com.unisp.gestioneunisp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class QRCodeGenerator {
    private static final String QR_CODE_PATH = "qrcodes/";

    public String generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

            String fileName = String.format("%s.png", System.currentTimeMillis());
            Path path = Paths.get(QR_CODE_PATH + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            return fileName;
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Errore nella generazione del QR Code", e);
        }
    }
}