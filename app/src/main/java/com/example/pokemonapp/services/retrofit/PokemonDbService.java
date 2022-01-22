package com.example.pokemonapp.services.retrofit;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.MoveType;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.PokemonMove;
import com.example.pokemonapp.entities.PokemonType;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeEffective;
import com.example.pokemonapp.entities.TypeNoEffect;
import com.example.pokemonapp.entities.TypeNotEffective;

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
