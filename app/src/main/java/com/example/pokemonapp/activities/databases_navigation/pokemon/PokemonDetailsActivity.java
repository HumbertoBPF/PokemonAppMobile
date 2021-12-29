package com.example.pokemonapp.activities.databases_navigation.pokemon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseDetailsActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PokemonDetailsActivity extends DatabaseDetailsActivity {

    private Pokemon pokemon;

    private TextView pokemonName;
    private CardView pokemonTypeContainer1;
    private TextView pokemonType1;
    private CardView pokemonTypeContainer2;
    private TextView pokemonType2;
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
    private TextView pokemonForce;
    private ImageView pokemonImage;

    private PokemonTypeDAO pokemonTypeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_pokemon_db);
        layout = R.layout.activity_pokemon_details;
        super.onCreate(savedInstanceState);

        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();

        pokemon = (Pokemon) getIntent().getSerializableExtra("databaseElement");

        getLayoutElements();
        bind();
    }

    protected void getLayoutElements() {
        pokemonName = findViewById(R.id.pokemon_name);
        pokemonTypeContainer1 = findViewById(R.id.pokemon_type_container_1);
        pokemonType1 = findViewById(R.id.pokemon_type_1);
        pokemonTypeContainer2 = findViewById(R.id.pokemon_type_container_2);
        pokemonType2 = findViewById(R.id.pokemon_type_2);
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
        pokemonForce = findViewById(R.id.pokemon_force);
        pokemonImage = findViewById(R.id.pokemon_image);
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
                Type type1 = (Type) objects.get(0);
                pokemonTypeContainer2.setCardBackgroundColor(Color.parseColor("#"+type1.getFColorCode()));
                pokemonType2.setText(type1.getFName());
                if (objects.size() > 1){    // if the pokémon has a second type, add it to layout
                    Type type2 = (Type) objects.get(1);
                    pokemonTypeContainer1.setCardBackgroundColor(Color.parseColor("#"+type2.getFColorCode()));
                    pokemonType1.setText(type2.getFName());
                }else{
                    pokemonTypeContainer1.setVisibility(View.GONE);
                }
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
        pokemonForce.setText("Force(sum of the stats) : "+(pokemon.getFAttack()+pokemon.getFDefense()+
                pokemon.getFSpAttack()+pokemon.getFSpDefense()+pokemon.getFSpeed()+pokemon.getFHp()));
        String pokemonImageName = "pokemon_"+
                pokemon.getFName().toLowerCase(Locale.ROOT)
                        .replace("'","")
                        .replace(" ","_")
                        .replace(".","");
        int imageId = getResources().getIdentifier(pokemonImageName,"drawable",getPackageName());
        pokemonImage.setImageResource(imageId);
    }

}