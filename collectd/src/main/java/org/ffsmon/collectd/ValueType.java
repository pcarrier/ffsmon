package org.ffsmon.collectd;

import lombok.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ValueType {
    COUNTER(0x00),
    GAUGE(0x01),
    DERIVE(0x02),
    ABSOLUTE(0x03);

    @EnumId @Getter
    private final int code;
}
