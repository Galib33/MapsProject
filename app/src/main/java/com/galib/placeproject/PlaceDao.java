package com.galib.placeproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.Observable;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM sheet")
    Flowable<List<PlaceEntity>> getAllData();

    @Query("SELECT * FROM sheet WHERE ID = :idinput")
    Flowable<List<PlaceEntity>> getcertainData(int idinput);

    @Insert
    Completable insertData(PlaceEntity entity);

    @Query("DELETE FROM sheet WHERE ID = :idsent")
    Completable delete(int idsent);





}
