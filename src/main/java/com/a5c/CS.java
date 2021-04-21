package com.a5c;

import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;

import java.sql.SQLException;

public class CS implements Runnable {
    private Thread thrCS;
    private final dbConnect db;
    private final readOPC opcR;
    private final RCTFUN rs;
    private final LCTF ls;

    public CS (clientOPC_UA op, dbConnect db, RCTFUN r, LCTF l) {
        this.opcR = new readOPC(op);
        this.db = db;
        this.rs=r;
        this.ls=l;
    }

    public void start() {
        if(thrCS==null) {
            thrCS = new Thread(this);
            thrCS.start();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (rs.isEndTransformRight()) {
                    db.updateMachinesStatistic(5, opcR.getMachine5Production());
                    db.updateMachinesStatistic(6, opcR.getMachine6Production());
                    db.updateMachinesStatistic(7, opcR.getMachine7Production());
                    db.updateMachinesStatistic(8, opcR.getMachine8Production());
                    //db.updateCurrentStores(opcR.getWareHouse());
                    opcR.getWareHouse();
                }

                if (rs.isEndUnload()) {
                    //db.updateCurrentStores(opcR.getWareHouse());
                    opcR.getWareHouse();
                }

                if (ls.isEndTransformLeft()) {
                    db.updateMachinesStatistic(1, opcR.getMachine1Production());
                    db.updateMachinesStatistic(2, opcR.getMachine2Production());
                    db.updateMachinesStatistic(3, opcR.getMachine3Production());
                    db.updateMachinesStatistic(4, opcR.getMachine4Production());
                    //db.updateCurrentStores(opcR.getWareHouse());
                    opcR.getWareHouse();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
