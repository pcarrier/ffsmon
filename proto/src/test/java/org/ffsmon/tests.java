package org.ffsmon;

import org.ffsmon.proto.Message;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class tests {
    public static void main(String[] args) {
        final MessagePack msgpack = new MessagePack();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Packer packer = msgpack.createPacker(out);

        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("foo", "baz");
        attrs.put("bar", 1);
        Message evt = new Message();
        evt.setUuid(UUID.randomUUID());
        evt.setAttributes(attrs);

        try {
            evt.pack(packer);
            System.out.println(String.format("%x", new BigInteger(1, out.toByteArray())));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
