package com.example.pokemonapp.entities.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "in_game_pokemon_team",
    primaryKeys = {"inGamePokemonId","teamId"},
    foreignKeys = {
        @ForeignKey(entity = InGamePokemon.class,
                    parentColumns = "id",
                    childColumns = "inGamePokemonId"),
        @ForeignKey(entity = Team.class,
                    parentColumns = "id",
                    childColumns = "teamId")
    })
public class InGamePokemonTeam {

    @NonNull
    private Long inGamePokemonId;
    @NonNull
    private Long teamId;

    public InGamePokemonTeam(@NonNull Long inGamePokemonId, Long teamId) {
        this.inGamePokemonId = inGamePokemonId;
        this.teamId = teamId;
    }

    @NonNull
    public Long getInGamePokemonId() {
        return inGamePokemonId;
    }

    public void setInGamePokemonId(@NonNull Long inGamePokemonId) {
        this.inGamePokemonId = inGamePokemonId;
    }

    @NonNull
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(@NonNull Long teamId) {
        this.teamId = teamId;
    }
}
