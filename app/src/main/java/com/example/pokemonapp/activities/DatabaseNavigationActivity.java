package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.loadingDialog;
import static com.example.pokemonapp.util.Tools.setAppbarColor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.DatabaseRecordsTask;
import com.example.pokemonapp.dao.BaseDAO;

import java.io.Serializable;
import java.util.List;

/**
 * Activity for screens of the app that show all the records concerning an entity <b>E</b>.
 * @param <E> entity concerned by the activity.
 */
public abstract class DatabaseNavigationActivity<E> extends AppCompatActivity implements DatabaseRecordsTask.DatabaseNavigationInterface<E>{

    protected BaseDAO<E> baseDAO;   // DAO allowing to communicate with the database containing the entity E
    protected RecyclerView recyclerView;        // RecyclerView to present the data
    protected TextView noDataTextView;          // message shown when there is no data to be shown
    protected Class detailsActivity;            // activity responsible for showing the details of a selected record
    private ProgressDialog loadingDialog;

    protected int colorAppbar;                  // customization of the appbar
    protected String titleAppbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_navigation);

        recyclerView = findViewById(R.id.pokemon_recycler_view);
        noDataTextView = findViewById(R.id.no_data_text_view);
        loadingDialog = loadingDialog(this);

        configureAppbar();
        configureRecyclerView();
    }

    private void configureAppbar() {
        setAppbarColor(this, colorAppbar);
        setTitle(titleAppbar);
    }

    protected void configureRecyclerView() {
        loadingDialog.show();
        new DatabaseRecordsTask<>(baseDAO, this).execute();
    }

    /**
     * Method allowing to specify the adapter to be set in the RecyclerView.
     * @param records list of the records that are going to populate the RecyclerView.
     * @return an adapter to be set in the RecyclerView.
     */
    protected abstract RecyclerView.Adapter getAdapter(List<E> records);

    /**
     * @param resource resource whose details are going to be shown.
     * @return an intent redirecting the user to an activity where the details of the concerned
     * database resource are shown.
     */
    protected Intent showDetails(Object resource){
        Intent intent = new Intent(getApplicationContext(),detailsActivity);
        intent.putExtra(getString(R.string.key_extra_db_resource), (Serializable) resource);
        return intent;
    }

    @Override
    public void onPostExecute(List<E> records) {
        if (records.size() == 0){
            noDataTextView.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(getAdapter(records));
        dismissDialogWhenViewIsDrawn(recyclerView, loadingDialog);
    }
}