package com.example.avfisica.models.treino;

public class Exercicio {

    private long id_excercicio;

    private String nome;

    private String tipo;

    private String path_gif;

    private String drawable;

    private long id_login;

    private String status;

    public long getId_excercicio() {
        return id_excercicio;
    }

    public void setId_excercicio(long id_excercicio) {
        this.id_excercicio = id_excercicio;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() {
        return tipo;
    }

    public void setTipo (String tipo) {this.tipo = tipo; }

    public String getPath_gif() {
        return path_gif;
    }

    public void setPath_gif(String path_gif) {
        this.path_gif = path_gif;
    }

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
        this.drawable = drawable;
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


