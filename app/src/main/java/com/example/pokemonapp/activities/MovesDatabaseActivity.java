package com.example.pokemonapp.activities;

import android.os.Bundle;

import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDatabaseActivity extends DatabaseNavigationActivity {

    private MoveDAO moveDAO;
    private List<Move> moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();

        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                moves = moveDAO.getAllMovesFromLocal();
                List<Object> objects = new ArrayList<>();
                objects.addAll(moves);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                recyclerView.setAdapter(new MovesAdapter(getApplicationContext(),moves));
            }
        }).execute();

    }
}