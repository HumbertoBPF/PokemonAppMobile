package com.example.pokemonapp.activities.game.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.TeamAdapter;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.List;

public class LoadTeamActivity extends SelectionActivity {

    private TeamDAO teamDAO;
    private int maxOverallPoints;
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

        maxOverallPoints = getIntent().getIntExtra("maxOverallPoints",-1);

        loadingDialog.show();
        teamDAO.getAllRecordsTask(new OnResultListener<List<Team>>() {
            @Override
            public void onResult(List<Team> result) {
                loadingDialog.dismiss();
                // shows all the teams previously saved
                playerRecyclerView.setAdapter(new TeamAdapter(getApplicationContext(), result, new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) { // when a team is selected, the details of this team are shown,
                                                                    // i.e. the details about the pok??mon and their moves
                        if (maxOverallPoints != -1){
                            if (((Team) object).getOverallPointsOfTeam() <= maxOverallPoints){
                                showTeamDetails((Team) object);
                            }else{
                                Toast.makeText(getApplicationContext(), R.string.max_op_load_team_warning, Toast.LENGTH_LONG).show();
                            }
                        }else{
                            showTeamDetails((Team) object);
                        }
                    }
                },false));
            }
        }).execute();
    }

    private void showTeamDetails(Team team) {
        Intent intent = new Intent(getApplicationContext(), TeamDetailsActivity.class);
        intent.putExtra(getString(R.string.key_extra_db_resource), team);
        // to show the details, an activity for result is launched. It expects the confirmation that the selected team
        // will be loaded
        startActivityForResult(intent,CONFIRM_CHOICE);
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