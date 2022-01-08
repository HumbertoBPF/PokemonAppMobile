package com.example.pokemonapp.activities.databases_navigation;

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
import com.example.pokemonapp.async_task.BaseAsyncTask;

import java.io.Serializable;
import java.util.List;

public abstract class DatabaseNavigationActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected TextView noDataTextView;
    protected int colorAppbar;
    protected String titleAppbar;
    protected Class detailsActivity;
    protected ProgressDialog loadingDialog;

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
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                return getResourcesFromLocal();
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                if (objects.size() == 0){
                    noDataTextView.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(getAdapter(objects));
                dismissDialogWhenViewIsDrawn(recyclerView, loadingDialog);
            }
        }).execute();
    }

    protected abstract List<Object> getResourcesFromLocal();

    protected abstract RecyclerView.Adapter getAdapter(List<Object> objects);

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

}