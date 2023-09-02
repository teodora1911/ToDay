package org.unibl.etf.mr.today.utils;

import android.content.Context;

import org.unibl.etf.mr.today.R;

public final class ItemTypeHelper {

    public static final int COUNT = 3;

    private ItemTypeHelper() { }

    public static String getCode(int id, Context context){
        if(id >= COUNT || id < 0)
            return null;

        switch(id) {
            case 0:
                return context.getString(R.string.item_type_travel);
            case 1:
                return context.getString(R.string.item_type_work);
            default:
                return context.getString(R.string.item_type_free_time);
        }
    }
}
