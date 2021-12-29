package com.example.pokemonapp.models;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to model a pokémon in a battle. The difference to the pokémon in the database (pokédex) is
 * that the pokémon here is <b>associated to a list of at most 4 moves</b> that correspond to the
 * <b>moves that it can use in the battle</b>. Hence, we have 3 attributes : <br>
 * <br>
 *     - <b>id</b> : a unique identifier.
 *     - <b>pokémonServer</b> : pokémon of the pokédex, from where we are going to obtain the most
 *     of information.
 *     - <b>moves</b> : list of at most 4 moves that can be used in a battle.
 */
public class InGamePokemon {

    private Integer id;
    private Pokemon pokemonServer;                  // pokémon of the pokédex
    private List<Move> moves = new ArrayList<>();   // list of at most 4 moves that can be used in a battle

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

    public List<Move> getMoves() {
        return moves;
    }

    public void addMove(Move move){
        moves.add(move);
    }

    public void removeMove(Move move){
        moves.remove(move);
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }
}
