package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeEffective;

import java.util.List;

@Dao
public abstract class TypeEffectiveDAO extends RemoteDAO<TypeEffective>{

    public TypeEffectiveDAO() {
        super("type_effective");
    }

    @Query("SELECT * FROM type INNER JOIN type_effective ON type.fId = type_effective.effectiveId WHERE type_effective.typeId = :typeId")
    public abstract List<Type> getEffectiveTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_effective ON type.fId = type_effective.effectiveId WHERE type_effective.typeId = :typeId")
    public abstract List<Long> getEffectiveTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM type_effective;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,List<Type>> effectiveTypesTask(Type type, OnResultListener<List<Type>> onResultListener){
        return new AsyncTask<Void, Void, List<Type>>() {
            @Override
            protected List<Type> doInBackground(Void... voids) {
                return getEffectiveTypes(type.getFId());
            }

            @Override
            protected void onPostExecute(List<Type> types) {
                super.onPostExecute(types);
                onResultListener.onResult(types);
            }
        };
    }

}
