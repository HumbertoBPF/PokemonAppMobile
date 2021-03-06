package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeNoEffect;

import java.util.List;

@Dao
public abstract class TypeNoEffectDAO extends RemoteDAO<TypeNoEffect>{

    public TypeNoEffectDAO() {
        super("type_no_effect");
    }

    @Query("SELECT * FROM type INNER JOIN type_no_effect ON type.fId = type_no_effect.noEffectId " +
            "WHERE type_no_effect.typeId = :typeId")
    public abstract List<Type> getNoEffectTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_no_effect ON type.fId = type_no_effect.noEffectId " +
            "WHERE type_no_effect.typeId = :typeId")
    public abstract List<Long> getNoEffectTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM move_type;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,List<Type>> noEffectTypesTask(Type type, OnResultListener<List<Type>> onResultListener){
        return new AsyncTask<Void, Void, List<Type>>() {
            @Override
            protected List<Type> doInBackground(Void... voids) {
                return getNoEffectTypes(type.getFId());
            }

            @Override
            protected void onPostExecute(List<Type> types) {
                super.onPostExecute(types);
                onResultListener.onResult(types);
            }
        };
    }

}
