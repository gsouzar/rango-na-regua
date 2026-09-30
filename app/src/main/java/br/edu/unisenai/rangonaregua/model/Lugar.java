package br.edu.unisenai.rangonaregua.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Lugar implements Serializable {

    private String id;          // id do documento (não é gravado como campo)
    private String uid;         // dono do lugar
    private String nome;
    private String categoria;
    private double precoMedio;
    private String observacao;
    private int votos;

    public Lugar() { }          // obrigatório para o toObject()

    public Lugar(String nome, String categoria, double precoMedio, String observacao, int votos) {
        this.nome = nome;
        this.categoria = categoria;
        this.precoMedio = precoMedio;
        this.observacao = observacao;
        this.votos = votos;
    }

    @Exclude public String getId() { return id; }
    @Exclude public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public double getPrecoMedio() { return precoMedio; }
    public void setPrecoMedio(double precoMedio) { this.precoMedio = precoMedio; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public int getVotos() { return votos; }
    public void setVotos(int votos) { this.votos = votos; }
}
