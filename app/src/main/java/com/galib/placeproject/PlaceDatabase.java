package com.galib.placeproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PlaceEntity.class},version = 3)
public abstract class PlaceDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}
