package com.github.sudobobo.meshconstruction;

import lombok.Data;

@Data
public class InitialConditionConfig {
    private String profile;
    private double width;
    private double amplitude;
    private double dX;
    private double dY;
    private double dZ;
    private double [] center;

    public InitialConditionConfig(String profile, double width, double amplitude,
                                  double dX, double dY, double dZ, double[] center) {
        this.profile = profile;
        this.width = width;
        this.amplitude = amplitude;

        double m = Math.sqrt(dX*dX + dY*dY + dZ*dZ);

        this.dX = dX / m;
        this.dY = dY / m;
        this.dZ = dZ / m;
        this.center = center;
    }
}
