package org.ffsmon.collectd;

import lombok.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PartType {
    HOST(0x0000),
    TIME(0x0001),
    TIME_HR(0x0008),
    PLUGIN(0x0002),
    PLUGIN_INSTANCE(0x0003),
    TYPE(0x0004),
    TYPE_INSTANCE(0x0005),
    VALUES(0x0006),
    INTERVAL(0x0007),
    INTERVAL_HR(0x0009),
    MESSAGE(0x0100),
    SEVERITY(0x0101),
    SIGNATURE(0x0200),
    ENCRYPTION(0x0210);

    @EnumId
    @Getter
    private final int code;
}
