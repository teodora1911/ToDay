package org.unibl.etf.mr.today.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.utils.Constants;

import java.util.Date;
import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM " + Constants.ItemTableName + " ORDER BY timestamp ASC")
    List<Item> getItems();

    @Query("SELECT * FROM " + Constants.ItemTableName + " WHERE title LIKE :query ORDER BY timestamp ASC")
    List<Item> getItems(String query);

    @Query("SELECT * FROM " + Constants.ItemTableName + " WHERE id = :id")
    Item getItemById(long id);

    @Query("SELECT * FROM " + Constants.ItemTableName + " WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    List<Item> getItems(Date start, Date end);

    @Insert
    long insertItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Delete
    void deleteItems(Item... items);
}
