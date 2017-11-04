package com.github.sudobobo.geometry;

import lombok.Data;

public @Data
class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Point a, Point b) {
        this(b.x - a.x, b.y - a.y, 0);
    }

    public static double mult2D(Vector a, Vector b) {
        return a.x * b.y - b.x * a.y;
    }

}
