package com.a5c.DATA;

public class Transform {
    private final int orderNumber;
    private final int from;
    private final int to;
    private final int quantity;
    private final int time;
    private final int maxDelay;
    private int penalty;
    private final int InitPenalty;
    private int ST;
    private int ET;
    private int PI;

    public Transform(int orderNumber, int from, int to, int quantity, int time, int maxDelay, int penalty) {
        this.orderNumber = orderNumber;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.time = time;
        this.maxDelay = maxDelay;
        this.penalty = penalty;
        this.InitPenalty = penalty;
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

    public int findTimeMachines(int from, int to) {
        switch(from) {
            case 1:
                if(to==2) { return 15; }
                else if(to==3) { return 30; }
                else if(to==4) { return 45; }
                else if(to==5) { return 60; }
                else if(to==6) { return 90; }
                else if(to==9) { return 90; }
                else if(to==7) { return 120; }
                else if(to==8) { return 105; }
            case 2:
                if(to==3) { return 15; }
                else if(to==4) { return 30; }
                else if(to==5) { return 45; }
                else if(to==6) { return 75; }
                else if(to==9) { return 75; }
                else if(to==7) { return 105; }
                else if(to==8) { return 90; }
            case 3:
                if(to==4) { return 15; }
                else if(to==5) { return 30; }
                else if(to==6) { return 60; }
                else if(to==9) { return 60; }
                else if(to==7) { return 90; }
                else if(to==8) { return 75; }
            case 4:
                if(to==5) { return 15; }
                else if(to==6) { return 45; }
                else if(to==9) { return 45; }
                else if(to==7) { return 75; }
                else if(to==8) { return 60; }
            case 5:
                if(to==6) { return 30; }
                else if(to==9) { return 30; }
                else if(to==7) { return 60; }
                else if(to==8) { return 45; }
            case 6:
                if(to==7) { return 30; }
                else if(to==8) { return 15; }
        }
        return 0;
    }

    public void predictST() {

    }

}