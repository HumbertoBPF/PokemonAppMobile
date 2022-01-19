package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

public class StruggleMoveTask extends AsyncTask<Void,Void, Move> {

    private Context context;
    private OnResultListener<Move> onResultListener;

    public StruggleMoveTask(Context context, OnResultListener<Move> onResultListener) {
        this.context = context;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Move doInBackground(Void... voids) {
        return PokemonAppDatabase.getInstance(context).getMoveDAO().getMoveByName("Struggle");
    }

    @Override
    protected void onPostExecute(Move move) {
        super.onPostExecute(move);
        onResultListener.onResult(move);
    }

}
