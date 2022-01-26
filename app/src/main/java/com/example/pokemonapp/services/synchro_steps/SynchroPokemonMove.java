package com.example.pokemonapp.services.synchro_steps;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.pokemonapp.entities.PokemonMove;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.services.retrofit.PokemonDbRetrofit;

public class SynchroPokemonMove extends SynchroStep<PokemonMove> {
    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     *
     * @param context       context of the activity that called the callback (it is required to get string
     *                      resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     */
    public SynchroPokemonMove(Context context, ProgressDialog loadingDialog) {
        super(context, loadingDialog, PokemonAppDatabase.getInstance(context).getPokemonMoveDAO(),
                new PokemonDbRetrofit().getPokemonDbService().getAllPokemonMovesFromRemote(), new SynchroTypeEffective(context, loadingDialog));
    }
}
