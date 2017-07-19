package com.widgetxpress.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Irving on 18/07/2017.
 */

public class Actividad
{

    @SerializedName("id")

    private String id;
    @SerializedName("titulo")
    private String titulo;
    @SerializedName("creador")
    private GoogleUser creador;
    @SerializedName("check")
    private Boolean check;
    @SerializedName("fecha_inicio")
    private String fechaInicio;
    @SerializedName("fecha_fin")
    private String fechaFin;
    @SerializedName("hour")
    private String hour;
    @SerializedName("recordatorio")
    private String recordatorio;

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public GoogleUser getCreador()
    {
        return creador;
    }

    public Boolean getCheck() {
        return check;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getHour() {
        return hour;
    }

    public String getRecordatorio() {
        return recordatorio;
    }
}
