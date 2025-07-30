package zmq.io.net;

import zmq.io.net.family.ProtocolFamily;

import java.net.SocketAddress;

public interface IZAddress<SA extends SocketAddress> {
    ProtocolFamily family();

    String toString(int port);

    SA resolve(String name, boolean ipv6, boolean local);

    SA address();

    SA sourceAddress();
}
