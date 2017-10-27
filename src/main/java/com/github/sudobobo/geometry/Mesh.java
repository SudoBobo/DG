package com.github.sudobobo.geometry;

import lombok.Data;

import static java.lang.Double.min;

public @Data
class Mesh {
    private Triangle[] triangles;
    private Point[] points;

    public double getMinSideLength() {
        double minSideLength = 1000000;
        for (Triangle t : triangles){
            for (Border b : t.getBorders()){
                minSideLength = min(b.getS(), minSideLength);
            }
        }

        return minSideLength;
    }

    public Point getLTPoint() {

        double ltX = points[0].x();
        double ltY = points[0].y();

        for (Point p : points) {
            if (p.x() <= ltX) {
                if (p.y() >= ltY) {
                    ltX = p.x();
                    ltY = p.y();
                }
            }
        }

        return new Point(-1, new double[]{ltX, ltY});
    }

    public Point getRBPoint() {

        double rbX = points[0].x();
        double rbY = points[0].y();

        for (Point p : points) {
            if (p.x() > rbX) {
                if (p.y() < rbY) {
                    rbX = p.x();
                    rbY = p.y();
                }
            }
        }

        return new Point(-1, new double[]{rbX, rbY});
    }

    public int size() {
        return triangles.length;
    }

//    public Double[] getFineDataArray() {
//        Double[] result = new Double[(triangles.size()) * 5];
//        int j = 0;
//        for (int i = 0; i < result.length; i++) {
//            if (j == 5) {
//                j = 0;
//            }
//            result[i] = triangles.get(i).u.get(j);
//            j++;
//        }
//        return result;
//    }

//    public Double[] getDataArray() {
//        /*
//        Из четырёх треугольников делаем одну точку для отображения. При маленьком размере одного треугольника
//        должно получиться нормальное отображение.
//        i - индекс позиции в выходном файле
//        j - индекс переменной из вектора u внутри одного треугольника
//        k - номер первого треугольника из обрабатываемой четвёрки треугольников
//         */
//
//        // TODO check this madness
//        Double[] result = new Double[(triangles.size() / 4) * 5];
//
//        int j = 0;
//
//        int k = 0;
//        for (int i = 0; i < result.length; i++) {
//            if (j == 5) {
//                j = 0;
//                k += 4;
//            }
//            result[i] = (triangles.get(k).rowSum(j) + triangles.get(k + 1).rowSum(j) +
//                    triangles.get(k + 2).rowSum(j) + triangles.get(k + 3).rowSum(j)) / 4;
//            j++;
//        }
//
//        return result;
//    }

//    public Long[] getRawExtent(double xMin, double xMax, double yMin, double yMax, double fine) {
//        long xExtent = (long) Math.floor((xMax - xMin) / fine);
//        long yExtent = (long) Math.floor((yMax - yMin) / fine);
//        long zExtent = 1;
//
//        return new Long[]{xExtent, yExtent, zExtent};
//    }


//    public Mesh getCopy() {
//        List<Triangle> newTriangles = deepCopy(this.triangles);
//        return new Mesh(newTriangles);
//    }
//
//    private List<Triangle> deepCopy(List<Triangle> origTriangles){
//        assert (false)
//        List<Triangle> newTriangles = new ArrayList<>(origTriangles.size());
//
//        for(Triangle t : origTriangles) {
//            newTriangles.add(t.clone());
//        }
//
//        return newTriangles;
//    }


}
