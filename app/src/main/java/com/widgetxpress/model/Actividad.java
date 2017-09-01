package com.widgetxpress.model;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
    @SerializedName("responsable")
    private GoogleUser responsable;
    @SerializedName("check")
    private boolean check;
    @SerializedName("fecha_inicio")
    private String fechaInicio;
    @SerializedName("fecha_fin")
    private String fechaFin;
    @SerializedName("hour")
    private String hour;
    @SerializedName("recordatorio")
    private String recordatorio;

    public Actividad(String titulo, GoogleUser creador, GoogleUser responsable, boolean check, String fechaInicio, String fechaFin, String recordatorio) {
        this.titulo = titulo;
        this.creador = creador;
        this.responsable= responsable;
        this.check = check;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.recordatorio = recordatorio;
    }



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

    public boolean getCheck() {
        return check;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public GoogleUser getResponsable() {
        return responsable;
    }

    public String getHour() {
        return hour;
    }

    public String getRecordatorio() {
        return recordatorio;
    }
}
