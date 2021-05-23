package com.a5c.NEXT;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

// RCTFUN - Right Control Transform & Unload
public class RCTFUN implements Runnable {
    private Thread thrRCTFUN;
    private final dbConnect db;
    private final readOPC opcR;
    private final sendOPC opcS;

    // Global var
    private static final int[] zeros = {0,0,0,0,0};
    private Transform[] tfs = null;
    private Unload[] unls = null;
    private boolean unloads;
    private boolean transforms;
    private final long MESInitTime;
    private int old_count;

    // Global Variables for State Machines
    // RS - Right Side
    private int StateRS;
    private int StateUnload;

    public RCTFUN(clientOPC_UA cl, dbConnect dbc, long ts) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateRS = 0;
        this.StateUnload = 0;
        this.unloads = false;
        this.transforms = false;
        this.MESInitTime = ts;
    }

    public void start() {
        if(thrRCTFUN==null) {
            thrRCTFUN = new Thread(this);
            thrRCTFUN.start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Priority for unload
                if (db.UnloadLength() != 0) {
                    unloads = true;
                    db.reading = false;
                    this.unls = db.getUnload();
                }
                // Next transform
                else if (db.TransformLength() != 0 && !db.reading) {
                    unloads = false;
                    transforms = true;
                    db.reading = true;
                    this.tfs = db.getTransform();

                    long nowTime = System.currentTimeMillis();

                    Transform temp;
                    tfs[0].setRealMaxDelay((int) (tfs[0].getMaxDelay() - (nowTime - MESInitTime) / 1000) - tfs[0].getExceptedTT());
                    for (int i = 1; i < tfs.length; i++) {
                        tfs[i].setRealMaxDelay((int) (tfs[i].getMaxDelay() - (nowTime - MESInitTime) / 1000) - tfs[i].getExceptedTT());
                        for (int j = i; j > 0; j--) {
                            if ((tfs[j].getRealMaxDelay() < tfs[j - 1].getRealMaxDelay()) || (tfs[j].getRealMaxDelay() == tfs[j - 1].getRealMaxDelay() && tfs[j].getPenalty() > tfs[j - 1].getPenalty())) {
                                temp = tfs[j];
                                tfs[j] = tfs[j - 1];
                                tfs[j - 1] = temp;
                            }
                        }
                    }
                } else {
                    unloads = false;
                    transforms = false;
                    db.reading = false;
                }

                // Unloads
                // Machine to control unloads
                if (this.StateUnload == 0 && !opcR.getACKRight() && unloads) {
                    this.StateUnload = 1;
                    opcS.sendRight(unls[0].getPath());
                    db.addEndUnload(unls[0]);
                    db.deleteUnload(unls[0]);
                } else if (this.StateUnload == 1 && opcR.getACKRight()) {
                    this.StateUnload = 0;
                    opcS.sendRight(zeros);
                }

                // Right Side Transform
                // Machine to control this transformation
                if (this.StateRS == 0 && !opcR.getACKRight() && transforms) {
                    db.reading=true;
                    this.StateRS = 1;
                    opcS.sendRight(tfs[0].getPath());
                    tfs[0] = db.addElapseTransform(tfs[0], "right");
                    db.deleteTransform(tfs[0], "Transform");
                    db.reading=false;
                } else if (this.StateRS == 1 && opcR.getACKRight()) {
                    this.StateRS = 0;
                    db.updateElapseTransform( tfs[0].getOrderNumber() , 0);
                    opcS.sendRight(zeros);
                } else if ( this.StateRS==1 && !opcR.getACKRight() ) {
                    if ( tfs[0].isDifficult() ) {
                        if( old_count > opcR.getCountRightPorProd() ) {
                            old_count = opcR.getCountRightPorProd();
                            db.updateElapseTransform( tfs[0].getOrderNumber() , opcR.getCountRightPorProd());
                        }
                    } else {
                        db.updateElapseTransform( tfs[0].getOrderNumber() , opcR.getCountRightPorProd());
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
