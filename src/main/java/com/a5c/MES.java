package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.sql.SQLException;
import java.util.Arrays;

/*
TODO list:

  [X]  OPC Communication
            - Read OK
            - Write OK
  [X]  UDP Communication
            - UDP Client OK
            - File XML OK
  [ ]  Statics
  [ ]  Unload
  [.]  XML
            - READ OK
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
        dbConnect db = new dbConnect();

        try {
            // TODO: É PRECISO CRIAR UMA THREAD PARA ESTAR SEMPRE A LER ISTO
            Transform[] tfs = db.getTransform();
            Unload[] unls = db.getUnload();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /*while(true) {
            // Fill with 0s -  Byte Array
            byte[] buffer = new byte[65536];
            Arrays.fill(buffer , (byte) 0);

            // DatagramPacker
            DatagramPacket packUDP = new DatagramPacket(buffer, 0, buffer.length);

            try {
                // Receive what ERP sent to us
                udp.socket.receive(packUDP);

                new Thread(new receiveUDP(udp,packUDP)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
