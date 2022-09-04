package com.example.avfisica.models.treino;

public class CargaPeso {

    private long id_carga_peso;

    private long id_exercicio;

    private long id_treino;

    private float peso;

    private long repeticao;

    private long flagExecutado; //0 = false , 1 = true

    private String obs;

    private long id_login;

    private String status;

    public long getId_carga_peso() {
        return id_carga_peso;
    }

    public void setId_carga_peso(long id_carga_peso) {
        this.id_carga_peso = id_carga_peso;
    }

    public long getId_exercicio() {
        return id_exercicio;
    }

    public void setId_exercicio(long id_exercicio) {
        this.id_exercicio = id_exercicio;
    }

    public long getId_treino() {
        return id_treino;
    }

    public void setId_treino(long id_treino) {
        this.id_treino = id_treino;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public long getRepeticao() {
        return repeticao;
    }

    public void setRepeticao(long repeticao) {
        this.repeticao = repeticao;
    }

    public long getFlagExecutado() {
        return flagExecutado;
    }

    public void setFlagExecutado(long flagExecutado) {
        this.flagExecutado = flagExecutado;
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


