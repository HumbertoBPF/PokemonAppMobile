package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public interface TypeDAO {

    @Insert(onConflict = REPLACE)
    void save(List<Type> types);

    @Query("SELECT * FROM Type")
    List<Type> getAllTypesFromLocal();

    @Query("SELECT COUNT(*) FROM Type;")
    Long getNbOfElements();

}
