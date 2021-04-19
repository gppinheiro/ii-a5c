package com.a5c;

import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;

public class ControlStatics implements Runnable {
    private Thread thrCS;
    private final dbConnect db;
    private final readOPC opcR;

    public ControlStatics(clientOPC_UA op, dbConnect db) {
        this.opcR = new readOPC(op);
        this.db = db;
    }

    public void start() {
        if(thrCS==null) {
            thrCS = new Thread(this);
            thrCS.start();
        }
    }

    public void WarehouseValues() {
        int[] wv = opcR.getWareHouse();

        for (int i=0; i<wv.length; i++) {

        }
    }

    @Override
    public void run() {

    }
}
