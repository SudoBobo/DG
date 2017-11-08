package com.github.sudobobo;

import lombok.Data;

import java.util.List;
import java.util.Map;

public @Data class Configuration {
    private double realFullTime;
    private double spatialStep;
    private String pathToMeshFile;
    private String initialCondition;
    private double spatialStepForNumericalIntegration;
    private List<Map<String, Double>> domains;
}
