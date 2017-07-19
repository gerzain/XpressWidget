package com.widgetxpress.api;

import com.widgetxpress.model.ResponseActividad;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Irving on 19/07/2017.
 */

public interface Api
{
    @GET("getActivities.php?=")
    Call<ResponseActividad>getActividades(@Query("id") String id);

}
