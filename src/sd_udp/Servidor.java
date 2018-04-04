/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author paulo
 */
public class Servidor {

    private static List<DatagramPacket> clientesConectados;

    public static void main(String[] args) {

        try {
            clientesConectados = new ArrayList<>();

            DatagramSocket s = new DatagramSocket(4545);
            System.out.println("Servidor esperando conexão....");

            //Recebe mensagem dos clientes na porta 4545
            while (true) {
                DatagramPacket recebe = new DatagramPacket(new byte[512], 512);
                s.receive(recebe);
                String mensagemRecebida = "";
                mensagemRecebida = new String(recebe.getData());

                if (recebe.getLength() == 1 && mensagemRecebida.trim().startsWith("@")) { //RECEBENDO A MENSAGEM DEFAULT DOS CLIENTE...
                    boolean isConnected = false;
                    for (DatagramPacket dp : clientesConectados) {
                        if (dp.getPort() == recebe.getPort()) {
                            isConnected = true;
                            DatagramPacket respForMe = new DatagramPacket(Constants.ALREADY_CONNECTED.getBytes(),
                                    Constants.ALREADY_CONNECTED.getBytes().length, recebe.getAddress(), recebe.getPort());
                            s.send(respForMe);
                        }
                    }

                    if (isConnected == false) {
                        clientesConectados.add(recebe);
                        sendMessages(recebe, mensagemRecebida, s);
                    }
                } else {
                    sendMessages(recebe, mensagemRecebida, s);
                }

            }
        } catch (IOException e) {
            System.out.println("IOException: "+e.getMessage());
        }
    }
    
    /**
     * Send message for user client connected
     * 
     * @param recebe
     * @param mensagemRecebida
     * @param s
     * @throws IOException 
     */
    private static void sendMessages(DatagramPacket recebe, String mensagemRecebida, DatagramSocket s) throws IOException {
        DatagramPacket resp;
        for (int i = 0; i < clientesConectados.size(); i++) {

            String msg = Constants.CUSTOMER + "(" + recebe.getSocketAddress() + ") said: " + mensagemRecebida;
            resp = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, clientesConectados.get(i).getAddress(), clientesConectados.get(i).getPort());

            if (!recebe.getSocketAddress().equals(resp.getSocketAddress())) {

                if (mensagemRecebida.trim().startsWith("@")) {
                    String conectadoForOtherClient = Constants.CUSTOMER + "(" + recebe.getSocketAddress() + ") said: Is connected...";
                    resp.setData(conectadoForOtherClient.getBytes());
                    resp.setLength(conectadoForOtherClient.getBytes().length);
                }

                s.send(resp);
            }
            
            //verificando se a mensagem vai para você personalizada;
            if (recebe.getSocketAddress().equals(resp.getSocketAddress())) {
                if (mensagemRecebida.getBytes()[0] == 64) {
                    DatagramPacket respForMeConnected = new DatagramPacket(Constants.CONNECTED.getBytes(),
                            Constants.CONNECTED.getBytes().length, recebe.getAddress(), recebe.getPort());
                    s.send(respForMeConnected);
                    
                } else if (mensagemRecebida.trim().startsWith(Constants.LOGOUT)) {
                    DatagramPacket respForMeConnected = new DatagramPacket(Constants.LOGOUT.getBytes(),
                            Constants.LOGOUT.getBytes().length, recebe.getAddress(), recebe.getPort());
                    s.send(respForMeConnected);
                    //percorrendo para remover o cliente que deseja sair do chat....
                    ListIterator<DatagramPacket> item = clientesConectados.listIterator();
                    if (item.hasNext()) {
                        if (item.next().getPort() == recebe.getPort()) {
                            item.remove();
                        }
                    }
                } else {
                    String messageCustomerSaid = Constants.YOU_SAID;
                    messageCustomerSaid += new String(recebe.getData());
                    DatagramPacket respForMe = new DatagramPacket(messageCustomerSaid.getBytes(),
                            messageCustomerSaid.getBytes().length, recebe.getAddress(), recebe.getPort());
                    s.send(respForMe);
                }
            }
        }
    }
}
