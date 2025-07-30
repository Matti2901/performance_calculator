package zmq.io.net.ipc.listener;

import java.net.InetSocketAddress;

import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.io.IOThread;
import zmq.io.net.ipc.IpcAddress;
import zmq.io.net.tcp.TcpListener;

// fake Unix domain socket
public class IpcListener extends TcpListener
{
    private IpcAddress address;

    public IpcListener(IOThread ioThread, SocketBase socket, Options options)
    {
        super(ioThread, socket, options);

    }

    // Get the bound address for use with wildcards
    @Override
    public String getAddress()
    {
        if (address.address().getPort() == 0) {
            return address(address);
        }
        else {
            return address.toString();
        }
    }

    //  Set address to listen on.
    @Override
    public boolean setAddress(String addr)
    {
        address = new IpcAddress(addr);

        InetSocketAddress sock = address.address();
        return setAddress(sock);
    }
}
