package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface PokemonAppDAO<E> {

    @Insert(onConflict = REPLACE)
    void save(List<E> entities);

    @RawQuery
    List<E> getAllRecords(SupportSQLiteQuery sqLiteQuery);

}
