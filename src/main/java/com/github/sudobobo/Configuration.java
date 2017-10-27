package com.github.sudobobo;

import lombok.Data;

public @Data class Configuration {
    private Double realFullTime;
    private Double spatialStep;
    private String pathToMeshFile;
    public String initialCondition;
}
