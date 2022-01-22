package com.example.pokemonapp.services.synchro_callbacks;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.services.retrofit.PokemonDbRetrofit;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.List;

import retrofit2.Call;

public class SynchroType extends SynchroCallback<Type>{
    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     *
     * @param context       context of the activity that called the callback (it is required to get string
     *                      resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     */
    public SynchroType(Context context, ProgressDialog loadingDialog) {
        super(context, loadingDialog, PokemonAppDatabase.getInstance(context).getTypeDAO(),
                new SynchroMoveType(context, loadingDialog));
    }

    @Override
    protected Call<List<Type>> callService() {
        return new PokemonDbRetrofit().getPokemonDbService().getAllTypesFromRemote();
    }
}
