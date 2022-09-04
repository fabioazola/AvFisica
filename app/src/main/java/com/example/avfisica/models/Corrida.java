package com.example.avfisica.models;

public class Corrida {

    private Long id_corrida;

    private String data;

    private float distancia;

    private String tempo;

    private float pace;

    private float vo2;

    private long nivel;

    private long id_login;

    private String status;

    public Long getId_corrida() {
        return id_corrida;
    }

    public void setId_corrida(Long id_corrida) {
        this.id_corrida = id_corrida;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public float getVo2() {
        return vo2;
    }

    public void setVo2(float vo2) {
        this.vo2 = vo2;
    }

    public long getNivel() {
        return nivel;
    }

    public void setNivel(long nivel) {
        this.nivel = nivel;
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


