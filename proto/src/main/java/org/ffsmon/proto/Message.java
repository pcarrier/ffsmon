package org.ffsmon.proto;

import lombok.Data;
import lombok.NonNull;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

@Data
public class Message {
    static private final int VERSION = 0;

    @NonNull private long timestamp;
    @NonNull private long id;

    static final int ITEMS_NR = 4; /* number of items */

    private Object payload;

    public void pack(Packer packer) throws IOException {
        packer.writeArrayBegin(8);
        packer.write(VERSION);
        packer.write(timestamp);
        packer.write(id);
        packer.write(payload);
        packer.writeArrayEnd(false);
    }

    static Message unpack(Unpacker unpacker) throws IOException {
        if (unpacker.readArrayBegin() != ITEMS_NR) /* wrong size */
            throw new MessageFormatException();

        if (unpacker.readShort() != VERSION) /* wrong version */
            throw new MessageFormatException();

        final long timestamp = unpacker.readLong();
        final long  id = unpacker.readLong();
        final Object payload = unpacker.read(Object.class);

        unpacker.readArrayEnd();

        Message evt = new Message(timestamp, id);
        if (payload != null)
            evt.setPayload(payload);

        return evt;
    }
}
