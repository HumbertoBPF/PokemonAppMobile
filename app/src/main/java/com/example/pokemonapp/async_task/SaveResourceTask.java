package com.example.pokemonapp.async_task;

import android.os.AsyncTask;

import com.example.pokemonapp.dao.BaseDAO;

public abstract class SaveResourceTask<E> extends AsyncTask<Void,Void,Void> {

    protected BaseDAO<E> baseDAO;
    protected Object entities;
    private OnSavingListener onSavingListener;

    protected SaveResourceTask(BaseDAO<E> baseDAO, Object entities, OnSavingListener onSavingListener) {
        this.baseDAO = baseDAO;
        this.entities = entities;
        this.onSavingListener = onSavingListener;
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
