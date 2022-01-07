package com.example.pokemonapp.activities.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.TeamAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoadTeamActivity extends SelectionActivity {

    private TeamDAO teamDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.select_team));

        // useless views for this activity
        playerTeamLabel.setVisibility(View.GONE);
        cpuTeamLabel.setVisibility(View.GONE);
        cpuRecyclerView.setVisibility(View.GONE);
        nextActivityButton.setVisibility(View.GONE);

        teamDAO = PokemonAppDatabase.getInstance(this).getTeamDAO();

        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.addAll(teamDAO.getAllTeams());
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                // shows all the teams previously saved
                playerRecyclerView.setAdapter(new TeamAdapter(getApplicationContext(), objects, new TeamAdapter.OnClickListener() {
                    @Override
                    public void onClick(Team team) {
                        Toast.makeText(getApplicationContext(), "Select", Toast.LENGTH_LONG).show();
                    }
                }));
            }
        }).execute();

    }
}