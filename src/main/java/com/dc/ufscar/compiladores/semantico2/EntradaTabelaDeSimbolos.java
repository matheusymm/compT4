package com.dc.ufscar.compiladores.semantico2;

import java.util.ArrayList;
import java.util.List;

import com.dc.ufscar.compiladores.semantico2.TabelaDeSimbolos.TipoJander;

public class EntradaTabelaDeSimbolos {
    String nome;
    TipoJander tipo;
    Boolean ponteiro;
    TabelaDeSimbolos tabelaRegistro;
    List<String> parametros = new ArrayList<String>();

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
