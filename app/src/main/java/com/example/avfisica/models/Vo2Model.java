package com.example.avfisica.models;

public class Vo2Model {

    private long id_vo2;

    private String data;

    private float vo2;

    private float distancia;

    private float nivel;

    private long id_login;

    private String status;


    public long getId_vo2() {
        return id_vo2;
    }

    public void setId_vo2(long id_vo2) {
        this.id_vo2 = id_vo2;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getVo2() {
        return vo2;
    }

    public void setVo2(float vo2) {
        this.vo2 = vo2;
    }

    public float getObjetivo() {
        return nivel;
    }

    public void setObjetivo(float objetivo) {
        this.nivel = objetivo;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
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


