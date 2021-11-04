package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.models.Move;

import java.util.List;

@Dao
public interface MoveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<Move> moves);

    @Query("SELECT * FROM Move")
    List<Move> getAllMovesFromLocal();

}
