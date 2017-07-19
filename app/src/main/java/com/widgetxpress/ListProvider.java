package com.widgetxpress;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.widgetxpress.model.Actividad;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Irving on 18/07/2017.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory
{

    private List<Actividad> actividades;
    private Context mContext = null;
    private static final String TAG=ListProvider.class.getSimpleName();
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        mContext = context;
        actividades=new ArrayList<>();
        populateListItem();
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void populateListItem()
    {
        if (WidgetFetchService.actividades != null)
        {
            actividades = new ArrayList<>(WidgetFetchService.actividades);
        } else
            {
            actividades = new ArrayList<>();
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate called"+ "onCreate ");
        Log.i(TAG,"Tamaño de la lista :"+ String.valueOf(actividades.size()));
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return actividades.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        final RemoteViews views=new RemoteViews(mContext.getPackageName(),R.layout.item_lista_actividad);
        final Actividad actividad=actividades.get(i);
        views.setTextViewText(R.id.titulo_actividad,actividad.getTitulo());
        views.setTextViewText(R.id.tv_fecha_inicio,actividad.getFechaInicio());
        views.setTextViewText(R.id.tv_creador_actividad,actividad.getCreador().getWikiname());

        return views;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
      return  i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
