package com.a5c.NEXT;

import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

// LCETF -> Left Control End Transform
public class LCETF implements Runnable{
    // Global variables for others JAVA classes
    private Thread thrLCETF;
    private final dbConnect db;
    private final readOPC opcR;
    private final sendOPC opcS;

    // Global var
    private int timeLS;
    private int number_order;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateLS;

    // Constructor.
    public LCETF(clientOPC_UA cl, dbConnect dbc) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateLS = 0;
        this.timeLS = 0;
        this.number_order = 0;
    }

    // Init thread.
    public void start() {
        if(thrLCETF ==null) {
            thrLCETF = new Thread(this);
            thrLCETF.start();
        }
    }

    // ALWAYS RUNNING
    @Override
    public void run() {
        while(true) {
            try {
                // Left Side End Transform
                if ( this.StateLS==0 && opcR.getNOrderLeft()!=0 ) {
                    this.StateLS=1;
                    // Get values from codesys
                    number_order = opcR.getNOrderLeft();
                    timeLS = opcR.getLeftTimer();
                    // Work on DB
                    Transform tfs = db.getElapseTransform(number_order,"left");
                    if(timeLS!=0) {
                        if (tfs != null) {
                            db.addEndTransform(tfs, "left", timeLS);
                            db.deleteTransform(tfs, "ElapseTransform");
                        }
                    }
                    // Statistics
                    db.updateMachinesStatistic(1, opcR.getMachine1Production());
                    db.updateMachinesStatistic(2, opcR.getMachine2Production());
                    db.updateMachinesStatistic(3, opcR.getMachine3Production());
                    db.updateMachinesStatistic(4, opcR.getMachine4Production());
                    db.updateCurrentStores(opcR.getWareHouse());
                    // Send Codesys confirmation
                    opcS.sendReadAckLeft(true);
                } else if (this.StateLS==1 && opcR.getNOrderLeft()==0) {
                    this.StateLS=0;
                    opcS.sendReadAckLeft(false);
                    timeLS = 0;
                    number_order = 0;
                } else if (this.StateLS==1 && opcR.getNOrderLeft()!=0) {
                    this.StateLS=0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
