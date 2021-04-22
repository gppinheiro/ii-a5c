package com.a5c.DATA;

import com.a5c.DB.dbConnect;
import com.a5c.NEXT.LCTF;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;

import java.sql.SQLException;

public class LCS implements Runnable {
    private Thread thrLCS;
    private final dbConnect db;
    private final readOPC opcR;
    private final LCTF ls;

    private int StateTL;

    public LCS (clientOPC_UA op, dbConnect db, LCTF l) {
        this.opcR = new readOPC(op);
        this.db = db;
        this.ls=l;
        this.StateTL=0;
    }

    public void start() {
        if(thrLCS==null) {
            thrLCS = new Thread(this);
            thrLCS.start();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (this.StateTL==0 && !ls.isEndTransformLeft()) {
                    this.StateTL=1;
                }
                else if(this.StateTL==1 && ls.isEndTransformLeft()) {
                    db.updateMachinesStatistic(1, opcR.getMachine1Production());
                    db.updateMachinesStatistic(2, opcR.getMachine2Production());
                    db.updateMachinesStatistic(3, opcR.getMachine3Production());
                    db.updateMachinesStatistic(4, opcR.getMachine4Production());
                    db.updateCurrentStores(opcR.getWareHouse());
                    this.StateTL=0;
                }

                System.out.println(this.StateTL);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
