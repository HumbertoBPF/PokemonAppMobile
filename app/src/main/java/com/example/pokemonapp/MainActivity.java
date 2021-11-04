package com.example.pokemonapp;

import static com.example.pokemonapp.util.Tools.loadingDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog loadingDialog;
    private MoveDAO moveDAO;
    private PokemonDAO pokemonDAO;
    private TypeDAO typeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingDialog = loadingDialog(this);
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
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
                            Toast.makeText(getApplicationContext(),"Perform synchro",Toast.LENGTH_LONG).show();
                            loadingDialog.show();
                            PokemonDbService pokemonDbService = new PokemonDbRetrofit().getPokemonDbService();
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    try {
                                        Call<List<Pokemon>> callPokemon = pokemonDbService.getAllPokemonFromRemote();
                                        Response<List<Pokemon>> responsePokemon = callPokemon.execute();
                                        List<Pokemon> pokemons = responsePokemon.body();
                                        if (pokemons != null) {
                                            Log.i("numberOfPokemons",pokemons.size()+"");
                                            Log.i("pokemon",pokemons.get(0).getFName());
                                        }else{
                                            Log.i("numberOfPokemons","null");
                                        }
                                        pokemonDAO.save(pokemons);

                                        Call<List<Move>> callMove = pokemonDbService.getAllMovesFromRemote();
                                        Response<List<Move>> responseMove = callMove.execute();
                                        List<Move> moves = responseMove.body();
                                        if (moves != null) {
                                            Log.i("numberOfMoves",moves.size()+"");
                                        }else{
                                            Log.i("numberOfMoves","null");
                                        }
                                        moveDAO.save(moves);

                                        Call<List<Type>> callType = pokemonDbService.getAllTypesFromRemote();
                                        Response<List<Type>> responseType = callType.execute();
                                        List<Type> types = responseType.body();
                                        if (types != null) {
                                            Log.i("numberOfTypes",types.size()+"");
                                        }else{
                                            Log.i("numberOfTypes","null");
                                        }
                                        typeDAO.save(types);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.dismiss();
                                }
                            }).execute();
                        }
                    }, null);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}