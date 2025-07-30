package zmq.socket.dummy;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class DummySocketChannel implements WritableByteChannel {
    private final int bufsize;
    private final byte[] buf;

    public DummySocketChannel() {
        this(64);
    }

    public DummySocketChannel(int bufsize) {
        this.bufsize = bufsize;
        buf = new byte[bufsize];
    }

    public byte[] data() {
        return buf;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public int write(ByteBuffer src) {
        int remaining = src.remaining();
        if (remaining > bufsize) {
            src.get(buf);
            return bufsize;
        }
        src.get(buf, 0, remaining);
        return remaining;
    }

}
