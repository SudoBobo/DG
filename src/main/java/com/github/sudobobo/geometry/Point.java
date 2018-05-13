package com.github.sudobobo.geometry;

import lombok.Data;

public @Data
class Point {
    private final int id;

    public double x;
    public double y;
    public double z;
    private int borderIndex;

    public Point(int id, double[] coordinates, int borderIndex){
        assert coordinates.length == 3;
        this.id = id;
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
        this.borderIndex = borderIndex;
    }
    public static double distance(Point a, Point b) {
//        for (int i = 0; i < a.coordinates.length; i++) {
//            d += (a.coordinates[i] - b.coordinates[i]) * (a.coordinates[i] - b.coordinates[i]);
//        }
//        return Math.sqrt(d);

//        for (int i = 0; i < a.coordinates.length; i++) {
//            d += (a.coordinates[i] - b.coordinates[i]) * (a.coordinates[i] - b.coordinates[i]);
//        }
//
        double d = Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2) +
            Math.pow((a.z - b.z), 2);
        return Math.sqrt(d);
    }


    public long[] coordinates() {
        long [] c = new long[2];
        c[0] = (long)x;
        c[1] = (long)y;
        c[2] = (long)z;
        return c;
    }
}



