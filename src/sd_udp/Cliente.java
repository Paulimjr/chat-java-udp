/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author paulo
 */
public class Cliente {

    public static void main(String[] args) {
        try {
            DatagramSocket s = new DatagramSocket();
            InetAddress dest = InetAddress.getByName("localhost");
            String envio;
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("> ");
            envio = teclado.readLine().trim();
            System.out.println("lido do teclado:" + envio);
            
            while (!envio.equalsIgnoreCase("")) {

                //Envio de mensagem
                byte[] buffer = envio.getBytes();
                DatagramPacket msg = new DatagramPacket(buffer, buffer.length, dest, 4545);
                s.send(msg);
                System.out.println("enviado...");
                // Recebendo mensagem
                DatagramPacket resposta = new DatagramPacket(new byte[512], 512);
                s.receive(resposta);
                String printResposta = new String(resposta.getData());
                System.out.println("responsta no cliente: " + printResposta);
                System.out.println("> ");
                envio = teclado.readLine();
                // alterar o servidor bastante
                // assim que o cliente conecta manda para o servidor uma mensagem padrao so parar o servidor armazenar no vetor os Address.
                // datagram...
            }

            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
