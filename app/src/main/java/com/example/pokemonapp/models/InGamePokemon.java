package com.example.pokemonapp.models;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

public class InGamePokemon {

    private Integer id;
    private Pokemon pokemonServer;
    private List<Move> moves;

    public InGamePokemon(Integer id, Pokemon pokemonServer) {
        this.id = id;
        this.pokemonServer = pokemonServer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pokemon getPokemonServer() {
        return pokemonServer;
    }

    public void setPokemonServer(Pokemon pokemonServer) {
        this.pokemonServer = pokemonServer;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }
}
