package zmq.helper;

import zmq.core.Msg;
import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.io.IOThread;
import zmq.io.SessionBase;
import zmq.io.net.Address;
import zmq.io.net.NetProtocol;
import zmq.socket.dummy.DummySocket;

import java.util.ArrayList;
import java.util.List;

public class DummySession extends SessionBase {
    public final List<Msg> out = new ArrayList<>();

    public DummySession() {
        this(new DummyIOThread(), false, new DummySocket(), new Options(), new Address(NetProtocol.tcp, "localhost:9090"));
    }

    public DummySession(IOThread ioThread, boolean connect, SocketBase socket, Options options, Address addr) {
        super(ioThread, connect, socket, options, addr);
    }

    @Override
    public boolean pushMsg(Msg msg) {
        System.out.println("session.write " + msg);
        out.add(msg);
        return true;
    }

    @Override
    public Msg pullMsg() {
        System.out.println("session.read " + out);
        if (out.isEmpty()) {
            return null;
        }

        return out.remove(0);
    }

}
