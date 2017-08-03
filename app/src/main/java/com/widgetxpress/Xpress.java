package com.widgetxpress;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    public static  final String ACTION_GOTO_LOGIN="ActionLogin";
    public static final String ACTION_GOTO_ACCOUNT="ActionAccount";
    public static final String EXTRA_ID="ID";
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
      Toast.makeText(context, "onReceive: " + intent.getAction(), Toast.LENGTH_SHORT).show();
        if(intent.getAction().equalsIgnoreCase(DATA_FETCHED))
        {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews=updateWidgetListView(context,appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }else if(intent.getAction().equalsIgnoreCase(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews=updateWidgetListView(context,appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId)
    {
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.xpress_widget);

        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        /*Intent login=new Intent(context, Login.class);
        login.setAction(ACTION_GOTO_LOGIN);
        login.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);*/
        Intent google_account=new Intent(context,Login.class);
        google_account.setAction(ACTION_GOTO_ACCOUNT);
        google_account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent agregar = new Intent(context, AgregarActividad.class);
        agregar.setAction(ACTION_GOTO_NEW_TASK);
        agregar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        remoteViews.setOnClickPendingIntent(R.id.widget_title_add, PendingIntent.getActivity(context,0,agregar,PendingIntent.FLAG_UPDATE_CURRENT));
        //remoteViews.setOnClickPendingIntent(R.id.btn_login,PendingIntent.getActivity(context,0,login,PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.widget_title_star,PendingIntent.getActivity(context,0,google_account,PendingIntent.FLAG_UPDATE_CURRENT));
        /***
         * Para acceder a un elemento de la lista
         */

        Intent clickIntentTemplate = new Intent(context, DetailActividad.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_main_view, clickPendingIntentTemplate);

        remoteViews.setRemoteAdapter(R.id.widget_main_view,svcIntent);
        remoteViews.setEmptyView(R.id.widget_main_view,R.layout.widget_card_empty);

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

