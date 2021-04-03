package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import com.a5c.OPC_UA.sendOPC;

import java.sql.SQLException;
import java.util.Arrays;

public class whatToDo implements Runnable{
    public dbConnect db;
    private clientOPC_UA opc;
    private readOPC opcR;
    private sendOPC opcS;
    private Thread thrWTD;

    public whatToDo(clientOPC_UA cl,dbConnect dbc) {
        this.opc = cl;
        this.db = dbc;
        this.opcR = new readOPC(opc);
        this.opcS = new sendOPC(opc);
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
                // CAETANO:
                // LER
                // Bools para cada maquina
                // Bools para tapetes de carga - Falta fazer
                // Quantidade de cada peça (P1 até P9) - Falta testar

                // MANDAR
                // Transformação: 0
                // Unload: 1
                
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

                // If we don't have unloads, we make transformations
                if (unls.length==0) {
                    int[] LeftMachines = new int[4];
                    LeftMachines[0]=1;
                    LeftMachines[1]=1;
                    LeftMachines[2]=1;
                    LeftMachines[3]=1;

                    opcS.sendLeftMachinesVector(LeftMachines);

                    //Select the first one tfs[0]
                    int[] trans = new int[3];
                    trans[0] = tfs[0].getFrom();
                    trans[1] = tfs[0].getTo();
                    trans[2] = tfs[0].getQuantity();

                    opcS.sendTransform(trans);

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
                    //Remove unload from db
                }

                // Create waiting lines (?)
                // When machines are available, send to fabric
                // Back to init

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
