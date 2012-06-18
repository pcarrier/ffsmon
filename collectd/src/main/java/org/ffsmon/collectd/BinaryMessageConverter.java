package org.ffsmon.collectd;

import com.google.common.hash.Hasher;
import org.ffsmon.proto.Message;
import org.ffsmon.proto.MessageFormatException;
import org.ffsmon.proto.UUIDs;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.UUID;

public class BinaryMessageConverter {
    static final long NS_L_MULT = 1000000000L;
    static final BigInteger NS_BI_MULT = BigInteger.valueOf(NS_L_MULT);
    static final BigInteger ORIG_BI_DIV = BigInteger.valueOf(1073741824L);


    public static Message convert(BinaryMessage bmsg, InetAddress clientAddress, int clientPort) throws MessageFormatException {
        final Message message = new Message(generateID(bmsg), convertTimestamp(bmsg));
        message.setFrom(clientAddress.getCanonicalHostName().getBytes());

        return message;
    }

    private static byte[] generateID(BinaryMessage bmsg) {
        final Hasher hasher = UUIDs.Hash.newHasher();

        final Long hr = bmsg.getIntervalHR();
        if (hr != null)
            hasher.putLong(hr);
        else {
            final Long time = bmsg.getTime();
            if (time != null)
                hasher.putLong(time);
        }
        feedHasherByBytes(hasher, bmsg.getHost());
        feedHasherByBytes(hasher, bmsg.getPlugin());
        feedHasherByBytes(hasher, bmsg.getPluginInstance());
        feedHasherByBytes(hasher, bmsg.getType());
        feedHasherByBytes(hasher, bmsg.getTypeInstance());
        feedHasherByBytes(hasher, bmsg.getMessage());
        return hasher.hash().asBytes();
    }

    private static void feedHasherByBytes(Hasher h, byte[] bytes) {
        if(bytes != null) {
            h.putBoolean(true);
            h.putBytes(bytes);
        } else {
            h.putBoolean(false);
        }
    }

    private static long convertTimestamp(BinaryMessage bmsg) throws MessageFormatException {
        final Long hr = bmsg.getIntervalHR();
        if (hr != null)
            return BigInteger.valueOf(hr).multiply(NS_BI_MULT).divide(ORIG_BI_DIV).longValue();

        final Long time = bmsg.getTime();
        if (time != null)
            return NS_L_MULT * NS_L_MULT;

        throw new MessageFormatException();
    }
}
