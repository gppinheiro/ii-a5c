package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
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

  [.]  Transformation
            - Choose Path OK
            //TODO
            - Choose Left or Right

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
            Transform tf_test = new Transform(1,1,2,2,0,0,0);
            Transform tf_test2 = new Transform(2,2,3,2,0,0,0);
            //Transform tf_test3 = new Transform(3,1,2,1,0,0,10);
            //Unload un_test = new Unload(4,1,2,1);
            db.addTransform(tf_test);
            db.addTransform(tf_test2);
            //db.addTransform(tf_test3);
            //db.addUnload(un_test);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        new receiveUDP(udp,db).start();
        new RCTFUN(opc,db).start();
        new LCTF(opc,db).start();
        new ControlStatics(db).start();

        // GAJO ENVIA PARA O LADO DIREITO
                /*if ( !opcR.getRightSide() || !opcR.getACKRight() ) {
                    // If we don't have unloads, we make transformations
                    if (unls.length==0) {

                        // It's difficult so we don't do on right side
                        if ( !( (tfs[0].getFrom()==1 && ( tfs[0].getTo()==6 || tfs[0].getTo()==7 || tfs[0].getTo()==8 || tfs[0].getTo()==9  )) || ( tfs[0].getFrom()==2 && ( tfs[0].getTo()==7 || tfs[0].getTo()==8 ) ) ) ) {

                        }

                        //Choose path:
                        // If difficult, send to left side
                        // If easy, send to right side
                        // Select path
                        //Create vector to send fabric
                        //Remove transformation from db
                    }
                    // If not we prioritize unloads
                    else {
                        //Select the first one unls[0]
                        //Create vector to send fabric
                        //opcS.sendRight(unls[0].getPath());
                        //Remove unload from db
                    }
                }*/

        // Create waiting lines (?)
        // When machines are available, send to fabric
        // Back to init

    }
}
