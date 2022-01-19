package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Score;

@Dao
public abstract class ScoreDAO extends LocalDAO<Score>{

    public ScoreDAO() {
        super("Score");
    }

    @Query("SELECT max(scoreValue) FROM Score")
    public abstract Long getMaxScore();

    public AsyncTask<Void,Void,Long> getMaxScoreTask(OnResultListener<Long> onResultListener){
        return new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return getMaxScore();
            }

            @Override
            protected void onPostExecute(Long maxScoreValue) {
                super.onPostExecute(maxScoreValue);
                onResultListener.onResult(maxScoreValue);
            }

        };
    }

}
