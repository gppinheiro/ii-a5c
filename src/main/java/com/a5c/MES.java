package com.a5c;

import com.a5c.OPC_UA.clientOPC_UA;

/*
TODO list:

  [.]  OPC Communication
            - Read OK
            - Write
  [ ]  UDP Communication
  [ ]  Statics
  [ ]  Unload
  [ ]  XML
  [ ]  Transformation

Legend:
   X   Done
   .   In progress
   \   Skipped
 */

public class MES {
    public static void main(final String[] args) {
        clientOPC_UA opc = new clientOPC_UA();

        // Just to test if reads everything ok
        System.out.println("Before writing:");
        System.out.println("PLC program, variable bool:" + opc.getValue("BOOL_var"));
        System.out.println("PLC program, variable int:" + opc.getValue("int_var"));

        opc.setValue("BOOL_var",true);
        opc.setValue("int_var",5);

        System.out.println("After writing:");
        System.out.println("PLC program, variable bool:" + opc.getValue("BOOL_var"));
        System.out.println("PLC program, variable int:" + opc.getValue("int_var"));
    }
}
