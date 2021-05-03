package com.a5c;

import com.a5c.DATA.RCS;
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

  [X]  Statics
            - MACHINE OK
            - UNLOAD OK

  [X]  Unload

  [X]  XML
            - READ OK
            - WRITE CurrentStores OK
            - WRITE OrderSchedule OK
            - Send XML OK

  [X]  Transformation
            - Choose Path OK
            - Choose Left or Right OK

  [X]  Thread
            - UDP OK
            - Left Side OK
            - Right Side OK
            - OPC OK
            - Statistics OK

  [X]  DB
            - Connection OK
            - Add and Get Transform OK
            - Add and Get Unload OK
            - Get CurrentStores OK
            - Statistics Machine OK
            - Statistics Unload OK

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
            db.deleteTransform("Transform");
            db.deleteTransform("ElapseTransform");
            db.deleteUnload();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        long initTime = System.currentTimeMillis();

        // Start UDP communication
        new receiveUDP(udp,db).start();

        // Start left side
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new LCTF(opc, db, initTime).start();
                    }
                },
                5000
        );

        // Wait to start Right Side after Left Side begin
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new RCTFUN(opc,db,initTime).start();
                        new RCS(opc,db).start();
                    }
                },
                10000
        );

    }
}
