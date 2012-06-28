package org.ffsmon.collectd;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.ffsmon.proto.MessageFormatException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SourcedCollectd2Ffs {
    static final long NS_L_MULT = 1000000000L;
    static final BigInteger NS_BI_MULT = BigInteger.valueOf(NS_L_MULT);
    static final BigInteger ORIG_BI_DIV = BigInteger.valueOf(1073741824L);

    @Inject private @NonNull EventBus eventBus;
    @Inject private static HashFunction hashFunction;

    public static org.ffsmon.proto.Message convert(SourcedMessage source) throws MessageFormatException {
        final Message collectdMessage = source.getMessage();
        final long id = generateID(collectdMessage);
        final long timestamp = convertTimestamp(collectdMessage);
        final HashMap<String, Object> payload = new HashMap<String, Object>();

        final org.ffsmon.proto.Message ffsMessage = new org.ffsmon.proto.Message(id, timestamp);

        putNullable(payload, "host", collectdMessage.getHost());
        putNullable(payload, "plugin", collectdMessage.getPlugin());
        putNullable(payload, "plugin_instance", collectdMessage.getPluginInstance());
        putNullable(payload, "type", collectdMessage.getType());
        putNullable(payload, "type_instance", collectdMessage.getTypeInstance());
        putNullable(payload, "time", collectdMessage.getTime());
        putNullable(payload, "time_hr", collectdMessage.getTimeHR());
        putNullable(payload, "interval_hr", collectdMessage.getIntervalHR());
        putNullable(payload, "severity", collectdMessage.getSeverity());
        putNullable(payload, "values", collectdMessage.getValues());
        putNullable(payload, "message", collectdMessage.getMessage());

        return ffsMessage;
    }

    private static long generateID(Message bmsg) {
        Hasher hasher = hashFunction.newHasher();
        final Long timeHR = bmsg.getTimeHR();
        final Long time = bmsg.getTime();

        if (timeHR != null)
            hasher.putLong(time);
        else if (time != null)
            hasher.putLong(time);

        feedHasherByNullableBytes(hasher, bmsg.getHost());
        feedHasherByNullableBytes(hasher, bmsg.getPlugin());
        feedHasherByNullableBytes(hasher, bmsg.getPluginInstance());
        feedHasherByNullableBytes(hasher, bmsg.getType());
        feedHasherByNullableBytes(hasher, bmsg.getTypeInstance());
        feedHasherByNullableBytes(hasher, bmsg.getMessage());

        return hasher.hash().asLong();
    }

    private static void feedHasherByNullableBytes(Hasher h, byte[] bytes) {
        if(bytes != null) {
            h.putBoolean(true);
            h.putBytes(bytes);
        } else {
            h.putBoolean(false);
        }
    }

    private static long convertTimestamp(Message bmsg) throws MessageFormatException {
        final Long hr = bmsg.getTimeHR();

        if (hr != null)
            return BigInteger.valueOf(hr).multiply(NS_BI_MULT).divide(ORIG_BI_DIV).longValue();

        final Long time = bmsg.getTime();
        if (time != null)
            return NS_L_MULT * NS_L_MULT;

        throw new MessageFormatException();
    }

    private static void putNullable(@NonNull Map map, @NonNull Object key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    @Subscribe
    public void SourcedCollectdToFfs(SourcedMessage sourcedMessage) {
        try {
            eventBus.post(convert(sourcedMessage));
        } catch (MessageFormatException e) {
            if (SourcedCollectd2Ffs.log.isErrorEnabled())
                SourcedCollectd2Ffs.log.error("{} {}", e.toString(), Arrays.toString(e.getStackTrace()));
        }
    }
}
