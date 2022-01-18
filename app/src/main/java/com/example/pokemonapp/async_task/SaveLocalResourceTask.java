package com.example.pokemonapp.async_task;

import android.os.AsyncTask;

import com.example.pokemonapp.dao.LocalDAO;

public class SaveLocalResourceTask<E> extends AsyncTask<Void,Void,Void> {

    private LocalDAO<E> localDAO;
    private E entity;
    private OnSavingListener onSavingListener;

    public SaveLocalResourceTask(LocalDAO<E> localDAO, E entity, OnSavingListener onSavingListener) {
        this.localDAO = localDAO;
        this.entity = entity;
        this.onSavingListener = onSavingListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        localDAO.save(entity);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        onSavingListener.onSave();
    }

    public interface OnSavingListener{
        void onSave();
    }

}
