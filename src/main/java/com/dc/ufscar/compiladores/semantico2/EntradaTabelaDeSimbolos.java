package com.dc.ufscar.compiladores.semantico2;

import com.dc.ufscar.compiladores.semantico2.TabelaDeSimbolos.TipoJander;

public class EntradaTabelaDeSimbolos {
    String nome;
    TipoJander tipo;
    Boolean ponteiro;
    TabelaDeSimbolos tabelaRegistro;
    // boolean constante ?

    public EntradaTabelaDeSimbolos(String nome, TipoJander tipo) {
        this.nome = nome;
        this.tipo = tipo;
        this.ponteiro = false;
    }

    public EntradaTabelaDeSimbolos(String nome, TipoJander tipo, Boolean ponteiro) {
        this.nome = nome;
        this.tipo = tipo;
        this.ponteiro = ponteiro;
    }
}
