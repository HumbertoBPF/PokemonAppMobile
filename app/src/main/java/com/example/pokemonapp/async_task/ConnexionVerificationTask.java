package com.example.pokemonapp.async_task;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnexionVerificationTask extends AsyncTask<Void,Void,Boolean> {

    private final Activity activity;
    private final OnInternetVerifiedListener onInternetVerifiedListener;

    public ConnexionVerificationTask(Activity activity, OnInternetVerifiedListener onInternetVerifiedListener) {
        this.activity = activity;
        this.onInternetVerifiedListener = onInternetVerifiedListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return hasInternetAccess();
    }

    @Override
    public void onPostExecute(Boolean hasInternetAccess) {
        super.onPostExecute(hasInternetAccess);
        if (hasInternetAccess){
            onInternetVerifiedListener.onDeviceConnected();
        }else{
            onInternetVerifiedListener.onDeviceOffline();
        }

    }

    public boolean hasInternetAccess() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection url = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204").openConnection());
                url.setRequestProperty("User-Agent", "Android");
                url.setRequestProperty("Connection", "close");
                url.setConnectTimeout(1500);
                url.connect();
                return (url.getResponseCode() == 204 && url.getContentLength() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public interface OnInternetVerifiedListener{
        void onDeviceConnected();
        void onDeviceOffline();
    }

}
