package com.widgetxpress;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.widgetxpress.activity.DetailActividad;
import com.widgetxpress.activity.Login;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.util.MyUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Irving on 18/07/2017.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory
{

    private List<Actividad> actividades;
    private Context mContext = null;
    private static final String TAG=ListProvider.class.getSimpleName();
    private int appWidgetId;
    private String tiempo;
    private String fecha;

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
    public RemoteViews getViewAt(int i)
    {
         String fmt = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(fmt, Locale.getDefault());
        RemoteViews views=new RemoteViews(mContext.getPackageName(),R.layout.card_actividades);
         Actividad actividad=actividades.get(i);
        try
        {
            Date dt = df.parse(actividad.getFechaInicio());
            Date ds=df.parse(actividad.getFechaInicio());
            DateFormat tdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            DateFormat dtf=new SimpleDateFormat("EEE, MMM dd",Locale.getDefault());
            tiempo = tdf.format(dt);
            fecha=dtf.format(ds);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
           //Log.d(TAG,String.valueOf(actividad.getCheck()));

        if(actividad.getCheck())
        {
            views.setImageViewResource(
                    R.id.widget_card_star,R.drawable.ic_star);
        }
        else
        {
            views.setImageViewResource(R.id.widget_card_star,R.drawable.ic_star_gris);
        }

            views.setTextViewText(R.id.tv_creador_actividad,actividad.getCreador().getWikiname());
            views.setTextViewText(R.id.widget_card_text, MyUtil.stringLengthCut(actividad.getTitulo()));
            views.setTextViewText(R.id.widget_card_time,tiempo);
            views.setTextViewText(R.id.widget_card_date,fecha);

            Intent fillInIntent = new Intent(mContext, DetailActividad.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", actividad.getId());
            bundle.putString("titulo", actividad.getTitulo());
            bundle.putString("fecha_inicio", actividad.getFechaInicio());
            bundle.putString("fecha_fin", actividad.getFechaFin());
            bundle.putString("responsable", actividad.getCreador().getEmail());
            bundle.putString("picture", actividad.getCreador().getPicture());
            bundle.putBoolean("check", actividad.getCheck());
            fillInIntent.putExtras(bundle);
            fillInIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            views.setOnClickFillInIntent(R.id.widget_card_click_edit, fillInIntent);




        return views;
    }


    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(),R.layout.loading_layout);
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
