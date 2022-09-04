package com.example.avfisica.models.treino;

public class TreinoModel {

    private long id_treino;

    private long id_ficha;

    private String data;

    private long id_login;

    private String status;

    public long getId_treino() {
        return id_treino;
    }

    public void setId_treino(long id_treino) {
        this.id_treino = id_treino;
    }

    public long getId_ficha() {
        return id_ficha;
    }

    public void setId_ficha(long id_ficha) {
        this.id_ficha = id_ficha;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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


