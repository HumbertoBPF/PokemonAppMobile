package com.example.pokemonapp.activities.databases_navigation.types;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;

import android.content.Intent;
import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.TypesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesDatabaseActivity extends DatabaseNavigationActivity {

    private TypeDAO typeDAO;
    private List<Type> types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.types_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_types_db);
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void configureRecyclerView() {
        loadingDialog.show();
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                types = typeDAO.getAllTypesFromLocal();
                List<Object> objects = new ArrayList<>();
                objects.addAll(types);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                recyclerView.setAdapter(new TypesAdapter(getApplicationContext(), types,
                        new TypesAdapter.OnClickListener() {
                            @Override
                            public void onClick(Type type) {
                                Intent intent = new Intent(getApplicationContext(),TypesDetailsActivity.class);
                                intent.putExtra("type",type);
                                startActivity(intent);
                            }
                        }));
                dismissDialogWhenViewIsDrawn(recyclerView, loadingDialog);
            }
        }).execute();
    }
}