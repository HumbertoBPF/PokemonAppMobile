package com.example.pokemonapp.entities.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "game_team",
        primaryKeys = {"gameId","teamId"},
        foreignKeys = {
                @ForeignKey(entity = Game.class,
                            parentColumns = "id",
                            childColumns = "gameId"),
                @ForeignKey(entity = Team.class,
                            parentColumns = "id",
                            childColumns = "teamId"
                )
        })
public class GameTeam {

    @NonNull
    private Long gameId;
    @NonNull
    private Long teamId;

    public GameTeam(@NonNull Long gameId, @NonNull Long teamId) {
        this.gameId = gameId;
        this.teamId = teamId;
    }

    @NonNull
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(@NonNull Long gameId) {
        this.gameId = gameId;
    }

    @NonNull
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(@NonNull Long teamId) {
        this.teamId = teamId;
    }
}
