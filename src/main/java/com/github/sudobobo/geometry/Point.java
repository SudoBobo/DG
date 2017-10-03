package com.github.sudobobo.geometry;

import lombok.Data;

public @Data
class Point {
    private final int id;
    public final double [] coordinates;

    public Point(int id, double[] coordinates){
        assert coordinates.length == 2 : "only 2D case is implemented ";
        this.id = id;
        this.coordinates = coordinates;

    }
    public static double distance(Point a, Point b) {
        double d = 0;
        for (int i = 0; i < a.coordinates.length; i++) {
            d += (a.coordinates[i] - b.coordinates[i]) * (a.coordinates[i] - b.coordinates[i]);
        }
        return Math.sqrt(d);
    }
}



