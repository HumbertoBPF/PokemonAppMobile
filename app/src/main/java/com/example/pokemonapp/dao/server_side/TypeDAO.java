package com.example.pokemonapp.dao.server_side;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.server_side.Type;

import java.util.List;

@Dao
public interface TypeDAO {

    @Insert(onConflict = REPLACE)
    void save(List<Type> types);

    @Query("SELECT * FROM Type")
    List<Type> getAllTypesFromLocal();

}
