package com.example.pokemonapp.activities.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class SaveTeamActivity extends AppCompatActivity {

    private TeamDAO teamDAO;
    private EditText nameTeamEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_team);
        setTitle("Save Team");

        teamDAO = PokemonAppDatabase.getInstance(this).getTeamDAO();

        nameTeamEditText = findViewById(R.id.name_team_edit_text);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTeam = nameTeamEditText.getText().toString();
                if (!nameTeam.isEmpty()){
                    new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                        @Override
                        public List<Object> doInBackground() {
                            List<Object> objects = new ArrayList<>();
                            objects.addAll(teamDAO.getAllTeamNames());
                            return objects;
                        }

                        @Override
                        public void onPostExecute(List<Object> objects) {
                            if (!objects.contains(nameTeam)){
                                new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                    @Override
                                    public List<Object> doInBackground() {
                                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.name_shared_preferences_file), MODE_PRIVATE);
                                        String json = sharedPreferences.getString(getString(R.string.filename_json_player_team), null);
                                        teamDAO.save(new Team(nameTeam,json));
                                        return null;
                                    }

                                    @Override
                                    public void onPostExecute(List<Object> objects) {
                                        finish();
                                    }
                                }).execute();
                            }else{
                                Toast.makeText(getApplicationContext(), "Name already exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute();

                }else{
                    Toast.makeText(getApplicationContext(), "You have to input a name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}