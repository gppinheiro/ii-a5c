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
    private final RCTFUN rs;

    // Global var
    private static final int[] zeros = {0,0,0,0,0};
    private Transform[] tfs;
    private final long MESInitTime;
    private boolean transforms;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateLS;

    public LCTF(clientOPC_UA cl, dbConnect dbc, long ts, RCTFUN rs) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateLS = 0;
        this.MESInitTime = ts;
        this.transforms = false;
        this.rs = rs;
    }

    public void start() {
        if(thrLCTF ==null) {
            thrLCTF = new Thread(this);
            thrLCTF.start();
        }
    }

    @Override
    public void run() {
        // To be independent, left side must stop one time to not collide with the right side
        while(rs.stopLeftSide);
        // RUN Forever
        while(true) {
            try {
                // Get DB
                if ( !opcR.getACKLeft() && db.TransformLength()!=0 && !db.reading ) {
                    transforms = true;
                    db.reading = true;
                    tfs = db.getTransform();

                    // Prioridade:
                    // Penalty - Se for grande, fazer esta primeiro
                    // MaxDelay - Se for pequeno, fazer esta primeiro
                    // Sort tfs vector with base on MaxDelay and Penalty
                    long nowTime = System.currentTimeMillis();
                    Transform temp;
                    tfs[0].setRealMaxDelay((int) ( tfs[0].getMaxDelay() - ( nowTime - MESInitTime )/1000 ) - tfs[0].getExceptedTT() );
                    for (int i = 1; i < tfs.length; i++) {
                        tfs[i].setRealMaxDelay((int) ( tfs[i].getMaxDelay() - ( nowTime - MESInitTime )/1000 ) - tfs[i].getExceptedTT() );
                        for (int j = i; j > 0; j--) {
                            if ((tfs[j].getRealMaxDelay() < tfs[j - 1].getRealMaxDelay()) || (tfs[j].getRealMaxDelay() == tfs[j - 1].getRealMaxDelay() && tfs[j].getPenalty() > tfs[j - 1].getPenalty())) {
                                temp = tfs[j];
                                tfs[j] = tfs[j - 1];
                                tfs[j-1] = temp;
                            }
                        }
                    }
                }
                else {
                    transforms = false;
                    db.reading = false;
                }

                // Left Side Transform
                // Machine to control this transformation
                if ( this.StateLS==0 && !opcR.getACKLeft() && transforms ) {
                    this.StateLS = 1;
                    opcS.sendLeft(tfs[0].getPath());
                    tfs[0] = db.addElapseTransform(tfs[0],"left");
                    db.deleteTransform(tfs[0],"Transform");
                } else if ( this.StateLS ==1 && opcR.getACKLeft() ) {
                    this.StateLS = 0;
                    opcS.sendLeft(zeros);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
