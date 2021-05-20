package com.a5c.OPC_UA;

public class sendOPC {
    private final clientOPC_UA client;

    public sendOPC(clientOPC_UA cl) {
        this.client = cl;
    }

    public void sendLeft(int[] vi) {
        client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.order_l", vi);
    }

    public void sendReadAckLeft(boolean b) {
        client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.t_left", b);
    }

    public void sendRight(int[] vi) {
        client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.order_r", vi);
    }

    public void sendReadAckRight(boolean b) {
        client.setValue("|var|CODESYS Control Win V3 x64.Application.GVL.t_right", b);
    }

}
