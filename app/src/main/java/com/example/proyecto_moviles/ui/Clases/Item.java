package com.example.proyecto_moviles.ui.Clases;

public class Item {
    public int id;
    public String nombre;

    public Item(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}