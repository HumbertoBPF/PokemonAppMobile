package com.example.pokemonapp.activities.databases_navigation.moves;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDetailsActivity extends DatabaseDetailsActivity {

    private Move move;

    private TextView moveName;
    private CardView moveTypeContainer;
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
        titleAppbar = getString(R.string.title_appbar_moves_db);
        layout = R.layout.activity_moves_details;
        super.onCreate(savedInstanceState);

        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();

        move = (Move) getIntent().getSerializableExtra(getString(R.string.key_extra_db_resource));

        getLayoutElements();
        bind();
    }

    protected void getLayoutElements() {
        moveName = findViewById(R.id.move_name);
        moveTypeContainer = findViewById(R.id.move_type_container);
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
        moveName.setText(getString(R.string.label_name)+" : "+move.getFName());
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.addAll(moveTypeDAO.getTypesOfMove(move.getFId()));
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                Type type = (Type) objects.get(0);
                moveTypeContainer.setCardBackgroundColor(Color.parseColor("#"+type.getFColorCode()));
                moveType.setText(listOfTypesAsString(objects));
            }
        }).execute();
        moveCategory.setText(getString(R.string.label_category)+"\n"+move.getFCategory());
        movePower.setText(getString(R.string.power_label)+"\n"+move.getFPower());
        moveAccuracy.setText(getString(R.string.accuracy_label)+"\n"+move.getFAccuracy());
        movePp.setText(getString(R.string.pp_label)+"\n"+move.getFPp());
        minimumNbHits.setText(getString(R.string.label_min_hits)+" : "+move.getFMinTimesPerTour());
        maximumNbHits.setText(getString(R.string.label_max_hits)+" : "+move.getFMaxTimesPerTour());
        userFaints.setText(getString(R.string.label_faints)+" : "+move.getFUserFaints());
        nbRoundsToLoad.setText(getString(R.string.label_nb_rounds_load)+" : "+move.getFRoundsToLoad());
        trapsOpponent.setText(getString(R.string.label_attack_traps)+" : "+move.getFTrapping());
        flinchingProbability.setText(getString(R.string.label_flinching_prob)+" : "+move.getFFlinchingProbability());
    }

}