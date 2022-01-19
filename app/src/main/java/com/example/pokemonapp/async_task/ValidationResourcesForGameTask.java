package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.room.PokemonAppDatabase;

public class ValidationResourcesForGameTask extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    private OnResultListener<Boolean> onResultListener;

    public ValidationResourcesForGameTask(Context context, OnResultListener<Boolean> onResultListener) {
        this.context = context;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return (PokemonAppDatabase.getInstance(context).getMoveDAO().getNbOfElements() > 0 &&
                PokemonAppDatabase.getInstance(context).getTypeDAO().getNbOfElements() > 0 &&
                PokemonAppDatabase.getInstance(context).getPokemonDAO().getNbOfElements() > 0);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        onResultListener.onResult(aBoolean);
    }
}
