package com.example.avfisica.models.treino;

public class FichaItens {

    private long id_ficha_itens;

    private long id_ficha;

    private long id_exercicio;

    private long quantidade;

    private long repeticao;

    private String obs;

    private long id_login;

    private String status;

    public long getId_ficha_itens() {
        return id_ficha_itens;
    }

    public void setId_ficha_itens(long id_ficha_itens) {
        this.id_ficha_itens = id_ficha_itens;
    }

    public long getId_ficha() {
        return id_ficha;
    }

    public void setId_ficha(long id_ficha) {
        this.id_ficha = id_ficha;
    }

    public long getId_exercicio() {
        return id_exercicio;
    }

    public void setId_exercicio(long id_exercicio) {
        this.id_exercicio = id_exercicio;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }

    public long getRepeticao() {
        return repeticao;
    }

    public void setRepeticao(long repeticao) {
        this.repeticao = repeticao;
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


