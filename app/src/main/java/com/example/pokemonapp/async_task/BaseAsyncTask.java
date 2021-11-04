package com.example.pokemonapp.async_task;

import android.os.AsyncTask;

import java.util.List;

public class BaseAsyncTask extends AsyncTask<Void,Void, List<Object>> {

    private final BaseAsyncTaskInterface baseAsyncTaskInterface;

    public BaseAsyncTask(BaseAsyncTaskInterface baseAsyncTaskInterface){
        this.baseAsyncTaskInterface = baseAsyncTaskInterface;
    }

    @Override
    protected List<Object> doInBackground(Void... voids) {
        return baseAsyncTaskInterface.doInBackground();
    }

    @Override
    protected void onPostExecute(List<Object> objects) {
        super.onPostExecute(objects);
        baseAsyncTaskInterface.onPostExecute(objects);
    }

    public interface BaseAsyncTaskInterface{
        List<Object> doInBackground();
        void onPostExecute(List<Object> objects);
    }

}
