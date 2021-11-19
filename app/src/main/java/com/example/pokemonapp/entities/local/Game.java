package com.example.pokemonapp.entities.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pokemonapp.enums.GameMode;

@Entity
public class Game {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private GameMode gameMode;
    // add relationship with 2 Team entities (CPU and player teams)

    public Game(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
