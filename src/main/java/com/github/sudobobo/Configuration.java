package com.github.sudobobo;

import lombok.Data;

public @Data class Configuration {
    private double realFullTime;
    private double spatialStep;
    private String pathToMeshFile;
    private String initialCondition;
    private double spatialStepForNumericalIntegration;
}
