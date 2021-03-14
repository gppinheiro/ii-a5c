package com.a5c;

import com.a5c.OPC_UA.mainOPC;

public class MES {
    public static void main(final String[] args) {
        mainOPC opc = new mainOPC();

        // Just to test if reads everything ok
        System.out.println("PLC program, variable bool:" + opc.getValue("BOOL_var"));
        System.out.println("PLC program, variable int:" + opc.getValue("int_var"));
        System.out.println("Global, variable bool:" + opc.getValue("global_bool_var"));
    }
}
