package com.github.sudobobo.calculations;

public class CosInitialConditionPhase implements InitialConditionPhase {

    private double a;
    private double phi;

    private double xWidth;
    private double yWidth;

    private double initialXCenter;
    private double initialYCenter;


    public CosInitialConditionPhase(double a, double phi, double xWidth, double yWidth, double initialXCenter, double initialYCenter) {
        this.a = a;
        this.phi = phi;
        this.xWidth = xWidth;
        this.yWidth = yWidth;
        this.initialXCenter = initialXCenter;
        this.initialYCenter = initialYCenter;
        System.out.println("Initial condition is hardcoded with fixed width!");

    }

    @Override
    public double calc(double x, double y) {

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

        double width = 50.0;
        if (x < 25 && x > -25) {
//            return Math.cos(Math.PI * (Math.abs(x) - initialXCenter) / width);
            return Math.cos(Math.PI * (x - initialXCenter) / width);

        } else {
            return 0.0;
        }

//        d = d.norm();
//        Vector a = new Vector(new Point(-1, centerPoint), new Point(-1, currentPoint));
//        float coordinate = a.scalar(d) / 1.0f;//1.0 == module d ->norm
//        float center = 0;
//
//        float min = center - width[0] / 2;
//        float max = center + width[0] / 2;
//        if (min > coordinate || max < coordinate) {
//            return 0;
//        }
//        return (float) Math.cos(Math.PI * (coordinate - center) / width[0]);
    }
}