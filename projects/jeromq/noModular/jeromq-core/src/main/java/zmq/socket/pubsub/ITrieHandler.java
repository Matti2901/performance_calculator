package zmq.socket.pubsub;

import zmq.pipe.Pipe;

public interface ITrieHandler {
    void added(byte[] data, int size, Pipe arg);
}
