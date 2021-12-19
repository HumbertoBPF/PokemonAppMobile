package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.Tools.loadingDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabasesActivity;
import com.example.pokemonapp.activities.game.GameModeSelectionActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.MoveType;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.PokemonMove;
import com.example.pokemonapp.entities.PokemonType;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeEffective;
import com.example.pokemonapp.entities.TypeNoEffect;
import com.example.pokemonapp.entities.TypeNotEffective;
import com.example.pokemonapp.models.RoundedButton;
import com.example.pokemonapp.retrofit.PokemonDbRetrofit;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.services.PokemonDbService;
import com.example.pokemonapp.util.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ButtonsActivity {

    private ProgressDialog loadingDialog;
    private MoveDAO moveDAO;
    private PokemonDAO pokemonDAO;
    private TypeDAO typeDAO;
    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private PokemonMoveDAO pokemonMoveDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;
    private PokemonDbService pokemonDbService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.app_name));
        super.onCreate(savedInstanceState);
        getDAOs();
        pokemonDbService = new PokemonDbRetrofit().getPokemonDbService();
        handler = new Handler();
    }

    @Override
    protected void declareButtons() {
        RoundedButton gameButton = new RoundedButton(getResources().getString(R.string.game_button_text),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                            @Override
                            public List<Object> doInBackground() {
                                List<Object> objects = new ArrayList<>();
                                objects.add((moveDAO.getNbOfElements() > 0 &&
                                        typeDAO.getNbOfElements() > 0 && pokemonDAO.getNbOfElements() > 0));
                                return objects;
                            }

                            @Override
                            public void onPostExecute(List<Object> objects) {
                                if ((Boolean) objects.get(0)){
                                    startActivity(new Intent(getApplicationContext(), GameModeSelectionActivity.class));
                                }else{
                                    Toast.makeText(getApplicationContext(),"It seems that you have not synchronized data. Please " +
                                            "click on the synchronization option to fetch data necessary for the game.",Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
                    }
                });
        RoundedButton databaseButton = new RoundedButton(getResources().getString(R.string.databases_button_text),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), DatabasesActivity.class));
                    }
                });
        buttons.add(gameButton);
        buttons.add(databaseButton);
    }

    private void getDAOs() {
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_sync) {
            Dialog dialog = Tools.yesOrNoDialog(this, getString(R.string.title_synchronization_dialog),
                    getString(R.string.message_synchronization_dialog), getString(R.string.confirm_button_text_synchronization_dialog),
                    getString(R.string.cancel_button_text_synchronization_dialog),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadingDialog = loadingDialog(MainActivity.this);
                            loadingDialog.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackPokemon();
                                }
                            },2000);
                        }
                    }, null);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callbackPokemon() {
        loadingDialog.setMessage(getString(R.string.fetch_pokemon_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<Pokemon>> callPokemon = pokemonDbService.getAllPokemonFromRemote();
                callPokemon.enqueue(new Callback<List<Pokemon>>() {
                    @Override
                    public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> responsePokemon) {
                        if (responsePokemon.isSuccessful()){
                            List<Pokemon> pokemons = responsePokemon.body();
                            if (pokemons != null) {
                                Log.i("numberOfPokemons",pokemons.size()+"");
                                Log.i("pokemon",pokemons.get(0).getFName());
                            }else{
                                Log.i("numberOfPokemons","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    pokemonDAO.save(pokemons);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_pokemon_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackMove();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_pokemon_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackMove();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_pokemon_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackMove();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackMove() {
        loadingDialog.setMessage(getString(R.string.fetch_move_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<Move>> callMove = pokemonDbService.getAllMovesFromRemote();
                callMove.enqueue(new Callback<List<Move>>() {
                    @Override
                    public void onResponse(Call<List<Move>> call, Response<List<Move>> responseMove) {
                        if (responseMove.isSuccessful()){
                            List<Move> moves = responseMove.body();
                            if (moves != null) {
                                Log.i("numberOfMoves",moves.size()+"");
                            }else{
                                Log.i("numberOfMoves","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    moveDAO.save(moves);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_moves_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackType();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_moves_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackType();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Move>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_moves_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackType();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackType() {
        loadingDialog.setMessage(getString(R.string.fetch_type_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<Type>> callType = pokemonDbService.getAllTypesFromRemote();
                callType.enqueue(new Callback<List<Type>>() {
                    @Override
                    public void onResponse(Call<List<Type>> call, Response<List<Type>> responseType) {
                        if (responseType.isSuccessful()){
                            List<Type> types = responseType.body();
                            if (types != null) {
                                Log.i("numberOfTypes",types.size()+"");
                            }else{
                                Log.i("numberOfTypes","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    typeDAO.save(types);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_types_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackMoveType();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_types_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackMoveType();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Type>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_types_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackMoveType();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackMoveType(){
        loadingDialog.setMessage(getString(R.string.fetch_move_type_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<MoveType>> callMoveType = pokemonDbService.getAllMoveTypesFromRemote();
                callMoveType.enqueue(new Callback<List<MoveType>>() {
                    @Override
                    public void onResponse(Call<List<MoveType>> call, Response<List<MoveType>> responseMoveType) {
                        if (responseMoveType.isSuccessful()){
                            List<MoveType> moveTypes = responseMoveType.body();
                            if (moveTypes != null) {
                                Log.i("numberOfMoveTypes",moveTypes.size()+"");
                            }else{
                                Log.i("numberOfMoveTypes","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    moveTypeDAO.save(moveTypes);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_move_types_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackPokemonType();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_move_types_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackPokemonType();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MoveType>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_move_types_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackPokemonType();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackPokemonType(){
        loadingDialog.setMessage(getString(R.string.fetch_pokemon_type_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<PokemonType>> callPokemonType = pokemonDbService.getAllPokemonTypesFromRemote();
                callPokemonType.enqueue(new Callback<List<PokemonType>>() {
                    @Override
                    public void onResponse(Call<List<PokemonType>> call, Response<List<PokemonType>> responsePokemonType) {
                        if (responsePokemonType.isSuccessful()){
                            List<PokemonType> pokemonTypes = responsePokemonType.body();
                            if (pokemonTypes != null) {
                                Log.i("numberOfPokemonTypes", pokemonTypes.size()+"");
                            }else{
                                Log.i("numberOfPokemonTypes","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    pokemonTypeDAO.save(pokemonTypes);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_pokemon_types_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackPokemonMove();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_pokemon_types_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackPokemonMove();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PokemonType>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_pokemon_types_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackPokemonMove();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackPokemonMove(){
        loadingDialog.setMessage(getString(R.string.fetch_pokemon_move_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<PokemonMove>> callPokemonMove = pokemonDbService.getAllPokemonMovesFromRemote();
                callPokemonMove.enqueue(new Callback<List<PokemonMove>>() {
                    @Override
                    public void onResponse(Call<List<PokemonMove>> call, Response<List<PokemonMove>> responsePokemonMove) {
                        if (responsePokemonMove.isSuccessful()){
                            List<PokemonMove> pokemonMoves = responsePokemonMove.body();
                            if (pokemonMoves != null) {
                                Log.i("numberOfPokemonMoves", pokemonMoves.size()+"");
                            }else{
                                Log.i("numberOfPokemonMoves","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    pokemonMoveDAO.save(pokemonMoves);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_pokemon_moves_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackTypeEffective();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_pokemon_moves_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackTypeEffective();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PokemonMove>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_pokemon_moves_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackTypeEffective();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackTypeEffective(){
        loadingDialog.setMessage(getString(R.string.fetch_type_effective_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<TypeEffective>> callTypeEffective = pokemonDbService.getAllTypeEffectiveFromRemote();
                callTypeEffective.enqueue(new Callback<List<TypeEffective>>() {
                    @Override
                    public void onResponse(Call<List<TypeEffective>> call, Response<List<TypeEffective>> responseTypeEffective) {
                        if (responseTypeEffective.isSuccessful()){
                            List<TypeEffective> typeEffectives = responseTypeEffective.body();
                            if (typeEffectives != null) {
                                Log.i("numberOfTypeEffectives", typeEffectives.size()+"");
                            }else{
                                Log.i("numberOfTypeEffectives","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    typeEffectiveDAO.save(typeEffectives);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_type_effective_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackTypeNotEffective();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_type_effective_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackTypeNotEffective();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TypeEffective>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_type_effective_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackTypeNotEffective();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackTypeNotEffective(){
        loadingDialog.setMessage(getString(R.string.fetch_type_not_effective_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<TypeNotEffective>> callTypeNotEffective = pokemonDbService.getAllNotEffectiveTypesFromRemote();
                callTypeNotEffective.enqueue(new Callback<List<TypeNotEffective>>() {
                    @Override
                    public void onResponse(Call<List<TypeNotEffective>> call, Response<List<TypeNotEffective>> responseTypeNotEffective) {
                        if (responseTypeNotEffective.isSuccessful()){
                            List<TypeNotEffective> typeNotEffectives = responseTypeNotEffective.body();
                            if (typeNotEffectives != null) {
                                Log.i("numbOfTypeNotEffectives", typeNotEffectives.size()+"");
                            }else{
                                Log.i("numbOfTypeNotEffectives","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    typeNotEffectiveDAO.save(typeNotEffectives);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_type_not_effective_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackTypeNoEffect();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_type_not_effective_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackTypeNoEffect();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TypeNotEffective>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_type_not_effective_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackTypeNoEffect();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private void callbackTypeNoEffect(){
        loadingDialog.setMessage(getString(R.string.fetch_type_no_effect_db));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<TypeNoEffect>> callTypeNoEffect = pokemonDbService.getAllNoEffectTypesFromRemote();
                callTypeNoEffect.enqueue(new Callback<List<TypeNoEffect>>() {
                    @Override
                    public void onResponse(Call<List<TypeNoEffect>> call, Response<List<TypeNoEffect>> responseTypeNoEffect) {
                        if (responseTypeNoEffect.isSuccessful()){
                            List<TypeNoEffect> typeNoEffects = responseTypeNoEffect.body();
                            if (typeNoEffects != null) {
                                Log.i("numberOfTypeNoEffects", typeNoEffects.size()+"");
                            }else{
                                Log.i("numberOfTypeNoEffects","null");
                            }
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    typeNoEffectDAO.save(typeNoEffects);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(getString(R.string.success_type_no_effect_download));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDialog.dismiss();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage(getString(R.string.fail_type_no_effect_download));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TypeNoEffect>> call, Throwable t) {
                        loadingDialog.setMessage(getString(R.string.fail_type_no_effect_download));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

}