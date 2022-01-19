package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.pokemonapp.async_task.OnResultListener;

import java.util.List;

/**
 * DAO allowing to perform the most basic operations for all entities of the database.
 * @param <E> entity concerned.
 */
@Dao
public abstract class BaseDAO<E> {

    protected String tableName;

    /**
     * @param tableName name of the table corresponding to the entity <b>E</b>.
     */
    public BaseDAO(String tableName) {
        this.tableName = tableName;
    }

    @RawQuery
    protected abstract List<E> getAllRecords(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the list of all the records concerning the entity <b>E</b>.
     * @return
     */
    public List<E> getAllRecords(){
        return getAllRecords(new SimpleSQLiteQuery("SELECT * FROM "+tableName));
    }

    public AsyncTask<Void,Void,List<E>> getAllRecordsTask(OnResultListener<List<E>> onResultListener){
        return new AsyncTask<Void, Void, List<E>>() {
            @Override
            protected List<E> doInBackground(Void... voids) {
                return getAllRecords();
            }

            @Override
            protected void onPostExecute(List<E> records) {
                super.onPostExecute(records);
                onResultListener.onResult(records);
            }
        };
    }

}
