package io.kavenegar.android.sample.standalone.utils;


import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

public class TimeUtils {


    public static Long getUnixTime() {
        return Instant.now().getEpochSecond();
    }

    public static Long getUnixTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime getLocalDateTime(Long epochSecond) {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String toISOFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public static LocalDateTime parseISOFormat(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) return null;
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
    }


    public static LocalDateTime today() {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
    }

    public static LocalDateTime yesterday() {
        return today().minusDays(1);
    }

    public static long getDifferentInSeconds(LocalDateTime first, LocalDateTime second) {
        return Duration.between(first, second).toMillis() / 1000;
    }

    public static long getDifferentFromNowInSeconds(LocalDateTime first) {
        return Duration.between(first, LocalDateTime.now()).toMillis() / 1000;
    }

}

