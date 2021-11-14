package com.example.pokemonapp.activities.databases_navigation.moves;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;

import android.os.Bundle;
import android.widget.TextView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDetailsActivity extends DatabaseDetailsActivity {

    private Move move;
    private TextView moveName;
    private TextView moveType;
    private TextView moveCategory;
    private TextView movePower;
    private TextView moveAccuracy;
    private TextView movePp;
    private TextView minimumNbHits;
    private TextView maximumNbHits;
    private TextView userFaints;
    private TextView nbRoundsToLoad;
    private TextView trapsOpponent;
    private TextView flinchingProbability;

    private MoveTypeDAO moveTypeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.moves_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_moves_db);
        layout = R.layout.activity_moves_details;
        super.onCreate(savedInstanceState);

        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();

        move = (Move) getIntent().getSerializableExtra("move");

        getLayoutElements();
        bind();
    }

    protected void getLayoutElements() {
        moveName = findViewById(R.id.move_name);
        moveType = findViewById(R.id.move_type);
        moveCategory = findViewById(R.id.move_category);
        movePower = findViewById(R.id.move_power);
        moveAccuracy = findViewById(R.id.move_accuracy);
        movePp = findViewById(R.id.move_pp);
        minimumNbHits = findViewById(R.id.minimum_nb_hits);
        maximumNbHits = findViewById(R.id.maximum_nb_hits);
        userFaints = findViewById(R.id.user_faints);
        nbRoundsToLoad = findViewById(R.id.nb_rounds_to_load);
        trapsOpponent = findViewById(R.id.traps_opponent);
        flinchingProbability = findViewById(R.id.flinching_probability);
    }

    protected void bind() {
        moveName.setText("Name : "+move.getFName());
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.addAll(moveTypeDAO.getTypesOfMove(move.getFId()));
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                moveType.setText(listOfTypesAsString(objects));
            }
        }).execute();
        moveCategory.setText("Category\n"+move.getFCategory());
        movePower.setText("Power\n"+move.getFPower());
        moveAccuracy.setText("Accuracty\n"+move.getFAccuracy());
        movePp.setText("PP\n"+move.getFPp());
        minimumNbHits.setText("The attack hits at least : "+move.getFMinTimesPerTour());
        maximumNbHits.setText("The attack hits at most : "+move.getFMaxTimesPerTour());
        userFaints.setText("Pokémon faints after attacking : "+move.getFUserFaints());
        nbRoundsToLoad.setText("Number of rounds to load the attack : "+move.getFRoundsToLoad());
        trapsOpponent.setText("Attack traps the opponent for 4 or 5 turns : "+move.getFTrapping());
        flinchingProbability.setText("Flinching probability : "+move.getFFlinchingProbability());
    }

}