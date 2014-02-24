package io.github.coswind.mytwitter.utils;

import android.content.Context;
import android.text.format.Time;

import java.util.Date;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-2-24.
 */
public class DateUtils {
    public static String formatTimestamp(Context context, Date date) {
        Time then = new Time();
        then.set(date.getTime());
        Time now = new Time();
        now.setToNow();

        if (now.year != then.year && now.month > then.month) {
            return String.format(context.getString(R.string.years_ago), now.year - then.year);
        } else if (now.month != then.month && now.monthDay > then.monthDay) {
            return String.format(context.getString(R.string.months_ago), now.month - then.month);
        } else if (now.monthDay != then.monthDay && now.hour > then.hour) {
            return String.format(context.getString(R.string.days_ago), now.monthDay - then.monthDay);
        } else if (now.hour - then.hour > 1) {
            if (now.hour >= then.minute) {
                return String.format(context.getString(R.string.hours_ago), now.hour - then.hour);
            } else {
                return String.format(context.getString(R.string.hours_ago), now.hour - then.hour - 1);
            }
        } else if (now.hour - then.hour == 1) {
            if (now.minute >= then.minute) {
                return String.format(context.getString(R.string.hours_ago), now.hour - then.hour);
            } else {
                return String.format(context.getString(R.string.minutes_ago), 60 + now.minute - then.minute);
            }
        } else {
            return String.format(context.getString(R.string.minutes_ago), now.minute - then.minute);
        }
    }
}
