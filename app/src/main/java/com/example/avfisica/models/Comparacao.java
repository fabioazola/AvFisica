package com.example.avfisica.models;

public class Comparacao {

    private Long id_comparacao;

    private String data;

    private String path;

    private String tipo;

    private long id_login;

    public Long getId_comparacao() {
        return id_comparacao;
    }

    public void setId_comparacao(Long id_comparacao) {
        this.id_comparacao = id_comparacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getId_login() {
        return id_login;
    }

    public void setId_login(long id_login) {
        this.id_login = id_login;
    }
}


