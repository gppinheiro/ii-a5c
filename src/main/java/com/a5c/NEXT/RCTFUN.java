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
    private Transform tfs = null;
    private Unload[] unls = null;
    private boolean unloads;
    private boolean transforms;
    private final long MESInitTime;
    private int old_count;

    // Global Variables for State Machines
    // RS - Right Side
    private int StateRS;
    private int StateUnload;

    // Constructor.
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

    // Init thread.
    public void start() {
        if(thrRCTFUN==null) {
            thrRCTFUN = new Thread(this);
            thrRCTFUN.start();
        }
    }

    // ALWAYS RUNNING
    @Override
    public void run() {
        while (true) {
            try {
                // Priority for unload
                if (db.UnloadLength() != 0) {
                    unloads = true;
                    transforms = false;
                    this.unls = db.getUnload();
                }
                // Next transform
                else if (db.TransformLength() != 0 ) {
                    unloads = false;
                    transforms = true;
                } else {
                    unloads = false;
                    transforms = false;
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
                if (this.StateRS == 0 && !opcR.getACKRight() && transforms && db.HowManyAreDoing("right")<=2 && !db.reading) {
                    this.StateRS = 1;
                    tfs = db.getFirstTransformSort(MESInitTime);
                    opcS.sendRight(tfs.getPath());
                    tfs = db.addElapseTransform(tfs, "right");
                    db.deleteTransform(tfs, "Transform");
                    db.reading = false;
                } else if (this.StateRS == 1 && opcR.getACKRight()) {
                    this.StateRS = 0;
                    opcS.sendRight(zeros);
                    db.updateElapseTransform( tfs.getOrderNumber() , 0);
                } else if ( this.StateRS==1 && !opcR.getACKRight() ) {
                    int porProd = opcR.getCountRightPorProd();
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
