package com.github.sudobobo.calculations;

public class SinInitialConditionPhase implements InitialConditionPhase {

    private double a;
    private double phi;

    private double xWidth;
    private double yWidth;

    private double initialXCenter;
    private double initialYCenter;


    public SinInitialConditionPhase(double a, double phi, double xWidth, double yWidth, double initialXCenter, double initialYCenter) {
        this.a = a;
        this.phi = phi;
        this.xWidth = xWidth;
        this.yWidth = yWidth;
        this.initialXCenter = initialXCenter;
        this.initialYCenter = initialYCenter;
    }

    @Override
    public double calc(double x, double y) {

        // TODO memorize this
        // TODO will not work on the border

        boolean is_x_inside = ((initialXCenter - xWidth) <= x) && (x <= (initialXCenter + xWidth));
        boolean is_y_inside = ((initialYCenter - yWidth) <= y) && (y <= (initialYCenter + yWidth));

        if (is_x_inside && is_y_inside) {
            return Math.cos(a * x + phi);
        } else {
            return 0.0;
        }
    }
}