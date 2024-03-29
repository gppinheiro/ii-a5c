package com.a5c.DATA;

public class Transform {
    private final int orderNumber;
    private final int from;
    private final int to;
    private final int quantity;
    private final int time;
    private int timeMES;
    private final int maxDelay;
    private int realMaxDelay;
    private int penalty;
    private int InitPenalty;
    private int ST;
    private int ET;
    private int ExceptedTT;
    private String side;

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

    public int getRealMaxDelay() {
        return realMaxDelay;
    }

    public void setRealMaxDelay(int realDelay) {
        this.realMaxDelay = realDelay;
    }

    public int getExceptedTT() {
        return ExceptedTT;
    }

    public void setExceptedTT(int exceptedTT) {
        ExceptedTT = exceptedTT;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    @Override
    public String toString() {
        return "Transform{" +
                "orderNumber=" + orderNumber +
                ", from=" + from +
                ", to=" + to +
                ", quantity=" + quantity +
                ", time=" + time +
                ", timeMES=" + timeMES +
                ", maxDelay=" + maxDelay +
                ", realMaxDelay=" + realMaxDelay +
                ", penalty=" + penalty +
                ", InitPenalty=" + InitPenalty +
                ", ST=" + ST +
                ", ET=" + ET +
                ", ExceptedTT=" + ExceptedTT +
                '}';
    }
}