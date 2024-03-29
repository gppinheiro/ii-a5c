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
    private static final int[] zeros = {0,0,0,0};
    private Transform[] tfs = null;
    private Unload[] unls = null;
    private boolean endTransformRight;
    private boolean endUnload;
    private int timeRS;
    private final long MESInitTime;

    // Global Variables for State Machines
    // RS - Right Side
    private int StateDifficult1RS;
    private int StateDifficult2RS;
    private int StateEasyRS;
    private int StatePenaltyRS;
    private int StateUnload;

    public RCTFUN(clientOPC_UA cl, dbConnect dbc, long ts) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateDifficult1RS = 0;
        this.StateDifficult2RS = 0;
        this.StateEasyRS = 0;
        this.StatePenaltyRS = 0;
        this.StateUnload = 0;
        this.endTransformRight = true;
        this.endUnload = true;
        this.timeRS = 0;
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
        boolean PenaltyRS;
        boolean DifficultRS;

        while(true) {
            try {

                if (opcR.getRightSide() && !opcR.getACKRight()) {
                    // Priority for unload
                    if (endUnload && db.UnloadLength()!=0) {
                        endUnload = false;
                        this.unls = db.getUnload();
                    }

                    // Next transform
                    else if (endTransformRight && db.TransformLength()!=0) {
                        endTransformRight = false;
                        this.tfs = db.getTransform();

                        long nowTime = System.currentTimeMillis();

                        Transform temp;
                        tfs[0].setRealMaxDelay((int) ( tfs[0].getMaxDelay() - ( nowTime - MESInitTime )/1000 ) - tfs[0].getExceptedTT() );
                        for (int i = 1; i < tfs.length; i++) {
                            tfs[i].setRealMaxDelay((int) ( tfs[i].getMaxDelay() - ( nowTime - MESInitTime )/1000 ) - tfs[0].getExceptedTT() );
                            for (int j = i; j > 0; j--) {
                                if ((tfs[j].getRealMaxDelay() < tfs[j - 1].getRealMaxDelay()) || (tfs[j].getRealMaxDelay() == tfs[j - 1].getRealMaxDelay() && tfs[j].getPenalty() > tfs[j - 1].getPenalty())) {
                                    temp = tfs[j];
                                    tfs[j] = tfs[j - 1];
                                    tfs[j-1] = temp;
                                }
                            }
                        }

                    }
                }

                // Unloads
                while(!endUnload && endTransformRight){
                    // Machine to control unloads
                    if ( this.StateUnload==0 && !opcR.getACKRight() && opcR.getRightSide() ) {
                        this.StateUnload=1;
                        opcS.sendRight(unls[0].getPath());
                        db.addEndUnload(unls[0]);
                        db.deleteUnload(unls[0]);
                    }
                    else if ( this.StateUnload==1 && opcR.getACKRight() ) {
                        this.StateUnload=2;
                        opcS.sendRight(zeros);
                    }
                    else if ( this.StateUnload==2 && opcR.getRightSide() ) {
                        this.StateUnload=0;
                        endUnload=true;
                    }
                }

                // Transformations
                PenaltyRS = false;
                while (!endTransformRight && endUnload) {
                    // All difficult ones from piece 1
                    assert tfs != null;
                    if (tfs[0].getFrom() == 1 && (tfs[0].getTo() == 6 || tfs[0].getTo() == 7 || tfs[0].getTo() == 8 || tfs[0].getTo() == 9)) {
                        DifficultRS = true;
                        // Divide into 2 transformations, we keep the essential equilibrium requested by Mario
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(), 1, 5, tfs[0].getQuantity(), 0, 0, 0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(), 5, tfs[0].getTo(), tfs[0].getQuantity(), tfs[0].getTime(), tfs[0].getMaxDelay(), tfs[0].getPenalty());

                        // Machine to control send both tfs
                        if (this.StateDifficult1RS == 0 && !opcR.getACKRight() && opcR.getRightSide()) {
                            this.StateDifficult1RS = 1;
                            opcS.sendRight(tf1.getPath());
                            tfs[0] = db.addElapseTransform(tfs[0], "right");
                            db.deleteTransform(tfs[0], "Transform");
                        } else if (this.StateDifficult1RS == 1 && opcR.getACKRight()) {
                            this.StateDifficult1RS = 2;
                            opcS.sendRight(zeros);
                        } else if (this.StateDifficult1RS == 2 && !opcR.getACKRight()) {
                            this.StateDifficult1RS = 3;
                            opcS.sendRight(tf2.getPath());
                        } else if (this.StateDifficult1RS == 3 && opcR.getACKRight()) {
                            this.StateDifficult1RS = 4;
                            opcS.sendRight(zeros);
                        } else if (this.StateDifficult1RS == 4 && opcR.getRightSide() && PenaltyRS) {
                            this.StateDifficult1RS = 0;
                            endTransformRight = true;
                            // Statistics
                            db.updateMachinesStatistic(5, opcR.getMachine5Production());
                            db.updateMachinesStatistic(6, opcR.getMachine6Production());
                            db.updateMachinesStatistic(7, opcR.getMachine7Production());
                            db.updateMachinesStatistic(8, opcR.getMachine8Production());
                            db.updateCurrentStores(opcR.getWareHouse());
                        }

                    }
                    // All difficult ones from piece 1
                    else if (tfs[0].getFrom() == 2 && (tfs[0].getTo() == 7 || tfs[0].getTo() == 8)) {
                        DifficultRS = true;
                        // Divide into 2 transformations, we keep the essential equilibrium requested by Mario
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(), 2, 6, tfs[0].getQuantity(), 0, 0, 0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(), 6, tfs[0].getTo(), tfs[0].getQuantity(), tfs[0].getTime(), tfs[0].getMaxDelay(), tfs[0].getPenalty());

                        // Machine to control send both tfs
                        if (this.StateDifficult2RS == 0 && !opcR.getACKRight() && opcR.getRightSide()) {
                            this.StateDifficult2RS = 1;
                            opcS.sendRight(tf1.getPath());
                            tfs[0] = db.addElapseTransform(tfs[0], "right");
                            db.deleteTransform(tfs[0], "Transform");
                        } else if (this.StateDifficult2RS == 1 && opcR.getACKRight()) {
                            this.StateDifficult2RS = 2;
                            opcS.sendRight(zeros);
                        } else if (this.StateDifficult2RS == 2 && !opcR.getACKRight()) {
                            this.StateDifficult2RS = 3;
                            opcS.sendRight(tf2.getPath());
                        } else if (this.StateDifficult2RS == 3 && opcR.getACKRight()) {
                            this.StateDifficult2RS = 4;
                            opcS.sendRight(zeros);
                        } else if (this.StateDifficult2RS == 4 && opcR.getRightSide() && PenaltyRS) {
                            this.StateDifficult2RS = 0;
                            endTransformRight = true;
                            // Statistics
                            db.updateMachinesStatistic(5, opcR.getMachine5Production());
                            db.updateMachinesStatistic(6, opcR.getMachine6Production());
                            db.updateMachinesStatistic(7, opcR.getMachine7Production());
                            db.updateMachinesStatistic(8, opcR.getMachine8Production());
                            db.updateCurrentStores(opcR.getWareHouse());
                        }

                    }
                    // Easy Ones
                    else {
                        DifficultRS = false;
                        // Machine to control this transformation
                        if (this.StateEasyRS == 0 && !opcR.getACKRight() && opcR.getRightSide()) {
                            this.StateEasyRS = 1;
                            opcS.sendRight(tfs[0].getPath());
                            tfs[0] = db.addElapseTransform(tfs[0], "right");
                            db.deleteTransform(tfs[0], "Transform");
                        } else if (this.StateEasyRS == 1 && opcR.getACKRight()) {
                            this.StateEasyRS = 2;
                            opcS.sendRight(zeros);
                        } else if (this.StateEasyRS == 2 && opcR.getRightSide() && PenaltyRS) {
                            this.StateEasyRS = 0;
                            endTransformRight = true;
                            // Statistics
                            db.updateMachinesStatistic(5, opcR.getMachine5Production());
                            db.updateMachinesStatistic(6, opcR.getMachine6Production());
                            db.updateMachinesStatistic(7, opcR.getMachine7Production());
                            db.updateMachinesStatistic(8, opcR.getMachine8Production());
                            db.updateCurrentStores(opcR.getWareHouse());
                        }
                    }

                    //Penalty
                    if (DifficultRS) {
                        // Penalty for difficult
                        if (this.StatePenaltyRS == 0 && opcR.getNewTimerRight() && !PenaltyRS && (this.StateDifficult1RS == 3 || this.StateDifficult2RS == 3)) {
                            this.StatePenaltyRS = 1;
                            timeRS = opcR.getRightTimer();
                            opcS.sendNewTimerRight(true);
                        } else if (this.StatePenaltyRS == 1 && !opcR.getNewTimerRight()) {
                            this.StatePenaltyRS = 2;
                            opcS.sendNewTimerRight(false);
                        } else if (this.StatePenaltyRS == 2 && opcR.getNewTimerRight() && !PenaltyRS && (this.StateDifficult1RS == 4 || this.StateDifficult2RS == 4)) {
                            this.StatePenaltyRS = 3;
                            timeRS += opcR.getRightTimer();
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0], "right", timeRS);
                            db.deleteTransform(tfs[0], "ElapseTransform");
                            opcS.sendNewTimerRight(true);
                        } else if (this.StatePenaltyRS == 3 && !opcR.getNewTimerRight()) {
                            this.StatePenaltyRS = 0;
                            PenaltyRS = true;
                            opcS.sendNewTimerRight(false);
                            timeRS = 0;
                        }
                    }
                    else {
                        // Penalty for easy
                        if (this.StatePenaltyRS == 0 && opcR.getNewTimerRight() && this.StateEasyRS == 2 && !PenaltyRS) {
                            this.StatePenaltyRS = 1;
                            timeRS = opcR.getRightTimer();
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0], "right", timeRS);
                            db.deleteTransform(tfs[0], "ElapseTransform");
                            opcS.sendNewTimerRight(true);
                        } else if (this.StatePenaltyRS == 1 && !opcR.getNewTimerRight()) {
                            this.StatePenaltyRS = 0;
                            PenaltyRS = true;
                            opcS.sendNewTimerRight(false);
                            timeRS = 0;
                        }
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
