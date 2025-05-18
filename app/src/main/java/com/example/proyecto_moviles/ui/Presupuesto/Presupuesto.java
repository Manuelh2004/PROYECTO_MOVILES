package com.example.proyecto_moviles.ui.Presupuesto;

import com.example.proyecto_moviles.ui.Categoria;

public class Presupuesto {

    String id;
    double monto;
    String fechaInicio;
    String fechaFin;

    Categoria categoria;

    public Presupuesto(String id, double monto, String fechaInicio, String fechaFin, Categoria categoria) {
        this.id = id;
        this.monto = monto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
