package com.a5c.OPC_UA;

public class readOPC {
    private final clientOPC_UA client;

    public readOPC(clientOPC_UA cl) {
        this.client = cl;
    }

    public int[] getWareHouse() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.warehouse");
    }

    public boolean[] getMachinesLeft() {
        return (boolean[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.ms_left");
    }

    public boolean[] getMachinesRight() {
        return (boolean[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.ms_right");
    }

}
