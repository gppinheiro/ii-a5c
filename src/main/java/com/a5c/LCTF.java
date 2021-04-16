package com.a5c;

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
    private static final int[] zeros = {0,0,0,0};
    private Transform[] tfs = null;
    private boolean endTransformLeft;
    private int timeLS;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateDifficult1LS;
    private int StateDifficult2LS;
    private int StateEasyLS;
    private int StatePenaltyLS;

    public LCTF(clientOPC_UA cl, dbConnect dbc) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateDifficult1LS = 0;
        this.StateDifficult2LS = 0;
        this.StateEasyLS = 0;
        this.StatePenaltyLS = 0;
        this.endTransformLeft = true;
        this.timeLS = 0;
    }

    public void start() {
        if(thrLCTF ==null) {
            thrLCTF = new Thread(this);
            thrLCTF.start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        boolean PenaltyLS;
        boolean DifficultLS;

        while(true) {
            try {
                // Get DB
                if ( opcR.getLeftSide() && !opcR.getACKLeft() && endTransformLeft && db.TransformLength()!=0 ) {
                    this.tfs = db.getTransform();
                    endTransformLeft=false;

                    // Prioridade:
                    // Penalty - Se for grande, fazer esta primeiro
                    // MaxDelay - Se for pequeno, fazer esta primeiro
                    // Sort tfs vector with base on MaxDelay and Penalty
                    Transform temp;
                    for (int i=1; i< tfs.length; i++) {
                        for (int j=i; j>0; j--) {
                            if( (tfs[j].getMaxDelay() < tfs[j-1].getMaxDelay()) || (tfs[j].getMaxDelay() == tfs[j-1].getMaxDelay() && tfs[j].getPenalty()>tfs[j-1].getPenalty()) ) {
                                temp = tfs[j];
                                tfs[j] = tfs[j-1];
                                tfs[j-1] = temp;
                            }
                        }
                    }
                }

                // Left Side Transform
                PenaltyLS=false;
                while (!endTransformLeft) {
                    // All difficult ones from piece 1
                    if ( tfs[0].getFrom()==1 && ( tfs[0].getTo()==6 || tfs[0].getTo()==7 || tfs[0].getTo()==8 || tfs[0].getTo()==9  ) ) {
                        DifficultLS=true;
                        // Divide into 2 transformations, we keep the essential equilibrium requested by Mario
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),1,5,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),5,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        // Machine to control send both tfs
                        if ( this.StateDifficult1LS==0 && !opcR.getACKLeft() && opcR.getLeftSide() ) {
                            this.StateDifficult1LS=1;
                            opcS.sendLeft(tf1.getPath());
                            db.addElapseTransform(tfs[0],"left");
                            db.deleteTransform(tfs[0],"Transform");
                        }
                        else if (this.StateDifficult1LS==1 && opcR.getACKLeft() ) {
                            this.StateDifficult1LS=2;
                            opcS.sendLeft(zeros);
                        }
                        else if (this.StateDifficult1LS==2 && !opcR.getACKLeft()) {
                            this.StateDifficult1LS=3;
                            opcS.sendLeft(tf2.getPath());
                        }
                        else if (this.StateDifficult1LS==3 && opcR.getACKLeft() ) {
                            this.StateDifficult1LS=4;
                            opcS.sendLeft(zeros);
                        }
                        else if ( this.StateDifficult1LS==4 && opcR.getLeftSide() && PenaltyLS ) {
                            this.StateDifficult1LS=0;
                            endTransformLeft=true;
                        }

                    }
                    // All difficult ones from piece 1
                    else if ( tfs[0].getFrom()==2 && ( tfs[0].getTo()==7 || tfs[0].getTo()==8 ) ) {
                        DifficultLS=true;
                        // Divide into 2 transformations, we keep the essential equilibrium requested by Mario
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),2,6,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),6,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        // Machine to control send both tfs
                        if ( this.StateDifficult2LS==0 && !opcR.getACKLeft() && opcR.getLeftSide() ) {
                            this.StateDifficult2LS=1;
                            opcS.sendLeft(tf1.getPath());
                            db.addElapseTransform(tfs[0],"left");
                            db.deleteTransform(tfs[0],"Transform");
                        }
                        else if (this.StateDifficult2LS==1 && opcR.getACKLeft() ) {
                            this.StateDifficult2LS=2;
                            opcS.sendLeft(zeros);
                        }
                        else if (this.StateDifficult2LS==2 && !opcR.getACKLeft()) {
                            this.StateDifficult2LS=3;
                            opcS.sendLeft(tf2.getPath());
                        }
                        else if (this.StateDifficult2LS==3 && opcR.getACKLeft() ) {
                            this.StateDifficult2LS=4;
                            opcS.sendLeft(zeros);
                        }
                        else if ( this.StateDifficult2LS==4 && opcR.getLeftSide() && PenaltyLS ) {
                            this.StateDifficult2LS=0;
                            endTransformLeft=true;
                        }

                    }
                    // All easy
                    else {
                        DifficultLS=false;
                        // Machine to control this transformation
                        if ( this.StateEasyLS==0 && !opcR.getACKLeft() && opcR.getLeftSide() ) {
                            this.StateEasyLS=1;
                            opcS.sendLeft(tfs[0].getPath());
                            db.addElapseTransform(tfs[0],"left");
                            db.deleteTransform(tfs[0],"Transform");
                        }
                        else if ( this.StateEasyLS==1 && opcR.getACKLeft() ) {
                            this.StateEasyLS=2;
                            opcS.sendLeft(zeros);
                        }
                        else if ( this.StateEasyLS==2 && opcR.getLeftSide() && PenaltyLS ) {
                            this.StateEasyLS=0;
                            endTransformLeft=true;
                        }
                    }

                    //Penalty
                    if(DifficultLS) {
                        // Penalty for difficult
                        if ( this.StatePenaltyLS==0 && opcR.getNewTimerLeft() && !PenaltyLS && (this.StateDifficult1LS==3 || this.StateDifficult2LS==3) ) {
                            this.StatePenaltyLS=1;
                            timeLS = opcR.getLeftTimer();
                            opcS.sendNewTimerLeft(true);
                        }
                        else if (this.StatePenaltyLS==1 && !opcR.getNewTimerLeft()) {
                            this.StatePenaltyLS=2;
                            opcS.sendNewTimerLeft(false);
                        }
                        else if (this.StatePenaltyLS==2 && opcR.getNewTimerLeft() && !PenaltyLS && (this.StateDifficult1LS==3 || this.StateDifficult2LS==3) ) {
                            this.StatePenaltyLS=3;
                            timeLS += opcR.getLeftTimer();
                            tfs[0].setPenalty( timeLS/50 * tfs[0].getPenalty() );
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0],"left",timeLS);
                            db.deleteTransform(tfs[0],"ElapseTransform");
                            opcS.sendNewTimerLeft(true);
                        }
                        else if (this.StatePenaltyLS==3 && !opcR.getNewTimerLeft()) {
                            this.StatePenaltyLS=0;
                            PenaltyLS=true;
                            opcS.sendNewTimerLeft(false);
                            timeLS=0;
                        }
                    }
                    else {
                        // Penalty for easy
                        if ( this.StatePenaltyLS==0 && opcR.getNewTimerLeft() && this.StateEasyLS==2 && !PenaltyLS) {
                            this.StatePenaltyLS=1;
                            timeLS = opcR.getLeftTimer();
                            tfs[0].setPenalty( timeLS/50 * tfs[0].getPenalty() );
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0],"left",timeLS);
                            db.deleteTransform(tfs[0],"ElapseTransform");
                            opcS.sendNewTimerLeft(true);
                        }
                        else if (this.StatePenaltyLS==1 && !opcR.getNewTimerLeft()) {
                            this.StatePenaltyLS=0;
                            PenaltyLS=true;
                            opcS.sendNewTimerLeft(false);
                            timeLS=0;
                        }
                    }

                    System.out.println("State Left Easy: "+this.StateEasyLS);
                    System.out.println("State Left Penalty: "+this.StatePenaltyLS);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
