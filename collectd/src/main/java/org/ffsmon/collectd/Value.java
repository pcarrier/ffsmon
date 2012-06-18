package org.ffsmon.collectd;

import lombok.Data;
import lombok.NonNull;

import java.awt.font.NumericShaper;

@Data
public class Value {
    @NonNull ValueType type;
    @NonNull Number value;
}
