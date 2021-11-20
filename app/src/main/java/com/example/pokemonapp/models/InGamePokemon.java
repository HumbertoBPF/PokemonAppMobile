package com.example.pokemonapp.models;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

public class InGamePokemon {

    private Pokemon pokemonServer;
    private List<Move> moves;

    public InGamePokemon(Pokemon pokemonServer) {
        this.pokemonServer = pokemonServer;
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
