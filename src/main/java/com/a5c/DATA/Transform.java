package com.a5c.DATA;

public class Transform {
    private final int orderNumber;
    private final int from;
    private final int to;
    private final int quantity;
    private final int time;
    private int timeMES;
    private final int maxDelay;
    private int penalty;

    private int InitPenalty;
    private int ST;
    private int ET;

    public Transform(int orderNumber, int from, int to, int quantity, int time, int maxDelay, int penalty) {
        this.orderNumber = orderNumber;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.time = time;
        this.maxDelay = maxDelay;
        this.penalty = penalty;
        this.InitPenalty = penalty;
        this.timeMES = time+1;
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

    public int getInitPenalty() { return InitPenalty; }

    public void setPenalty(int p) {
        this.penalty = p;
    }

    public void setST(int ST) {
        this.ST = ST;
    }

    public int getST() {
        return ST;
    }

    public int getET() {
        return ET;
    }

    public void setET(int ET) {
        this.ET = ET;
    }

    public void setInitPenalty(int initPenalty) {
        InitPenalty = initPenalty;
    }

    public int getTimeMES() {
        return timeMES;
    }

    public void setTimeMES(int timeMES) {
        this.timeMES = timeMES;
    }
}