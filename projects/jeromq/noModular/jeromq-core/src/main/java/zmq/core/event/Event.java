package zmq.core.event;

import zmq.core.Ctx;
import zmq.core.Msg;
import zmq.core.SocketBase;
import zmq.core.ZMQ;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;

public class Event {
    private static final int VALUE_INTEGER = 1;
    private static final int VALUE_CHANNEL = 2;

    public final int event;
    public final String addr;
    public final Object arg;
    private final int flag;

    public Event(int event, String addr, Object arg) {
        this.event = event;
        this.addr = addr;
        this.arg = arg;
        if (arg instanceof Integer) {
            flag = VALUE_INTEGER;
        } else if (arg instanceof SelectableChannel) {
            flag = VALUE_CHANNEL;
        } else {
            flag = 0;
        }
    }

    private Event(int event, String addr, Object arg, int flag) {
        this.event = event;
        this.addr = addr;
        this.arg = arg;
        this.flag = flag;
    }

    public boolean write(SocketBase s) {
        Msg msg = new Msg(serialize(s.getCtx()));
        return s.send(msg, 0);
    }

    private ByteBuffer serialize(Ctx ctx) {
        int size = 4 + 1 + addr.length() + 1; // event + len(addr) + addr + flag
        if (flag == VALUE_INTEGER || flag == VALUE_CHANNEL) {
            size += 4;
        }

        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(event);
        buffer.put((byte) addr.length());
        buffer.put(addr.getBytes(ZMQ.CHARSET));
        buffer.put((byte) flag);
        if (flag == VALUE_INTEGER) {
            buffer.putInt((Integer) arg);
        } else if (flag == VALUE_CHANNEL) {
            int channeldId = ctx.forwardChannel((SelectableChannel) arg);
            buffer.putInt(channeldId);
        }
        buffer.flip();
        return buffer;
    }

    /**
     * Resolve the channel that was associated with this event.
     * Implementation note: to be backward compatible, {@link #arg} only store Integer value, so
     * the channel is resolved using this call.
     * <p>
     * Internally socket are kept using weak values, so it's better to retrieve the channel as early
     * as possible, otherwise it might get lost.
     *
     * @param socket the socket that send the event
     * @return the channel in the event, or null if was not a channel event.
     */
    public SelectableChannel getChannel(SocketBase socket) {
        return getChannel(socket.getCtx());
    }

    /**
     * Resolve the channel that was associated with this event.
     * Implementation note: to be backward compatible, {@link #arg} only store Integer value, so
     * the channel is resolved using this call.
     * <p>
     * Internally socket are kept using weak values, so it's better to retrieve the channel as early
     * as possible, otherwise it might get lost.
     *
     * @param ctx the socket that send the event
     * @return the channel in the event, or null if was not a channel event.
     */
    public SelectableChannel getChannel(Ctx ctx) {
        if (flag == VALUE_CHANNEL) {
            return ctx.getForwardedChannel((Integer) arg);
        } else {
            return null;
        }
    }

    public static Event read(SocketBase s, int flags) {
        Msg msg = s.recv(flags);
        if (msg == null) {
            return null;
        }

        ByteBuffer buffer = msg.buf();

        int event = buffer.getInt();
        int len = buffer.get();
        byte[] addr = new byte[len];
        buffer.get(addr);
        int flag = buffer.get();
        Object arg = null;

        if (flag == VALUE_INTEGER || flag == VALUE_CHANNEL) {
            arg = buffer.getInt();
        }

        return new Event(event, new String(addr, ZMQ.CHARSET), arg, flag);
    }

    public static Event read(SocketBase s) {
        return read(s, 0);
    }
}
