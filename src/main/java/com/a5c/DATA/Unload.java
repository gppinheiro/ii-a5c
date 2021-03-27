package com.a5c.DATA;

public class Unload {

    private final int orderNumber,type,destination,quantity;

    public Unload(int orderNumber, int type, int destination, int quantity) {
        this.orderNumber = orderNumber;
        this.type = type;
        this.destination = destination;
        this.quantity = quantity;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public int getType() {
        return type;
    }

    public int getDestination() {
        return destination;
    }

    public int getQuantity() {
        return quantity;
    }

    public int[] getPath(){
        int[] unloads = new int[] {0,0,0};
        if(this.destination==1){
            //Create vector to send fabric
            unloads[0]=-1;
            unloads[1]=0;
            unloads[2]=0;
            //Remove unload from db

            //Put on unfinished unloads
        }
        if(this.destination==2){
            //Create vector to send fabric
            unloads[0]=0;
            unloads[1]=-1;
            unloads[2]=0;
            //Remove unload from db
            //Put on unfinished unloads
        }
        if(this.destination==3){
            //Create vector to send fabric
            unloads[0]=0;
            unloads[1]=0;
            unloads[2]=-1;
            //Remove unload from db
            //Put on unfinished unloads
        }

        return unloads;

    }
}
