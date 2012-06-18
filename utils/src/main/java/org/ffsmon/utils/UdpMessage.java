package org.ffsmon.utils;

import lombok.Data;
import lombok.NonNull;
import java.net.InetAddress;
import java.nio.ByteBuffer;

@Data
public class UdpMessage {
    @NonNull private InetAddress address;
    @NonNull private int port;
    @NonNull ByteBuffer contents;
}
