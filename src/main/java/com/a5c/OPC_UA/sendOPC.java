package com.a5c.OPC_UA;

public class sendOPC {
    private clientOPC_UA client;

    public sendOPC(clientOPC_UA cl) {
        this.client = cl;
    }

    public boolean sendLeftMachinesVector(int[] vi) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.mach_pl",vi);
    }

    public boolean sendTransform(int[] vi) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.order_l",vi);
    }

}
