package com.example.pokemonapp.activities.databases_navigation.pokemon;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;

import android.os.Bundle;
import android.widget.TextView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDetailsActivity extends DatabaseDetailsActivity {

    private Pokemon pokemon;
    private TextView pokemonName;
    private TextView pokemonTypes;
    private TextView pokemonCategory;
    private TextView pokemonDescription;
    private TextView pokemonHeight;
    private TextView pokemonWeight;
    private TextView pokemonGender;
    private TextView pokemonAttack;
    private TextView pokemonDefense;
    private TextView pokemonSpAttack;
    private TextView pokemonSpDefense;
    private TextView pokemonSpeed;
    private TextView pokemonHp;

    private PokemonTypeDAO pokemonTypeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_pokemon_db);
        layout = R.layout.activity_pokemon_details;
        super.onCreate(savedInstanceState);

        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();

        pokemon = (Pokemon) getIntent().getSerializableExtra("pokemon");

        getLayoutElements();
        bind();
    }

    protected void getLayoutElements() {
        pokemonName = findViewById(R.id.pokemon_name);
        pokemonTypes = findViewById(R.id.pokemon_types);
        pokemonCategory = findViewById(R.id.pokemon_category);
        pokemonDescription = findViewById(R.id.pokemon_description);
        pokemonWeight = findViewById(R.id.pokemon_weight);
        pokemonHeight = findViewById(R.id.pokemon_height);
        pokemonGender = findViewById(R.id.pokemon_gender);
        pokemonAttack = findViewById(R.id.pokemon_attack);
        pokemonDefense = findViewById(R.id.pokemon_defense);
        pokemonSpAttack = findViewById(R.id.pokemon_sp_attack);
        pokemonSpDefense = findViewById(R.id.pokemon_sp_defense);
        pokemonSpeed = findViewById(R.id.pokemon_speed);
        pokemonHp = findViewById(R.id.pokemon_hp);
    }

    protected void bind() {
        pokemonName.setText("Name : "+pokemon.getFName());
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                objects.addAll(pokemonTypeDAO.getTypesOfPokemon(pokemon.getFId()));
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                pokemonTypes.setText("Types : "+listOfTypesAsString(objects));
            }
        }).execute();
        pokemonCategory.setText("Category : "+pokemon.getFCategory());
        pokemonDescription.setText("Description : "+pokemon.getFDescription());
        pokemonWeight.setText("Weight\n"+pokemon.getFWeight());
        pokemonHeight.setText("Height\n"+pokemon.getFHeight());
        pokemonGender.setText("Gender\n"+pokemon.getFGender());
        pokemonAttack.setText("Att.\n"+pokemon.getFAttack());
        pokemonDefense.setText("Def.\n"+pokemon.getFDefense());
        pokemonSpAttack.setText("Sp.Att.\n"+pokemon.getFSpAttack());
        pokemonSpDefense.setText("Sp.Def.\n"+pokemon.getFSpDefense());
        pokemonSpeed.setText("Speed\n"+pokemon.getFSpeed());
        pokemonHp.setText("HP\n"+pokemon.getFHp());
    }

}