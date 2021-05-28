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
    private Transform[] tfs;
    private final long MESInitTime;
    private boolean transforms;
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
        this.transforms = false;
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
                // Get DB
                if ( !opcR.getACKLeft() && db.TransformLength()!=0 && !db.reading && db.HowManyAreDoing("left")<=3 ) {
                    transforms = true;
                    tfs = db.getAllTransformsSort(MESInitTime);
                }
                else {
                    transforms = false;
                }

                // Left Side Transform
                // Machine to control this transformation
                if ( this.StateLS==0 && !opcR.getACKLeft() && transforms ) {
                    this.StateLS = 1;
                    opcS.sendLeft(tfs[0].getPath());
                    tfs[0] = db.addElapseTransform(tfs[0],"left");
                    db.deleteTransform(tfs[0],"Transform");
                    old_count = tfs[0].getQuantity();
                    db.reading = false;
                } else if ( this.StateLS==1 && opcR.getACKLeft() ) {
                    this.StateLS = 0;
                    opcS.sendLeft(zeros);
                    db.updateElapseTransform( tfs[0].getOrderNumber() , 0);
                } else if ( this.StateLS==1 && !opcR.getACKLeft() ) {
                    int porProd = opcR.getCountLeftPorProd();
                    if ( tfs[0].isDifficult() ) {
                        if( old_count > porProd ) {
                            old_count = porProd;
                            db.updateElapseTransform( tfs[0].getOrderNumber() , porProd);
                        }
                    } else {
                        db.updateElapseTransform( tfs[0].getOrderNumber() , porProd);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
