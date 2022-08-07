package com.galib.placeproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sheet")
public class PlaceEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public int ID;

    @ColumnInfo(name = "placeName")
    public String placeName;

    @ColumnInfo(name = "lattitude")
    public Double lattitude;

    @ColumnInfo(name = "longitude")
    public Double longitude;

    public PlaceEntity(String placeName, Double lattitude, Double longitude) {
        this.placeName = placeName;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
}
