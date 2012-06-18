package org.ffsmon.utils;

import lombok.NonNull;
import lombok.Synchronized;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class UdpSocket extends DatagramSocket {
    /* max collectd message size by spec ("maximum payload size when using UDP/IPv6 over Ethernet") */
    final static int maxSize = 1452;
    final byte[] buffer = new byte[maxSize];

    public UdpSocket(int port) throws SocketException {
        super(port);
    }

    @Synchronized @NonNull
    protected UdpMessage getPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, maxSize);
        this.receive(packet);
        final InetAddress address = packet.getAddress();
        final int port = packet.getPort();
        final ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        return new UdpMessage(address, port, bb);
    }

    protected void finalize() {
        this.close();
    }
}