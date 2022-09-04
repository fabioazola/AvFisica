package com.example.avfisica.models;

public class Peso {

    private long id_peso;

    private String data;

    private float peso;

    private float objetivo;

    private long id_login;

    private String status;


    public long getId_peso() {
        return id_peso;
    }

    public void setId_peso(long id_peso) {
        this.id_peso = id_peso;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public float getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(float objetivo) {
        this.objetivo = objetivo;
    }

    public long getId_login() {
        return id_login;
    }

    public void setId_login(long id_login) {
        this.id_login = id_login;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


