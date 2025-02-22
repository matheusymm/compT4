package com.dc.ufscar.compiladores.semantico2;

import java.util.LinkedList;
import java.util.List;

public class Escopos {
    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;
    
    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo();
    }

    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

    public List<TabelaDeSimbolos> percorrerEscoposAninhados() {
        return pilhaDeTabelas;
    }

    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }

    public void adicionarEscopo(TabelaDeSimbolos tabela) {
        pilhaDeTabelas.push(tabela);
    }
}
