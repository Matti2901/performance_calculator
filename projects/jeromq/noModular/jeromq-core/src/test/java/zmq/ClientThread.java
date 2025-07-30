package zmq;

import zmq.core.Msg;
import zmq.core.SocketBase;
import zmq.core.ZMQ;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

//  Client threads loop on send/recv until told to exit
class ClientThread extends Thread {
    final SocketBase client;

    ClientThread(SocketBase client) {
        this.client = client;
    }

    public void run() {
        for (int count = 0; count < 15000; count++) {
            Msg msg = new Msg("0".getBytes());
            int rc = ZMQ.send(client, msg, 0);
            assertThat(rc, is(1));
        }
        Msg msg = new Msg("1".getBytes());
        int rc = ZMQ.send(client, msg, 0);
        assertThat(rc, is(1));
    }
}
