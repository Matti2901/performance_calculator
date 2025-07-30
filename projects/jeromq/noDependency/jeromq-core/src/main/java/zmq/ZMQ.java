package zmq;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import zmq.io.Metadata;
import zmq.util.Utils;

public class ZMQ
{
    /******************************************************************************/
    /*  0MQ versioning support.                                                   */

    /******************************************************************************/
    /*  0MQ socket definition.                                                    */

    /******************************************************************************/
    /*  0MQ socket events and monitoring                                          */

    // Default values for options


    /**
     * An interface used to consume events in monitor
     */
    public interface EventConsummer
    {
        void consume(Event ev);

        /**
         * An optional method to close the monitor if needed
         */
        default void close()
        {
            // Default do nothing
        }
    }

    public static class Event
    {
        private static final int VALUE_INTEGER = 1;
        private static final int VALUE_CHANNEL = 2;

        public final int    event;
        public final String addr;
        public final Object arg;
        private final int   flag;

        public Event(int event, String addr, Object arg)
        {
            this.event = event;
            this.addr = addr;
            this.arg = arg;
            if (arg instanceof Integer) {
                flag = VALUE_INTEGER;
            }
            else if (arg instanceof SelectableChannel) {
                flag = VALUE_CHANNEL;
            }
            else {
                flag = 0;
            }
        }

        private Event(int event, String addr, Object arg, int flag)
        {
            this.event = event;
            this.addr = addr;
            this.arg = arg;
            this.flag = flag;
        }

        public boolean write(SocketBase s)
        {
            Msg msg = new Msg(serialize(s.getCtx()));
            return s.send(msg, 0);
        }

        private ByteBuffer serialize(Ctx ctx)
        {
            int size = 4 + 1 + addr.length() + 1; // event + len(addr) + addr + flag
            if (flag == VALUE_INTEGER || flag == VALUE_CHANNEL) {
                size += 4;
            }

            ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
            buffer.putInt(event);
            buffer.put((byte) addr.length());
            buffer.put(addr.getBytes(ZMQUtilConstant.CHARSET));
            buffer.put((byte) flag);
            if (flag == VALUE_INTEGER) {
                buffer.putInt((Integer) arg);
            }
            else if (flag == VALUE_CHANNEL) {
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
        public SelectableChannel getChannel(SocketBase socket)
        {
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
        public SelectableChannel getChannel(Ctx ctx)
        {
            if (flag == VALUE_CHANNEL) {
                return ctx.getForwardedChannel((Integer) arg);
            }
            else {
                return null;
            }
        }

        public static Event read(SocketBase s, int flags)
        {
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

            return new Event(event, new String(addr, ZMQUtilConstant.CHARSET), arg, flag);
        }

        public static Event read(SocketBase s)
        {
            return read(s, 0);
        }
    }

    //  New context API
    public static Ctx createContext()
    {
        //  Create 0MQ context.
        return new Ctx();
    }

    private static void checkContext(Ctx ctx)
    {
        if (ctx == null || !ctx.isActive()) {
            throw new IllegalStateException();
        }
    }

    private static void destroyContext(Ctx ctx)
    {
        checkContext(ctx);
        ctx.terminate();
    }

    public static void setContextOption(Ctx ctx, int option, int optval)
    {
        checkContext(ctx);
        ctx.set(option, optval);
    }

    public static int getContextOption(Ctx ctx, int option)
    {
        checkContext(ctx);
        return ctx.get(option);
    }

    //  Stable/legacy context API
    public static Ctx init(int ioThreads)
    {
        Utils.checkArgument(ioThreads >= 0, "I/O threads must not be negative");
        Ctx ctx = createContext();
        setContextOption(ctx, ZMQUtilConstant.ZMQ_IO_THREADS, ioThreads);
        return ctx;
    }

    public static void term(Ctx ctx)
    {
        destroyContext(ctx);
    }

    // Sockets
    public static SocketBase socket(Ctx ctx, int type)
    {
        checkContext(ctx);
        return ctx.createSocket(type);
    }

    private static void checkSocket(SocketBase s)
    {
        if (s == null || !s.isActive()) {
            throw new IllegalStateException();
        }
    }

    public static void closeZeroLinger(SocketBase s)
    {
        checkSocket(s);
        s.setSocketOpt(ZMQUtilConstant.ZMQ_LINGER, 0);
        s.close();
    }

    public static void close(SocketBase s)
    {
        checkSocket(s);
        s.close();
    }

    public static boolean setSocketOption(SocketBase s, int option, Object optval)
    {
        checkSocket(s);
        return s.setSocketOpt(option, optval);
    }

    public static Object getSocketOptionExt(SocketBase s, int option)
    {
        checkSocket(s);
        return s.getSocketOptx(option);
    }

    public static int getSocketOption(SocketBase s, int opt)
    {
        return s.getSocketOpt(opt);
    }

    public static boolean monitorSocket(SocketBase s, final String addr, int events)
    {
        checkSocket(s);

        return s.monitor(addr, events);
    }

    public static boolean bind(SocketBase s, final String addr)
    {
        checkSocket(s);

        return s.bind(addr);
    }

    public static boolean connect(SocketBase s, String addr)
    {
        checkSocket(s);
        return s.connect(addr);
    }

    public static int connectPeer(SocketBase s, String addr)
    {
        checkSocket(s);
        return s.connectPeer(addr);
    }

    public static boolean disconnectPeer(SocketBase s, int routingId)
    {
        checkSocket(s);
        return s.disconnectPeer(routingId);
    }

    public static boolean unbind(SocketBase s, String addr)
    {
        checkSocket(s);
        return s.termEndpoint(addr);
    }

    public static boolean disconnect(SocketBase s, String addr)
    {
        checkSocket(s);
        return s.termEndpoint(addr);
    }

    // Sending functions.
    public static int send(SocketBase s, String str, int flags)
    {
        byte[] data = str.getBytes(ZMQUtilConstant.CHARSET);
        return send(s, data, data.length, flags);
    }

    public static int send(SocketBase s, Msg msg, int flags)
    {
        int rc = sendMsg(s, msg, flags);
        if (rc < 0) {
            return -1;
        }

        return rc;
    }

    public static int send(SocketBase s, byte[] buf, int flags)
    {
        return send(s, buf, buf.length, flags);
    }

    public static int send(SocketBase s, byte[] buf, int len, int flags)
    {
        checkSocket(s);

        Msg msg = new Msg(len);
        msg.put(buf, 0, len);

        int rc = sendMsg(s, msg, flags);
        if (rc < 0) {
            return -1;
        }

        return rc;
    }

    // Send multiple messages.
    //
    // If flag bit ZMQ_SNDMORE is set the vector is treated as
    // a single multi-part message, i.e. the last message has
    // ZMQ_SNDMORE bit switched off.
    //
    public int sendiov(SocketBase s, byte[][] a, int count, int flags)
    {
        checkSocket(s);
        int rc = 0;
        Msg msg;

        for (int i = 0; i < count; ++i) {
            msg = new Msg(a[i]);
            if (i == count - 1) {
                flags = flags & ~ZMQUtilConstant.ZMQ_SNDMORE;
            }
            rc = sendMsg(s, msg, flags);
            if (rc < 0) {
                rc = -1;
                break;
            }
        }
        return rc;

    }

    public static boolean sendMsg(SocketBase socket, byte[]... data)
    {
        int rc;
        if (data.length == 0) {
            return false;
        }
        for (int idx = 0; idx < data.length - 1; ++idx) {
            rc = send(socket, new Msg(data[idx]), ZMQUtilConstant.ZMQ_MORE);
            if (rc < 0) {
                return false;
            }
        }
        rc = send(socket, new Msg(data[data.length - 1]), 0);
        return rc >= 0;
    }

    public static int sendMsg(SocketBase s, Msg msg, int flags)
    {
        int sz = msgSize(msg);
        boolean rc = s.send(msg, flags);
        if (!rc) {
            return -1;
        }
        return sz;
    }

    // Receiving functions.
    public static Msg recv(SocketBase s, int flags)
    {
        checkSocket(s);
        return recvMsg(s, flags);
    }

    // Receive a multi-part message
    //
    // Receives up to *count_ parts of a multi-part message.
    // Sets *count_ to the actual number of parts read.
    // ZMQ_RCVMORE is set to indicate if a complete multi-part message was read.
    // Returns number of message parts read, or -1 on error.
    //
    // Note: even if -1 is returned, some parts of the message
    // may have been read. Therefore the client must consult
    // *count_ to retrieve message parts successfully read,
    // even if -1 is returned.
    //
    // The iov_base* buffers of each iovec *a_ filled in by this
    // function may be freed using free().
    //
    // Implementation note: We assume zmq::msg_t buffer allocated
    // by zmq::recvmsg can be freed by free().
    // We assume it is safe to steal these buffers by simply
    // not closing the zmq::msg_t.
    //
    public int recviov(SocketBase s, byte[][] a, int count, int flags)
    {
        checkSocket(s);

        int nread = 0;
        boolean recvmore = true;

        for (int i = 0; recvmore && i < count; ++i) {
            // Cheat! We never close any msg
            // because we want to steal the buffer.
            Msg msg = recvMsg(s, flags);
            if (msg == null) {
                nread = -1;
                break;
            }

            // Cheat: acquire zmq_msg buffer.
            a[i] = msg.data();

            // Assume zmq_socket ZMQ_RVCMORE is properly set.
            recvmore = msg.hasMore();
        }
        return nread;
    }

    public static Msg recvMsg(SocketBase s, int flags)
    {
        return s.recv(flags);
    }

    public static boolean join(SocketBase s, String group)
    {
        checkSocket(s);
        return s.join(group);
    }

    public static boolean leave(SocketBase s, String group)
    {
        checkSocket(s);
        return s.leave(group);
    }

    public static Msg msgInit()
    {
        return new Msg();
    }

    public static Msg msgInitWithSize(int messageSize)
    {
        return new Msg(messageSize);
    }

    public static int msgSize(Msg msg)
    {
        return msg.size();
    }

    public static int getMessageOption(Msg msg, int option)
    {
        switch (option) {
        case ZMQUtilConstant.ZMQ_MORE:
            return msg.hasMore() ? 1 : 0;
        default:
            throw new IllegalArgumentException();
        }
    }

    //  Get message metadata string
    public static String getMessageMetadata(Msg msg, String property)
    {
        String data = null;
        Metadata metadata = msg.getMetadata();
        if (metadata != null) {
            data = metadata.get(property);
        }
        return data;
    }

    //  Set routing id on a message sent over SERVER socket type
    public boolean setMessageRoutingId(Msg msg, int routingId)
    {
        return msg.setRoutingId(routingId);
    }

    //  Get the routing id of a message that came from SERVER socket type
    public int getMessageRoutingId(Msg msg)
    {
        return msg.getRoutingId();
    }

    public boolean setMessageGroup(Msg msg, String group)
    {
        return msg.setGroup(group);
    }

    public String getMessageGroup(Msg msg)
    {
        return msg.getGroup();
    }

    public static void sleep(long seconds)
    {
        sleep(seconds, TimeUnit.SECONDS);
    }

    public static void msleep(long milliseconds)
    {
        sleep(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static void sleep(long amount, TimeUnit unit)
    {
        LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(amount, unit));
    }

    //  The proxy functionality
    public static boolean proxy(SocketBase frontend, SocketBase backend, SocketBase capture)
    {
        Utils.checkArgument(frontend != null, "Frontend socket has to be present for proxy");
        Utils.checkArgument(backend != null, "Backend socket has to be present for proxy");
        return Proxy.proxy(frontend, backend, capture, null);
    }

    public static boolean proxy(SocketBase frontend, SocketBase backend, SocketBase capture, SocketBase control)
    {
        Utils.checkArgument(frontend != null, "Frontend socket has to be present for proxy");
        Utils.checkArgument(backend != null, "Backend socket has to be present for proxy");
        return Proxy.proxy(frontend, backend, capture, control);
    }

    public static boolean device(int device, SocketBase frontend, SocketBase backend)
    {
        Utils.checkArgument(frontend != null, "Frontend socket has to be present for proxy");
        Utils.checkArgument(backend != null, "Backend socket has to be present for proxy");
        return Proxy.proxy(frontend, backend, null, null);
    }

    public static long startStopwatch()
    {
        return System.nanoTime();
    }

    public static long stopStopwatch(long watch)
    {
        return (System.nanoTime() - watch) / 1000;
    }

    public static int makeVersion(int major, int minor, int patch)
    {
        return ((major) * 10000 + (minor) * 100 + (patch));
    }

    public static String strerror(int errno)
    {
        return "Errno = " + errno;
    }
}
