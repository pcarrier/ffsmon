package org.ffsmon.collectd;

import lombok.extern.slf4j.Slf4j;
import org.ffsmon.utils.UdpMessage;
import org.ffsmon.utils.UdpSocket;

import java.io.IOException;
import java.net.SocketException;

@Slf4j
public class CollectdServer extends UdpSocket {
    static final int STANDARD_PORT = 25826;
    boolean shouldContinue = true;

    void stop() {
        shouldContinue = false;
    }

    CollectdServer(int port) throws SocketException {
        super(port);
    }

    void loop() {
        while(shouldContinue) {
            try {
                final BinaryMessage binaryMessage = grabMessage();
                System.out.println(binaryMessage);
            } catch (IOException ignored) {
                // TODO
            }
        }
    }

    BinaryMessage grabMessage() throws IOException {
        final UdpMessage packet = this.getPacket();
        return BinaryMessage.parse(packet.getContents());
    }
}
