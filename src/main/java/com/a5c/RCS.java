package com.a5c;

import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;

import java.sql.SQLException;

public class RCS implements Runnable {
    private Thread thrCS;
    private final dbConnect db;
    private final readOPC opcR;
    private final RCTFUN rs;

    private int StateTR;
    private int StateU;

    public RCS (clientOPC_UA op, dbConnect db, RCTFUN r) {
        this.opcR = new readOPC(op);
        this.db = db;
        this.rs=r;
        this.StateTR=0;
        this.StateU=0;
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
                if (this.StateTR==0 && rs.isEndTransformRight()) {
                    this.StateTR=1;
                    db.updateMachinesStatistic(5, opcR.getMachine5Production());
                    db.updateMachinesStatistic(6, opcR.getMachine6Production());
                    db.updateMachinesStatistic(7, opcR.getMachine7Production());
                    db.updateMachinesStatistic(8, opcR.getMachine8Production());
                    db.updateCurrentStores(opcR.getWareHouse());
                }
                else if(this.StateTR==1 && !rs.isEndTransformRight()) {
                    this.StateTR=0;
                }

                if (this.StateU==0 && rs.isEndUnload()) {
                    this.StateU=1;
                    db.updateCurrentStores(opcR.getWareHouse());
                }
                else if (this.StateU==1 && !rs.isEndUnload()) {
                    this.StateU=0;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
