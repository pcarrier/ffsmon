package org.ffsmon.collectd;

import lombok.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Severity {
    FAILURE(0x01),
    WARNING(0x02),
    OKAY(0x04);

    @EnumId @Getter private final int code;
}
