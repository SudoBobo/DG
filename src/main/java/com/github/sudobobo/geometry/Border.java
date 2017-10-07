package com.github.sudobobo.geometry;

import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

public @Data @Builder
class Border {
    // numeration of vertexes are important
    // edge 'starts' from beginPoint and 'ends' in endPoint

    private Point beginPoint;
    private Point endPoint;

    private boolean isEdgeOfMesh;

    private int borderNumber;
    private double S;

    private DoubleMatrix T;
    private DoubleMatrix TInv;

    private double [] outerNormal;

    private Triangle neighborTriangle;
    private Border neighborBorder;

    public static boolean doBordersPointsMatch(Border b, Border potentialNeibBorder) {

        return ((b.getBeginPoint().equals(potentialNeibBorder.getBeginPoint()) &&
                (b.getEndPoint().equals(potentialNeibBorder.getEndPoint())))
                ||
                ((b.getBeginPoint().equals(potentialNeibBorder.getEndPoint()) &&
                        (b.getEndPoint().equals(potentialNeibBorder.getBeginPoint())))));
    }
}
