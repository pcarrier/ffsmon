package org.ffsmon.proto;

import lombok.NonNull;
import com.google.common.hash.*;
import java.nio.ByteBuffer;
import java.util.*;

public class UUIDs {
    static final public UUID ZERO = new UUID(0, 0);
    static final public HashFunction Hash = Hashing.murmur3_128();

    static @NonNull byte[] toByteArray (@NonNull UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    static @NonNull UUID fromByteArray (@NonNull byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    static @NonNull UUID fromUnorderedUUIDs (@NonNull UUID[] uuids) {
        return fromHashCode(Hashing.combineUnordered(hashCodes(uuids)));
    }

    static @NonNull UUID fromOrderedUUIDs (@NonNull UUID[] uuids) {
        return fromHashCode(Hashing.combineOrdered(hashCodes(uuids)));
    }


    static private @NonNull
    HashCode hash(@NonNull UUID uuid) {
        return HashCodes.fromBytes(UUIDs.toByteArray(uuid));
    }

    static private @NonNull UUID fromHashCode(@NonNull HashCode code) {
        ByteBuffer bb = ByteBuffer.wrap(code.asBytes());
        return new UUID(bb.getLong(), bb.getLong());
    }

    @NonNull
    static private List<HashCode> hashCodes(UUID[] uuids) {
        final List<HashCode> hashCodes = new LinkedList<HashCode>();
        for (UUID uuid: uuids)
            hashCodes.add(hash(uuid));
        return hashCodes;
    }
}
