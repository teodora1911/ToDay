package org.unibl.etf.mr.today.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.unibl.etf.mr.today.utils.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.PictureTableName)
public class Picture implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pic_id")
    private long id;

    @ColumnInfo(name = "item_uuid")
    private String itemUuid;

    @ColumnInfo(name = "pic_url")
    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Picture(long id, String itemUuid, String url){
        this.id = id;
        this.itemUuid = itemUuid;
        this.url = url;
    }

    @Ignore
    public Picture(String itemUuid, String url){
        this(0, itemUuid, url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return id == picture.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", itemUuid='" + itemUuid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
