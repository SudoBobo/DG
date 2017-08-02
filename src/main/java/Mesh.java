package main.java;

import java.util.List;

public class Mesh {
    public List<Triangle> triangles;

    public Mesh(List<Triangle> triangles) {
        this.triangles = triangles;
    }

    public Double[] getFineDataArray() {
        Double[] result = new Double[(triangles.size()) * 5];
        int j = 0;
        for (int i = 0; i < result.length; i++) {
            if (j == 5){
                j = 0;
            }
            result[i] = triangles.get(i).u.get(j);
            j++;
        }
        return result;
    }

    public Double[] getDataArray() {
        /*
        Из четырёх треугольников делаем одну точку для отображения. При маленьком размере одного треугольника
        должно получиться нормальное отображение.
        i - индекс позиции в выходном файле
        j - индекс переменной из вектора u внутри одного треугольника
        k - номер четвёрки треугольников
         */
        Double[] result = new Double[(triangles.size() / 4) * 5];
        int j = 0;
        int k = 0;
        for (int i = 0; i < result.length; i++) {
            if (j == 5) {
                j = 0;
                k++;
            }
            result[i] = (triangles.get(k).u.get(j) + triangles.get(k + 1).u.get(j) +
                    triangles.get(k + 2).u.get(j) + triangles.get(k + 3).u.get(j)) / 4;
            j++;
        }

        return result;
    }

    public Long[] getRawExtent(int xMin, int xMax, int yMin, int yMax, int fine) {
        long xExtent = (long)((xMax - xMin) / fine);
        long yExtent = (long)((yMax - yMin) / fine);
        long zExtent = 1;
        Long [] result = new Long[3];

        result[0] = xExtent;
        result[1] = yExtent;
        result[2] = zExtent;

        return new Long[] {xExtent, yExtent, zExtent};
    }
}

