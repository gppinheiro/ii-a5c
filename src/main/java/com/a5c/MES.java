package com.a5c;

import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/*
TODO list:

  [.]  OPC Communication
            - Read OK
            - Write OK
  [ ]  UDP Communication
            - UDP Client OK
            - File XML Need test
  [ ]  Statics
  [ ]  Unload
  [.]  XML
            - READ Need test
            - WRITE
  [ ]  Transformation
  [ ]  Thread

Legend:
   X   Done
   .   In progress
   \   Skipped
 */

public class MES {
    public static void main(final String[] args) {
        //clientOPC_UA opc = new clientOPC_UA();
        clientUDP udp = new clientUDP();

        while(true) {
            // Fill with 0s -  Byte Array
            byte[] buffer = new byte[65536];
            Arrays.fill(buffer , (byte) 0);

            // DatagramPacker
            DatagramPacket packUDP = new DatagramPacket(buffer, 0, buffer.length);

            try {
                // Receive what Mario sent to us
                udp.socket.receive(packUDP);

                new Thread(new receiveUDP(udp,packUDP)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
