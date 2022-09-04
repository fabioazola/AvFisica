package com.example.avfisica.models.treino;

public class Ficha {

    private long id_ficha;

    private String nome;

    private String data;

    private int flagAtivo;

    private String obs;

    private long id_login;

    private String status;

    public long getId_ficha() {
        return id_ficha;
    }

    public void setId_ficha(long id_ficha) {
        this.id_ficha = id_ficha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getFlagAtivo() {
        return flagAtivo;
    }

    public void setFlagAtivo(int flagAtivo) {
        this.flagAtivo = flagAtivo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
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


