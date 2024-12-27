package it.unisp.util;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM YYYY", Locale.ITALY);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatDate(LocalDateTime date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : "";
    }

    public static boolean isScaduto(LocalDateTime dataScadenza) {
        return dataScadenza != null && dataScadenza.isBefore(LocalDateTime.now());
    }

    public static long giorniTraDate(LocalDateTime data1, LocalDateTime data2) {
        if (data1 == null || data2 == null) {
            throw new IllegalArgumentException("Le date non possono essere null");
        }

        // Convertiamo LocalDateTime in LocalDate
        LocalDate date1 = data1.toLocalDate();
        LocalDate date2 = data2.toLocalDate();

        // Calcoliamo la differenza in giorni
        return Math.abs(date1.toEpochDay() - date2.toEpochDay());
    }


    public static LocalDateTime addGiorni(LocalDateTime data, long giorni) {
        return data.plusDays(giorni);
    }
}
