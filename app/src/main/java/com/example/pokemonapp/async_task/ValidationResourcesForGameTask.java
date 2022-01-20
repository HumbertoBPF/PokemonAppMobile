package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.room.PokemonAppDatabase;

/**
 * AsyncTask to verify if resources necessary for the game (pokémon, moves and types) have been
 * synchronized from the server. If there is at least one record of each entity on the local database,
 * the app concludes that all the resources are available locally, since the user only can delete all
 * the app data (i.e. it should not be possible to delete a specific pokémon, move or type). The
 * records from the join tables are not verified since they are not essential to play the game (i.e.
 * the game does not crashes without them).
 */
public class ValidationResourcesForGameTask extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    private OnResultListener<Boolean> onResultListener;

    public ValidationResourcesForGameTask(Context context, OnResultListener<Boolean> onResultListener) {
        this.context = context;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return (PokemonAppDatabase.getInstance(context).getMoveDAO().getNbOfElements() > 0 &&   // verifying moves
                PokemonAppDatabase.getInstance(context).getTypeDAO().getNbOfElements() > 0 &&   // verifying types
                PokemonAppDatabase.getInstance(context).getPokemonDAO().getNbOfElements() > 0); // verifying pokémon
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        onResultListener.onResult(aBoolean);
    }
}
