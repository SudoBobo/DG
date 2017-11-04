package com.github.sudobobo.geometry;

import lombok.Data;

public @Data
class Point {
    private final int id;

    public double x;
    public double y;

    public Point(int id, double[] coordinates){
        assert coordinates.length == 2 : "only 2D case is implemented ";
        this.id = id;
        this.x = coordinates[0];
        this.y = coordinates[1];

    }
    public static double distance(Point a, Point b) {
        double d = 0;
//        for (int i = 0; i < a.coordinates.length; i++) {
//            d += (a.coordinates[i] - b.coordinates[i]) * (a.coordinates[i] - b.coordinates[i]);
//        }
//        return Math.sqrt(d);

//        for (int i = 0; i < a.coordinates.length; i++) {
//            d += (a.coordinates[i] - b.coordinates[i]) * (a.coordinates[i] - b.coordinates[i]);
//        }
//
        d = Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2);
        return Math.sqrt(d);
    }


    public long[] coordinates() {
        long [] c = new long[2];
        c[0] = (long)x;
        c[1] = (long)y;
        return c;
    }
}



