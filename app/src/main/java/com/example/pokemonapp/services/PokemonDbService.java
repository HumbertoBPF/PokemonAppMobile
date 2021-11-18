package com.example.pokemonapp.services;

import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.models.MoveType;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.models.PokemonMove;
import com.example.pokemonapp.models.PokemonType;
import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.models.TypeEffective;
import com.example.pokemonapp.models.TypeNoEffect;
import com.example.pokemonapp.models.TypeNotEffective;

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

    @GET("moveTypes")
    Call<List<MoveType>> getAllMoveTypesFromRemote();

    @GET("pokemonTypes")
    Call<List<PokemonType>> getAllPokemonTypesFromRemote();

    @GET("pokemonMoves")
    Call<List<PokemonMove>> getAllPokemonMovesFromRemote();

    @GET("effectiveTypes")
    Call<List<TypeEffective>> getAllTypeEffectiveFromRemote();

    @GET("notEffectiveTypes")
    Call<List<TypeNotEffective>> getAllNotEffectiveTypesFromRemote();

    @GET("noEffectTypes")
    Call<List<TypeNoEffect>> getAllNoEffectTypesFromRemote();

}
