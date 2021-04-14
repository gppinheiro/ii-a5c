package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

public class ControlTU implements Runnable{
    // Global variables for others JAVA classes
    private Thread thrCTU;
    private final dbConnect db;
    private final readOPC opcR;
    private final sendOPC opcS;

    // Global var
    private static final int[] zeros = {0,0,0,0};
    private Transform[] tfs = null;

    // Global Variables for State Machines
    // LS - Left Side
    private int StateDifficult1LS;
    private int StateDifficult2LS;
    private int StateEasyLS;
    private int StatePenaltyLS;

    private boolean endTransformLeft;

    public ControlTU(clientOPC_UA cl, dbConnect dbc) {
        this.db = dbc;
        this.opcR = new readOPC(cl);
        this.opcS = new sendOPC(cl);
        this.StateDifficult1LS = 0;
        this.StateDifficult2LS = 0;
        this.StateEasyLS = 0;
        this.StatePenaltyLS = 0;
        this.endTransformLeft = true;
    }

    public void start() {
        if(thrCTU==null) {
            thrCTU = new Thread(this);
            thrCTU.start();
        }
    }

    public int TimeLeftSideDiff() {
        int time=0;

        if ( this.StatePenaltyLS ==0 && opcR.getNewTimerLeft() ) {
            this.StatePenaltyLS =1;
            time += opcR.getLeftTimer();
            opcS.sendNewTimerLeft(true);
        }
        else if ( this.StatePenaltyLS ==1 && !opcR.getNewTimerLeft() ) {
            this.StatePenaltyLS =0;
            opcS.sendNewTimerLeft(false);
        }

        return time;
    }

    @Override
    public void run() {
        try {
            Transform tf_test = new Transform(1,1,2,4,0,0,0);
            Transform tf_test2 = new Transform(2,1,2,1,0,0,2);
            db.addTransform(tf_test);
            db.addTransform(tf_test2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        while(true) {
            try {
                if ( opcR.getLeftSide() && !opcR.getACKLeft() && endTransformLeft ) {
                    this.tfs = db.getTransform();
                    endTransformLeft=false;

                    // TODO - Implement Unloads
                    //Unload[] unls = db.getUnload();

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

                // Send both or neither or left or right only - TODO: Implement this conditions

                if (tfs.length!=0) {
                    // Send Left Side
                    boolean difficultLS = false;
                    int timeLS=0;

                    // All difficult ones from piece 1
                    if ( tfs[0].getFrom()==1 && ( tfs[0].getTo()==6 || tfs[0].getTo()==7 || tfs[0].getTo()==8 || tfs[0].getTo()==9  ) ) {
                        // Divide into 2 transformations, we keep the essential equilibrium requested by Mario
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),1,5,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),5,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        // Machine to send both tfs
                        if ( this.StateDifficult1LS==0 && !opcR.getACKLeft() ) {
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
                        else if (this.StateDifficult1LS==3 && opcR.getACKLeft()) {
                            this.StateDifficult1LS=0;
                            difficultLS=true;
                        }

                        // Read the final value - TODO: TEST IT
                        timeLS = TimeLeftSideDiff();
                    }

                    // All dificults from piece 2
                    else if ( tfs[0].getFrom()==2 && ( tfs[0].getTo()==7 || tfs[0].getTo()==8 ) ) {
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),2,6,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),6,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        if ( this.StateDifficult2LS==0 && !opcR.getACKLeft() ) {
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
                        else if (this.StateDifficult2LS==3 && opcR.getACKLeft()) {
                            this.StateDifficult2LS=0;
                            difficultLS=true;
                        }

                        timeLS = TimeLeftSideDiff();
                    }

                    // The others are easy for us
                    else {
                        // Machine to control this transformation
                        if ( this.StateEasyLS==0 && !opcR.getACKLeft() ) {
                            this.StateEasyLS=1;
                            opcS.sendLeft(tfs[0].getPath());
                            db.addElapseTransform(tfs[0],"left");
                        }
                        else if ( this.StateEasyLS==1 && opcR.getACKLeft() ) {
                            this.StateEasyLS=2;
                            opcS.sendLeft(zeros);
                        }
                        else if ( this.StateEasyLS==2 && opcR.getLeftSide() ) {
                            this.StateEasyLS=0;

                            endTransformLeft=true;
                            difficultLS=false;

                            timeLS = opcR.getLeftTimer();
                            tfs[0].setPenalty( timeLS/50 * tfs[0].getPenalty() );
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0],"left",timeLS);
                            db.deleteTransform(tfs[0],"Transform");
                            db.deleteTransform(tfs[0],"ElapseTransform");
                            opcS.sendNewTimerLeft(true);
                        }

                        // Get Timer and with that select the correspondent penalty
                        /*if ( opcR.getNewTimerLeft() ) {
                            timeLS = opcR.getLeftTimer();
                            tfs[0].setPenalty( timeLS/50 * tfs[0].getPenalty() );
                            // When end, we add it into a new table and delete from other
                            db.addEndTransform(tfs[0],"left",timeLS);
                            db.deleteTransform(tfs[0],"Transform");
                            db.deleteTransform(tfs[0],"ElapseTransform");
                            opcS.sendNewTimerLeft(true);
                        }*/

                    }

                    // Penalty - TODO: TEST IT, I think this will not work unfortunately
                    if (difficultLS) {
                        tfs[0].setPenalty( timeLS/50 * tfs[0].getPenalty() );
                        db.addEndTransform(tfs[0],"left",timeLS);
                        db.deleteTransform(tfs[0],"ElapseTransform");
                    }
                }

                // GAJO ENVIA PARA O LADO DIREITO
                /*if ( !opcR.getRightSide() || !opcR.getACKRight() ) {
                    // If we don't have unloads, we make transformations
                    if (unls.length==0) {

                        // It's difficult so we don't do on right side
                        if ( !( (tfs[0].getFrom()==1 && ( tfs[0].getTo()==6 || tfs[0].getTo()==7 || tfs[0].getTo()==8 || tfs[0].getTo()==9  )) || ( tfs[0].getFrom()==2 && ( tfs[0].getTo()==7 || tfs[0].getTo()==8 ) ) ) ) {

                        }

                        //Choose path:
                        // If difficult, send to left side
                        // If easy, send to right side
                        // Select path
                        //Create vector to send fabric
                        //Remove transformation from db
                    }
                    // If not we prioritize unloads
                    else {
                        //Select the first one unls[0]
                        //Create vector to send fabric
                        //opcS.sendRight(unls[0].getPath());
                        //Remove unload from db
                    }
                }*/

                // Create waiting lines (?)
                // When machines are available, send to fabric
                // Back to init

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
