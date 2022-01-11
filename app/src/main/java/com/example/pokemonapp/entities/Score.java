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

    /**
     * The score aims to give feedback for the users about their performance. It is computed only when
     * the player wins the battle and takes into account 6 criteria to evaluate the battle:<br>
     *     - The time that was taken to win the cpu. Longer the battle, lower the score.<br>
     *     - The force (overall points) of the cpu's team. Stronger the cpu's team, greater the score.<br>
     *     - The force (overall points) of the player's team. Stronger the player's team, lower the score.<br>
     *     - Number of remaining player's pokémon (pokémon that have not fainted at the end of a battle).
     *     More player's pokémon remain, greater the score.<br>
     *     - Game mode. The score is penalized by 25% when playing in the random mode due to randomness.<br>
     *     - Game level. The score is penalized by 25% when playing in easy level due to the absence of AI.<br>
     * Besides, some information about the battle are stored with the score value. Such information concerns
     * the criteria mentioned above and other related information such as the JSON corresponding to player's
     * and cpu's team and the date string (containing time, day, month and year) of the battle.
     * @param scoreValue score value.
     * @param battleDuration duration of the battle.
     * @param nbPlayerRemainingPokemon number of pokémon that have not fainted at the end of a battle.
     * @param playerTeamOverallPoints sum of the overall points of all the pokémon of player's team.
     * @param cpuTeamOverallPoints sum of the overall points of all the pokémon of cpu's team.
     * @param gameMode game mode.
     * @param gameLevel game level.
     * @param playerTeam JSON String corresponding to player's team.
     * @param cpuTeam JSON String corresponding to cpu's team.
     * @param date String corresponding to the date object specifying the time, day, month and year
     *             when the battle happened.
     */
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
