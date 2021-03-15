package com.a5c.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class threadedUDP implements Runnable {
    public Thread threadUDP;
    public clientUDP client;

    public threadedUDP() {
        threadUDP = new Thread(this,"Threaded UDP");
        client = new clientUDP();
        threadUDP.start();
    }

    @Override
    public void run() {
        try {
            // Fill with 0s -  Byte Array
            byte[] buffer = new byte[65536];
            Arrays.fill(buffer , (byte) 0);

            // DatagramPacker
            DatagramPacket packUDP = new DatagramPacket(buffer, 0, buffer.length);

            // Receive what Mario sent to us
            client.socket.receive(packUDP);

            // TODO: TEST THIS SHIT - NEED TO KNOW IF PORT AND ADDRESS WILL BE THE SAME AS CLIENT - IF IT IS, WE CAN USE clientUDP class
            // SocketAddress - I don't know if it is localhost or not. So we must play on the safe side.
            SocketAddress SocketAddr = packUDP.getSocketAddress();
            // Address + Port
            String aux = SocketAddr.toString();
            // Address
            String addressServer = aux.substring(1, aux.indexOf(":"));
            // Port
            int portServer = Integer.parseInt(aux.substring(aux.indexOf(":") + 1));
            // InetAddress with the before result
            InetAddress address = InetAddress.getByName(addressServer);

            // TODO: WHAT I SAID BEFORE
            if ( address==client.address && portServer==client.port){
                System.out.println("Everything the same.");
            }
            else {
                System.out.println("Everything not the same.");
            }

            // Receive Orders XML
            byte[] buffer2 = Arrays.copyOfRange(packUDP.getData(), 0, packUDP.getLength());
            Files.write(Paths.get("receiveOrdersXML.xml"), buffer2);

            // TODO: READ XML
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
