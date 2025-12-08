package com.school.management.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDateTime(String dateTimeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());
            return sdf.parse(dateTimeString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTimeAgo(Date date) {
        if (date == null) return "";
        
        long time = date.getTime();
        long now = System.currentTimeMillis();
        long difference = now - time;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        long days = TimeUnit.MILLISECONDS.toDays(difference);

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            return formatDate(date);
        }
    }

    public static int getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    public static boolean isDateInRange(Date date, Date startDate, Date endDate) {
        if (date == null || startDate == null || endDate == null) return false;
        return !date.before(startDate) && !date.after(endDate);
    }

    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static int getAge(Date birthDate) {
        if (birthDate == null) return 0;
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public static String getCurrentAcademicYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        
        // Academic year starts in April (month 3)
        if (month < 3) {
            return (year - 1) + "-" + year;
        } else {
            return year + "-" + (year + 1);
        }
    }
}
