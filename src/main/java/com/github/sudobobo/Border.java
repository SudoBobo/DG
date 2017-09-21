package com.github.sudobobo;

public class Border {
    double nX;
    double nY;

    private Triangle neighbor;

    Border(double nX, double nY, Triangle neighbor) {
        this.nX = nX;
        this.nY = nY;
        this.neighbor = neighbor;
    }

    public double getnX() {
        return nX;
    }

    public double getnY() {
        return nY;
    }

    public Triangle getNeighbor() {
        return neighbor;
    }
}


