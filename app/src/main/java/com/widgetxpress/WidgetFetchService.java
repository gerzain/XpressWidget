package com.widgetxpress;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widgetxpress.api.Api;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.ResponseActividad;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Irving on 18/07/2017.
 */

public class WidgetFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String TAG = WidgetFetchService.class.getSimpleName();
    public static List<Actividad> actividades = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        new ObtenerDatos().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    public class ObtenerDatos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

           fetchData();
            return null;
        }
    }

    public void fetchData()
    {

        String url = "http://xpress.genniux.net/php/xpress2.0/agenda/";

        Gson gson=new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Api servicio=retrofit.create(Api.class);
        Call<ResponseActividad> responseActividadCall=servicio.getActividades("105536481769353298421");

        responseActividadCall.enqueue(new Callback<ResponseActividad>() {
            @Override
            public void onResponse(Call<ResponseActividad> call, retrofit2.Response<ResponseActividad> response) {
                if(response.isSuccessful())
                {
                    String status=response.body().getStatus();
                    Log.i(TAG,status);
                    ResponseActividad responseActividad=response.body();

                    actividades=responseActividad.getActividades();

                    populateWidget();
                }
            }

            @Override
            public void onFailure(Call<ResponseActividad> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });


    }
    private void populateWidget() {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(Xpress.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }

}
