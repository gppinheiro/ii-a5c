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

                //TODO:
                // WE HAVE TFS AND UNLS
                // NEED INFORMATION FROM OPC
                // WE NEED TO DEFINE PRIOR

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
