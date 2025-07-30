package zmq.socket.pubsub;

import zmq.pipe.Pipe;

final class SendUnsubscription implements IMtrieHandler {
    @Override
    public void invoke(Pipe pipe, byte[] data, int size, XPub self) {
        self.sendUnsubscription(data, size);
    }
}
