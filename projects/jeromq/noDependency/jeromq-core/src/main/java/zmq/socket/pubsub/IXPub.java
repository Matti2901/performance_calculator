package zmq.socket.pubsub;

import zmq.pipe.Pipe;

public interface IXPub {
    void sendUnsubscription(byte[] data, int size);

    void markAsMatching(Pipe pipe);
}
