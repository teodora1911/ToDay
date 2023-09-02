package org.unibl.etf.mr.today.utils;

import java.util.List;

public final class Constants {

    private Constants() { }

    public static final String ItemTableName = "items";
    public static final String PictureTableName = "pictures";
    public static final String DatabaseName = "itemsdb.db";

    public enum NotifType {
        OFF,
        HOUR,
        DAY,
        WEEK
    }
}
