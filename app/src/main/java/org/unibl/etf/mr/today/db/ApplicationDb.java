package org.unibl.etf.mr.today.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.unibl.etf.mr.today.db.dao.ItemDao;
import org.unibl.etf.mr.today.db.dao.PictureDao;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.db.entities.Picture;
import org.unibl.etf.mr.today.utils.Constants;
import org.unibl.etf.mr.today.utils.DateRoomConverter;

@Database(entities = {Item.class, Picture.class}, version = 4)
@TypeConverters({DateRoomConverter.class})
public abstract class ApplicationDb extends RoomDatabase {

    public abstract ItemDao itemDao();
    public abstract PictureDao picureDao();

    private static ApplicationDb Instance;

    public static synchronized ApplicationDb getInstance(Context context) {
        if (null == Instance) {
            Instance = buildDatabaseInstance(context);
        }
        return Instance;
    }

    private static ApplicationDb buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                                    ApplicationDb.class,
                                    Constants.DatabaseName).allowMainThreadQueries().build();
    }

    public  void cleanUp(){
        Instance = null;
    }
}
