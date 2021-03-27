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

    public int[] getPath() {

        int[] factory = new int[]{0, 0, 0, 0, 0};

        if (this.from == 1 && this.to == 2) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 1;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 3) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 1;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 4) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 1;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 5) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 1;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 15;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 6) {
            //Choose path
            //Create vector to send fabric

            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 9) {
            //Choose path
            //Create vector to send fabric

            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 7) {
            //Choose path
            //Create vector to send fabric

            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 1 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            //Remove transformation from db
            //Put on unfinished transformations
        }
        //tfs[0] = P2
        else if (this.from == 2 && this.to == 3) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 2;
            factory[1] = 0;
            factory[2] = 15;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 4) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 2;
            factory[1] = 0;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 5) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 2;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 6) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 2;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 30;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 9) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 2;
            factory[1] = 15;
            factory[2] = 15;
            factory[3] = 15;
            factory[4] = 30;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 7) {
            //Choose path
            //Create vector to send fabric
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 2 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            //Remove transformation from db
            //Put on unfinished transformations
        }
        //tfs[0] = P3
        else if (this.from == 3 && this.to == 4) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 0;
            factory[2] = 0;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 3 && this.to == 5) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 3 && this.to == 6) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 15;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 3 && this.to == 9) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 15;
            factory[4] = 30;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 3 && this.to == 7) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 15;
            factory[4] = 30;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 3 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 3;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 15;
            factory[4] = 15;
            //Remove transformation from db
            //Put on unfinished transformations
        }
        //tfs[0] = P4
        else if (this.from == 4 && this.to == 5) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 4;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 4 && this.to == 6) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 4;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 4 && this.to == 9) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 4;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 30;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 4 && this.to == 7) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 4;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 30;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 4 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 4;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 30;
            factory[4] = 15;
            //Remove transformation from db
            //Put on unfinished transformations
        }
        //tfs[0] = P5
        else if (this.from == 5 && this.to == 6) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 5;
            factory[1] = 0;
            factory[2] = 30;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 5 && this.to == 9) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 5;
            factory[1] = 0;
            factory[2] = 0;
            factory[3] = 30;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 5 && this.to == 7) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 5;
            factory[1] = 0;
            factory[2] = 30;
            factory[3] = 30;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 5 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 5;
            factory[1] = 15;
            factory[2] = 30;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        }
        //tfs[0] = P6
        else if (this.from == 6 && this.to == 7) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 6;
            factory[1] = 0;
            factory[2] = 0;
            factory[3] = 30;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        } else if (this.from == 6 && this.to == 8) {
            //Choose path
            //Create vector to send fabric
            factory[0] = 6;
            factory[1] = 15;
            factory[2] = 0;
            factory[3] = 0;
            factory[4] = 0;
            //Remove transformation from db
            //Put on unfinished transformations
        }

        return factory;


    }


    public int[] getMaquinas() {
        //Aqui mandamos a sequencia das 4 maquinas
        int[] maquinas = new int[]{0, 0, 0, 0};

        if (this.from == 1 && this.to == 2) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=1;
            maquinas[3]=1;
        } else if (this.from == 1 && this.to == 3) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=2;
            maquinas[3]=2;
        } else if (this.from == 1 && this.to == 4) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=2;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 1 && this.to == 5) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=2;
            maquinas[2]=3;
            maquinas[3]=1;
        } else if (this.from == 1 && this.to == 6) {
            //Choose maquinas to operate

        } else if (this.from == 1 && this.to == 9) {
            //Choose maquinas to operate

        } else if (this.from == 1 && this.to == 7) {
            //Choose maquinas to operate

        } else if (this.from == 1 && this.to == 8) {
            //Choose maquinas to operate

        }
        //tfs[0] = P2
        else if (this.from == 2 && this.to == 3) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=2;
            maquinas[2]=2;
            maquinas[3]=2;
        } else if (this.from == 2 && this.to == 4) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=2;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 2 && this.to == 5) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=3;
            maquinas[2]=1;
            maquinas[3]=1;
        } else if (this.from == 2 && this.to == 6) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=3;
            maquinas[2]=1;
            maquinas[3]=2;
        } else if (this.from == 2 && this.to == 9) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=3;
            maquinas[2]=1;
            maquinas[3]=3;
        } else if (this.from == 2 && this.to == 7) {
            //Choose maquinas to operate

        } else if (this.from == 2 && this.to == 8) {
            //Choose maquinas to operate

        }
        //tfs[0] = P3
        else if (this.from == 3 && this.to == 4) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=3;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 3 && this.to == 5) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=3;
            maquinas[2]=1;
            maquinas[3]=1;
        } else if (this.from == 3 && this.to == 6) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=1;
            maquinas[2]=2;
            maquinas[3]=2;
        } else if (this.from == 3 && this.to == 9) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=1;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 3 && this.to == 7) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=1;
            maquinas[2]=2;
            maquinas[3]=3;
        } else if (this.from == 3 && this.to == 8) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=1;
            maquinas[2]=2;
            maquinas[3]=1;
        }
        //tfs[0] = P4
        else if (this.from == 4 && this.to == 5) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=1;
            maquinas[3]=1;
        } else if (this.from == 4 && this.to == 6) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=2;
            maquinas[3]=2;
        } else if (this.from == 4 && this.to == 9) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 4 && this.to == 7) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=2;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 4 && this.to == 8) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=2;
            maquinas[2]=1;
            maquinas[3]=1;
        }
        //tfs[0] = P5
        else if (this.from == 5 && this.to == 6) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=2;
            maquinas[2]=2;
            maquinas[3]=2;
        } else if (this.from == 5 && this.to == 9) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=3;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 5 && this.to == 7) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=2;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 5 && this.to == 8) {
            //Choose maquinas to operate
            maquinas[0]=2;
            maquinas[1]=2;
            maquinas[2]=1;
            maquinas[3]=1;
        }
        //tfs[0] = P6
        else if (this.from == 6 && this.to == 7) {
            //Choose maquinas to operate
            maquinas[0]=3;
            maquinas[1]=3;
            maquinas[2]=3;
            maquinas[3]=3;
        } else if (this.from == 6 && this.to == 8) {
            //Choose maquinas to operate
            maquinas[0]=1;
            maquinas[1]=1;
            maquinas[2]=1;
            maquinas[3]=1;
        }

        return maquinas;
    }
}