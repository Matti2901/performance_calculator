package zmq.io.net.tipc;

import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.io.IOThread;
import zmq.io.net.tcp.TcpListener;

public class TipcListener extends TcpListener
{
    public TipcListener(IOThread ioThread, SocketBase socket, final Options options)
    {
        super(ioThread, socket, options);
        // TODO V4 implement tipc
        throw new UnsupportedOperationException("TODO implement tipc");
    }
}
