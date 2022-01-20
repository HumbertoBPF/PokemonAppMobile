package com.example.pokemonapp.activities.databases_navigation.types;

import static com.example.pokemonapp.util.GeneralTools.listOfTypesAsString;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

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
        type = (Type) getIntent().getSerializableExtra(getString(R.string.key_extra_db_resource));
        getDAOs();
        super.onCreate(savedInstanceState);
    }

    private void getDAOs() {
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();
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
        pokemonTypeDAO.nbPokemonWithThisTypeTask(type, new OnResultListener<Integer>() {
            @Override
            public void onResult(Integer result) {
                pokemonType.setText(result.toString() + getString(R.string.pokemon_have_type));
            }
        }).execute();
        moveTypeDAO.nbMovesWithThisTypeTask(type, new OnResultListener<Integer>() {
            @Override
            public void onResult(Integer result) {
                movesType.setText(result.toString() + getString(R.string.moves_have_type));
            }
        }).execute();
        typeEffectiveDAO.effectiveTypesTask(type, new OnResultListener<List<Type>>() {
            @Override
            public void onResult(List<Type> result) {
                typeEffective.setText(getString(R.string.label_effective_against)+" : "+ listOfTypesAsString(result));
            }
        }).execute();
        typeNotEffectiveDAO.notEffectiveTypesTask(type, new OnResultListener<List<Type>>() {
            @Override
            public void onResult(List<Type> result) {
                typeNotEffective.setText(getString(R.string.label_not_effective_agains)+" : "+ listOfTypesAsString(result));
            }
        }).execute();
        typeNoEffectDAO.noEffectTypesTask(type, new OnResultListener<List<Type>>() {
            @Override
            public void onResult(List<Type> result) {
                typeNoEffect.setText(getString(R.string.label_no_effect_against)+" : "+ listOfTypesAsString(result));
            }
        }).execute();
    }
}