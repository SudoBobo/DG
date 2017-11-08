package com.github.sudobobo.geometry;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data public class Domain {
    private final int index;
    private final double rho;
    private final double mu;
    private final double lambda;

    public static Domain[] createDomains(List<Map<String, Double>> domains) {

        Domain[] domainObjects = new Domain[domains.size()];
        for (int i = 0; i < domains.size(); i++) {
            Map <String, Double> d = domains.get(i);
            domainObjects[i] = new Domain(i, d.get("rho"), d.get("mu"), d.get("lambda"));
        }
        return domainObjects;
    }
}
