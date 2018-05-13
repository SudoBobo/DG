package com.github.sudobobo.calculations;

import com.github.sudobobo.meshconstruction.InitialConditionConfig;
import org.jblas.DoubleMatrix;

public class CosInitialConditionPhase implements InitialConditionPhase {
    private double width;
    private double center[];

    // direction
    private DoubleMatrix d;

    public CosInitialConditionPhase(InitialConditionConfig initialConditionConfig) {
        this.width = initialConditionConfig.getWidth();
        this.center = initialConditionConfig.getCenter();
        this.d = new DoubleMatrix(1, 3);

        double m = Math.sqrt(
            initialConditionConfig.getDX() * initialConditionConfig.getDX() +
            initialConditionConfig.getDY() * initialConditionConfig.getDY() +
            initialConditionConfig.getDZ() * initialConditionConfig.getDZ());

        d.put(0, 0, initialConditionConfig.getDX() / m);
        d.put(0, 1, initialConditionConfig.getDY() / m);
        d.put(0, 2, initialConditionConfig.getDZ() / m);
    }

    @Override
    public double calc(double x, double y, double z) {

//        // TODO memorize this
//        // TODO will not work on the border
//
//        boolean is_x_inside = ((initialXCenter - xWidth) <= x) && (x <= (initialXCenter + xWidth));
//        boolean is_y_inside = ((initialYCenter - yWidth) <= y) && (y <= (initialYCenter + yWidth));
//
//        if (is_x_inside && is_y_inside) {
//            return Math.cos(a * x + phi);
//        } else {
//            return 0.0;
//        }
//    }

        // Taken from Denis

//        double width = 50.0;
//        if (x < 25 && x > -25) {
////            return Math.cos(Math.PI * (Math.abs(x) - initialXCenter) / width);
//            return Math.cos(Math.PI * (x - initialXCenter) / width);
//
//        } else {
//            return 0.0;
//        }


        // from center to current point
        DoubleMatrix a = new DoubleMatrix(3, 1);
        a.put(0, 0, x - center[0]);
        a.put(1, 0, y - center[1]);
        a.put(2, 0, z - center[2]);

        double coordinate = a.dot(d);

        double min = - width / 2;
        double max =   width / 2;
        if (min > coordinate || max < coordinate) {
            return 0;
        }
        return Math.cos(Math.PI * coordinate / width);
    }
}