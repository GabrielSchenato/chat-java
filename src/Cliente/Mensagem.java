/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Gabriel Schenato <gabriel@uniplaclages.edu.br>
 */
public class Mensagem implements Serializable {

    private String nome;
    private String mensagem;
    private String nomeClientePrivado;
    private File arquivo;
    private int acao;

    public static final int CONECTAR = 1;
    public static final int CONECTADO = 2;
    public static final int DESCONECTADO = 3;
    public static final int DESCONECTAR = 4;
    public static final int ENVIAR_PARA_UM = 5;
    public static final int ENVIA_PARA_TODOS = 6;
    public static final int CLIENTES_ONLINE = 7;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getNomeClientePrivado() {
        return nomeClientePrivado;
    }

    public void setNomeClientePrivado(String nomeClientePrivado) {
        this.nomeClientePrivado = nomeClientePrivado;
    }

    public int getAcao() {
        return acao;
    }

    public void setAcao(int acao) {
        this.acao = acao;
    }
}
