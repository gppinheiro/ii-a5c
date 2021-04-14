package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;

public class whatToDo implements Runnable{
    public dbConnect db;
    private final clientOPC_UA opc;
    private final readOPC opcR;
    private final sendOPC opcS;
    private Thread thrWTD;
    private int State1;
    private int State2;
    private int State3;
    private int StatePenaltyL;
    private final int[] zeros = {0,0,0,0};

    public whatToDo(clientOPC_UA cl,dbConnect dbc) {
        this.opc = cl;
        this.db = dbc;
        this.opcR = new readOPC(opc);
        this.opcS = new sendOPC(opc);
        this.State1 = 0;
        this.State2 = 0;
        this.State3 = 0;
        this.StatePenaltyL = 0;
    }

    public void start() {
        if(thrWTD==null) {
            thrWTD = new Thread(this);
            thrWTD.start();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Transform[] tfs = db.getTransform();
                Unload[] unls = db.getUnload();

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

                // PODE ENVIAR PARA UM OU PARA OUTRO OU PARA AMBOS OU PARA NENHUM

                // GAJO ENVIA PARA O LADO ESQUERDO

                    boolean difficult = false;
                    int time=0;

                    // 1 dificil
                    if ( tfs[0].getFrom()==1 && ( tfs[0].getTo()==6 || tfs[0].getTo()==7 || tfs[0].getTo()==8 || tfs[0].getTo()==9  ) ) {
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),1,5,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),5,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        if ( this.State1==0 && !opcR.getACKLeft() ) {
                            this.State1=1;
                            opcS.sendLeft(tf1.getPath());
                            db.addTransform(tfs[0],"l");
                            db.deleteTransform(tfs[0],"Transform");
                        }
                        else if (this.State1==1 && opcR.getACKLeft() ) {
                            this.State1=2;
                            opcS.sendLeft(this.zeros);
                        }
                        else if (this.State1==2 && !opcR.getACKLeft()) {
                            this.State1=3;
                            opcS.sendLeft(tf2.getPath());
                        }
                        else if (this.State1==3 && opcR.getACKLeft()) {
                            this.State1=0;
                            difficult=true;
                        }

                        if ( this.StatePenaltyL==0 && opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=1;
                            time += opcR.getLeftTimer();
                            opcS.sendNewTimerLeft(true);
                        }
                        else if ( this.StatePenaltyL==1 && !opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=0;
                            opcS.sendNewTimerLeft(false);
                        }

                    }

                    // 2 dificil
                    else if ( tfs[0].getFrom()==2 && ( tfs[0].getTo()==7 || tfs[0].getTo()==8 ) ) {
                        Transform tf1 = new Transform(tfs[0].getOrderNumber(),2,6,tfs[0].getQuantity(),0,0,0);
                        Transform tf2 = new Transform(tfs[0].getOrderNumber(),6,tfs[0].getTo(),tfs[0].getQuantity(),tfs[0].getTime(),tfs[0].getMaxDelay(),tfs[0].getPenalty());

                        if ( this.State2==0 && !opcR.getACKLeft() ) {
                            this.State2=1;
                            opcS.sendLeft(tf1.getPath());
                        }
                        else if (this.State2==1 && opcR.getACKLeft() ) {
                            this.State2=2;
                            opcS.sendLeft(this.zeros);
                        }
                        else if (this.State2==2 && !opcR.getACKLeft()) {
                            this.State2=3;
                            opcS.sendLeft(tf2.getPath());
                        }
                        else if (this.State2==3 && opcR.getACKLeft()) {
                            this.State2=0;
                            difficult=true;
                        }

                        if ( this.StatePenaltyL==0 && opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=1;
                            time += opcR.getLeftTimer();
                            opcS.sendNewTimerLeft(true);
                        }
                        else if ( this.StatePenaltyL==1 && !opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=0;
                            opcS.sendNewTimerLeft(false);
                        }

                    }
                    // facil
                    else {
                        if ( this.State3==0 && !opcR.getACKLeft() ) {
                            this.State3=1;
                            opcS.sendLeft(tfs[0].getPath());
                        }
                        else if (this.State3==1 && opcR.getACKLeft() ) {
                            this.State3=2;
                            opcS.sendLeft(this.zeros);
                        }
                        else if (this.State3==2 && opcR.getACKLeft()) {
                            this.State3=0;
                        }

                        if ( this.StatePenaltyL==0 && opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=1;
                            time = opcR.getLeftTimer();
                            tfs[0].setPenalty( time/50 * tfs[0].getPenalty() );
                            db.addEndTransform(tfs[0],time);
                            opcS.sendNewTimerLeft(true);
                        }
                        else if ( this.StatePenaltyL==1 && !opcR.getNewTimerLeft() ) {
                            this.StatePenaltyL=0;
                            opcS.sendNewTimerLeft(false);
                        }

                    }

                    // Penalty
                    if (difficult) {
                        tfs[0].setPenalty( time/50 * tfs[0].getPenalty() );
                        db.addEndTransform(tfs[0],time);
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
                } */

                // Create waiting lines (?)
                // When machines are available, send to fabric
                // Back to init

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
