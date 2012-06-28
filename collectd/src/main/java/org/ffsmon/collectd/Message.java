package org.ffsmon.collectd;

import lombok.Data;
import lombok.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Data
public class Message {
    private byte[] host, plugin, pluginInstance, type, typeInstance, message;
    private Long time, timeHR, interval, intervalHR;
    private Severity severity;
    private List<Value> values;

    public static Message parse(ByteBuffer buffer) throws UnsupportedTypeException, InvalidFormatException {
        Message res = new Message();

        buffer.order(ByteOrder.BIG_ENDIAN);

        while(buffer.hasRemaining()) {
            PartType type = PartType.findByCode(buffer.getShort());

            switch (type) {
                case HOST:
                    res.setHost(getBinaryString(buffer));
                    break;
                case TIME:
                    res.setTime(getNumeric(buffer));
                    break;
                case TIME_HR:
                    res.setTimeHR(getNumeric(buffer));
                    break;
                case INTERVAL:
                    res.setInterval(getNumeric(buffer));
                    break;
                case INTERVAL_HR:
                    res.setIntervalHR(getNumeric(buffer));
                    break;
                case SEVERITY:
                    res.setSeverity(Severity.findByCode((short)getNumeric(buffer)));
                    break;
                case PLUGIN:
                    res.setPlugin(getBinaryString(buffer));
                    break;
                case PLUGIN_INSTANCE:
                    res.setPluginInstance(getBinaryString(buffer));
                    break;
                case TYPE:
                    res.setType(getBinaryString(buffer));
                    break;
                case TYPE_INSTANCE:
                    res.setTypeInstance(getBinaryString(buffer));
                    break;
                case VALUES:
                    res.setValues(parseValues(buffer));
                    break;
                case MESSAGE:
                    res.setMessage(getBinaryString(buffer));
                    break;
                case SIGNATURE:
                case ENCRYPTION:
                default:
                    throw new UnsupportedTypeException();
            }
        }

        return res;
    }

    private static List<Value> parseValues(ByteBuffer buffer) throws InvalidFormatException {
        final int length = getLength(buffer);
        final int valuesNum = buffer.getShort();
        if (length + 6 != valuesNum * 9)
            throw new InvalidFormatException();

        final List<Value> values = new ArrayList<Value>(valuesNum);

        for (int entry = 0; entry < valuesNum; entry++) {
            ValueType type = ValueType.findByCode(buffer.getChar());
            switch (type) {
                case COUNTER:
                    values.add(new Value(ValueType.COUNTER, buffer.getLong()));
                    break;
                case GAUGE:
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    values.add(new Value(ValueType.GAUGE, buffer.getDouble()));
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    break;
                case DERIVE:
                    values.add(new Value(ValueType.DERIVE, buffer.getLong()));
                    break;
                case ABSOLUTE:
                    values.add(new Value(ValueType.ABSOLUTE, buffer.getLong()));
                    break;
            }
        }
        return values;
    }

    private static byte[] getBinaryString(ByteBuffer buffer) {
        final int length = getLength(buffer);
        final byte[] string = new byte[length];
        buffer.get(string);
        return string;
    }

    private static @NonNull int getLength(ByteBuffer buffer) {
        return (buffer.getShort() - 4);
    }

    private static @NonNull long getNumeric(ByteBuffer buffer) throws InvalidFormatException {
        if (getLength(buffer) != 8)
            throw new InvalidFormatException();
        return buffer.getLong();
    }
}
