package com.example.avfisica.models.treino;

public class TreinoJoin {

    private long id_treino;

    private long id_ficha;

    private String ficha_nome;

    private long id_exercicio;

    private String exercicio_nome;

    private long id_carga_peso;

    private float peso;

    private String data;

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

    public String getFicha_nome() {
        return ficha_nome;
    }

    public void setFicha_nome(String ficha_nome) {
        this.ficha_nome = ficha_nome;
    }

    public long getId_exercicio() {
        return id_exercicio;
    }

    public void setId_exercicio(long id_exercicio) {
        this.id_exercicio = id_exercicio;
    }

    public String getExercicio_nome() {
        return exercicio_nome;
    }

    public void setExercicio_nome(String exercicio_nome) {
        this.exercicio_nome = exercicio_nome;
    }

    public long getId_carga_peso() {
        return id_carga_peso;
    }

    public void setId_carga_peso(long id_carga_peso) {
        this.id_carga_peso = id_carga_peso;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}



