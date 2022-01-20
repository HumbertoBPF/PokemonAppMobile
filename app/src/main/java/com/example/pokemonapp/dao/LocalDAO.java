package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Delete;
import androidx.room.Insert;

import com.example.pokemonapp.async_task.OnTaskListener;

/**
 * DAO specifying the operations that are common to all the entities available only on local.
 * @param <E> entity concerned.
 */
public abstract class LocalDAO<E> extends BaseDAO<E> {

    public LocalDAO(String tableName) {
        super(tableName);
    }

    /**
     * Saves locally the entity specified as argument.
     * @param entity entity to be saved.
     */
    @Insert(onConflict = REPLACE)
    public abstract void save(E entity);

    /**
     * Deletes locally the entity specified as argument.
     * @param entity entity to be deleted.
     */
    @Delete
    public abstract void delete(E entity);

    /**
     * Performs the async task to save an entity.
     * @param entity entity to be saved.
     * @param onTaskListener code to be executed after the task has finished.
     * @return the AsyncTask allowing to execute the saving task.
     */
    public AsyncTask<Void,Void,Void> saveTask(E entity, OnTaskListener onTaskListener){
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                save(entity);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                onTaskListener.onTask();
            }
        };
    }

    /**
     * Performs the async task to delete an entity.
     * @param entity entity to be deleted.
     * @param onTaskListener code to be executed after the task has finished.
     * @return the AsyncTask allowing to execute the deleting task.
     */
    public AsyncTask<Void,Void,Void> deleteTask(E entity, OnTaskListener onTaskListener){
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                delete(entity);
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
