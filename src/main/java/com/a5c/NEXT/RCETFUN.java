package com.a5c.NEXT;

import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

// RCETFUN -> Right Control End Transform and Unload
public class RCETFUN implements Runnable{
    // Global variables for others JAVA classes
    private Thread thrRCETFUN;
    private final dbConnect db;
    private final readOPC opcR;
    private final sendOPC opcS;

    // Global var
    private int timeRS;
    private int number_order;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateRS;
    private int StateP1;
    private int StateP2;
    private int StateP3;

    public RCETFUN(clientOPC_UA cl, dbConnect dbc) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateRS = 0;
        this.timeRS = 0;
        this.number_order = 0;
        this.StateP1=0;
        this.StateP2=0;
        this.StateP3=0;
    }

    public void start() {
        if(thrRCETFUN ==null) {
            thrRCETFUN = new Thread(this);
            thrRCETFUN.start();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Left Side End Transform
                if ( this.StateRS==0 && opcR.getNOrderRight()!=0 ) {
                    this.StateRS=1;
                    // Get values from codesys
                    number_order = opcR.getNOrderRight();
                    timeRS = opcR.getRightTimer();
                    // Work on DB
                    Transform tfs = db.getElapseTransform(number_order,"right");
                    if (tfs!=null) {
                        db.addEndTransform(tfs,"right",timeRS);
                        db.deleteTransform(tfs,"ElapseTransform");
                    }
                    // Statistics
                    db.updateMachinesStatistic(5, opcR.getMachine5Production());
                    db.updateMachinesStatistic(6, opcR.getMachine6Production());
                    db.updateMachinesStatistic(7, opcR.getMachine7Production());
                    db.updateMachinesStatistic(8, opcR.getMachine8Production());
                    db.updateCurrentStores(opcR.getWareHouse());
                    // Send Codesys confirmation
                    opcS.sendReadAckRight(true);
                }
                else if (this.StateRS==1 && opcR.getNOrderRight()==0) {
                    this.StateRS=0;
                    opcS.sendReadAckRight(false);
                    timeRS = 0;
                    number_order = 0;
                }

                if(this.StateP1==0 && opcR.getPusher1BOOL()) {
                    this.StateP1=1;
                    db.updatePushersStatistic(1,opcR.getPusher1());
                    db.updateCurrentStores(opcR.getWareHouse());
                } else if (this.StateP1==1 && !opcR.getPusher1BOOL()) {
                    this.StateP1=0;
                }

                if(this.StateP2==0 && opcR.getPusher2BOOL()) {
                    this.StateP2=1;
                    db.updatePushersStatistic(2,opcR.getPusher2());
                    db.updateCurrentStores(opcR.getWareHouse());
                } else if (this.StateP2==1 && !opcR.getPusher2BOOL()) {
                    this.StateP2=0;
                }

                if(this.StateP3==0 && opcR.getPusher3BOOL()) {
                    this.StateP3=1;
                    db.updatePushersStatistic(3,opcR.getPusher3());
                    db.updateCurrentStores(opcR.getWareHouse());
                } else if (this.StateP3==1 && !opcR.getPusher3BOOL()) {
                    this.StateP3=0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
