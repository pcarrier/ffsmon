package org.ffsmon.utils;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Slf4j
public class UdpServer extends DatagramSocket {
    /* max collectd message size by spec ("maximum payload size when using UDP/IPv6 over Ethernet") */
    private static final int maxSize = 1452;

    private final byte[] buffer = new byte[maxSize];
    private boolean shouldContinue = true;

    @Inject private @NonNull EventBus eventBus;

    public UdpServer(int port) throws SocketException {
        super(port);
    }

    void stop() {
        log.info("asked to stop the UDP recv loop");
        shouldContinue = false;
    }

    void loop() {
        log.info("firing up UDP recv loop");
        while(shouldContinue) {
            try {
                eventBus.post(this.getPacket());
            } catch (IOException e) {
                if (log.isWarnEnabled())
                    log.warn("{} {}", e.toString(), Arrays.toString(e.getStackTrace()));
            }
        }
        log.info("UDP recv loop stopped");
    }

    @Synchronized @NonNull
    private UdpMessage getPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, maxSize);
        this.receive(packet);
        final InetAddress address = packet.getAddress();
        final int port = packet.getPort();
        final ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        return new UdpMessage(address, port, bb);
    }
}
