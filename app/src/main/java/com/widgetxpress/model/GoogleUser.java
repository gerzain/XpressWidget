package com.widgetxpress.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Irving on 18/07/2017.
 */

public class GoogleUser
{

    @SerializedName("id")
    private String id;
    @SerializedName("wikiname")
    private String wikiname;
    @SerializedName("email")
    private String email;
    @SerializedName("picture")
    private String picture;
    @SerializedName("id_origen")
    private String idOrigen;
    @SerializedName("origen")
    private String origen;
    @SerializedName("token")
    private String token;

    public GoogleUser(String id, String wikiname, String email, String picture, String idOrigen, String origen) {
        this.id = id;
        this.wikiname = wikiname;
        this.email = email;
        this.picture = picture;
        this.idOrigen = idOrigen;
        this.origen = origen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWikiname() {
        return wikiname;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public String getOrigen() {
        return origen;
    }

    public String getToken() {
        return token;
    }
}
