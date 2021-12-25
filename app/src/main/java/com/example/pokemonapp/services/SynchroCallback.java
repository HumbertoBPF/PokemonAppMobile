package com.example.pokemonapp.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonAppDAO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class allows to define a general callback used to synchronize the local database with the
 * remote one. Synchronizing in this context means to replace the local database with the remote one.
 * @param <E> entity which models the SQLite table that stands for the resource that is concerned by
 *           the synchronization.
 */
public class SynchroCallback<E> {

    private Context context;
    private Handler handler = new Handler();
    private ProgressDialog loadingDialog;
    private PokemonAppDAO<E> pokemonAppDAO;
    private CallbackSetup callbackSetup;
    private final Class<E> aClass;

    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     * @param context context of the activity that called the callback (it is required to get string
     *                resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     * @param pokemonAppDAO DAO allowing to communicate with the local database.
     * @param aClass class which models the entity of the SQLite tables of the resources being
     *               synchronized.
     * @param callbackSetup interface to configure the callback.
     */
    public SynchroCallback(Context context, ProgressDialog loadingDialog,
                           PokemonAppDAO pokemonAppDAO, Class<E> aClass, CallbackSetup<E> callbackSetup) {
        this.context = context;
        this.loadingDialog = loadingDialog;
        this.pokemonAppDAO = pokemonAppDAO;
        this.aClass = aClass;
        this.callbackSetup = callbackSetup;
    }

    public void run(){
        loadingDialog.setMessage(context.getString(
                context.getResources().getIdentifier("fetch_"+getGenericClassName()+"_db",
                        "string", context.getPackageName())));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<E>> callForResources = callbackSetup.callService();    // request the remote resources
                callForResources.enqueue(new Callback<List<E>>() {
                    @Override
                    public void onResponse(Call<List<E>> call, Response<List<E>> responsePokemon) {
                        if (responsePokemon.isSuccessful()){    // when a response is recuperated, save the resources in the local
                                                                // database, show a message informing that to the user and go to the
                                                                // next step (onResult method of the interface CallbackSetup)
                            List<E> resources = responsePokemon.body();
                            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                @Override
                                public List<Object> doInBackground() {
                                    pokemonAppDAO.save(resources);
                                    return null;
                                }

                                @Override
                                public void onPostExecute(List<Object> objects) {
                                    loadingDialog.setMessage(context.getString(
                                            context.getResources().getIdentifier("success_"+getGenericClassName()+"_download",
                                                    "string", context.getPackageName())));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callbackSetup.onResult();
                                        }
                                    },2000);
                                }
                            }).execute();
                        }else{  // if the request was not successful, show fail message and go to the next step
                                // TODO maybe it is a better idea to stop the synchro
                            loadingDialog.setMessage(context.getString(
                                    context.getResources().getIdentifier("fail_"+getGenericClassName()+"_download",
                                            "string", context.getPackageName())));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callbackSetup.onResult();
                                }
                            },2000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<E>> call, Throwable t) {// if the request failed,
                                                                            // show fail message and go to the next step
                                                                            // TODO maybe it is a better idea to stop the synchro
                        loadingDialog.setMessage(context.getString(
                                context.getResources().getIdentifier("fail_"+getGenericClassName()+"_download",
                                        "string", context.getPackageName())));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callbackSetup.onResult();
                            }
                        },2000);
                    }
                });
            }
        },2000);
    }

    private String getGenericClassName(){
        return aClass.getSimpleName().toLowerCase();
    }

    /**
     * Interface allowing to customize the callback.
     * @param <E> entity which models the SQLite table that stands for the resource that is concerned by
     *            the synchronization.
     */
    public interface CallbackSetup<E> {
        /**
         * Call of the service responsible for getting the remote database, which is expressed here
         * by a list of entity objects.
         * @return a list object representing the elements of the remote SQL table.
         */
        Call<List<E>> callService();

        /**
         * Method containing what should be executed at the end of the synchronization of an entity.
         */
        void onResult();
    }

}
