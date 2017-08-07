package com.widgetxpress;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widgetxpress.activity.Login;
import com.widgetxpress.api.Api;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.ResponseActividad;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.widgetxpress.Xpress.ACTION_GOTO_LOGIN;

/**
 * Created by Irving on 18/07/2017.
 */

public class WidgetFetchService extends Service
{

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String TAG = WidgetFetchService.class.getSimpleName();
    public static List<Actividad> actividades = new ArrayList<>();
    private boolean isLogin;
    private String id_google;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
       Log.e(TAG,"Se llama onDestroy");
        super.onDestroy();
    }

    @Override
    public void onCreate() {
       Log.e(TAG,"Se llama onCreate");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        id_google=sharedPref.getString("pref_id",null);
        //Log.e(TAG,id_google);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        fetchData();
        return super.onStartCommand(intent, flags, startId);
    }

    public void fetchData() {

        if (Login.id_google == null)
        {
            AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(getApplicationContext());
            ComponentName watchWidget=new ComponentName(getApplicationContext(),Xpress.class);
            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.xpress_widget);

            views.setEmptyView(R.id.widget_main_view,R.id.empty_view);
            Intent google_account=new Intent(getApplicationContext(),Login.class);
            google_account.setAction(Xpress.ACTION_GOTO_LOGIN);
            google_account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            google_account.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            views.setOnClickPendingIntent(R.id.empty_view,PendingIntent.getActivity(getApplicationContext(),0,google_account,PendingIntent.FLAG_UPDATE_CURRENT));
            appWidgetManager.updateAppWidget(watchWidget,views);
            stopSelf();

        } else

        {


            isLogin = true;
            String url = "http://xpress.genniux.net/php/xpress2.0/agenda/";
            Log.e(TAG, "fetchData");

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            Api servicio = retrofit.create(Api.class);
            Call<ResponseActividad> responseActividadCall = servicio.getActividades(Login.id_google);

            responseActividadCall.enqueue(new Callback<ResponseActividad>() {
                @Override
                public void onResponse(Call<ResponseActividad> call, retrofit2.Response<ResponseActividad> response) {
                    if (response.isSuccessful())

                    {
                        String status = response.body().getStatus();
                        Log.i(TAG, status);
                        ResponseActividad responseActividad = response.body();


                        actividades = responseActividad.getActividades();

                        populateWidget();
                    }
                }

                @Override
                public void onFailure(Call<ResponseActividad> call, Throwable t) {
                    Log.e(TAG, " onFailure: " + t.getMessage());
                }
            });
        }
    }





    private void populateWidget() {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(Xpress.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
    public static void sendRefreshBroadcast(Context context) {
    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    intent.setComponent(new ComponentName(context, Xpress.class));
    context.sendBroadcast(intent);
}

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
