package com.example.pokemonapp.async_task;

import android.os.AsyncTask;

import com.example.pokemonapp.dao.BaseDAO;

import java.util.List;

/**
 * The DatabaseRecordsTask is responsible for fetching all the records from the local database
 * regarding an entity.
 * @param <E> entity concerned by the activity.
 */
public class DatabaseRecordsTask<E> extends AsyncTask<Void,Void,List<E>> {

    private BaseDAO<E> baseDAO;
    private DatabaseNavigationInterface<E> databaseNavigationInterface;

    /**
     * @param baseDAO DAO allowing to communicate with the database containing the entity <b>E</b>.
     * @param databaseNavigationInterface interface to specify what should be done after fetching
     *                                    the data concerning the entity <b>E</b>.
     */
    public DatabaseRecordsTask(BaseDAO<E> baseDAO, DatabaseNavigationInterface<E> databaseNavigationInterface) {
        this.baseDAO = baseDAO;
        this.databaseNavigationInterface = databaseNavigationInterface;
    }

    @Override
    protected List<E> doInBackground(Void... voids) {
        return baseDAO.getAllRecords();
    }

    @Override
    protected void onPostExecute(List<E> records) {
        super.onPostExecute(records);
        databaseNavigationInterface.onPostExecute(records);
    }

    public interface DatabaseNavigationInterface<E>{
        void onPostExecute(List<E> records);
    }

}
