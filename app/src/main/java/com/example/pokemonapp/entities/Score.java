package com.example.pokemonapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Score {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Double scoreValue;
    private Long battleDuration;
    private Integer nbPlayerRemainingPokemon;
    private Long playerTeamOverallPoints;
    private Long cpuTeamOverallPoints;
    private String gameMode;
    private String gameLevel;
    private String playerTeam;
    private String cpuTeam;
    private String date;

    public Score(){

    }

    public Score(Double scoreValue, Long battleDuration, Integer nbPlayerRemainingPokemon, Long playerTeamOverallPoints,
                 Long cpuTeamOverallPoints, String gameMode, String gameLevel, String playerTeam, String cpuTeam, String date) {
        this.scoreValue = scoreValue;
        this.battleDuration = battleDuration;
        this.nbPlayerRemainingPokemon = nbPlayerRemainingPokemon;
        this.playerTeamOverallPoints = playerTeamOverallPoints;
        this.cpuTeamOverallPoints = cpuTeamOverallPoints;
        this.gameMode = gameMode;
        this.gameLevel = gameLevel;
        this.playerTeam = playerTeam;
        this.cpuTeam = cpuTeam;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Long getBattleDuration() {
        return battleDuration;
    }

    public void setBattleDuration(Long battleDuration) {
        this.battleDuration = battleDuration;
    }

    public Integer getNbPlayerRemainingPokemon() {
        return nbPlayerRemainingPokemon;
    }

    public void setNbPlayerRemainingPokemon(Integer nbPlayerRemainingPokemon) {
        this.nbPlayerRemainingPokemon = nbPlayerRemainingPokemon;
    }

    public Long getPlayerTeamOverallPoints() {
        return playerTeamOverallPoints;
    }

    public void setPlayerTeamOverallPoints(Long playerTeamOverallPoints) {
        this.playerTeamOverallPoints = playerTeamOverallPoints;
    }

    public Long getCpuTeamOverallPoints() {
        return cpuTeamOverallPoints;
    }

    public void setCpuTeamOverallPoints(Long cpuTeamOverallPoints) {
        this.cpuTeamOverallPoints = cpuTeamOverallPoints;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(String gameLevel) {
        this.gameLevel = gameLevel;
    }

    public String getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(String playerTeam) {
        this.playerTeam = playerTeam;
    }

    public String getCpuTeam() {
        return cpuTeam;
    }

    public void setCpuTeam(String cpuTeam) {
        this.cpuTeam = cpuTeam;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
