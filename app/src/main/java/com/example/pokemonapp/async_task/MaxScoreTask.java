package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.room.PokemonAppDatabase;

public class MaxScoreTask extends AsyncTask<Void,Void,Long> {

    private Context context;
    private OnResultListener<Long> onResultListener;

    public MaxScoreTask(Context context, OnResultListener<Long> onResultListener) {
        this.context = context;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        return PokemonAppDatabase.getInstance(context).getScoreDAO().getMaxScore();
    }

    @Override
    protected void onPostExecute(Long maxScoreValue) {
        super.onPostExecute(maxScoreValue);
        onResultListener.onResult(maxScoreValue);
    }
}
