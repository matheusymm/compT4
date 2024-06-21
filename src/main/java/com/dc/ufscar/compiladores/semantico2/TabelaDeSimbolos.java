package com.dc.ufscar.compiladores.semantico2;

import java.util.HashMap;

public class TabelaDeSimbolos {
    public enum TipoJander {
        LITERAL,
        INTEIRO,
        REAL,
        LOGICO,
        // PINTEIRO,
        // PREAL,
        // PLITERAL,
        // PLOGICO,
        INVALIDO,
        VOID,
        REGISTRO
    }

    class EntradaTabelaDeSimbolos {
        String nome;
        TipoJander tipo;
        Boolean ponteiro;
        TabelaDeSimbolos tabelaRegistro;
        String nomeRegistro;
        // boolean constante ?

        private EntradaTabelaDeSimbolos(String nome, TipoJander tipo) {
            this.nome = nome;
            this.tipo = tipo;
            this.ponteiro = false;
        }

        private EntradaTabelaDeSimbolos(String nome, TipoJander tipo, Boolean ponteiro) {
            this.nome = nome;
            this.tipo = tipo;
            this.ponteiro = ponteiro;
        }

        // public void criaTabelaRegistro() {
        // this.tabelaRegistro = new TabelaDeSimbolos();
        // }
    }

    private final HashMap<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    public void adicionar(String nome, TipoJander tipo) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo));
    }

    public void setNomeRegistro(String nome, String tipoRegistro) {
        tabela.get(nome).nomeRegistro = tipoRegistro;
    }

    public void adicionar(String nome, TipoJander tipo, Boolean ponteiro) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, ponteiro));
    }

    public void printTabela() {
        System.out.println("---------Tabela de Simbolos--------");
        for (HashMap.Entry<String, EntradaTabelaDeSimbolos> entry : tabela.entrySet()) {
            System.out.println("--1--Nome: " + entry.getValue().nome + " Tipo: " + entry.getValue().tipo + " Ponteiro: "
                    + entry.getValue().ponteiro);
        }
    }

    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    public TipoJander verificar(String nome) {
        return tabela.get(nome).tipo;
    }

    public TabelaDeSimbolos verificarRegistro(String nome) {
        return tabela.get(nome).tabelaRegistro;
    }

    public Boolean verificarPonteiro(String nome) {
        return tabela.get(nome).ponteiro;
    }

    public void adicionarRegistro(String nome, TabelaDeSimbolos tabelaRegistro) {
        tabela.get(nome).tabelaRegistro = tabelaRegistro;
    }
}
