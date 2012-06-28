package org.ffsmon.collectd;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.ffsmon.utils.UdpMessage;

import java.util.Arrays;

@Slf4j
public class Udp2SourcedCollectd {
    @Inject private @NonNull EventBus eventBus;

    @Subscribe public void convertUdpToCollectd(UdpMessage udpMessage) {
        final Message collectdMessage;
        try {
            collectdMessage = Message.parse(udpMessage.getContents());
            eventBus.post(new SourcedMessage(collectdMessage, udpMessage.getAddress(), udpMessage.getPort()));
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("{} {}", e.toString(), Arrays.toString(e.getStackTrace()));
        }
    }
}
