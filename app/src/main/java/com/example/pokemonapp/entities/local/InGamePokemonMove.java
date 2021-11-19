package com.example.pokemonapp.entities.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.pokemonapp.entities.server_side.Move;

@Entity(tableName = "in_game_pokemon_move",
        primaryKeys = {"inGamePokemonId","moveId"},
        foreignKeys = {
                @ForeignKey(entity = InGamePokemon.class,
                            parentColumns = "id",
                            childColumns = "inGamePokemonId"),
                @ForeignKey(entity = Move.class,
                            parentColumns = "fId",
                            childColumns = "moveId")
            })
public class InGamePokemonMove {

    @NonNull
    private Long inGamePokemonId;
    @NonNull
    private Long moveId;

    public InGamePokemonMove(@NonNull Long inGamePokemonId, @NonNull Long moveId) {
        this.inGamePokemonId = inGamePokemonId;
        this.moveId = moveId;
    }

    @NonNull
    public Long getInGamePokemonId() {
        return inGamePokemonId;
    }

    public void setInGamePokemonId(@NonNull Long inGamePokemonId) {
        this.inGamePokemonId = inGamePokemonId;
    }

    @NonNull
    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(@NonNull Long moveId) {
        this.moveId = moveId;
    }
}
