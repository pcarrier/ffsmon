package org.ffsmon.collectd;

import lombok.Data;
import lombok.NonNull;
import org.ffsmon.collectd.Message;

import java.net.InetAddress;

@Data
public class SourcedMessage {
    @NonNull
    Message message;
    @NonNull InetAddress sourceAddress;
    @NonNull Integer sourcePort;
}
