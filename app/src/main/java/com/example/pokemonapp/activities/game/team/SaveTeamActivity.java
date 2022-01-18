package com.example.pokemonapp.activities.game.team;

import static com.example.pokemonapp.util.Tools.getInGamePokemonFromTeam;
import static com.example.pokemonapp.util.Tools.loadingDialog;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.async_task.SaveLocalResourceTask;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class SaveTeamActivity extends AppCompatActivity {

    private TeamDAO teamDAO;

    private Team team;

    private ProgressDialog loadingDialog;

    private EditText nameTeamEditText;
    private RecyclerView teamToSaveRecyclerView;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_team);
        setTitle(R.string.save_team_text_button);

        teamDAO = PokemonAppDatabase.getInstance(this).getTeamDAO();

        loadingDialog = loadingDialog(this);

        team = getTeamIfExists();

        getLayoutElements();

        nameTeamEditText.setText(team.getName());   // if the team's name has already been defined (edition of existing team), we fill
                                                    // the EditText
        configureRecyclerView();
        configureSaveButton();
    }

    /**
     * Verifies if some team was sent by the intent (we are editing an existing team in such case).
     * If it is the case, the team sent is the team that will be saved (updated to be more precise).
     * Otherwise, we are saving a new team, so we create a new instance of Team with the team stored
     * in Shared Preferences.
     * @return a new or an existing instance of Team depending on the cases described above.
     */
    private Team getTeamIfExists() {
        Team team = (Team) getIntent().getSerializableExtra(getString(R.string.key_extra_db_resource));
        if (team == null){
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.name_shared_preferences_file), MODE_PRIVATE);
            String json = sharedPreferences.getString(getString(R.string.filename_json_player_team), null);
            team = new Team();
            team.setTeam(json);
        }
        return team;
    }

    private void getLayoutElements() {
        nameTeamEditText = findViewById(R.id.name_team_edit_text);
        teamToSaveRecyclerView = findViewById(R.id.team_to_save_recycler_view);
        saveButton = findViewById(R.id.save_button);
    }

    private void configureRecyclerView() {
        List<InGamePokemon> teamToSave = getInGamePokemonFromTeam(team);
        teamToSaveRecyclerView.setAdapter(new PokemonMovesAdapter(this, teamToSave));
    }

    private void configureSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTeam = nameTeamEditText.getText().toString();    // gets the name input by the user
                if (!nameTeam.isEmpty()){                                   // if the name is not ""
                    loadingDialog.show();
                    new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                        @Override
                        public List<Object> doInBackground() {
                            List<Object> objects = new ArrayList<>();
                            objects.addAll(teamDAO.getAllTeamNames());
                            return objects;
                        }

                        @Override
                        public void onPostExecute(List<Object> objects) {
                            if (!objects.contains(nameTeam)){               // AND if the name has not been used yet
                                team.setName(nameTeam);
                                new SaveLocalResourceTask<>(teamDAO, team, new SaveLocalResourceTask.OnSavingListener() {
                                    @Override
                                    public void onSave() {
                                        loadingDialog.dismiss();
                                        finish();
                                    }
                                }).execute();
                            }else{
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.name_exists_warning, Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute();

                }else{
                    Toast.makeText(getApplicationContext(), R.string.name_empty_warning, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}