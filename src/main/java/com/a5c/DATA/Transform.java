package com.a5c.DATA;

public class Transform {
    private final int orderNumber, from, to, quantity, time, maxDelay, penalty;

    public Transform(int orderNumber, int from, int to, int quantity, int time, int maxDelay, int penalty) {
        this.orderNumber = orderNumber;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.time = time;
        this.maxDelay = maxDelay;
        this.penalty = penalty;
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
}
