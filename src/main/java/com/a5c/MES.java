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

        // ERP -> Ler DB -> Definir Caminho -> Comunicar com OPC
        // ERP
        new receiveUDP(udp,db).start();

        // Controlo propriamente dito - O que fazer com base em tudo
        // Ler DB Transformações e Unloads -> Definir Caminho
        new whatToDo(db).start();

    }
}
