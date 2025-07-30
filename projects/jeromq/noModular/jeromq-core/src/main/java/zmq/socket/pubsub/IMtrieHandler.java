package zmq.socket.pubsub;

import zmq.pipe.Pipe;

public interface IMtrieHandler {
    void invoke(Pipe pipe, byte[] data, int size, XPub arg);
}
