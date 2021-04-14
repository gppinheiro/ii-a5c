package com.a5c.OPC_UA;

public class sendOPC {
    private clientOPC_UA client;

    public sendOPC(clientOPC_UA cl) {
        this.client = cl;
    }

    public boolean sendLeft(int[] vi) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.order_l",vi);
    }

    public boolean sendNewTimerLeft(boolean b) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.t_left",b);
    }

    public boolean sendRight(int[] vi) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.order_r",vi);
    }

    public boolean sendNewTimerRight(boolean b) {
        return client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.nt_right",b);
    }

}
