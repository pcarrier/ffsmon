package org.ffsmon.proto;

import lombok.Data;
import lombok.NonNull;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.UUID;

@org.msgpack.annotation.Message
@Data
public class Message {
    static private final int VERSION = 0;

    @NonNull private byte[] id;

    @NonNull private long timestamp;

    private byte[] type, from, to, name;

    private Object payload;

    public UUID getUuid() {
        return UUIDs.fromByteArray(id);
    }

    public void setUuid(UUID uuid) {
        this.id = UUIDs.toByteArray(uuid);
    }

    public void pack(Packer packer) throws IOException {
        packer.writeArrayBegin(8);
        packer.write(VERSION);
        packer.write(id);
        packer.write(timestamp);
        packer.write(type);
        packer.write(from);
        packer.write(to);
        packer.write(name);
        packer.write(payload);
        packer.writeArrayEnd(false);
    }

    static Message unpack(Unpacker unpacker) throws IOException {
        if (unpacker.readArrayBegin() != 8) /* wrong size */
            throw new MessageFormatException();

        if (unpacker.readShort() != 0) /* wrong version */
            throw new MessageFormatException();

        final byte[] uuid = unpacker.readByteArray();
        final long timestamp = unpacker.readLong();
        final byte[] type = unpacker.readByteArray();
        final byte[] from = unpacker.readByteArray();
        final byte[] to = unpacker.readByteArray();
        final byte[] name = unpacker.readByteArray();
        final Object payload = unpacker.read(Object.class);

        unpacker.readArrayEnd();

        Message evt = new Message(uuid, timestamp);
        if (type != null)
            evt.setType(type);
        if (from != null)
            evt.setFrom(from);
        if (to != null)
            evt.setTo(to);
        if (name != null)
            evt.setName(name);
        if (payload != null)
            evt.setPayload(payload);

        return evt;
    }
}
