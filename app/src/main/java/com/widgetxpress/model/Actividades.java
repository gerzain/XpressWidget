package com.widgetxpress.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by resen on 27/07/2017.
 */

public class Actividades
{
    @SerializedName("id")

    private String id;
    @SerializedName("titulo")
    private String titulo;
    @SerializedName("creador")
    private String creador;
    @SerializedName("responsable")
    private String responsable;
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

    public Actividades(String titulo, String creador, String responsable, Boolean check, String fechaInicio, String fechaFin, String recordatorio) {
        this.titulo = titulo;
        this.creador = creador;
        this.responsable = responsable;
        this.check = check;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.recordatorio=recordatorio;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCreador() {
        return creador;
    }

    public String getResponsable() {
        return responsable;
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
