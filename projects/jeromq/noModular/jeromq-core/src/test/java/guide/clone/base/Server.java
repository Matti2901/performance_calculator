package guide.clone.base;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

//  .split working with servers
//  The backend agent manages a set of servers, which we implement using
//  our simple class model:
class Server {
    final String address;    //  Server address
    final int port;       //  Server port
    final ZMQ.Socket snapshot;   //  Snapshot socket
    final ZMQ.Socket subscriber; //  Incoming updates
    long expiry;     //  When server expires
    int requests;   //  How many snapshot requests made?

    protected Server(ZContext ctx, String address, int port, String subtree) {
        System.out.printf("I: adding server %s:%d...\n", address, port);
        this.address = address;
        this.port = port;

        snapshot = ctx.createSocket(SocketType.DEALER);
        snapshot.connect(String.format("%s:%d", address, port));
        subscriber = ctx.createSocket(SocketType.SUB);
        subscriber.connect(String.format("%s:%d", address, port + 1));
        subscriber.subscribe(subtree.getBytes(ZMQ.CHARSET));
    }

    protected void destroy() {
    }
}
