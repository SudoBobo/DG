package com.github.sudobobo.geometry;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data public class Domain {
    private final int index;
    private final double rho;
    private final double mu;
    private final double lambda;
//    private final String borderType;

    public static Domain[] createDomains(List<Map<String, Object>> domains) {
        Domain[] domainObjects = new Domain[domains.size()];
        for (int i = 0; i < domains.size(); i++) {
            Map <String, Object> d = domains.get(i);

            assert(d.get("index") != null);
            int index = (int) d.get("index");

            assert(d.get("rho") != null);
            double rho = (double) d.get("rho");

            assert(d.get("mu") != null);
            double mu = (double) d.get("mu");

            assert(d.get("lambda") != null);
            double lambda = (double) d.get("lambda");

//            assert(d.get("borderType") != null);
//            String borderType = (String) d.get("borderType");

//            domainObjects[i] = new Domain(index, rho, mu,
//                    lambda, borderType);
            domainObjects[i] = new Domain(index, rho, mu,
                lambda);
        }
        return domainObjects;
    }
}
