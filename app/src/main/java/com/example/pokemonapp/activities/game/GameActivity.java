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
import com.example.pokemonapp.models.Trainer;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final String TAG = "GameActivity";

    private TextView gameDescription;
    private RecyclerView playerRecyclerView;

    private Handler handler = new Handler();

    private PokemonDAO pokemonDAO;
    private MoveDAO moveDAO;
    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;

    private List<Pokemon> allPokemon;
    private List<Integer> indexesSequenceCPU;

    private final Trainer player = new Trainer();
    private final Trainer cpu = new Trainer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Game activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getLayoutElements();
        getDAOs();

        player.setTeam(loadTeam(this,getResources().getString(R.string.filename_json_player_team)));
        cpu.setTeam(loadTeam(this,getResources().getString(R.string.filename_json_cpu_team)));
        indexesSequenceCPU = getDistinctRandomIntegers(0,player.getTeam().size()-1,cpu.getTeam().size());

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
        player.setCurrentPokemonName(findViewById(R.id.player_pokemon_name));
        player.setCurrentPokemonHP(findViewById(R.id.player_pokemon_hp));
        cpu.setCurrentPokemonName(findViewById(R.id.cpu_pokemon_name));
        cpu.setCurrentPokemonHP(findViewById(R.id.cpu_pokemon_hp));
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
        Log.i(TAG,"Pokemon player HP : "+player.getCurrentPokemon().getPokemonServer().getFHp());
        Log.i(TAG,"CPU player HP : "+cpu.getCurrentPokemon().getPokemonServer().getFHp());
        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
            pickMoveForPlayer(new OnChoiceListener() {
                @Override
                public void onChoice() {
                    pickMoveForCpu(new OnChoiceListener() {
                        @Override
                        public void onChoice() {
                            Log.i(TAG,"Pokemon player speed : "+player.getCurrentPokemon().getPokemonServer().getFSpeed());
                            Log.i(TAG,"Pokemon CPU speed : "+cpu.getCurrentPokemon().getPokemonServer().getFSpeed());
                            if (player.getCurrentPokemon().getPokemonServer().getFSpeed()<cpu.getCurrentPokemon().getPokemonServer().getFSpeed()){
                                attack(cpu.getCurrentPokemon(),player.getCurrentPokemon(),cpu.getCurrentMove(), new OnChoiceListener() {
                                    @Override
                                    public void onChoice() {
                                        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
                                            attack(player.getCurrentPokemon(),cpu.getCurrentPokemon(),player.getCurrentMove(),
                                                    new OnChoiceListener() {
                                                        @Override
                                                        public void onChoice() {
                                                            battle();
                                                        }
                                                    });
                                        }else if (!player.isPokemonAlive()){
                                            gameDescription.setText(getString(R.string.player_possessive)+
                                                    player.getCurrentPokemon().getPokemonServer().getFName()+ getString(R.string.fainted));
                                            handler.postDelayed(GameActivity.this::pickAnotherPlayerPokemonOrEndGame,3000);
                                        }else if (!cpu.isPokemonAlive()){
                                            gameDescription.setText(getString(R.string.foe_possessive)+
                                                    cpu.getCurrentPokemon().getPokemonServer().getFName()+ getString(R.string.fainted));
                                            handler.postDelayed(GameActivity.this::pickAnotherCpuPokemonOrEndgame,3000);
                                        }
                                    }
                                });
                            }else{
                                attack(player.getCurrentPokemon(),cpu.getCurrentPokemon(),player.getCurrentMove(), new OnChoiceListener() {
                                    @Override
                                    public void onChoice() {
                                        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
                                            attack(cpu.getCurrentPokemon(),player.getCurrentPokemon(),cpu.getCurrentMove(),new OnChoiceListener() {
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
                                        }else if (!player.isPokemonAlive()){
                                            gameDescription.setText(getString(R.string.player_possessive)+
                                                    player.getCurrentPokemon().getPokemonServer().getFName()+ getString(R.string.fainted));
                                            handler.postDelayed(GameActivity.this::pickAnotherPlayerPokemonOrEndGame,3000);
                                        }else if (!cpu.isPokemonAlive()){
                                            gameDescription.setText(getString(R.string.foe_possessive)+
                                                    cpu.getCurrentPokemon().getPokemonServer().getFName()+ getString(R.string.fainted));
                                            handler.postDelayed(GameActivity.this::pickAnotherCpuPokemonOrEndgame,3000);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else if (!player.isPokemonAlive()){
            gameDescription.setText(getString(R.string.player_possessive)+
                    player.getCurrentPokemon().getPokemonServer().getFName()+getString(R.string.fainted));
            handler.postDelayed(this::pickAnotherPlayerPokemonOrEndGame,3000);
        }else if (!cpu.isPokemonAlive()){
            gameDescription.setText(getString(R.string.foe_possessive)+
                    cpu.getCurrentPokemon().getPokemonServer().getFName()+getString(R.string.fainted));
            handler.postDelayed(this::pickAnotherCpuPokemonOrEndgame,3000);
        }
    }

    private void pickAnotherCpuPokemonOrEndgame() {
        cpu.reset();
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
           gameDescription.setText(R.string.foe_defeat_msg);
        }
    }

    private void pickAnotherPlayerPokemonOrEndGame() {
        player.reset();
        if (getNbOfRemainingPokemonPlayer() > 0){
            Log.i(TAG,"Player changes its pokemon");
            pickPokemonForPlayer(new OnChoiceListener() {
                @Override
                public void onChoice() {
                    battle();
                }
            });
        }else{
            Log.i(TAG,"Player was defeated");
            gameDescription.setText(R.string.player_defeat_msg);
        }
    }

    private int getNbOfRemainingPokemonPlayer(){
        int nbOfRemainingPokemon = 0;
        for (InGamePokemon inGamePokemon : player.getTeam()){
            if (inGamePokemon.getPokemonServer().getFHp() > 0){
                nbOfRemainingPokemon++;
            }
        }
        Log.i(TAG,"nbOfRemainingPokemon = "+nbOfRemainingPokemon);
        return nbOfRemainingPokemon;
    }

    private void pickPokemonForPlayer(OnChoiceListener onChoiceListener){
        gameDescription.setText(R.string.choose_pokemon_msg);
        playerRecyclerView.setVisibility(View.VISIBLE);
        playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(), getPokemonPlayer(),
                new PokemonAdapter.OnClickListener() {
                    @Override
                    public void onClick(Pokemon pokemon) {
                        for (InGamePokemon inGamePokemon : player.getTeam()){
                            if (inGamePokemon.getPokemonServer().getFId().equals(pokemon.getFId())){
                                player.setCurrentPokemon(inGamePokemon);
                                playerRecyclerView.setVisibility(View.GONE);
                            }
                        }
                        Pokemon pokemonServerPlayer = player.getCurrentPokemon().getPokemonServer();
                        gameDescription.setText(getString(R.string.player_chooses)+pokemonServerPlayer.getFName());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setTextHP(pokemonServerPlayer, player.getCurrentPokemonName(), player.getCurrentPokemonHP());
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
        for (InGamePokemon inGamePokemon : player.getTeam()){
            Pokemon pokemon = inGamePokemon.getPokemonServer();
            if (pokemon.getFHp() > 0){
                pokemonList.add(pokemon);
            }
        }
        return pokemonList;
    }

    private void pickPokemonForCPU(){
        if (!cpu.isLoading()){
            if (!indexesSequenceCPU.isEmpty()){
                cpu.setCurrentPokemon(cpu.getTeam().get(indexesSequenceCPU.get(0)));
                indexesSequenceCPU.remove(0);
                gameDescription.setText(getString(R.string.cpu_chooses)+cpu.getCurrentPokemon().getPokemonServer().getFName());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTextHP(cpu.getCurrentPokemon().getPokemonServer(), cpu.getCurrentPokemonName(), cpu.getCurrentPokemonHP());
                    }
                },3000);
            }else{
                Toast.makeText(this,"The game is finished",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void pickMoveForPlayer(OnChoiceListener onChoiceListener){
        if (!player.isLoading()){
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
                        player.setCurrentMove((Move) objects.get(0));
                        onChoiceListener.onChoice();
                    }
                }).execute();
            }else{
                gameDescription.setText(R.string.choose_move_msg);
                playerRecyclerView.setVisibility(View.VISIBLE);
                playerRecyclerView.setAdapter(new MovesAdapter(this, moves, new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(Move move) {
                        player.setCurrentMove(move);
                        playerRecyclerView.setVisibility(View.GONE);
                        onChoiceListener.onChoice();
                    }
                }));
            }
        }else{
            onChoiceListener.onChoice();
        }
    }

    private List<Move> getMovesPlayerPokemon() {
        List<Move> moves = new ArrayList<>();
        for (Move move : player.getCurrentPokemon().getMoves()){
            if (move.getFPp() > 0){
                moves.add(move);
            }
        }
        return moves;
    }

    private void pickMoveForCpu(OnChoiceListener onChoiceListener){
        List<Move> availableMoves = new ArrayList<>();
        for (Move move : cpu.getCurrentPokemon().getMoves()){
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
                    cpu.setCurrentMove((Move) objects.get(0));
                    onChoiceListener.onChoice();
                }
            }).execute();
        }else{
            int randomIndex = getDistinctRandomIntegers(0,availableMoves.size()-1,1).get(0);
            cpu.setCurrentMove(availableMoves.get(randomIndex));
            onChoiceListener.onChoice();
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
        pokemonHP.setText(getString(R.string.hp_label)+hp.toString());
        if (hp < 0){
            pokemonHP.setText(getString(R.string.hp_label)+"0");
        }
    }

    private void attack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener){
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                Pokemon attackingPokemonServer = attackingPokemon.getPokemonServer();
                Pokemon defendingPokemonServer = defendingPokemon.getPokemonServer();

                Long moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0).getFId();
                List<Long> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(attackingPokemonServer.getFId());
                List<Long> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(defendingPokemonServer.getFId());

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
                Pokemon attackingPokemonServer = attackingPokemon.getPokemonServer();
                Pokemon defendingPokemonServer = defendingPokemon.getPokemonServer();

                Long moveType = (Long) objects.get(0);
                List<Long> attackingPokemonTypes = (List<Long>) objects.get(1);
                List<Long> defendingPokemonTypes = (List<Long>) objects.get(2);
                List<Long> effectiveTypes = (List<Long>) objects.get(3);
                List<Long> notEffectiveTypes = (List<Long>) objects.get(4);
                List<Long> noEffectType = (List<Long>) objects.get(5);

                double stab = attackingPokemonTypes.contains(moveType) ? 1.5 : 1.0;
                double typeFactor = computeTypeFactor(defendingPokemonTypes, effectiveTypes, notEffectiveTypes, noEffectType);
                String messageEffectiveness = getMessageEffectiveness(typeFactor);
                double attackStat = move.getFCategory().equals("Special")?attackingPokemonServer.getFSpAttack(): attackingPokemonServer.getFAttack();
                double defenseStat = move.getFCategory().equals("Special")?defendingPokemonServer.getFSpDefense():defendingPokemonServer.getFDefense();

                int minHits = move.getFMinTimesPerTour();
                int maxHits = move.getFMaxTimesPerTour();

                int nbOfHits = getDistinctRandomIntegers(minHits,maxHits,1).get(0);

                if (move.getFRoundsToLoad() == 1){
                    if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                        if (!player.isLoading()){
                            player.setLoading(true);
                            gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+getString(R.string.loads_attack));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onChoiceListener.onChoice();
                                }
                            },3000);
                            return;
                        }
                    }else{
                        if (!cpu.isLoading()){
                            cpu.setLoading(true);
                            gameDescription.setText(getString(R.string.foe_possessive)+attackingPokemonServer.getFName()+getString(R.string.loads_attack));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onChoiceListener.onChoice();
                                }
                            },3000);
                            return;
                        }
                    }
                }

                if (move.getFRoundsToLoad() == -1){
                    if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                        if (player.isLoading()){
                            player.setLoading(false);
                            gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+getString(R.string.is_reloading));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onChoiceListener.onChoice();
                                }
                            },3000);
                            return;
                        }
                    }else{
                        if (cpu.isLoading()){
                            cpu.setLoading(false);
                            gameDescription.setText(getString(R.string.foe_possessive)+attackingPokemonServer.getFName()+getString(R.string.is_reloading));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onChoiceListener.onChoice();
                                }
                            },3000);
                            return;
                        }
                    }
                }

                if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())) {
                    gameDescription.setText(getString(R.string.player_possessive) + player.getCurrentPokemon().getPokemonServer().getFName() +
                            getString(R.string.used) + player.getCurrentMove().getFName());
                }else{
                    gameDescription.setText(getString(R.string.foe_possessive) + cpu.getCurrentPokemon().getPokemonServer().getFName() +
                            getString(R.string.used) + cpu.getCurrentMove().getFName());
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hit(defendingPokemon, move, stab, typeFactor, messageEffectiveness,
                                attackStat, defenseStat, 1, nbOfHits, onChoiceListener);
                    }
                }, 3000);
            }
        }).execute();
    }

    private void hit(InGamePokemon defendingPokemon, Move move, double stab, double typeFactor,
                     String messageEffectiveness, double attackStat, double defenseStat, int currentHit, int nbOfHits,
                     OnChoiceListener onChoiceListener) {

        if (defendingPokemon.getPokemonServer().getFHp() <= 0){
            onChoiceListener.onChoice();
        }else{
            double randomFactor = Math.random()*0.15 + 0.85;

            if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                if (currentHit == 1){
                    player.getCurrentPokemon().setMoves(updatePPs(player.getCurrentPokemon(), move));
                }
                if (!attackMissed(move)) {
                    Log.i(TAG, "RANDOM FACTOR:" + randomFactor + " " + "STAB:" + stab + " " + "TYPE_FACTOR:" + typeFactor);
                    double damage = ((42 * move.getFPower() * (attackStat / defenseStat)) / 50 + 2) * randomFactor * stab * typeFactor;
                    double defendingPokemonCurrentHP = defendingPokemon.getPokemonServer().getFHp();
                    if (player.isLoading()){
                        player.setLoading(false);
                    }
                    cpu.getCurrentPokemon().getPokemonServer().setFHp((int) (defendingPokemonCurrentHP - damage));
                    setTextHP(cpu.getCurrentPokemon().getPokemonServer(),cpu.getCurrentPokemonName(),cpu.getCurrentPokemonHP());
                    if (move.getFUserFaints()){
                        player.getCurrentPokemon().getPokemonServer().setFHp(0);
                        setTextHP(player.getCurrentPokemon().getPokemonServer(),player.getCurrentPokemonName(),player.getCurrentPokemonHP());
                    }
                    if (move.getFRoundsToLoad() == -1){
                        player.setLoading(true);
                    }
                    gameDescription.setText(messageEffectiveness);
                }else{
                    gameDescription.setText(R.string.attack_missed_msg);
                }
            }else{
                if (currentHit == 1){
                    cpu.getCurrentPokemon().setMoves(updatePPs(cpu.getCurrentPokemon(), move));
                }
                if (!attackMissed(move)){
                    Log.i(TAG, "RANDOM FACTOR:" + randomFactor + " " + "STAB:" + stab + " " + "TYPE_FACTOR:" + typeFactor);
                    double damage = ((42 * move.getFPower() * (attackStat / defenseStat)) / 50 + 2) * randomFactor * stab * typeFactor;
                    double defendingPokemonCurrentHP = defendingPokemon.getPokemonServer().getFHp();
                    if (cpu.isLoading()){
                        cpu.setLoading(false);
                    }
                    player.getCurrentPokemon().getPokemonServer().setFHp((int) (defendingPokemonCurrentHP - damage));
                    setTextHP(player.getCurrentPokemon().getPokemonServer(),player.getCurrentPokemonName(),player.getCurrentPokemonHP());
                    if (move.getFUserFaints()){
                        cpu.getCurrentPokemon().getPokemonServer().setFHp(0);
                        setTextHP(cpu.getCurrentPokemon().getPokemonServer(),cpu.getCurrentPokemonName(),cpu.getCurrentPokemonHP());
                    }
                    if (move.getFRoundsToLoad() == -1){
                        cpu.setLoading(true);
                    }
                    gameDescription.setText(messageEffectiveness);
                }else{
                    gameDescription.setText(R.string.attack_missed_msg);
                }
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (nbOfHits > 1){
                        gameDescription.setText(getString(R.string.hit)+currentHit+getString(R.string.times));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (currentHit < nbOfHits){
                                    hit(defendingPokemon, move, stab, typeFactor, messageEffectiveness,
                                            attackStat, defenseStat, currentHit+1, nbOfHits, onChoiceListener);
                                }else if (currentHit == nbOfHits){
                                    onChoiceListener.onChoice();
                                }
                            }
                        },3000);
                    }else{
                        onChoiceListener.onChoice();
                    }
                }
            },3000);
        }
    }

    private boolean attackMissed(Move move) {
        double accuracy = move.getFAccuracy();
        double random = Math.random()*100;
        Log.i(TAG,"RANDOM FOR ACCURACY:"+random);

        return (random > accuracy);
    }

    private String getMessageEffectiveness(double typeFactor) {
        String messageEffectiveness = "";
        if (typeFactor > 1){
            messageEffectiveness = getString(R.string.super_effective_msg);
        }else if (typeFactor < 1 && typeFactor >0){
            messageEffectiveness = getString(R.string.not_effective_msg);
        }else if (typeFactor == 0){
            messageEffectiveness = getString(R.string.no_effect_msg);
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