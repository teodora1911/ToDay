package org.unibl.etf.mr.today.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeProvider {

    private static final String FORMAT = "HH:mm dd.MM.yyyy.";

    private DateTimeProvider() {}

    public static String printDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.format(date);
    }
}
