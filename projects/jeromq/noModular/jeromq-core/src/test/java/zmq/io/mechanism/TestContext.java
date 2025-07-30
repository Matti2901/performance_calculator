package zmq.io.mechanism;

import zmq.core.Ctx;
import zmq.core.SocketBase;

abstract class TestContext {
    Ctx zctxt;
    SocketBase server;
    SocketBase client;
    SocketBase zapHandler;
    final String host = "tcp://127.0.0.1:*";
}
