package zmq.socket.pubsub;

import zmq.pipe.Pipe;

import java.util.concurrent.atomic.AtomicInteger;

final class MtrieHandler implements IMtrieHandler {
    final AtomicInteger counter = new AtomicInteger();

    @Override
    public void invoke(Pipe pipe, byte[] data, int size, XPub pub) {
        counter.incrementAndGet();
    }
}
