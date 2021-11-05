package com.example.pokemonapp;

import static com.example.pokemonapp.util.Tools.loadingDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.retrofit.PokemonDbRetrofit;
import com.example.pokemonapp.room.PokemonAppDatabase;
import com.example.pokemonapp.services.PokemonDbService;
import com.example.pokemonapp.util.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog loadingDialog;
    private MoveDAO moveDAO;
    private PokemonDAO pokemonDAO;
    private TypeDAO typeDAO;
    private PokemonDbService pokemonDbService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
        pokemonDbService = new PokemonDbRetrofit().getPokemonDbService();
        handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_sync) {
            Dialog dialog = Tools.yesOrNoDialog(this, "Database Synchronization",
                    "Would you like to perform a synchronization now ?", "Confirm", "Cancel",
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
        loadingDialog.setMessage("Fetching pokémon database");
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
                                    loadingDialog.setMessage("Pokémon database downloaded");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackMove();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage("Fail to get pokémon database");
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
                        loadingDialog.setMessage("Fail to get pokémon database");
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
        loadingDialog.setMessage("Fetching move database");
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
                                    loadingDialog.setMessage("Move database downloaded");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackType();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage("Fail to get move database");
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
                        loadingDialog.setMessage("Fail to get move database");
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
        loadingDialog.setMessage("Fetching type database");
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
                                    loadingDialog.setMessage("Type database downloaded");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDialog.dismiss();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{
                            loadingDialog.setMessage("Fail to get type database");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Type>> call, Throwable t) {
                        loadingDialog.setMessage("Fail to get type database");
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