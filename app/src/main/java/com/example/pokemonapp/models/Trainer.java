package com.example.pokemonapp.models;

import android.widget.TextView;

import com.example.pokemonapp.entities.Move;

import java.util.List;

public class Trainer {

    private List<InGamePokemon> team;
    private InGamePokemon currentPokemon;
    private Move currentMove;
    private TextView currentPokemonName;
    private TextView currentPokemonHP;
    private boolean loading = false;

    public Trainer() {
    }

    public List<InGamePokemon> getTeam() {
        return team;
    }

    public void setTeam(List<InGamePokemon> team) {
        this.team = team;
    }

    public InGamePokemon getCurrentPokemon() {
        return currentPokemon;
    }

    public void setCurrentPokemon(InGamePokemon currentPokemon) {
        this.currentPokemon = currentPokemon;
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Move currentMove) {
        this.currentMove = currentMove;
    }

    public TextView getCurrentPokemonName() {
        return currentPokemonName;
    }

    public void setCurrentPokemonName(TextView currentPokemonName) {
        this.currentPokemonName = currentPokemonName;
    }

    public TextView getCurrentPokemonHP() {
        return currentPokemonHP;
    }

    public void setCurrentPokemonHP(TextView currentPokemonHP) {
        this.currentPokemonHP = currentPokemonHP;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void reset(){
        this.loading = false;
    }

    public boolean isPokemonAlive(){
        return getCurrentPokemon().getPokemonServer().getFHp()>0;
    }

}
