package com.github.sudobobo.meshconstruction;

import lombok.Data;

import java.util.List;
import java.util.Map;

// MeshBorder represents one border type from config.yml
// - index: some_index
//   borderType: enclosed
// MeshBorder should be used only during mesh building
// After mesh building all information should be contained in
// Border objects.
@Data
public class MeshBorder {
    private final int index;
    private final String borderType;
    private static final String [] validBorderTypes = {"enclosed", "free",
                                                       "absorbing"};

    public MeshBorder(int index, String borderType) {

        boolean is_contained = false;
        for (String s: validBorderTypes){
            is_contained = (borderType.equals(s));
            if (is_contained) break;
        }
        assert (is_contained) :
            "An attempt to create MeshBorder with invalid borderType param " +
                borderType;

        this.index = index;
        this.borderType = borderType;
    }

    public static MeshBorder[] createMeshBorders(List<Map<String, Object>> borders, String defaultBorderType) {
        MeshBorder[] res = new MeshBorder[borders.size() + 1];
        for (int i = 0; i < borders.size(); i++) {
            Map <String, Object> b = borders.get(i);

            assert (b.get("index") != null);
            int index = (int) b.get("index");

            assert (b.get("borderType") != null);
            String borderType = (String) b.get("borderType");

            res[i] = new MeshBorder(index, borderType);
        }

        // default border case
        res[borders.size()] = new MeshBorder(-1, defaultBorderType);
        return res;
    }
}
