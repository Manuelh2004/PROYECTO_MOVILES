package com.example.proyecto_moviles.ui.Movimiento;

public class Movimiento {
    String id_movimiento;
    String usuario;
    String tipo_movimiento;
    String categoria;
    String monto;
    String fecha;
    String descripcion;
    String estado;
    public Movimiento(String id_movimiento, String usuario, String tipo_movimiento, String categoria,
                      String monto, String fecha, String descripcion, String estado) {
        this.id_movimiento = id_movimiento;
        this.usuario = usuario;
        this.tipo_movimiento = tipo_movimiento;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getId_movimiento() {
        return id_movimiento;
    }

    public void setId_movimiento(String id_movimiento) {
        this.id_movimiento = id_movimiento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTipo_movimiento() {
        return tipo_movimiento;
    }

    public void setTipo_movimiento(String tipo_movimiento) {
        this.tipo_movimiento = tipo_movimiento;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
