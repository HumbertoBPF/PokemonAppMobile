package com.example.pokemonapp.services.synchro_callbacks;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.pokemonapp.entities.TypeEffective;
import com.example.pokemonapp.retrofit.PokemonDbRetrofit;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.List;

import retrofit2.Call;

public class SynchroTypeEffective extends SynchroCallback<TypeEffective> {
    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     *
     * @param context       context of the activity that called the callback (it is required to get string
     *                      resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     */
    public SynchroTypeEffective(Context context, ProgressDialog loadingDialog) {
        super(context, loadingDialog, PokemonAppDatabase.getInstance(context).getTypeEffectiveDAO(),
                new SynchroTypeNotEffective(context, loadingDialog));
    }

    @Override
    protected Call<List<TypeEffective>> callService() {
        return new PokemonDbRetrofit().getPokemonDbService().getAllTypeEffectiveFromRemote();
    }
}
