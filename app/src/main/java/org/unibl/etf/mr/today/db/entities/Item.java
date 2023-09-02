package org.unibl.etf.mr.today.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.unibl.etf.mr.today.utils.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity(tableName = Constants.ItemTableName)
public class Item implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "location")
    private String location;

    @Ignore
    public Item(String uuid, @NonNull String title, int type, String description, Date timestamp){
        this(uuid, title, type, description, timestamp, null);
    }

    @Ignore
    public Item(String uuid, @NotNull String title, int type, String description, Date timestamp, String location){
        this.uuid = uuid;
        this.title = title;
        this.timestamp = timestamp;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    public Item(long id, String uuid, String title, Date timestamp, String description, int type, String location){
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.timestamp = timestamp;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "Id=" + id +
                ", Title='" + title + '\'' +
                ", Timestamp=" + timestamp +
                ", Location=" + location +
                ", Description='" + description + '\'' +
                '}';
    }
}
