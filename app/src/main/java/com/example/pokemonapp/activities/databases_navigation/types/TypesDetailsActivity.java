package com.example.pokemonapp.activities.databases_navigation.types;

import static com.example.pokemonapp.util.Tools.listOfTypesAsStringFromTypeList;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesDetailsActivity extends DatabaseDetailsActivity {

    private Type type;

    private CardView typeNameContainer;
    private TextView typeName;
    private TextView pokemonType;
    private TextView movesType;
    private TextView typeEffective;
    private TextView typeNotEffective;
    private TextView typeNoEffect;

    private PokemonTypeDAO pokemonTypeDAO;
    private MoveTypeDAO moveTypeDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.types_theme_color);
        titleAppbar = getString(R.string.title_appbar_types_db);
        layout = R.layout.activity_types_details;
        super.onCreate(savedInstanceState);

        type = (Type) getIntent().getSerializableExtra(getString(R.string.key_extra_db_resource));

        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();

        getLayoutElements();
        bind();
    }

    @Override
    protected void getLayoutElements() {
        typeNameContainer = findViewById(R.id.type_name_container);
        typeName = findViewById(R.id.type_name);
        pokemonType = findViewById(R.id.pokemon_type);
        movesType = findViewById(R.id.moves_type);
        typeEffective = findViewById(R.id.type_effective);
        typeNotEffective = findViewById(R.id.type_not_effective);
        typeNoEffect = findViewById(R.id.type_no_effect);
    }

    @Override
    protected void bind() {
        typeNameContainer.setCardBackgroundColor(Color.parseColor("#"+type.getFColorCode()));
        typeName.setText(type.getFName());
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.add(pokemonTypeDAO.getNbPokemonWithThisType(type.getFId()));
                objects.add(moveTypeDAO.getNbMovesWithThisType(type.getFId()));
                objects.add(listOfTypesAsStringFromTypeList(typeEffectiveDAO.getEffectiveTypes(type.getFId())));
                objects.add(listOfTypesAsStringFromTypeList(typeNotEffectiveDAO.getNotEffectiveTypes(type.getFId())));
                objects.add(listOfTypesAsStringFromTypeList(typeNoEffectDAO.getNoEffectTypes(type.getFId())));
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                Integer nbOfPokemon = (Integer) objects.get(0);
                Integer nbOfMoves = (Integer) objects.get(1);
                String effectiveTypes = (String) objects.get(2);
                String notEffectiveTypes = (String) objects.get(3);
                String noEffectTypes = (String) objects.get(4);
                pokemonType.setText(nbOfPokemon.toString() + getString(R.string.pokemon_have_type));
                movesType.setText(nbOfMoves.toString() + getString(R.string.moves_have_type));
                typeEffective.setText(getString(R.string.label_effective_against)+" : "+effectiveTypes);
                typeNotEffective.setText(getString(R.string.label_not_effective_agains)+" : "+notEffectiveTypes);
                typeNoEffect.setText(getString(R.string.label_no_effect_against)+" : "+noEffectTypes);
            }
        }).execute();
    }
}