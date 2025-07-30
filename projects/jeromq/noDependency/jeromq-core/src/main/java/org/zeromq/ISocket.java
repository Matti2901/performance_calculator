package org.zeromq;

import zmq.SocketBase;

public interface ISocket {
    SocketBase base();

    void mayRaise();
}
