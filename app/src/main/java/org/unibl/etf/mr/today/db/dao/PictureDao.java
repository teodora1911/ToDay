package org.unibl.etf.mr.today.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.unibl.etf.mr.today.db.entities.Picture;
import org.unibl.etf.mr.today.utils.Constants;

import java.util.List;

@Dao
public interface PictureDao {

    @Query("SELECT * FROM " + Constants.PictureTableName + " WHERE item_uuid = :itemUuid")
    List<Picture> getPicsForItem(String itemUuid);

    @Insert
    long insertPic(Picture item);

    @Delete
    void deletePic(Picture item);

    @Delete
    void deletePics(Picture... items);
}
