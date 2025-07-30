package guide.ibbroker;

import guide.ZHelper;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * Basic request-reply client using REQ socket
 */
class ClientTask extends Thread {
    @Override
    public void run() {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            ZMQ.Socket client = context.createSocket(SocketType.REQ);
            ZHelper.setId(client); //  Set a printable identity

            client.connect("ipc://frontend.ipc");

            //  Send request, get reply
            client.send("HELLO");
            String reply = client.recvStr();
            System.out.println("Client: " + reply);
        }
    }
}
