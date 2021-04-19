package com.a5c.OPC_UA;

public class readOPC {
    private final clientOPC_UA client;

    public readOPC(clientOPC_UA cl) {
        this.client = cl;
    }

    public boolean getACKRight() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.ack_right");
    }

    public boolean getRightSide() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.free_right");
    }

    public boolean getNewTimerRight() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.nt_right");
    }

    public int getRightTimer() {
        String s = (String) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.timer_r");
        s = s.replace("T#","");
        String[] sparts = s.split("s");

        if (sparts[0].contains("m")) {
            String[] mparts = sparts[0].split("m");
            return Integer.parseInt(mparts[0])*60 + Integer.parseInt(mparts[1]);
        }
        else {
            return Integer.parseInt(sparts[0]);
        }
    }

    public boolean getACKLeft() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.ack_left");
    }

    public boolean getLeftSide() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.free_left");
    }

    public boolean getNewTimerLeft() {
        return (boolean) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.nt_left");
    }

    public int getLeftTimer() {
        String s = (String) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.timer_l");
        s = s.replace("T#","");
        String[] sparts = s.split("s");

        if (sparts[0].contains("m")) {
            String[] mparts = sparts[0].split("m");
            return Integer.parseInt(mparts[0])*60 + Integer.parseInt(mparts[1]);
        }
        else {
            return Integer.parseInt(sparts[0]);
        }
    }

    public int[] getWareHouse() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.warehouse");
    }

    public int[] getMachine1Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p1");
    }

    public int[] getMachine2Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p2");
    }

    public int[] getMachine3Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p3");
    }

    public int[] getMachine4Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p4");
    }

    public int[] getMachine5Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p5");
    }

    public int[] getMachine6Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p6");
    }

    public int[] getMachine7Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p7");
    }

    public int[] getMachine8Production() {
        return (int[]) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.p8");
    }

}
