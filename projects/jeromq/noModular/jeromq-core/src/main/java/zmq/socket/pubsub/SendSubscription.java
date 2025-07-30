package zmq.socket.pubsub;

import zmq.pipe.Pipe;

final class SendSubscription implements ITrieHandler {
    private final XSub xSub;

    public SendSubscription(XSub xSub) {
        this.xSub = xSub;
    }

    @Override
    public void added(byte[] data, int size, Pipe pipe) {
        xSub.sendSubscription(data, size, pipe);
    }
}
