package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.Tools.loadingDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.pokemonapp.services.SynchroCallback;
import com.example.pokemonapp.util.Tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends ButtonsActivity {

    private final String TAG = "MainActivity";
    // progress dialog to inform the current step of the synchro
    private ProgressDialog loadingDialog;
    // DAOs allowing to communicate with the local DB
    private MoveDAO moveDAO;
    private PokemonDAO pokemonDAO;
    private TypeDAO typeDAO;
    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private PokemonMoveDAO pokemonMoveDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;
    // service that is used to communicate with the remote DB
    private PokemonDbService pokemonDbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.app_name));
        super.onCreate(savedInstanceState);
        getDAOs();
        pokemonDbService = new PokemonDbRetrofit().getPokemonDbService();
    }

    @Override
    protected void declareButtons() {
        // button to launch a game
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
        // button to navigate through the database
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
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                        @Override
                                        public List<Object> doInBackground() {
                                            loadingDialog.setMessage("Verifying internet connexion...");
                                            List<Object> objects = new ArrayList<>();
                                            objects.add(hasInternetAccess());
                                            return objects;
                                        }

                                        @Override
                                        public void onPostExecute(List<Object> objects) {
                                            if ((Boolean) objects.get(0)){
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        callbackPokemon();
                                                    }
                                                },2000);
                                            }else{
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                Dialog noInternetDialog = Tools.singleButtonDialog(
                                                                        MainActivity.this,
                                                                        "No internet connexion",
                                                                        "No internet connexion detected. Please connect your device" +
                                                                                "to synchronize data.",
                                                                        "Understood",
                                                                        new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();
                                                                            }
                                                                        }
                                                                );
                                                                noInternetDialog.setCancelable(false);
                                                                noInternetDialog.show();
                                                            }
                                                        });
                                                        loadingDialog.dismiss();
                                                    }
                                                },2000);
                                            }
                                        }
                                    }).execute();
                                }
                            },2000);
                        }
                    }, null);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //================================callback to get the pokemon DB================================

    private void callbackPokemon() {
        SynchroCallback<Pokemon> pokemonCallback = new SynchroCallback<Pokemon>(
                this, loadingDialog, pokemonDAO, Pokemon.class,
                new SynchroCallback.CallbackSetup<Pokemon>() {
                    @Override
                    public Call<List<Pokemon>> callService() {
                        return pokemonDbService.getAllPokemonFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackMove();
                    }
                });
        pokemonCallback.run();
    }

    //================================callback to get the move DB===================================

    private void callbackMove() {
        SynchroCallback<Move> moveCallback = new SynchroCallback<Move>(
                this, loadingDialog, moveDAO, Move.class,
                new SynchroCallback.CallbackSetup<Move>() {
                    @Override
                    public Call<List<Move>> callService() {
                        return pokemonDbService.getAllMovesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackType();
                    }
                }
        );
        moveCallback.run();
    }

    //================================callback to get the type DB===================================

    private void callbackType() {
        SynchroCallback<Type> typeCallback = new SynchroCallback<Type>(
                this, loadingDialog, typeDAO, Type.class,
                new SynchroCallback.CallbackSetup<Type>() {
                    @Override
                    public Call<List<Type>> callService() {
                        return pokemonDbService.getAllTypesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackMoveType();
                    }
                }
        );
        typeCallback.run();
    }

    //==============================callback to get the move_type DB================================

    private void callbackMoveType(){
        SynchroCallback<MoveType> moveTypeCallback = new SynchroCallback<MoveType>(
                this, loadingDialog, moveTypeDAO, MoveType.class,
                new SynchroCallback.CallbackSetup<MoveType>() {
                    @Override
                    public Call<List<MoveType>> callService() {
                        return pokemonDbService.getAllMoveTypesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackPokemonType();
                    }
                }
        );
        moveTypeCallback.run();
    }

    //=============================callback to get the pokemon_type DB==============================

    private void callbackPokemonType(){
        SynchroCallback<PokemonType> pokemonTypeCallback = new SynchroCallback<PokemonType>(
                this, loadingDialog, pokemonTypeDAO, PokemonType.class,
                new SynchroCallback.CallbackSetup<PokemonType>() {
                    @Override
                    public Call<List<PokemonType>> callService() {
                        return pokemonDbService.getAllPokemonTypesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackPokemonMove();
                    }
                }
        );
        pokemonTypeCallback.run();
    }

    //==============================callback to get the pokemon_move DB=============================

    private void callbackPokemonMove(){
        SynchroCallback<PokemonMove> pokemonMoveCallback = new SynchroCallback<PokemonMove>(
                this, loadingDialog, pokemonMoveDAO, PokemonMove.class,
                new SynchroCallback.CallbackSetup<PokemonMove>() {
                    @Override
                    public Call<List<PokemonMove>> callService() {
                        return pokemonDbService.getAllPokemonMovesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackTypeEffective();
                    }
                }
        );
        pokemonMoveCallback.run();
    }

    //=========================callback to get the type_typeEffective DB============================

    private void callbackTypeEffective(){
        SynchroCallback<TypeEffective> typeEffectiveCallback = new SynchroCallback<TypeEffective>(
                this, loadingDialog, typeEffectiveDAO, TypeEffective.class,
                new SynchroCallback.CallbackSetup<TypeEffective>() {
                    @Override
                    public Call<List<TypeEffective>> callService() {
                        return pokemonDbService.getAllTypeEffectiveFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackTypeNotEffective();
                    }
                }
        );
        typeEffectiveCallback.run();
    }

    //=========================callback to get the type_typeNotEffective DB=========================

    private void callbackTypeNotEffective(){
        SynchroCallback<TypeNotEffective> typeNotEffectiveCallback = new SynchroCallback<TypeNotEffective>(
                this, loadingDialog, typeNotEffectiveDAO, TypeNotEffective.class,
                new SynchroCallback.CallbackSetup<TypeNotEffective>() {
                    @Override
                    public Call<List<TypeNotEffective>> callService() {
                        return pokemonDbService.getAllNotEffectiveTypesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        callbackTypeNoEffect();
                    }
                }
        );
        typeNotEffectiveCallback.run();
    }

    //===========================callback to get the type_typeNoEffect DB===========================

    private void callbackTypeNoEffect(){
        SynchroCallback<TypeNoEffect> typeNoEffectCallback = new SynchroCallback<TypeNoEffect>(
                this, loadingDialog, typeNoEffectDAO, TypeNoEffect.class,
                new SynchroCallback.CallbackSetup<TypeNoEffect>() {
                    @Override
                    public Call<List<TypeNoEffect>> callService() {
                        return pokemonDbService.getAllNoEffectTypesFromRemote();
                    }

                    @Override
                    public void onResult() {
                        loadingDialog.dismiss();
                    }
                }
        );
        typeNoEffectCallback.run();
    }

    public boolean hasInternetAccess() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection url = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204").openConnection());
                url.setRequestProperty("User-Agent", "Android");
                url.setRequestProperty("Connection", "close");
                url.setConnectTimeout(1500);
                url.connect();
                return (url.getResponseCode() == 204 && url.getContentLength() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}