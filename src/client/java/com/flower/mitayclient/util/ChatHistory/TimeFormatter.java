package com.flower.mitayclient.util.ChatHistory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeFormatter {

    private static final DateTimeFormatter PARSER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'-*-'HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private static LocalDate referenceDate = LocalDate.now();

    public static void setReferenceDate(LocalDate date) {
        referenceDate = date;
    }

    public static void resetReferenceDate() {
        referenceDate = LocalDate.now();
    }

    public static String getTime(String time1, String time2) {
        // ---------- 调试日志（可删除）----------
//        System.out.println("【getTime】time1=" + time1 + ", time2=" + time2);
        // -------------------------------------

        if (time1 == null || time1.isEmpty()) return null;

        LocalDateTime dt1;
        try {
            dt1 = LocalDateTime.parse(time1, PARSER);
        } catch (Exception e) {
            return null;
        }

        if (time2 == null) {
            return formatDateTime(dt1);
        }

        if (time2.isEmpty()) return null;

        LocalDateTime dt2;
        try {
            dt2 = LocalDateTime.parse(time2, PARSER);
        } catch (Exception e) {
            return null;
        }

        // 🚫 完全相同的时间 → 不显示
        if (dt1.equals(dt2)) {
            return null;
        }

        // 🚫 间隔 ≤ 60 秒 → 不显示（按你的最新要求）
        long secondsDiff = Math.abs(Duration.between(dt1, dt2).toSeconds());
        if (secondsDiff <= 60) {
            return null;
        }

        return formatDateTime(dt1);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        LocalDate targetDate = dateTime.toLocalDate();
        long daysDiff = targetDate.until(referenceDate, ChronoUnit.DAYS);
        if (daysDiff < 0) daysDiff = -daysDiff;

        String timeStr = dateTime.format(TIME_FORMATTER);

        if (daysDiff == 0)
        {
            return timeStr;
        } else if (daysDiff == 1)
        {
            return "昨天 " + timeStr;
        } else if (daysDiff == 2)
        {
            return "前天 " + timeStr;
        } else
        {
            if (targetDate.getYear() == referenceDate.getYear()) {
                return targetDate.getMonthValue() + "月" + targetDate.getDayOfMonth() + "日 " + timeStr;
            } else {
                return targetDate.getYear() + "年" + targetDate.getMonthValue() + "月" + targetDate.getDayOfMonth() + "日 " + timeStr;
            }
        }
    }
}