package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import com.a5c.NEXT.LCETF;
import com.a5c.NEXT.LCTF;
import com.a5c.NEXT.RCETFUN;
import com.a5c.NEXT.RCTFUN;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.UDP.clientUDP;
import com.a5c.UDP.receiveUDP;

import java.sql.SQLException;

public class MES {
    public static void main(final String[] args) {
        clientOPC_UA opc = new clientOPC_UA();
        clientUDP udp = new clientUDP();
        dbConnect db = new dbConnect();
        readOPC opcR = new readOPC(opc);

        // See if there is any ElapseTransform to do again
        try {
            if (db.ElapseTransformLength()!=0) {
                Transform[] tfDOING = db.getElapseTransform();
                for (Transform tf: tfDOING) {
                    db.addTransform(tf);
                }
            }
            // Reset all tables
            db.resetMachinesStatistic();
            db.resetPushersStatistic();
            db.updateCurrentStores(opcR.getWareHouse());
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        long initTime = System.currentTimeMillis();

        // Start UDP communication
        new receiveUDP(udp,db,opc).start();

        // Start right side
        RCTFUN rs = new RCTFUN(opc,db,initTime);
        rs.start();
        new RCETFUN(opc,db).start();

        System.out.println(rs.stopLeftSide);

        // First Wait
        while(rs.stopLeftSide) {
            //Wait until right side don't finish it
            System.out.println(1);
        }

        // Then Start left side
        new LCTF(opc, db, initTime).start();
        new LCETF(opc,db).start();

    }
}
