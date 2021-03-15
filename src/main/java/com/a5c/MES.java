package com.a5c;

import com.a5c.OPC_UA.clientOPC_UA;

/*
TODO list:

  [.]  OPC Communication
            - Read OK
            - Write OK
  [ ]  UDP Communication
            - UDP Client
            - File XML
  [ ]  Statics
  [ ]  Unload
  [ ]  XML
  [ ]  Transformation
  [ ]  Thread

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
        System.out.println("PLC program, variable:" + opc.getValue("v1"));

        int[] test = new int[3];
        test[0]=1;
        test[1]=1;
        test[2]=1;
        opc.setValue("v1",test);

        System.out.println("After writing:");
        System.out.println("PLC program, variable:" + opc.getValue("v1"));
    }
}
