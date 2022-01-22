package com.example.pokemonapp.services.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonDbRetrofit {

    private final PokemonDbService pokemonDbService;

    public PokemonDbRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.106:8080/PokemonApp/db/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokemonDbService = retrofit.create(PokemonDbService.class);
    }

    public PokemonDbService getPokemonDbService(){
        return pokemonDbService;
    }

}
