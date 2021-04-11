package com.a5c.DATA;

public class Unload {

    private final int orderNumber,type,destination,quantity;

    public Unload(int orderNumber, int type, int destination, int quantity) {
        this.orderNumber = orderNumber;
        this.type = type;
        this.destination = destination;
        this.quantity = quantity;
    }

    public int[] getPath() {
        int[] factory = new int[]{0, 0, 0, 0};

        factory[0] = this.type;
        factory[1] = -1;
        factory[2] = this.quantity;
        factory[3] = this.destination;

        return factory;
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
}
