package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeNotEffective;

import java.util.List;

@Dao
public abstract class TypeNotEffectiveDAO extends RemoteDAO<TypeNotEffective>{

    public TypeNotEffectiveDAO() {
        super("type_not_effective");
    }

    @Query("SELECT * FROM type INNER JOIN type_not_effective ON type.fId = type_not_effective.notEffectiveId " +
            "WHERE type_not_effective.typeId = :typeId")
    public abstract List<Type> getNotEffectiveTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_not_effective ON type.fId = type_not_effective.notEffectiveId " +
            "WHERE type_not_effective.typeId = :typeId")
    public abstract List<Long> getNotEffectiveTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM move_type;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,List<Type>> notEffectiveTypesTask(Type type, OnResultListener<List<Type>> onResultListener){
        return new AsyncTask<Void, Void, List<Type>>() {
            @Override
            protected List<Type> doInBackground(Void... voids) {
                return getNotEffectiveTypes(type.getFId());
            }

            @Override
            protected void onPostExecute(List<Type> types) {
                super.onPostExecute(types);
                onResultListener.onResult(types);
            }
        };
    }

}
