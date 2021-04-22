package com.a5c.DATA;

import com.a5c.DB.dbConnect;
import com.a5c.NEXT.RCTFUN;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;

import java.sql.SQLException;

public class RCS implements Runnable {
    private Thread thrRCS;
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
        if(thrRCS==null) {
            thrRCS = new Thread(this);
            thrRCS.start();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (this.StateTR==0 && !rs.isEndTransformRight()) {
                    this.StateTR=1;
                }
                else if(this.StateTR==1 && rs.isEndTransformRight()) {
                    this.StateTR=0;
                    db.updateMachinesStatistic(5, opcR.getMachine5Production());
                    db.updateMachinesStatistic(6, opcR.getMachine6Production());
                    db.updateMachinesStatistic(7, opcR.getMachine7Production());
                    db.updateMachinesStatistic(8, opcR.getMachine8Production());
                    db.updateCurrentStores(opcR.getWareHouse());
                }

                if (this.StateU==0 && !rs.isEndUnload()) {
                    this.StateU=1;
                }
                else if (this.StateU==1 && rs.isEndUnload()) {
                    this.StateU=0;
                    db.updatePushersStatistic(1,opcR.getPusher1());
                    db.updatePushersStatistic(2,opcR.getPusher2());
                    db.updatePushersStatistic(3,opcR.getPusher3());
                    db.updateCurrentStores(opcR.getWareHouse());
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
