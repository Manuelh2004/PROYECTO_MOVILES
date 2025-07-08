package com.example.proyecto_moviles.ui.Clases;
public class Comentario {
    private int id_comentario;
    private String men_comentario;
    private String fre_comentario;
    private String est_comentario;

    public Comentario(int id_comentario, String men_comentario, String fre_comentario, String est_comentario) {
        this.id_comentario = id_comentario;
        this.men_comentario = men_comentario;
        this.fre_comentario = fre_comentario;
        this.est_comentario = est_comentario;
    }

    public int getId_comentario() {
        return id_comentario;
    }

    public void setId_comentario(int id_comentario) {
        this.id_comentario = id_comentario;
    }

    public String getMen_comentario() {
        return men_comentario;
    }

    public void setMen_comentario(String men_comentario) {
        this.men_comentario = men_comentario;
    }

    public String getFre_comentario() {
        return fre_comentario;
    }

    public void setFre_comentario(String fre_comentario) {
        this.fre_comentario = fre_comentario;
    }

    public String getEst_comentario() {
        return est_comentario;
    }

    public void setEst_comentario(String est_comentario) {
        this.est_comentario = est_comentario;
    }
}
