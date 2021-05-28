package com.a5c.NEXT;

import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

// LCTF -> Left Control Transforms
public class LCTF implements Runnable{
    // Global variables for others JAVA classes
    private Thread thrLCTF;
    private final dbConnect db;
    private final readOPC opcR;
    private final sendOPC opcS;

    // Global var
    private static final int[] zeros = {0,0,0,0,0};
    private Transform tfs;
    private final long MESInitTime;
    private int old_count;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateLS;

    // Constructor.
    public LCTF(clientOPC_UA cl, dbConnect dbc, long ts) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateLS = 0;
        this.MESInitTime = ts;
    }

    // Init thread.
    public void start() {
        if(thrLCTF ==null) {
            thrLCTF = new Thread(this);
            thrLCTF.start();
        }
    }

    // ALWAYS RUNNING
    @Override
    public void run() {
        // RUN Forever
        while(true) {
            try {
                // Left Side Transform
                // Machine to control this transformation
                if ( this.StateLS==0 && !opcR.getACKLeft() && db.TransformLength()!=0 && db.HowManyAreDoing("left")<=3 && !db.reading ) {
                    this.StateLS = 1;
                    tfs = db.getFirstTransformSort(MESInitTime);
                    opcS.sendLeft(tfs.getPath());
                    tfs = db.addElapseTransform(tfs,"left");
                    db.deleteTransform(tfs,"Transform");
                    old_count = tfs.getQuantity();
                    db.reading = false;
                } else if ( this.StateLS==1 && opcR.getACKLeft() ) {
                    this.StateLS = 0;
                    opcS.sendLeft(zeros);
                    db.updateElapseTransform( tfs.getOrderNumber() , 0);
                } else if ( this.StateLS==1 && !opcR.getACKLeft() ) {
                    int porProd = opcR.getCountLeftPorProd();
                    if ( tfs.isDifficult() ) {
                        if( old_count > porProd ) {
                            old_count = porProd;
                            db.updateElapseTransform( tfs.getOrderNumber() , porProd);
                        }
                    } else {
                        db.updateElapseTransform( tfs.getOrderNumber() , porProd);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
