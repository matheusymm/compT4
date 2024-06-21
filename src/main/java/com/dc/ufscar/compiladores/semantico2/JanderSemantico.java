package com.dc.ufscar.compiladores.semantico2;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.dc.ufscar.compiladores.semantico2.TabelaDeSimbolos.TipoJander;

public class JanderSemantico extends JanderBaseVisitor<Void> {
    TabelaDeSimbolos REGISTROS = new TabelaDeSimbolos();
    Escopos escoposAninhados = new Escopos();
    int qtdEscopos = 0;

    // aqui temos uma tabela, a global, precisamos ver
    // como faremos em relação a tabela de escopos
    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        // tabela = new TabelaDeSimbolos();
        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitDecl_local_global(JanderParser.Decl_local_globalContext ctx) {

        if (ctx.declaracao_local() != null) {
            visitDeclaracao_local(ctx.declaracao_local());
        } else if (ctx.declaracao_global() != null) {
            visitDeclaracao_global(ctx.declaracao_global());
        }
        return super.visitDecl_local_global(ctx);
    }

    @Override
    public Void visitDeclaracao_local(JanderParser.Declaracao_localContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        if (ctx.variavel() != null) {
            System.out.println("Qtd Escolo DL: " + escoposAninhados.percorrerEscoposAninhados().size());
            System.out.println("Declaracao Local Chama VisitVariavel");
            return visitVariavel(ctx.variavel());
        } else if (ctx.const_ != null) {
            String nomeVar = ctx.IDENT().getText();
            String strTipoVar = ctx.tipo_basico().getText();
            TipoJander tipoVar = JanderSemanticoUtils.getTipo(strTipoVar);

            if (tabela.existe(nomeVar)) {
                JanderSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "Variável " + nomeVar + " já existe");
            } else {
                if (nomeVar.contains("^")) {
                    tabela.adicionar(nomeVar, tipoVar, true);
                } else {
                    tabela.adicionar(nomeVar, tipoVar);
                }
            }
        } else if (ctx.IDENT() != null) {
            String nomeVar = ctx.IDENT().getText();

            if (tabela.existe(nomeVar)) {
                JanderSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + nomeVar + " ja declarado anteriormente");
            } else {
                tabela.adicionar(nomeVar, TipoJander.REGISTRO);
                TabelaDeSimbolos tabelaRegistro = new TabelaDeSimbolos();
                tabela.adicionarRegistro(nomeVar, tabelaRegistro);
                System.out.println("Visit Local: " + ctx.getText());
            }
        }

        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitDeclaracao_global(JanderParser.Declaracao_globalContext ctx) {
        escoposAninhados.criarNovoEscopo();
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        if (ctx.FUNCAO() != null) {
            String nomeFuncao = ctx.IDENT().getText();
            // TipoJander tipoFuncao = JanderSemanticoUtils.verificarTipo(tabela,
            // ctx.tipo_estendido());
            TipoJander tipoFuncao = TipoJander.REAL;
            tabela.adicionar(nomeFuncao, tipoFuncao);
        } else if (ctx.PROCEDIMENTO() != null) {
            String nomeProcedimento = ctx.IDENT().getText();
            tabela.adicionar(nomeProcedimento, TipoJander.VOID);
        }
        for (JanderParser.ParametroContext param : ctx.parametros().parametro()) {
            for (JanderParser.IdentificadorContext ident : param.identificador()) {
                String nomeParam = ident.getText();
                String strTipoParam = param.tipo_estendido().getText();
                TipoJander tipoParam = JanderSemanticoUtils.getTipo(strTipoParam);

                if (tabela.existe(nomeParam)) {
                    JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                            "Parâmetro " + nomeParam + " já existe");
                } else {
                    tabela.adicionar(nomeParam, tipoParam);
                }
            }
        }
        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Void visitVariavel(JanderParser.VariavelContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        String strTipoVar = ctx.tipo().getText();
        TipoJander tipoVar = JanderSemanticoUtils.getTipo(strTipoVar.startsWith("registro") ? "registro" : strTipoVar);
        Boolean registro = false;
        List<TabelaDeSimbolos> tabelasRegistro = new ArrayList<>();

        System.out.println("VisitVariavel: " + strTipoVar);
        if (qtdEscopos == 0) {
            for (JanderParser.IdentificadorContext ident : ctx.identificador()) {
                String nomeVar = ident.getText();
                if (tabela.existe(nomeVar)) {
                    JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                            "identificador " + nomeVar + " ja declarado anteriormente");
                } else {
                    System.out.println("VisitVariavel = 0: " + nomeVar + " " + tipoVar);
                    tabela.adicionar(nomeVar, tipoVar);
                    if (tipoVar == TipoJander.REGISTRO) {
                        System.out.println("Tipo Registro = 0");
                        registro = true;
                        TabelaDeSimbolos tabelaRegistro = new TabelaDeSimbolos();
                        tabelasRegistro.add(tabelaRegistro);
                        REGISTROS.adicionarRegistro(nomeVar, tabelaRegistro);
                    } else if (tipoVar == TipoJander.INVALIDO) {
                        JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                                "tipo " + strTipoVar + " nao declarado");
                    }
                }
            }
        }
        while (qtdEscopos > 0) {
            for (JanderParser.IdentificadorContext ident : ctx.identificador()) {
                String nomeVar = ident.getText();
                if (tabela.existe(nomeVar)) {
                    JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                            "identificador " + nomeVar + " ja declarado anteriormente");
                } else {
                    tabela.adicionar(nomeVar, tipoVar);
                    if (tipoVar == TipoJander.REGISTRO) {
                        registro = true;
                        System.out.println("Variavel Registro: " + nomeVar);
                        TabelaDeSimbolos tabelaRegistro = new TabelaDeSimbolos();
                        tabelasRegistro.add(tabelaRegistro);
                        REGISTROS.adicionarRegistro(nomeVar, tabelaRegistro);
                    } else if (tipoVar == TipoJander.INVALIDO) {
                        JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                                "tipo " + strTipoVar + " nao declarado");
                    }
                }
            }
            qtdEscopos--;
            escoposAninhados.abandonarEscopo();
            tabela = escoposAninhados.obterEscopoAtual();
        }
        if (registro) {
            for (TabelaDeSimbolos tabelaRegistro : tabelasRegistro) {
                escoposAninhados.adicionarEscopo(tabelaRegistro);
                qtdEscopos++;
            }
            tabelasRegistro.clear();
        }
        System.out.println("qtdEscopo Variavel: " + escoposAninhados.percorrerEscoposAninhados().size());
        TabelaDeSimbolos tab2 = tabela.verificarRegistro("tVinho");
        tab2.printTabela();
        // na duvida tira (:
        return super.visitVariavel(ctx);
    }

    @Override
    public Void visitCmdAtribuicao(JanderParser.CmdAtribuicaoContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        JanderSemanticoUtils.setNomeVarAtrib(ctx.identificador().getText());
        System.out.println("expCmdATRIB :" + ctx.expressao().getText());
        TipoJander tipoExpressao = JanderSemanticoUtils.verificarTipo(tabela, ctx.expressao());
        TipoJander tipoVar;
        if (tipoExpressao != TipoJander.INVALIDO) {
            String nomeVar = ctx.identificador().getText().split("\\.")[0];
            ;
            if (!tabela.existe(nomeVar)) {
                JanderSemanticoUtils.adicionarErroSemantico(ctx.identificador().start,
                        "identificador1 " + nomeVar + " nao declarado");
            } else {

                if (tabela.verificar(nomeVar) == TipoJander.REGISTRO) {
                    TabelaDeSimbolos tabelaRegistro = tabela.verificarRegistro(nomeVar);

                    tipoVar = tabelaRegistro.verificar(ctx.identificador().getText().split("\\.")[1]);

                } else {
                    tipoVar = tabela.verificar(nomeVar);
                }

                // Se for endereço verificar de outra forma, como ?
                if (JanderSemanticoUtils.verificarTipoCompativeL(tabela, tipoVar, tipoExpressao)
                        || (tabela.verificarPonteiro(nomeVar) && !ctx.expressao().getText().contains("&"))) {
                    JanderSemanticoUtils.adicionarErroSemantico(ctx.identificador().start,
                            "atribuicao nao compativel para " + (ctx.circ != null ? ctx.circ.getText() : "")
                                    + ctx.identificador().getText());
                }
            }
        }

        return super.visitCmdAtribuicao(ctx);
    }

    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        for (JanderParser.IdentificadorContext ident : ctx.identificador()) {
            String nomeVar = ident.getText();
            if (!tabela.existe(nomeVar.split("\\.")[0])) {
                JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                        "identificador " + nomeVar + " nao declarado");
            } else {
                System.out.println("cmdLeia " + nomeVar.split("\\.")[0]);
                TabelaDeSimbolos tabelaRegistro = tabela.verificarRegistro(nomeVar.split("\\.")[0]);
                System.out.println("QtdEscopos: " + escoposAninhados.percorrerEscoposAninhados().size());
                if (!tabelaRegistro.existe(nomeVar.split("\\.")[1])) {
                    JanderSemanticoUtils.adicionarErroSemantico(ident.start,
                            "identificador " + nomeVar.split("\\.")[1] + " nao declarado");
                }
            }
        }
        return super.visitCmdLeia(ctx);
    }

    @Override
    public Void visitParcela_nao_unario(JanderParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        if (ctx.identificador() != null) {
            JanderSemanticoUtils.verificarTipo(tabela, ctx);
        } else if (ctx.CADEIA() != null) {

        }

        return super.visitParcela_nao_unario(ctx);
    }

    @Override
    public Void visitParcela_unario(JanderParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
        if (ctx.identificador() != null) {
            JanderSemanticoUtils.verificarTipo(tabela, ctx);
        } else if (ctx.NUM_INT() != null) {
        } else if (ctx.NUM_REAL() != null) {
        }

        return super.visitParcela_unario(ctx);
    }

    // tb->tb1
    // tb(como registro), tbRegistros
    // @Override
    // public Void visitIdentificador(JanderParser.IdentificadorContext ctx) {
    // TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
    // System.out.println("VisitIdentNm: " + ctx.getText());
    // System.out.println(" qnts escopos: " +
    // escoposAninhados.percorrerEscoposAninhados().size());
    // tabela.printTabela();
    // System.out.println("--------------------");

    // String nomeVar = ctx.getText().split("\\.")[0];
    // if (tabela.existe(nomeVar)) {
    // TipoJander tipoVar = tabela.verificar(nomeVar);
    // if (tipoVar == TipoJander.REGISTRO) {
    // TabelaDeSimbolos tabelaRegistro = tabela.verificarRegistro(nomeVar);
    // if (tabelaRegistro.existe(ctx.getText().split("\\.")[1])) {
    // System.out.println("VisitIdentIf2: " + nomeVar + " " + tipoVar);
    // } else {
    // JanderSemanticoUtils.adicionarErroSemantico(ctx.start,
    // "identificador " + ctx.getText() + " nao declarado");
    // }
    // }
    // System.out.println("VisitIdentIf: " + nomeVar + " " + tipoVar);
    // }

    // return super.visitIdentificador(ctx);
    // }

    // @Override
    // public Void visitTipo(JanderParser.TipoContext ctx) {
    //     TabelaDeSimbolos tabela = escoposAninhados.obterEscopoAtual();
    //     TabelaDeSimbolos tabela2 = tabela.verificarRegistro("tVinho");
    //     System.out.println("------------tipo----------");
    //     tabela2.printTabela();
    //     System.out.println("vtipo: " + ctx.getText());
    //     System.out.println("------------fimtipo----------");

    //     return super.visitTipo(ctx);
    // }
}
