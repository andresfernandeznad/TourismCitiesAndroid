package com.example.andres.tourismcities.modelos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Favoritos implements Serializable {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("lugaresFavoritos")
    private List<Lugar> lugaresFavoritos;

    public Favoritos(String id) {
        this.id = id;
    }

    public Favoritos(String id, List<Lugar> lugaresFavoritos) {
        this.id = id;
        this.lugaresFavoritos = lugaresFavoritos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Lugar> getLugaresFavoritos() {
        return lugaresFavoritos;
    }

    public void setLugaresFavoritos(List<Lugar> lugaresFavoritos) {
        this.lugaresFavoritos = lugaresFavoritos;
    }

    public void addLugar(Lugar lugar) {
        this.lugaresFavoritos.add(lugar);
    }
}
