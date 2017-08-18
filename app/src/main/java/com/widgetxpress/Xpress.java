package com.widgetxpress;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.widgetxpress.activity.AgregarActividad;
import com.widgetxpress.activity.DetailActividad;
import com.widgetxpress.activity.Login;
import com.widgetxpress.util.SessionPrefs;

/**
 * Implementation of App Widget functionality.
 */
public class Xpress extends AppWidgetProvider
{

    public static final String DATA_FETCHED="com.widgetxpress.DATA_FETCHED";
    public static final String TAG=Xpress.class.getSimpleName();
    public static final String ACTION_GOTO_NEW_TASK = "ActionGotoNewTask";
    public static final String ACTION_GOTO_LOGIN="ActionLogin";
    public static final String ACTION_GOTO_ACCOUNT="ActionAccount";
    public static final String ACTION_GOTO_LOGOUT="com.widgetxpress.ACTION_GOTO_LOGOUT";
    public static final String ACTION_REFRESH="com.widgetxpress.ACTION_REFRESH";
    public static final String EXTRA_ID="ID";
    private static Handler sWorkerQueue;
    private static HandlerThread sWorkerThread;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.xpress_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent serviceIntent = new Intent(context,WidgetFetchService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            context.startService(serviceIntent);
            //updateAppWidget(context,appWidgetManager,appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "onReceive" + intent.getAction());
      //Toast.makeText(context, "onReceive: " + intent.getAction(), Toast.LENGTH_SHORT).show();
       final  int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if(intent.getAction().equalsIgnoreCase(DATA_FETCHED))
        {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews=updateWidgetListView(context,appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }else if(intent.getAction().equalsIgnoreCase(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews=updateWidgetListView(context,appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }else if( intent.getAction().equalsIgnoreCase(ACTION_REFRESH))
        {

            Log.i(TAG,"ActionRefresh");
            updateWidget(context);


        }
    }

    /**
     * Permite notificar cuando ocurre un cambio dentro de la lista
     * @param context El conexto de clase Context
     */

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, Xpress.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_main_view);
    }


    /***
     * El metodo para obtener los datos en la lista y establecer acciones mediante los botones
     * @param context El contexto de clase Context
     * @param appWidgetId el id del widget
     * @return  las vistas de
     */
    private RemoteViews updateWidgetListView(Context context, int appWidgetId)
    {
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.xpress_widget);

        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        Intent google_account=new Intent(context,Login.class);
        google_account.setAction(ACTION_GOTO_ACCOUNT);
        google_account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent agregar = new Intent(context, AgregarActividad.class);
        agregar.setAction(ACTION_GOTO_NEW_TASK);
        agregar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        remoteViews.setOnClickPendingIntent(R.id.widget_title_add,
                PendingIntent.getActivity(context,0,agregar,
                PendingIntent.FLAG_UPDATE_CURRENT));
        //remoteViews.setOnClickPendingIntent(R.id.btn_login,PendingIntent.getActivity(context,0,login,PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.widget_title_star,PendingIntent.
                getActivity(context,0,google_account,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        /***
         * Para acceder a un elemento de la lista
         */

        Intent clickIntentTemplate = new Intent(context, DetailActividad.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_main_view, clickPendingIntentTemplate);

        remoteViews.setRemoteAdapter(R.id.widget_main_view,svcIntent);
        remoteViews.setEmptyView(R.id.widget_main_view,R.id.empty_view);


        /**
         * Para acceder al boton de refrescar
         */

        Intent refresh=new Intent(context,Xpress.class);
        refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        refresh.setAction(Xpress.ACTION_REFRESH);
        PendingIntent refreshPendingIntent=PendingIntent.getBroadcast(context,appWidgetId,refresh,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.refresh,refreshPendingIntent);

        return  remoteViews;

    }




    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

