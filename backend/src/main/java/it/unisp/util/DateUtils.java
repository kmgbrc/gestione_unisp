package com.unisp.gestione.utils;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static boolean isScaduto(LocalDateTime dataScadenza) {
        return dataScadenza != null && dataScadenza.isBefore(LocalDateTime.now());
    }

    public static long giorniTraDate(LocalDateTime data1, LocalDateTime data2) {
        return ChronoUnit.DAYS.between(data1, data2);
    }

    public static LocalDateTime addGiorni(LocalDateTime data, long giorni) {
        return data.plusDays(giorni);
    }
}
