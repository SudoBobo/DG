package com.github.sudobobo.IO;

import com.github.sudobobo.calculations.Value;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValueToWrite {
    Value associatedValue;
    double x;
    double y;
}



