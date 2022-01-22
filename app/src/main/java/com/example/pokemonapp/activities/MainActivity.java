package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.DialogTools.dualButtonDialog;
import static com.example.pokemonapp.util.DialogTools.loadingDialog;
import static com.example.pokemonapp.util.DialogTools.singleButtonDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.RemoteDatabasesActivity;
import com.example.pokemonapp.activities.game.GameModeSelectionActivity;
import com.example.pokemonapp.async_task.ConnexionVerificationTask;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.async_task.ValidationResourcesForGameTask;
import com.example.pokemonapp.models.RoundedButton;
import com.example.pokemonapp.services.synchro_callbacks.SynchroPokemon;

public class MainActivity extends ButtonsActivity implements ConnexionVerificationTask.OnInternetVerifiedListener{

    private final String TAG = "MainActivity";
    // progress dialog to inform the current step of the synchro
    private ProgressDialog loadingDialog;
    // Handler to execute code delayed
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getString(R.string.app_name));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void declareButtons() {
        // button to launch a game
        RoundedButton gameButton = new RoundedButton(getResources().getString(R.string.game_button_text),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ValidationResourcesForGameTask(getApplicationContext(), new OnResultListener<Boolean>() {
                            @Override
                            public void onResult(Boolean result) {
                                if (result){
                                    startActivity(new Intent(getApplicationContext(), GameModeSelectionActivity.class));
                                }else{
                                    Toast.makeText(getApplicationContext(),R.string.needs_synchro_warning,Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
                    }
                });
        // button to navigate through the database
        RoundedButton databaseButton = new RoundedButton(getString(R.string.databases_button_text),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), RemoteDatabasesActivity.class));
                    }
                });
        buttons.add(gameButton);
        buttons.add(databaseButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_sync) {
            Dialog dialog = dualButtonDialog(this, getString(R.string.title_synchronization_dialog),
                    getString(R.string.message_synchronization_dialog), getString(R.string.confirm_button_text_synchronization_dialog),
                    getString(R.string.cancel_button_text_synchronization_dialog),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadingDialog = loadingDialog(MainActivity.this);
                            loadingDialog.setMessage(getString(R.string.verifying_internet));
                            loadingDialog.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new ConnexionVerificationTask(MainActivity.this, MainActivity.this).execute();
                                }
                            },2000);
                        }
                    }, null);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeviceConnected() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new SynchroPokemon(getApplicationContext(), loadingDialog).execute();
            }
        }, 2000);
    }

    @Override
    public void onDeviceOffline() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Dialog noInternetDialog = singleButtonDialog(
                                MainActivity.this,
                                getString(R.string.no_internet_dialog_title),
                                getString(R.string.no_internet_dialog_text),
                                getString(R.string.understood_button_text),
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