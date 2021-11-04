package com.example.pokemonapp.services;

import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.models.Type;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PokemonDbService {

    @GET("pokemon")
    Call<List<Pokemon>> getAllPokemonFromRemote();

    @GET("moves")
    Call<List<Move>> getAllMovesFromRemote();

    @GET("types")
    Call<List<Type>> getAllTypesFromRemote();

}
