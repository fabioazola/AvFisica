package com.example.avfisica.models;

public class TaxaModel {

    private long id_taxa_metabolica;

    private String data;

    private String tipo;

    private float kilos;

    private float tempo;

    private float gasto_treino;

    private float taxa;

    private float proteina;

    private float gordura;

    private float carbo;

    private long id_login;

    private String status;

    public long getId_taxa() {
        return id_taxa_metabolica;
    }
    public void setId_taxa(long id_taxa) {this.id_taxa_metabolica = id_taxa;}

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public String getTipo() { return tipo;}
    public void setTipo(String tipo) { this.tipo = tipo;    }

    public float getPeso() {
        return kilos;
    }   // ALTERAR O NOME NA NUVEM
    public void setPeso(float peso) {
        this.kilos = peso;
    }

    public float getTempo() {
        return tempo;
    }
    public void setTempo(float tempo) {
        this.tempo = tempo;
    }


    public float getGasto_treino() {
        return gasto_treino;
    }
    public void setGasto_treino(float gasto_treino) {
        this.gasto_treino = gasto_treino;
    }


    public float getOption() {
        return taxa;
    }   // ALTERAR O NOME NA NUVEM
    public void setOption(float option) {
        this.taxa = option;
    }

    public float getProteina() {
        return proteina;
    }
    public void setProteina(float proteina) {
        this.proteina = proteina;
    }

    public float getGordura() {
        return gordura;
    }
    public void setGordura(float gordura) {this.gordura = gordura;
    }

    public float getCarboidrato() {
        return carbo;
    }           // ALTERAR NA NUVEM
    public void setCarboidrato(float carboidrato) {
        this.carbo = carboidrato;
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


