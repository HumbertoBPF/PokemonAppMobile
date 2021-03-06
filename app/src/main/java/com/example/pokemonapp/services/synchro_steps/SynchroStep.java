package com.example.pokemonapp.services.synchro_steps;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.dao.RemoteDAO;

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
public abstract class SynchroStep<E> {

    private Context context;
    private Handler handler = new Handler();
    private ProgressDialog loadingDialog;
    private RemoteDAO<E> remoteDAO;
    private Call<List<E>> service;
    private SynchroStep nextStep;    // Chain of Responsibility pattern : next synchro in the chain of synchros to be performed

    /**
     * Constructor of the callback allowing to synchronize the local database with the remote one.
     * @param context context of the activity that called the callback (it is required to get string
     *                resources).
     * @param loadingDialog dialog showing the progress of the synchronization.
     * @param remoteDAO DAO concerning an entity also available on remote.
     * @param nextStep next SynchroStep to be called (next synchronization step).
     */
    public SynchroStep(Context context, ProgressDialog loadingDialog, RemoteDAO<E> remoteDAO, Call<List<E>> service,
                       SynchroStep nextStep) {
        this.context = context;
        this.loadingDialog = loadingDialog;
        this.remoteDAO = remoteDAO;
        this.service = service;
        this.nextStep = nextStep;
    }

    public void execute(){
        loadingDialog.setMessage(context.getString(R.string.fetch_table)+remoteDAO.getTableName());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<E>> callForResources = service;    // request the remote resources
                callForResources.enqueue(new Callback<List<E>>() {
                    @Override
                    public void onResponse(Call<List<E>> call, Response<List<E>> responsePokemon) {
                        if (responsePokemon.isSuccessful()){    // when a response is recuperated, save the resources in the local
                                                                // database, show a message informing that to the user and go to the
                                                                // next step
                            List<E> resources = responsePokemon.body();
                            remoteDAO.saveTask(resources, new OnTaskListener() {
                                @Override
                                public void onTask() {
                                    concludeSyncStep(true);
                                }
                            }).execute();
                        }else{  // if the request was not successful, show fail message and go to the next step
                            concludeSyncStep(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<E>> call, Throwable t) {// if the request failed,
                                                                            // show fail message and go to the next step
                        concludeSyncStep(false);
                    }
                });
            }
        },2000);
    }

    private void concludeSyncStep(boolean isSuccessful) {
        if (isSuccessful){
            loadingDialog.setMessage(remoteDAO.getTableName()+context.getString(R.string.success_table_download));
        }else{
            // TODO maybe it is a better idea to stop the synchro
            loadingDialog.setMessage(context.getString(R.string.fail_table_download)+remoteDAO.getTableName());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextStep != null){
                    nextStep.execute();
                }else{
                    loadingDialog.dismiss();
                }
            }
        }, 2000);
    }

}
