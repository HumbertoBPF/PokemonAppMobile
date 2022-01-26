package com.example.pokemonapp.services.synchro_steps;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.services.retrofit.PokemonDbRetrofit;

public class SynchroPokemon extends SynchroStep<Pokemon> {

    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     *
     * @param context       context of the activity that called the callback (it is required to get string
     *                      resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     */
    public SynchroPokemon(Context context, ProgressDialog loadingDialog) {
        super(context, loadingDialog, PokemonAppDatabase.getInstance(context).getPokemonDAO(),
                new PokemonDbRetrofit().getPokemonDbService().getAllPokemonFromRemote(), new SynchroMove(context, loadingDialog));
    }
}
