package com.a5c;

import com.a5c.DATA.LCS;
import com.a5c.DATA.RCS;
import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.NEXT.LCTF;
import com.a5c.NEXT.RCTFUN;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;

import java.sql.SQLException;

/*
TODO list:

  [X]  OPC Communication
            - Read OK 
            - Write OK

  [X]  UDP Communication
            - UDP Client OK
            - File XML OK

  [.]  Statics //TODO

  [ ]  Unload

  [.]  XML
            - READ OK
            - WRITE CurrentStores OK
            //TODO
            - WRITE OrderSchedule
            - Send XML OK

  [X]  Transformation
            - Choose Path OK
            - Choose Left or Right OK

  [.]  Thread
            - UDP OK
            - WhatToDo OK
            - OPC OK

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

        try {
            Transform tf_test3 = new Transform(3,1,2,4,0,0,0);
            Unload un_test = new Unload(4,1,2,1);
            db.addTransform(tf_test3);
            db.addUnload(un_test);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Start UDP communication
        new receiveUDP(udp,db).start();
        // Start right side
        RCTFUN rs = new RCTFUN(opc,db);
        rs.start();
        // Wait to start Left Side after Right Side begin
        LCTF ls = new LCTF(opc,db);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ls.start();
                    }
                },
                5000
        );
        // Only starts when half a day passed
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new RCS(opc,db,rs).start();
                        new LCS(opc,db,ls).start();
                    }
                },
                10000
        );

    }
}
