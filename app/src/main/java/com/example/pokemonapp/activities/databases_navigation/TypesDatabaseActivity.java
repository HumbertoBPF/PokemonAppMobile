package com.example.pokemonapp.activities.databases_navigation;

import static com.example.pokemonapp.util.Tools.setAppbarColor;

import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.TypesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesDatabaseActivity extends DatabaseNavigationActivity {

    private TypeDAO typeDAO;
    private List<Type> types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setAppbarColor(this, getResources().getColor(R.color.types_theme_color));
        setTitle("Types Database");
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();

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
                recyclerView.setAdapter(new TypesAdapter(getApplicationContext(),types));
            }
        }).execute();

    }
}