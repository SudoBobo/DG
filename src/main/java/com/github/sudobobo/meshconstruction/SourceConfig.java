package com.github.sudobobo.meshconstruction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SourceConfig {
    private String name;
    private String profile;
    private double amplitude;
    private double[] vector;
    private double[] point;
    private double[] size;
    private double omega;

    public static SourceConfig[] createSourceConfigs(List<Map<String, Object>> sources) {
        SourceConfig[] res = new SourceConfig[sources.size()];
        int i = 0;
        for (Map<String, Object> s: sources){
            assert (s.get("name") != null);
            assert (s.get("profile") != null);
            assert (s.get("amplitude") != null);

            Map<String, Object> vector = (Map<String, Object>) s.get("vector");
            assert (vector != null);
            Map<String, Object> point = (Map<String, Object>) s.get("point");
            assert (point != null);
            Map<String, Object> size = (Map<String, Object>) s.get("size");
            assert (size != null);

            assert (s.get("omega") != null);

            res[i] = new SourceConfig(
                (String) s.get("name"),
                (String) s.get("profile"),
                (double) s.get("amplitude"),
                new double[]{
                    (double)((Map<String, Object>) s.get("vector")).get("x"),
                    (double)((Map<String, Object>) s.get("vector")).get("y"),
                    (double)((Map<String, Object>) s.get("vector")).get("z")
                },
                new double[]{
                    (double)((Map<String, Object>) s.get("point")).get("x"),
                    (double)((Map<String, Object>) s.get("point")).get("y"),
                    (double)((Map<String, Object>) s.get("point")).get("z")

                },
                new double[]{
                    (double)((Map<String, Object>) s.get("size")).get("x"),
                    (double)((Map<String, Object>) s.get("size")).get("y"),
                    (double)((Map<String, Object>) s.get("size")).get("z")
                },
                (double) s.get("omega")
            );
            i++;
        }
        return res;
    }
}
