package com.github.sudobobo;

import lombok.Data;

import java.util.List;
import java.util.Map;

public @Data class Configuration {
    private double realFullTime;
    private double spatialStep;
    private String pathToMeshFile;
    private Map<String, Object> initialCondition;
    private double spatialStepForNumericalIntegration;
    private String defaultBorderType;
    private List<Map<String, Object>> domains;
    private List<Map<String, Object>> borders;
    private String resName;
    private Map<String, Object> center;
}
