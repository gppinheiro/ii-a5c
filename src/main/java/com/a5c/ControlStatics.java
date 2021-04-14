package com.a5c;

import com.a5c.DB.dbConnect;

public class ControlStatics implements Runnable {
    private Thread thrCS;
    private final dbConnect db;

    public ControlStatics(dbConnect db) {
        this.db = db;
    }

    public void start() {
        if(thrCS==null) {
            thrCS = new Thread(this);
            thrCS.start();
        }
    }

    @Override
    public void run() {

    }
}
