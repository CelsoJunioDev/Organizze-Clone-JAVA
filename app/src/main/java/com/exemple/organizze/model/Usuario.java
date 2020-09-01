package com.exemple.organizze.model;

import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

//cria os atributos, gera um construtor vazio. gera getter e setter dos atributos.
public class Usuario {
    private String nome, email, senha, idUsuario;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;

    public Usuario() {
    }
    public void salvar (){ //metodo para salvar usuario

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase(); //recupera o database
        firebase.child("usuarios")
                .child(this.idUsuario) //salva o email codificado
                .setValue(this); //salva os valores ja configurados no Objeto Usuario
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    @Exclude //remove o condigo do email do database. ignora na hora de salvar os dados
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //remove a senha do database. ignora na hora de salvar os dados
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
