package com.example.proyecto_moviles.ui.Clases;

public class Usuario {
    private String nombre;
    private String correo;
    private String estado;

    public Usuario(String nombre, String correo, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
