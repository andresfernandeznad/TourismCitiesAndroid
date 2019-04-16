package com.example.andres.tourismcities.modelos;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario implements Serializable {

    @Expose
    @SerializedName("idUsuario")
    private String idUsuario ;

    @Expose
    @SerializedName("usuario")
    private String usuario ;

    @Expose
    @SerializedName("nombre")
    private String nombre ;

    @Expose
    @SerializedName("apellidos")
    private String apellidos ;

    @Expose
    @SerializedName("email")
    private String email ;

    @Expose
    @SerializedName("photo")
    private Uri urlProfilePhoto;

    public Usuario(String idUsuario, /*String usuario,*/ String nombre, String apellidos, String email, Uri urlProfilePhoto) {
        this.idUsuario = idUsuario;
        //this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.urlProfilePhoto = urlProfilePhoto;
    }

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /*public String getUsuario() {
        return usuario;
    }*/

    /*public void setUsuario(String usuario) {
        this.usuario = usuario;
    }*/

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getUrlProfilePhoto() {
        return urlProfilePhoto;
    }

    public void setUrlProfilePhoto(Uri urlProfilePhoto) {
        this.urlProfilePhoto = urlProfilePhoto;
    }

    public String getNombreCompleto() {
        return this.getNombre() + " " + this.getApellidos();
    }
}
