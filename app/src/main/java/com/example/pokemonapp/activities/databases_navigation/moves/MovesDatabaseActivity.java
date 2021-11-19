package com.example.pokemonapp.activities.databases_navigation.moves;

import android.content.Intent;
import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDatabaseActivity extends DatabaseNavigationActivity {

    private MoveDAO moveDAO;
    private List<Move> moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.moves_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_moves_db);
        detailsActivity = MovesDetailsActivity.class;
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void configureRecyclerView() {
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
                recyclerView.setAdapter(new MovesAdapter(getApplicationContext(), moves, new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(Move move) {
                        Intent intent = new Intent(getApplicationContext(),detailsActivity);
                        intent.putExtra("move",move);
                        startActivity(intent);
                    }
                }));
            }
        }).execute();
    }
}