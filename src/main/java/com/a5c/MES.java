package com.a5c;

import com.a5c.DATA.RCS;
import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.NEXT.LCTF;
import com.a5c.NEXT.RCTFUN;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;

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

  [.]  XML
            - READ OK
            - WRITE CurrentStores OK
            //TODO
            - WRITE OrderSchedule
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
        Logger logger = LoggerFactory.getLogger(MES.class);
        logger.info("MES IS STARTING!");
        clientOPC_UA opc = new clientOPC_UA();
        /*clientUDP udp = new clientUDP();
        dbConnect db = new dbConnect();

        try {
            db.addTransform(new Transform(1,1,2,1,0,10,10));
            db.addTransform(new Transform(2,1,2,2,0,15,20));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        long initTime = System.currentTimeMillis();

        // Start UDP communication
        new receiveUDP(udp,db).start();
        // Start right side
        new RCTFUN(opc,db,initTime).start();
        // Wait to start Left Side after Right Side begin
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new LCTF(opc,db,initTime).start();
                        new RCS(opc,db).start();
                    }
                },
                5000
        );
        */
    }
}
