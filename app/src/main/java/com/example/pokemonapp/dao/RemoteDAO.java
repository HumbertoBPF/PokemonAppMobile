package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Insert;

import com.example.pokemonapp.async_task.OnTaskListener;

import java.util.List;

/**
 * DAO specifying the operations that are common to all the entities also available on remote.
 * @param <E> entity concerned.
 */
public abstract class RemoteDAO<E> extends BaseDAO<E>{

    public RemoteDAO(String tableName) {
        super(tableName);
    }

    /**
     * Saves locally all the entities contained in the list specified as parameter.
     * @param entities list of entities to be saved.
     */
    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entities);

    public AsyncTask<Void,Void,Void> saveTask(List<E> entities, OnTaskListener onTaskListener){
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                save(entities);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                onTaskListener.onTask();
            }
        };
    }

}
