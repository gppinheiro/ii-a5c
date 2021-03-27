package com.a5c;

import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;

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
            - WRITE CurrentStores OK
            //TODO
            - WRITE OrderSchedule
            - Send XML OK

  [.]  Transformation
            //TODO
            - Choose Path
            //TODO
            - Choose Left or Right

  [.]  Thread
            - UDP OK
            - WhatToDo OK
            //TODO
            - OPC

  [.]  DB
            - Connection OK
            - Add and Get Transform OK
            - Add and Get Unload OK
            - Get CurrentStores OK

Legend:
   X   Done
   .   In progress
   \   Skipped
 */

public class MES {
    public static void main(final String[] args) {
        clientOPC_UA opc = new clientOPC_UA();
        clientUDP udp = new clientUDP();
        dbConnect db = new dbConnect();

        new receiveUDP(udp,db).start();
        new whatToDo(opc,db).start();

    }
}
