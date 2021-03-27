package com.a5c;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;

import java.sql.SQLException;

public class whatToDo implements Runnable{
    public dbConnect db;
    private clientOPC_UA opc;
    private Thread thrWTD;

    public whatToDo(dbConnect dbc) {
        this.db = dbc;
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
                // Bools para tapetes de carga
                // Quantidade de cada peça (P1 até P9)

                // MANDAR
                // Transformação: 0
                // Unload: 1
                
                Transform[] tfs = db.getTransform();
                Unload[] unls = db.getUnload();

                int[] fabric = new int[] {0,0,0,0,0};

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
                    //Select the first one tfs[0]
                    //tfs[0] = P1
                    if (tfs[0].getFrom()==1 && tfs[0].getTo()==2){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=1;
                        fabric[1]=15;
                        fabric[2]=0;
                        fabric[3]=0;
                        fabric[4]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==3){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=1;
                        fabric[1]=15;
                        fabric[2]=15;
                        fabric[3]=0;
                        fabric[4]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==4){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=1;
                        fabric[1]=15;
                        fabric[2]=15;
                        fabric[3]=15;
                        fabric[4]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==5){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=1;
                        fabric[1]=15;
                        fabric[2]=15;
                        fabric[3]=15;
                        fabric[4]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==6){
                        //Choose path
                        //Create vector to send fabric

                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==9){
                        //Choose path
                        //Create vector to send fabric

                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric

                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==1 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    //tfs[0] = P2
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==3){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=2;
                        fabric[1]=0;
                        fabric[2]=15;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==4){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=2;
                        fabric[1]=0;
                        fabric[2]=15;
                        fabric[3]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==5){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=2;
                        fabric[1]=15;
                        fabric[2]=15;
                        fabric[3]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==6){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==9){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==2 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    //tfs[0] = P3
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==4){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=3;
                        fabric[1]=0;
                        fabric[2]=0;
                        fabric[3]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==5){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=3;
                        fabric[1]=15;
                        fabric[2]=0;
                        fabric[3]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==6){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=3;
                        fabric[1]=15;
                        fabric[2]=15;
                        fabric[3]=15;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==9){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==3 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    //tfs[0] = P4
                    else if (tfs[0].getFrom()==4 && tfs[0].getTo()==5){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=4;
                        fabric[1]=15;
                        fabric[2]=0;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==4 && tfs[0].getTo()==6){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=4;
                        fabric[1]=15;
                        fabric[2]=30;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==4 && tfs[0].getTo()==9){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=4;
                        fabric[1]=15;
                        fabric[2]=0;
                        fabric[3]=30;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    } else if (tfs[0].getFrom()==4 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=4;
                        fabric[1]=15;
                        fabric[2]=30;
                        fabric[3]=30;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==4 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    //tfs[0] = P5
                    else if (tfs[0].getFrom()==5 && tfs[0].getTo()==6){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=5;
                        fabric[1]=0;
                        fabric[2]=30;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==5 && tfs[0].getTo()==9){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=5;
                        fabric[1]=0;
                        fabric[2]=0;
                        fabric[3]=30;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==5 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=5;
                        fabric[1]=0;
                        fabric[2]=30;
                        fabric[3]=30;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==5 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=5;
                        fabric[1]=15;
                        fabric[2]=30;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    //tfs[0] = P6
                    else if (tfs[0].getFrom()==6 && tfs[0].getTo()==7){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=6;
                        fabric[1]=0;
                        fabric[2]=0;
                        fabric[3]=30;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }
                    else if (tfs[0].getFrom()==6 && tfs[0].getTo()==8){
                        //Choose path
                        //Create vector to send fabric
                        fabric[0]=6;
                        fabric[1]=15;
                        fabric[2]=0;
                        fabric[3]=0;
                        //Remove transformation from db
                        //Put on unfinished transformations
                    }

                }

                // If not we prioritize unloads
                else {
                    //Select the first one unls[0]
                    //unls[0]=P1
                    if(unls[0].getDestination()==1){
                        //Create vector to send fabric
                        //Remove unload from db
                        //Put on unfinished unloads
                    }
                    if(unls[0].getDestination()==2){
                        //Create vector to send fabric
                        //Remove unload from db
                        //Put on unfinished unloads
                    }
                    if(unls[0].getDestination()==3){
                        //Create vector to send fabric
                        //Remove unload from db
                        //Put on unfinished unloads
                    }



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
