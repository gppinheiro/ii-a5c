package com.a5c.DATA;

public class Transform {
    private final int orderNumber;
    private final int from;
    private final int to;
    private final int quantity;
    private final int time;
    private final int maxDelay;
    private int penalty;

    public Transform(int orderNumber, int from, int to, int quantity, int time, int maxDelay, int penalty) {
        this.orderNumber = orderNumber;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.time = time;
        this.maxDelay = maxDelay;
        this.penalty = penalty;
    }

    public int[] getPath() {
        int[] factory = new int[]{0, 0, 0, 0};

        factory[0] = this.from;
        factory[1] = this.to;
        factory[2] = this.quantity;

        return factory;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTime() {
        return time;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int p) {
        this.penalty = p;
    }

}