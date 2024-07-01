package com.example.quizlearning;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getRelativeTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            long timeInMillis = date.getTime();

            long now = System.currentTimeMillis();
            long timeDifference = now - timeInMillis;

            if (Math.abs(timeDifference) < DateUtils.DAY_IN_MILLIS) {
                // Nếu là trong vòng 1 ngày
                if (DateUtils.isToday(timeInMillis)) {
                    return "Hôm nay";
                } else if (DateUtils.isToday(timeInMillis - DateUtils.DAY_IN_MILLIS)) {
                    return "Hôm qua";
                } else if (DateUtils.isToday(timeInMillis - 2 * DateUtils.DAY_IN_MILLIS)) {
                    return "Hôm kia";
                }
            }

            // Nếu lâu hơn 2 ngày, hiển thị ngày tháng
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}
