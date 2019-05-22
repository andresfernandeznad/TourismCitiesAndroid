package com.example.andres.tourismcities.modelos;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Lugar implements Serializable {


    @Expose
    @SerializedName("nombre")
    private String nombre;

    @Expose
    @SerializedName("latitud")
    private double latitud;

    @Expose
    @SerializedName("longitud")
    private double longitud;

    @Expose
    @SerializedName("imagen")
    private String imagen;

    @Expose
    @SerializedName("descripcion")
    private String descripcion;

    @Expose
    @SerializedName("posicionFirebaseFav")
    private int posicionFirebaseFav;

    public Lugar(String nombre, double latitud, double longitud, String imagen, String descripcion) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPosicionFirebaseFav() {
        return posicionFirebaseFav;
    }

    public void setPosicionFirebaseFav(int posicionFirebaseFav) {
        this.posicionFirebaseFav = posicionFirebaseFav;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lugar lugar = (Lugar) o;
        return Double.compare(lugar.latitud, latitud) == 0 &&
                Double.compare(lugar.longitud, longitud) == 0 &&
                Objects.equals(nombre, lugar.nombre);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(nombre, latitud, longitud);
    }
}
