package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Move;

@Dao
public abstract class MoveDAO extends RemoteDAO<Move>{

    public MoveDAO() {
        super("Move");
    }

    @Query("SELECT * FROM Move WHERE fName = :name LIMIT 1")
    public abstract Move getMoveByName(String name);

    @Query("SELECT COUNT(*) FROM Move;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,Move> getStruggleMoveTask(OnResultListener<Move> onResultListener){
        return new AsyncTask<Void, Void, Move>() {
            @Override
            protected Move doInBackground(Void... voids) {
                return getMoveByName("Struggle");
            }

            @Override
            protected void onPostExecute(Move move) {
                super.onPostExecute(move);
                onResultListener.onResult(move);
            }
        };
    }

}
