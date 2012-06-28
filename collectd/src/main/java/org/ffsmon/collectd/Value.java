package org.ffsmon.collectd;

import lombok.Data;
import lombok.NonNull;

@Data
public class Value {
    @NonNull
    ValueType type;
    @NonNull Number value;
}
