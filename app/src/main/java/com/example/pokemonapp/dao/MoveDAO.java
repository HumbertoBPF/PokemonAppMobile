package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Move;

import java.util.List;

@Dao
public interface MoveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<Move> moves);

    @Query("SELECT * FROM Move")
    List<Move> getAllMovesFromLocal();

    @Query("SELECT * FROM Move WHERE fName = :name LIMIT 1")
    Move getMoveByName(String name);

}
