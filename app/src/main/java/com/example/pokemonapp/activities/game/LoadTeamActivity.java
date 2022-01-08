package com.example.pokemonapp.activities.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

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

    private final int CONFIRM_CHOICE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.select_team));

        // useless views for this activity are set as GONE
        playerTeamLabel.setVisibility(View.GONE);
        cpuTeamLabel.setVisibility(View.GONE);
        cpuRecyclerView.setVisibility(View.GONE);
        nextActivityButton.setVisibility(View.GONE);

        teamDAO = PokemonAppDatabase.getInstance(this).getTeamDAO();

        loadingDialog.show();
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.addAll(teamDAO.getAllTeams());
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                loadingDialog.dismiss();
                // shows all the teams previously saved
                playerRecyclerView.setAdapter(new TeamAdapter(getApplicationContext(), objects, new TeamAdapter.OnClickListener() {
                    @Override
                    public void onClick(Team team) {    // when a team is selected, the details of this team are shown, i.e. the details
                                                        // about the pokémon and their moves
                        Intent intent = new Intent(getApplicationContext(),TeamDetailsActivity.class);
                        intent.putExtra("team", team);
                        // to show the details, an activity for result is launched. It expects the confirmation that the selected team
                        // will be loaded
                        startActivityForResult(intent,CONFIRM_CHOICE);
                    }
                }));
            }
        }).execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIRM_CHOICE && resultCode == RESULT_OK){  // if the choice was requested and confirmed
                                                                        // (resultCode = RESULT_OK)
            // set the result of the previous activity as RESULT_OK and finishes this activity
            setResult(RESULT_OK);
            finish();
        }
    }
}