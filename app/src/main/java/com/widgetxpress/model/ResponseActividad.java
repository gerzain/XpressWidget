package com.widgetxpress.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Irving on 18/07/2017.
 */

public class ResponseActividad
{
    @SerializedName("status")
    @Expose
    private String status;
    @Expose
    @SerializedName("data")
    private List<Actividad> actividades;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades)
    {
        this.actividades = actividades;
    }
}
