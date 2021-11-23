package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView gameDescription;
    private RecyclerView playerRecyclerView;
    private TextView playerPokemonName;
    private TextView playerPokemonHP;
    private TextView cpuPokemonName;
    private TextView cpuPokemonHP;

    private Handler handler;

    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;

    private List<InGamePokemon> teamPlayer;
    private InGamePokemon pokemonPlayer;
    private List<InGamePokemon> teamCPU;
    private InGamePokemon pokemonCPU;
    private List<Integer> indexesSequenceCPU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Game activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameDescription = findViewById(R.id.game_description);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        playerPokemonName = findViewById(R.id.player_pokemon_name);
        playerPokemonHP = findViewById(R.id.player_pokemon_hp);
        cpuPokemonName = findViewById(R.id.cpu_pokemon_name);
        cpuPokemonHP = findViewById(R.id.cpu_pokemon_hp);

        handler = new Handler();

        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();

        teamPlayer = loadTeam(this,getResources().getString(R.string.filename_json_player_team));
        teamCPU = loadTeam(this,getResources().getString(R.string.filename_json_cpu_team));
        indexesSequenceCPU = getDistinctRandomIntegers(0,teamCPU.size()-1,teamCPU.size());

        pickPokemonForCPU();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pickPokemonForPlayer();
            }
        },5000);
    }

    private void pickPokemonForPlayer(){
        gameDescription.setText("Choose a pokémon");
        playerRecyclerView.setVisibility(View.VISIBLE);
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : teamPlayer){
            Pokemon pokemon = inGamePokemon.getPokemonServer();
            if (pokemon.getFHp() > 0){
                pokemonList.add(pokemon);
            }
        }
        playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                pokemonList, new PokemonAdapter.OnClickListener() {
            @Override
            public void onClick(Pokemon pokemon) {
                for (InGamePokemon inGamePokemon : teamPlayer){
                    if (inGamePokemon.getPokemonServer().getFId().equals(pokemon.getFId())){
                        pokemonPlayer = inGamePokemon;
                        playerRecyclerView.setVisibility(View.GONE);
                    }
                }
                Pokemon pokemonServerPlayer = pokemonPlayer.getPokemonServer();
                gameDescription.setText("You choose "+pokemonServerPlayer.getFName());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTextHP(pokemonServerPlayer, playerPokemonName, playerPokemonHP);
                    }
                },2000);
            }
        }));
    }

    private void setTextHP(Pokemon pokemonServer, TextView pokemonName, TextView pokemonHP) {
        pokemonName.setText(pokemonServer.getFName());
        int hpBarColor = R.color.pokemon_theme_color;
        if (pokemonServer.getFHp()>50){
            hpBarColor = R.color.green_hp_bar;
        }else if (pokemonServer.getFHp()>20){
            hpBarColor = R.color.moves_theme_color;
        }
        pokemonHP.setTextColor(getResources().getColor(hpBarColor));
        pokemonHP.setText("HP : "+pokemonServer.getFHp().toString());
    }

    private void pickPokemonForCPU(){
        if (!indexesSequenceCPU.isEmpty()){
            pokemonCPU = teamCPU.get(indexesSequenceCPU.get(0));
            indexesSequenceCPU.remove(0);
            gameDescription.setText("CPU chooses "+pokemonCPU.getPokemonServer().getFName());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTextHP(pokemonCPU.getPokemonServer(), cpuPokemonName, cpuPokemonHP);
                }
            },2000);
        }else{
            Toast.makeText(this,"The game is finished",Toast.LENGTH_LONG).show();
        }
    }

    private void attack(Pokemon attackingPokemon, Pokemon defendingPokemon, Move move){
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                Double stab = 1.0;
                Double typeFactor = 1.0;
                Type moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0);
                List<Type> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(attackingPokemon.getFId());
                List<Type> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(defendingPokemon.getFId());
                for (Type typeAttack : attackingPokemonTypes){
                    List<Type> effectiveTypes = typeEffectiveDAO.getEffectiveTypes(typeAttack.getFId());
                    List<Type> notEffectiveTypes = typeNotEffectiveDAO.getNotEffectiveTypes(typeAttack.getFId());
                    List<Type> noEffectType = typeNoEffectDAO.getNoEffectTypes(typeAttack.getFId());
                    for (Type typeDefending : defendingPokemonTypes){
                        if (effectiveTypes.contains(typeDefending)){
                            typeFactor *= 2;
                        }
                        if (notEffectiveTypes.contains(typeDefending)){
                            typeFactor *= 0.5;
                        }
                        if (noEffectType.contains(typeDefending)){
                            typeFactor *= 0;
                        }
                    }
                }
                if (attackingPokemonTypes.contains(moveType)){
                    stab = 1.5;
                }
                List<Object> objects = new ArrayList<>();
                objects.add(stab);
                objects.add(typeFactor);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                double attackStat;
                double defenseStat;
                double random = Math.random()*0.15 + 0.85;
                Double stab = (Double) objects.get(0);
                Double typeFactor = (Double) objects.get(1);
                if (move.getFCategory().equals("Special")){
                    attackStat = attackingPokemon.getFSpAttack();
                    defenseStat = defendingPokemon.getFSpDefense();
                }else{
                    attackStat = attackingPokemon.getFAttack();
                    defenseStat = defendingPokemon.getFDefense();
                }
                double damage = ((42*move.getFPower()*(attackStat/defenseStat))/50 + 2)*random*stab*typeFactor;
                double defendingPokemonCurrentHP = defendingPokemon.getFHp();
                defendingPokemon.setFHp((int) (defendingPokemonCurrentHP - damage));
            }
        }).execute();
    }

}