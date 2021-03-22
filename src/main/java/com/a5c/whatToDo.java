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
                Transform[] tfs = db.getTransform();
                Unload[] unls = db.getUnload();

                // CAETANO:
                // LER
                // Bools para cada maquina
                // Bools para tapetes de carga

                // MANDAR
                // Transformação: 0
                // Unload: 1

                // Prioridade:
                // Penalty - Se for grande, fazer esta primeiro
                // MaxDelay - Se for pequeno, fazer esta primeiro

                // Temos que definir lado de esquerdo (peças mais dificeis) e direito(peças mais faceis)

                //TODO:
                // DEFINE PRIOR

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
