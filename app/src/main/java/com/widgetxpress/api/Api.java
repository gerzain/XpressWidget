package com.widgetxpress.api;

import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.Actividades;
import com.widgetxpress.model.EditarActividad;
import com.widgetxpress.model.ResponseActividad;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Irving on 19/07/2017.
 */

public interface Api
{
    /**
     * Función que permite obtener las actividades
     * @param id El id unico de Google
     * @return Las actividades que tiene el usuario
     */
    @GET("getActivities.php?=")
    Call<ResponseActividad>getActividades(@Query("id") String id);

    @POST("addActivity.php?actividad=")
    Call<Actividad> agregarActividad(@Body  Actividad actividad);

}
