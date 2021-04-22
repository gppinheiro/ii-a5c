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

    private int StateP1;
    private int StateP2;
    private int StateP3;

    public RCS (clientOPC_UA op, dbConnect db) {
        this.opcR = new readOPC(op);
        this.db = db;
        this.StateP1=0;
        this.StateP2=0;
        this.StateP3=0;
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

                /*if(this.StateP1==0 && opcR.getPusher1BOOL()) {
                    this.StateP1=1;
                    db.updatePushersStatistic(1,opcR.getPusher1());
                    db.updateCurrentStores(opcR.getWareHouse());
                }
                else if (this.StateP2==1 && !opcR.getPusher1BOOL()) {
                    this.StateP1=0;
                }

                if(this.StateP2==0 && opcR.getPusher2BOOL()) {
                    this.StateP2=1;
                    db.updatePushersStatistic(2,opcR.getPusher2());
                    db.updateCurrentStores(opcR.getWareHouse());
                }
                else if (this.StateP2==1 && !opcR.getPusher2BOOL()) {
                    this.StateP2=0;
                }

                if(this.StateP3==0 && opcR.getPusher3BOOL()) {
                    this.StateP3=1;
                    db.updatePushersStatistic(3,opcR.getPusher3());
                    db.updateCurrentStores(opcR.getWareHouse());
                }
                else if (this.StateP3==1 && !opcR.getPusher3BOOL()) {
                    this.StateP3=0;
                }*/

        }
    }

}
