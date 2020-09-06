/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Cliente.Mensagem;
import Cliente.Online;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel Schenato <gabriel@uniplaclages.edu.br>
 */
public class SocketTCPServidorObjeto implements Runnable {

    public Socket cliente;
    private static Map<String, ObjectOutputStream> usuariosOnline = new HashMap<String, ObjectOutputStream>();

    public SocketTCPServidorObjeto(Socket cliente) {
        this.cliente = cliente;
    }

    public static void main(String[] args) {

        try {
            int porta = 2018;
            ServerSocket servidor;
            servidor = new ServerSocket(porta);
            System.out.println("*** Servidor ***");
            System.out.println("*** Porta de escuta (listen): " + porta);

            System.out.println("Aguardando conexão do cliente...");

            while (true) {
                Socket cliente = servidor.accept();
                SocketTCPServidorObjeto tratamento = new SocketTCPServidorObjeto(cliente);
                Thread t = new Thread(tratamento);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }

    }

    public void run() {
        Mensagem mensagem = null;
        System.out.println("Nova conexao com o cliente " + this.cliente.getInetAddress().getHostAddress());
        ObjectOutputStream saida = null;
        ObjectInputStream entrada = null;

        try {
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            while (true) {
                mensagem = (Mensagem) entrada.readObject();
                int acao = mensagem.getAcao();
                switch (acao) {
                    case Mensagem.CONECTAR:
                        boolean isConnected = conectar(mensagem, saida);
                        if (isConnected) {
                            usuariosOnline.put(mensagem.getNome(), saida);
                            enviarClientesOnline();
                        }
                        break;
                    case Mensagem.DESCONECTAR:
                        desconectar(mensagem, saida, cliente);
                        enviarClientesOnline();
                        return;
                    case Mensagem.ENVIAR_PARA_UM:
                        enviaParaUmCliente(mensagem);
                        break;
                    case Mensagem.ENVIA_PARA_TODOS:
                        enviarParaTodosClientes(mensagem);
                        break;
                    case Mensagem.CLIENTES_ONLINE:
                        enviarClientesOnline();
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            Mensagem ms = new Mensagem();
            ms.setNome(mensagem.getNome());
            desconectar(ms, saida, cliente);
            enviarClientesOnline();
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarParaTodosClientes(Mensagem mensagem) {
        usuariosOnline.entrySet().stream().filter((kv) -> (!kv.getKey().equals(mensagem.getNome()))).forEachOrdered((kv) -> {
            mensagem.setAcao(Mensagem.ENVIAR_PARA_UM);
            try {
                kv.getValue().writeObject(mensagem);
            } catch (IOException ex) {
                Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private boolean conectar(Mensagem mensagem, ObjectOutputStream output) {
        if (usuariosOnline.isEmpty()) {
            mensagem.setAcao(Mensagem.CONECTADO);
            enviar(mensagem, output);
            return true;
        }

        if (usuariosOnline.containsKey(mensagem.getNome())) {
            mensagem.setAcao(Mensagem.DESCONECTADO);
            enviar(mensagem, output);
            return false;
        } else {
            mensagem.setAcao(Mensagem.CONECTADO);
            enviar(mensagem, output);
            return true;
        }
    }

    private void desconectar(Mensagem mensagem, ObjectOutputStream output, Socket cliente) {
        mensagem.setAcao(Mensagem.DESCONECTAR);
        enviar(mensagem, output);
        usuariosOnline.remove(mensagem.getNome());
        mensagem.setMensagem(" até logo!");

        mensagem.setAcao(Mensagem.ENVIAR_PARA_UM);

        enviarParaTodosClientes(mensagem);
        try {
            cliente.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviar(Mensagem mensagem, ObjectOutputStream output) {
        try {
            output.writeObject(mensagem);
        } catch (IOException e) {
            Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void enviarClientesOnline() {
        Online online = new Online();
        usuariosOnline.entrySet().forEach((kv) -> {
            online.getClientesOnline().add((kv.getKey()));
        });

        usuariosOnline.entrySet().forEach((kv) -> {
            try {
                kv.getValue().writeObject(online);
            } catch (IOException ex) {
                Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void enviaParaUmCliente(Mensagem mensagem) {
        usuariosOnline.entrySet().stream().filter((kv) -> (kv.getKey().equals(mensagem.getNomeClientePrivado()))).forEachOrdered((kv) -> {
            try {
                kv.getValue().writeObject(mensagem);
            } catch (IOException ex) {
                Logger.getLogger(SocketTCPServidorObjeto.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
