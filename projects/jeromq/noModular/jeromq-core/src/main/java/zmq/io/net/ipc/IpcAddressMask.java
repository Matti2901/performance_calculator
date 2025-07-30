package zmq.io.net.ipc;

import zmq.io.net.tcp.TcpAddress;

import java.net.SocketAddress;

public class IpcAddressMask extends TcpAddress
{
    public IpcAddressMask(String addr, boolean ipv6)
    {
        super(addr, ipv6);
    }

    public boolean matchAddress(SocketAddress addr)
    {
        return address().equals(addr);
    }
}
