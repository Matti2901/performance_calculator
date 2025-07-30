package zmq.io.net.ipc.connector;

import java.net.InetSocketAddress;

import zmq.core.Options;
import zmq.io.IOThread;
import zmq.io.SessionBase;
import zmq.io.net.Address;
import zmq.io.net.tcp.TcpConnecter;

public class IpcConnecter extends TcpConnecter
{
    public IpcConnecter(IOThread ioThread, SessionBase session, final Options options, final Address<InetSocketAddress> addr, boolean wait)
    {
        super(ioThread, session, options, addr, wait);
    }
}
