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
    public static void main(final String[] args) throws InterruptedException {
        clientOPC_UA opc = new clientOPC_UA();
        clientUDP udp = new clientUDP();
        readOPC opcR = new readOPC(opc);
        dbConnect db = new dbConnect(opcR);

        // See if there is any ElapseTransform to do again
        try {
            if (db.ElapseTransformLength()==0) {
                // Reset all tables
                db.resetMachinesStatistic();
                db.resetPushersStatistic();
                db.updateCurrentStores(opcR.getWareHouse());
            }

            // Transforms to test
            // 1
            db.addTransform(new Transform(1,1,2,1,0,0,0));
            db.addTransform(new Transform(2,1,2,4,0,0,0));
            db.addTransform(new Transform(3,1,3,1,0,0,0));
            db.addTransform(new Transform(4,1,3,4,0,0,0));
            db.addTransform(new Transform(5,1,4,1,0,0,0));
            db.addTransform(new Transform(6,1,4,4,0,0,0));
            db.addTransform(new Transform(7,1,5,1,0,0,0));
            db.addTransform(new Transform(8,1,5,4,0,0,0));
            /*db.addTransform(new Transform(9,1,6,1,0,0,0));
            db.addTransform(new Transform(10,1,6,4,0,0,0));
            db.addTransform(new Transform(11,1,9,1,0,0,0));
            db.addTransform(new Transform(12,1,9,4,0,0,0));
            db.addTransform(new Transform(13,1,7,1,0,0,0));
            db.addTransform(new Transform(14,1,7,4,0,0,0));
            db.addTransform(new Transform(15,1,8,1,0,0,0));
            db.addTransform(new Transform(16,1,8,4,0,0,0));

            // 2
            db.addTransform(new Transform(17,2,3,1,0,0,0));
            db.addTransform(new Transform(18,2,3,4,0,0,0));
            db.addTransform(new Transform(19,2,4,1,0,0,0));
            db.addTransform(new Transform(20,2,4,4,0,0,0));
            db.addTransform(new Transform(21,2,5,1,0,0,0));
            db.addTransform(new Transform(22,2,5,4,0,0,0));
            db.addTransform(new Transform(23,2,6,1,0,0,0));
            db.addTransform(new Transform(24,2,6,4,0,0,0));
            db.addTransform(new Transform(25,2,9,1,0,0,0));
            db.addTransform(new Transform(26,2,9,4,0,0,0));
            db.addTransform(new Transform(27,2,7,1,0,0,0));
            db.addTransform(new Transform(28,2,7,4,0,0,0));
            db.addTransform(new Transform(29,2,8,1,0,0,0));
            db.addTransform(new Transform(30,2,8,4,0,0,0));

            // 3
            db.addTransform(new Transform(31,3,4,1,0,0,0));
            db.addTransform(new Transform(32,3,4,4,0,0,0));
            db.addTransform(new Transform(33,3,5,1,0,0,0));
            db.addTransform(new Transform(34,3,5,4,0,0,0));
            db.addTransform(new Transform(35,3,6,1,0,0,0));
            db.addTransform(new Transform(36,3,6,4,0,0,0));
            db.addTransform(new Transform(37,3,9,1,0,0,0));
            db.addTransform(new Transform(38,3,9,4,0,0,0));
            db.addTransform(new Transform(39,3,7,1,0,0,0));
            db.addTransform(new Transform(40,3,7,4,0,0,0));
            db.addTransform(new Transform(41,3,8,1,0,0,0));
            db.addTransform(new Transform(42,3,8,4,0,0,0));

            // 4
            db.addTransform(new Transform(43,4,5,1,0,0,0));
            db.addTransform(new Transform(44,4,5,4,0,0,0));
            db.addTransform(new Transform(45,4,6,1,0,0,0));
            db.addTransform(new Transform(46,4,6,4,0,0,0));
            db.addTransform(new Transform(47,4,9,1,0,0,0));
            db.addTransform(new Transform(48,4,9,4,0,0,0));
            db.addTransform(new Transform(49,4,7,1,0,0,0));
            db.addTransform(new Transform(50,4,7,4,0,0,0));
            db.addTransform(new Transform(51,4,8,1,0,0,0));
            db.addTransform(new Transform(52,4,8,4,0,0,0));

            // 5
            db.addTransform(new Transform(53,5,6,1,0,0,0));
            db.addTransform(new Transform(54,5,6,4,0,0,0));
            db.addTransform(new Transform(55,5,9,1,0,0,0));
            db.addTransform(new Transform(56,5,9,4,0,0,0));
            db.addTransform(new Transform(57,5,7,1,0,0,0));
            db.addTransform(new Transform(58,5,7,4,0,0,0));
            db.addTransform(new Transform(59,5,8,1,0,0,0));
            db.addTransform(new Transform(60,5,8,4,0,0,0));

            // 6
            db.addTransform(new Transform(61,6,7,1,0,0,0));
            db.addTransform(new Transform(62,6,7,4,0,0,0));
            db.addTransform(new Transform(63,6,8,1,0,0,0));
            db.addTransform(new Transform(64,6,8,4,0,0,0));*/

        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        long initTime = System.currentTimeMillis();

        // Start UDP communication
        new receiveUDP(udp,db).start();

        // Start right side
        new RCTFUN(opc,db,initTime).start();
        new RCETFUN(opc,db).start();

        // Then Start left side
        Thread.sleep(2500);
        while(db.reading) {
            // Direito vai ler, portanto espera
        }
        new LCTF(opc, db, initTime).start();
        new LCETF(opc,db).start();

    }
}
