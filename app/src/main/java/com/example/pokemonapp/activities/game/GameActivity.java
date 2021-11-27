package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final String TAG = "GameActivity";

    private TextView gameDescription;
    private RecyclerView playerRecyclerView;
    private TextView playerPokemonName;
    private TextView playerPokemonHP;
    private TextView cpuPokemonName;
    private TextView cpuPokemonHP;

    private Handler handler = new Handler();

    private PokemonDAO pokemonDAO;
    private MoveDAO moveDAO;
    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;

    private List<Pokemon> allPokemon;
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

        getLayoutElements();
        getDAOs();

        teamPlayer = loadTeam(this,getResources().getString(R.string.filename_json_player_team));
        teamCPU = loadTeam(this,getResources().getString(R.string.filename_json_cpu_team));
        indexesSequenceCPU = getDistinctRandomIntegers(0,teamCPU.size()-1,teamCPU.size());

        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                allPokemon = pokemonDAO.getPokemonFromLocal();
                objects.addAll(allPokemon);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                pickPokemonForCPU();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pickPokemonForPlayer(new OnChoiceListener() {
                            @Override
                            public void onChoice() {
                                battle();
                            }
                        });
                    }
                },6000);
            }
        }).execute();
    }

    private void getLayoutElements() {
        gameDescription = findViewById(R.id.game_description);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        playerPokemonName = findViewById(R.id.player_pokemon_name);
        playerPokemonHP = findViewById(R.id.player_pokemon_hp);
        cpuPokemonName = findViewById(R.id.cpu_pokemon_name);
        cpuPokemonHP = findViewById(R.id.cpu_pokemon_hp);
    }

    private void getDAOs() {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();
    }

    private void battle() {
        Log.i(TAG,"Pokemon player HP : "+pokemonPlayer.getPokemonServer().getFHp());
        Log.i(TAG,"CPU player HP : "+pokemonCPU.getPokemonServer().getFHp());
        if ((pokemonPlayer.getPokemonServer().getFHp()>0)&&(pokemonCPU.getPokemonServer().getFHp()>0)){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"Pokemon player speed : "+pokemonPlayer.getPokemonServer().getFSpeed());
                    Log.i(TAG,"Pokemon CPU speed : "+pokemonCPU.getPokemonServer().getFSpeed());
                    if (pokemonPlayer.getPokemonServer().getFSpeed()<pokemonCPU.getPokemonServer().getFSpeed()){
                        pickMoveForCpuAndAttack(new OnChoiceListener() {
                            @Override
                            public void onChoice() {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pokemonPlayer.getPokemonServer().getFHp() > 0){
                                            pickMoveForPlayerAndAttack(new OnChoiceListener() {
                                                @Override
                                                public void onChoice() {
                                                    battle();
                                                }
                                            });
                                        }else{
                                            gameDescription.setText("Player's "+pokemonPlayer.getPokemonServer().getFName()+" fainted.");
                                            handler.postDelayed(GameActivity.this::pickAnotherPlayerPokemonOrEndGame,3000);
                                        }
                                    }
                                },3000);
                            }
                        });
                    }else{
                        pickMoveForPlayerAndAttack(new OnChoiceListener() {
                            @Override
                            public void onChoice() {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pokemonCPU.getPokemonServer().getFHp() > 0){
                                            pickMoveForCpuAndAttack(new OnChoiceListener() {
                                                @Override
                                                public void onChoice() {
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            battle();
                                                        }
                                                    },3000);
                                                }
                                            });
                                        }else{
                                            gameDescription.setText("Foe's "+pokemonCPU.getPokemonServer().getFName()+" fainted.");
                                            handler.postDelayed(GameActivity.this::pickAnotherCpuPokemonOrEndgame,3000);
                                        }
                                    }
                                },3000);
                            }
                        });
                    }
                }
            },5000);
        }else if (pokemonPlayer.getPokemonServer().getFHp()<=0){
            gameDescription.setText("Player's "+pokemonPlayer.getPokemonServer().getFName()+" fainted.");
            handler.postDelayed(this::pickAnotherPlayerPokemonOrEndGame,3000);
        }else if (pokemonCPU.getPokemonServer().getFHp()<=0){
            gameDescription.setText("Foe's "+pokemonCPU.getPokemonServer().getFName()+" fainted.");
            handler.postDelayed(this::pickAnotherCpuPokemonOrEndgame,3000);
        }
    }

    private void pickAnotherCpuPokemonOrEndgame() {
        if (!indexesSequenceCPU.isEmpty()){
            Log.i(TAG,"CPU changes its pokemon");
            pickPokemonForCPU();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    battle();
                }
            },6000);
        }else{
            Log.i(TAG,"CPU was defeated");
           gameDescription.setText("Foe was defeated.");
        }
    }

    private void pickAnotherPlayerPokemonOrEndGame() {
        if (getNbOfRemainingPokemonPlayer() > 0){
            Log.i(TAG,"Player changes its pokemon");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pickPokemonForPlayer(new OnChoiceListener() {
                        @Override
                        public void onChoice() {
                            battle();
                        }
                    });
                }
            },6000);
        }else{
            Log.i(TAG,"Player was defeated");
            gameDescription.setText("Player was defeated.");
        }
    }

    private int getNbOfRemainingPokemonPlayer(){
        int nbOfRemainingPokemon = 0;
        for (InGamePokemon inGamePokemon : teamPlayer){
            if (inGamePokemon.getPokemonServer().getFHp() > 0){
                nbOfRemainingPokemon++;
            }
        }
        Log.i(TAG,"nbOfRemainingPokemon = "+nbOfRemainingPokemon);
        return nbOfRemainingPokemon;
    }

    private void pickPokemonForPlayer(OnChoiceListener onChoiceListener){
        gameDescription.setText("Choose a pokémon");
        playerRecyclerView.setVisibility(View.VISIBLE);
        playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(), getPokemonPlayer(),
                new PokemonAdapter.OnClickListener() {
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
                                onChoiceListener.onChoice();
                            }
                        },3000);
                    }
                })
        );
    }

    @NonNull
    private List<Pokemon> getPokemonPlayer() {
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : teamPlayer){
            Pokemon pokemon = inGamePokemon.getPokemonServer();
            if (pokemon.getFHp() > 0){
                pokemonList.add(pokemon);
            }
        }
        return pokemonList;
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
            },3000);
        }else{
            Toast.makeText(this,"The game is finished",Toast.LENGTH_LONG).show();
        }
    }

    private void pickMoveForPlayerAndAttack(OnChoiceListener onChoiceListener){
        List<Move> moves = getMovesPlayerPokemon();
        if (moves.size() == 0){
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    Move move = moveDAO.getMoveByName("Struggle");
                    List<Object> objects = new ArrayList<>();
                    objects.add(move);
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    Move move = (Move) objects.get(0);
                    gameDescription.setText("Player's pokémon used "+move.getFName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            attack(pokemonPlayer.getPokemonServer(),pokemonCPU.getPokemonServer(),move, new OnChoiceListener() {
                                @Override
                                public void onChoice() {
                                    onChoiceListener.onChoice();
                                }
                            });
                        }
                    },3000);
                }
            }).execute();
        }else{
            gameDescription.setText("Choose a move");
            playerRecyclerView.setVisibility(View.VISIBLE);
            playerRecyclerView.setAdapter(new MovesAdapter(this, moves, new MovesAdapter.OnClickListener() {
                @Override
                public void onClick(Move move) {
                    playerRecyclerView.setVisibility(View.GONE);
                    gameDescription.setText("Player's "+pokemonPlayer.getPokemonServer().getFName()+" used "+move.getFName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            attack(pokemonPlayer.getPokemonServer(), pokemonCPU.getPokemonServer(), move, new OnChoiceListener() {
                                @Override
                                public void onChoice() {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            onChoiceListener.onChoice();
                                        }
                                    },3000);
                                }
                            });
                        }
                    },3000);
                }
            }));
        }
    }

    private List<Move> getMovesPlayerPokemon() {
        List<Move> moves = new ArrayList<>();
        for (Move move : pokemonPlayer.getMoves()){
            if (move.getFPp() > 0){
                moves.add(move);
            }
        }
        return moves;
    }

    private void pickMoveForCpuAndAttack(OnChoiceListener onChoiceListener){
        List<Move> availableMoves = new ArrayList<>();
        for (Move move : pokemonCPU.getMoves()){
            if (move.getFPp() > 0){
                availableMoves.add(move);
            }
        }
        if (availableMoves.isEmpty()){
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    Move move = moveDAO.getMoveByName("Struggle");
                    List<Object> objects = new ArrayList<>();
                    objects.add(move);
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    Move move = (Move) objects.get(0);
                    gameDescription.setText("Foe's pokémon used "+move.getFName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            attack(pokemonCPU.getPokemonServer(),pokemonPlayer.getPokemonServer(),move,onChoiceListener);
                        }
                    },3000);
                }
            }).execute();
        }else{
            int randomIndex = getDistinctRandomIntegers(0,availableMoves.size()-1,1).get(0);
            Move move = availableMoves.get(randomIndex);
            gameDescription.setText("Foe's pokémon used "+move.getFName());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attack(pokemonCPU.getPokemonServer(),pokemonPlayer.getPokemonServer(),move,onChoiceListener);
                }
            },3000);
        }
    }

    private void setTextHP(Pokemon pokemonServer, TextView pokemonName, TextView pokemonHP) {
        pokemonName.setText(pokemonServer.getFName());
        long fullHp = 100;
        for (Pokemon pokemon : allPokemon){
            if (pokemon.getFId().equals(pokemonServer.getFId())){
                fullHp = pokemon.getFHp();
                break;
            }
        }
        int hpBarColor = R.color.pokemon_theme_color;
        if (pokemonServer.getFHp()>0.5*fullHp){
            hpBarColor = R.color.green_hp_bar;
        }else if (pokemonServer.getFHp()>0.2*fullHp){
            hpBarColor = R.color.moves_theme_color;
        }
        pokemonHP.setTextColor(getResources().getColor(hpBarColor));
        Integer hp = pokemonServer.getFHp();
        pokemonHP.setText("HP : "+hp.toString());
        if (hp < 0){
            pokemonHP.setText("HP : 0");
        }
    }

    private void attack(Pokemon attackingPokemon, Pokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener){
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                Long moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0).getFId();
                List<Long> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(attackingPokemon.getFId());
                List<Long> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(defendingPokemon.getFId());

                Log.i(TAG,"Move type : "+moveType);
                Log.i(TAG,"Attacking pokémon types : "+attackingPokemonTypes.toString());
                Log.i(TAG,"Defending pokémon types : "+defendingPokemonTypes.toString());

                List<Long> effectiveTypes = typeEffectiveDAO.getEffectiveTypesIds(moveType);
                List<Long> notEffectiveTypes = typeNotEffectiveDAO.getNotEffectiveTypesIds(moveType);
                List<Long> noEffectType = typeNoEffectDAO.getNoEffectTypesIds(moveType);

                Log.i(TAG,"Effective types : "+effectiveTypes.toString());
                Log.i(TAG,"Not effective types : "+notEffectiveTypes.toString());
                Log.i(TAG,"No effect types : "+noEffectType.toString());

                List<Object> objects = new ArrayList<>();
                objects.add(moveType);
                objects.add(attackingPokemonTypes);
                objects.add(defendingPokemonTypes);
                objects.add(effectiveTypes);
                objects.add(notEffectiveTypes);
                objects.add(noEffectType);

                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                Long moveType = (Long) objects.get(0);
                List<Long> attackingPokemonTypes = (List<Long>) objects.get(1);
                List<Long> defendingPokemonTypes = (List<Long>) objects.get(2);
                List<Long> effectiveTypes = (List<Long>) objects.get(3);
                List<Long> notEffectiveTypes = (List<Long>) objects.get(4);
                List<Long> noEffectType = (List<Long>) objects.get(5);

                double stab = attackingPokemonTypes.contains(moveType) ? 1.5 : 1.0;
                double typeFactor = computeTypeFactor(defendingPokemonTypes, effectiveTypes, notEffectiveTypes, noEffectType);
                String messageEffectiveness = getMessageEffectiveness(typeFactor);
                double attackStat = move.getFCategory().equals("Special")?attackingPokemon.getFSpAttack():attackingPokemon.getFAttack();
                double defenseStat = move.getFCategory().equals("Special")?defendingPokemon.getFSpDefense():defendingPokemon.getFDefense();
                double random = Math.random()*0.15 + 0.85;

                Log.i(TAG,"RANDOM:"+random+" "+"STAB:"+stab+" "+"TYPE_FACTOR:"+typeFactor);
                double damage = ((42*move.getFPower()*(attackStat/defenseStat))/50 + 2)*random*stab*typeFactor;
                double defendingPokemonCurrentHP = defendingPokemon.getFHp();
                if (defendingPokemon.getFId().equals(pokemonCPU.getPokemonServer().getFId())){
                    pokemonCPU.getPokemonServer().setFHp((int) (defendingPokemonCurrentHP - damage));
                    setTextHP(pokemonCPU.getPokemonServer(),cpuPokemonName,cpuPokemonHP);
                    pokemonPlayer.setMoves(updatePPs(pokemonPlayer,move));
                }else{
                    pokemonPlayer.getPokemonServer().setFHp((int) (defendingPokemonCurrentHP - damage));
                    setTextHP(pokemonPlayer.getPokemonServer(),playerPokemonName,playerPokemonHP);
                    pokemonCPU.setMoves(updatePPs(pokemonCPU,move));
                }
                gameDescription.setText(messageEffectiveness);

                onChoiceListener.onChoice();
            }
        }).execute();
    }

    private String getMessageEffectiveness(double typeFactor) {
        String messageEffectiveness = null;
        if (typeFactor > 1){
            messageEffectiveness = "It's super effective!";
        }else if (typeFactor < 1 && typeFactor >0){
            messageEffectiveness = "It's not effective...";
        }else if (typeFactor == 0){
            messageEffectiveness = "It has no effect...";
        }
        return messageEffectiveness;
    }

    private double computeTypeFactor(List<Long> defendingPokemonTypes, List<Long> effectiveTypes, List<Long> notEffectiveTypes, List<Long> noEffectType) {
        double typeFactor = 1.0;
        for (Long typeDefending : defendingPokemonTypes){
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
        return typeFactor;
    }

    @NonNull
    private List<Move> updatePPs(InGamePokemon inGamePokemon, Move move) {
        List<Move> moves = inGamePokemon.getMoves();
        for (Move m : moves){
            if (m.getFId().equals(move.getFId())){
                int pp = m.getFPp()-1;
                m.setFPp(pp);
            }
        }
        return moves;
    }

    interface OnChoiceListener {
        void onChoice();
    }

}