package com.example.pokemonapp.services.synchro_steps;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.pokemonapp.entities.TypeNoEffect;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.services.retrofit.PokemonDbRetrofit;

public class SynchroTypeNoEffect extends SynchroStep<TypeNoEffect> {
    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     *
     * @param context       context of the activity that called the callback (it is required to get string
     *                      resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     */
    public SynchroTypeNoEffect(Context context, ProgressDialog loadingDialog) {
        super(context, loadingDialog, PokemonAppDatabase.getInstance(context).getTypeNoEffectDAO(),
                new PokemonDbRetrofit().getPokemonDbService().getAllNoEffectTypesFromRemote(),null);
    }
}
