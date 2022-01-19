package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Team;

import java.util.List;

@Dao
public abstract class TeamDAO extends LocalDAO<Team>{

    public TeamDAO() {
        super("Team");
    }

    @Query("SELECT * FROM Team WHERE name = :name;")
    public abstract List<Team> findTeamByName(String name);

    public AsyncTask<Void,Void,List<Team>> findTeamByNameTask(String name, OnResultListener<List<Team>> onResultListener){
        return new AsyncTask<Void, Void, List<Team>>() {
            @Override
            protected List<Team> doInBackground(Void... voids) {
                return findTeamByName(name);
            }

            @Override
            protected void onPostExecute(List<Team> teams) {
                super.onPostExecute(teams);
                onResultListener.onResult(teams);
            }
        };
    }

}
